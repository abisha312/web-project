using FirebaseAdmin.Messaging;
using Microsoft.EntityFrameworkCore;
using SmartAlerts.API.Interfaces;
using SmartAlerts.API.IOModels;
using SmartAlerts.API.Models;
using SmartAlerts.API.Utilities;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace SmartAlerts.API.Implementation
{
    public class GeoFenceService : IGeoFenceService
    {
        private readonly SmartAlertsDbContext _context;

        public GeoFenceService(SmartAlertsDbContext context)
        {
            _context = context;
        }

        public async Task<APIResult<string>> CheckAndSendNotification(GeoFenceRequest request)
        {
            var result = new APIResult<string>();

            try
            {
                if (request == null || request.UserId <= 0 || string.IsNullOrEmpty(request.DeviceToken))
                    return result.ValidationResponse("Invalid request data");

                double userLat = request.UserLat;
                double userLon = request.UserLon;

                var allShops = await _context.ShopDetails
                    .Where(s => !string.IsNullOrEmpty(s.Latitude) && !string.IsNullOrEmpty(s.Longitude))
                    .ToListAsync();

                var nearbyShops = allShops
                    .Where(shop =>
                    {
                        if (double.TryParse(shop.Latitude, out double shopLat) &&
                            double.TryParse(shop.Longitude, out double shopLon))
                        {
                            return GetDistance(userLat, userLon, shopLat, shopLon) <= 0.5;
                        }
                        return false;
                    })
                    .ToList();

                if (!nearbyShops.Any())
                {
                    // No shops nearby, return success but with empty ResponseData
                    return result.Success("No nearby shops detected");
                }

                foreach (var shop in nearbyShops)
                {
                    string body = $"New offers near you at {shop.ShopName}!";
                    var pushResult = await PushNotification.SendNotification(body, request.DeviceToken, 1);
                    result.ResponseData.Add($"Shop: {shop.ShopName}, Result: {pushResult}");
                }

                return result.Success("Notifications sent to nearby users.");
            }
            catch (Exception ex)
            {
                return result.ErrorResponse("Failed to send notifications", ex);
            }
        }



        // Haversine formula to calculate distance in KM
        private double GetDistance(double lat1, double lon1, double lat2, double lon2)
        {
            double R = 6371;
            double dLat = ToRadians(lat2 - lat1);
            double dLon = ToRadians(lon2 - lon1);
            double a = Math.Sin(dLat / 2) * Math.Sin(dLat / 2) +
                       Math.Cos(ToRadians(lat1)) * Math.Cos(ToRadians(lat2)) *
                       Math.Sin(dLon / 2) * Math.Sin(dLon / 2);
            double c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            return R * c;
        }

        private double ToRadians(double deg)
        {
            return deg * (Math.PI / 180);
        }
    }
}
