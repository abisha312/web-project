using Microsoft.EntityFrameworkCore;
using SmartAlerts.API.Interfaces;
using SmartAlerts.API.IOModels;
using SmartAlerts.API.Models;
using System.Globalization;

namespace SmartAlerts.API.Implementation
{
    public class SaleOperations : ISaleOperations
    {
        SmartAlertsDbContext _dbContext1;
        private readonly IWebHostEnvironment _env;
        private readonly IHttpContextAccessor _httpContextAccessor;
        public SaleOperations(SmartAlertsDbContext dbContext, IWebHostEnvironment env, IHttpContextAccessor httpContextAccessor)
        {
            _dbContext1 = dbContext;
            _env = env;
            _httpContextAccessor = httpContextAccessor;
        }
        public async Task<APIResult<ExistingDealModel>> AddDeal(DealModel request)
        {
            var result = new APIResult<ExistingDealModel>();

            try
            {
                if (request.UserID <= 0)
                    return result.ValidationResponse("Invalid Shop ID");

                if (string.IsNullOrWhiteSpace(request.DealImage1))
                    return result.ValidationResponse("DealImage1 is required");

                if (string.IsNullOrWhiteSpace(request.DealName))
                    return result.ValidationResponse("Deal name is required");

                if (request.DealStartDate == default)
                    return result.ValidationResponse("Please provide a valid start date for the deal");

                if (request.DealEndDate == default)
                    return result.ValidationResponse("Please provide a valid end date for the deal");

                if (request.DealStartDate >= request.DealEndDate)
                    return result.ValidationResponse("Deal end date must be after start date");

                if (request.DealPrice.HasValue && request.DealPrice < 0)
                    return result.ValidationResponse("Deal price cannot be negative");

                if (request.DealPercent.HasValue && (request.DealPercent < 0 || request.DealPercent > 100))
                    return result.ValidationResponse("Deal percent must be between 0 and 100");

                var user = await _dbContext1.UserMasters.FirstOrDefaultAsync(s => s.UserID == request.UserID);
                if (user == null)
                    return result.ValidationResponse("User not found");

                var shop = await _dbContext1.ShopDetails.FirstOrDefaultAsync(s => s.UserID == user.UserID);
                if (shop == null)
                    return result.ValidationResponse("Shop not found");

                // Upload image 1 (required)
                request.DealImage1 = await SaveBase64Image(request.DealImage1, "DealImages");

                // Upload image 2 (optional)
                if (!string.IsNullOrWhiteSpace(request.DealImage2))
                    request.DealImage2 = await SaveBase64Image(request.DealImage2, "DealImages");

                // Upload image 3 (optional)
                if (!string.IsNullOrWhiteSpace(request.DealImage3))
                    request.DealImage3 = await SaveBase64Image(request.DealImage3, "DealImages");

                var deal = new ShopDeal
                {
                    ShopTypeID = shop.ShopTypeID,
                    ShopID = shop.ShopDetailID,
                    ShopLocation = shop.ShopLocation,
                    DealName = request.DealName,
                    DealStartDate = request.DealStartDate,
                    DealEndDate = request.DealEndDate,
                    DealPrice = request.DealPrice,
                    DealPercent = request.DealPercent,
                    DealImage1 = request.DealImage1,
                    DealImage2 = request.DealImage2,
                    DealImage3 = request.DealImage3,
                    CreatedBy = shop.ShopDetailID,
                    CreatedOn = DateTime.UtcNow,
                    ShopDealStatus = 1
                };

                await _dbContext1.ShopDeals.AddAsync(deal);
                await _dbContext1.SaveChangesAsync();

                result.ResponseData.Add(new ExistingDealModel
                {
                    ShopDealID = deal.ShopDealID,
                    DealEndDate=deal.DealEndDate,
                    DealStartDate=deal.DealStartDate,
                    DealName=deal.DealName,
                    DealPrice=deal.DealPrice,
                    DealPercent=deal.DealPercent,
                });

                return result.Success("Deal added successfully");
            }
            catch (Exception ex)
            {
                return result.ErrorResponse("Failed to add deal", ex);
            }
        }

        private async Task<string> SaveBase64Image(string base64Image, string folderName)
        {
            try
            {
                if (base64Image.StartsWith("data:image"))
                {
                    int commaIndex = base64Image.IndexOf(',');
                    base64Image = base64Image.Substring(commaIndex + 1);
                }

                byte[] imageBytes = Convert.FromBase64String(base64Image);
                string uploadsFolder = Path.Combine(_env.WebRootPath, "Uploads", "DealImages");
                Directory.CreateDirectory(uploadsFolder); // Ensures it exists

                string uniqueFileName = Guid.NewGuid().ToString() + ".jpg";
                string filePath = Path.Combine(uploadsFolder, uniqueFileName);
                await File.WriteAllBytesAsync(filePath, imageBytes);

                var requestHost = _httpContextAccessor.HttpContext?.Request;
                return $"{requestHost?.Scheme}://{requestHost?.Host}/SmartAlertsAPI/Uploads/{folderName}/{uniqueFileName}";
            }
            catch
            {
                return null; // Optional fields will just be null if error occurs
            }
        }




        //2025-06-19T14:30:00
        public async Task<APIResult<ExistingDealModel>> UpdateDeal(ExistingDealModel request)
        {
            var result = new APIResult<ExistingDealModel>();

            try
            {
                /* ---------- 1. Fetch existing record ---------- */
                var shopDeal = await _dbContext1.ShopDeals
                                                .FirstOrDefaultAsync(u => u.ShopDealID == request.ShopDealID);

                if (shopDeal == null)
                    return result.ValidationResponse("Invalid Deal ID");

                /*  OPTIONAL:  Authorize the caller. 
                    If you trust ShopDealID alone, skip this block.   */
                // var shop = await _dbContext1.ShopDetails
                //                           .FirstOrDefaultAsync(s => s.ShopDetailID == shopDeal.ShopID);
                // if (shop == null || shop.UserID != request.UserID)
                //     return result.ValidationResponse("Not authorized to update this deal");

                /* ---------- 2. Validate incoming data ---------- */

                // Dates (only if provided)
                // ✅ Only run date comparison if both are explicitly supplied (not default)
                if (request.DealStartDate != default && request.DealEndDate != default)
                {
                    if (request.DealStartDate >= request.DealEndDate)
                        return result.ValidationResponse("Deal end date must be after start date");
                }

                // Price
                if (request.DealPrice.HasValue && request.DealPrice < 0)
                    return result.ValidationResponse("Deal price cannot be negative");

                // Percent
                if (request.DealPercent.HasValue &&
                    (request.DealPercent < 0 || request.DealPercent > 100))
                    return result.ValidationResponse("Deal percent must be between 0 and 100");

                /* ---------- 3. Handle new images (upload & replace) ---------- */

                if (!string.IsNullOrWhiteSpace(request.DealImage1))
                    shopDeal.DealImage1 = await SaveBase64Image(request.DealImage1, "DealImages");

                if (!string.IsNullOrWhiteSpace(request.DealImage2))
                    shopDeal.DealImage2 = await SaveBase64Image(request.DealImage2, "DealImages");

                if (!string.IsNullOrWhiteSpace(request.DealImage3))
                    shopDeal.DealImage3 = await SaveBase64Image(request.DealImage3, "DealImages");

                /* ---------- 4. Update scalar fields only if supplied ---------- */

                if (!string.IsNullOrWhiteSpace(request.DealName))
                    shopDeal.DealName = request.DealName;

                if (request.DealStartDate != default)
                    shopDeal.DealStartDate = request.DealStartDate;

                if (request.DealEndDate != default)
                    shopDeal.DealEndDate = request.DealEndDate;

                if (request.DealPrice.HasValue)
                    shopDeal.DealPrice = request.DealPrice;

                if (request.DealPercent.HasValue)
                    shopDeal.DealPercent = request.DealPercent;

                shopDeal.LastModifiedOn = DateTime.UtcNow;
                shopDeal.LastModifiedBy = shopDeal.ShopID;

                await _dbContext1.SaveChangesAsync();

                /* ---------- 5. Build response ---------- */
                var updated = new ExistingDealModel
                {
                    ShopDealID = shopDeal.ShopDealID,
                    DealName = shopDeal.DealName,
                    DealStartDate = shopDeal.DealStartDate,
                    DealEndDate = shopDeal.DealEndDate,
                    DealPrice = shopDeal.DealPrice,
                    DealPercent = shopDeal.DealPercent,
                    DealImage1 = shopDeal.DealImage1,
                    DealImage2 = shopDeal.DealImage2,
                    DealImage3 = shopDeal.DealImage3,
                };

                result.ResponseData.Add(updated);
                return result.Success("Deal updated successfully");
            }
            catch (Exception ex)
            {
                return result.ErrorResponse("Error updating deal", ex);
            }
        }


        public async Task<APIResult<string>> DeleteDeal(long shopDealId)
        {
            var result = new APIResult<string>();

            try
            {
                var deal = _dbContext1.ShopDeals.FirstOrDefault(d => d.ShopDealID == shopDealId);
                if (deal == null)
                {
                    result.ValidationResponse("Invalid Deal ID");
                    return result;
                }

                _dbContext1.ShopDeals.Remove(deal);
                await _dbContext1.SaveChangesAsync();

                result.ResponseData.Add("Deal deleted successfully");
                result.Success("Deal deleted successfully");
                return result;
            }
            catch (Exception ex)
            {
                result.ErrorResponse("Error deleting deal", ex);
                return result;
            }
        }

        public async Task<APIResult<DealListResult>> ViewDeals(long shopId)
        {
            var result = new APIResult<DealListResult>();

            try
            {
                if (shopId <= 0)
                    return result.ValidationResponse("Invalid Shop ID");

                var shop = await _dbContext1.ShopDetails.FirstOrDefaultAsync(s => s.ShopDetailID == shopId);
                if (shop == null)
                    return result.ValidationResponse("Shop not found");

                var currentTime = DateTime.UtcNow;

                var deals = await _dbContext1.ShopDeals
                    .Where(d => d.ShopID == shopId)
                    .OrderBy(d => d.DealEndDate < currentTime) // active first
                    .ThenBy(d => d.DealEndDate)
                    .Select(d => new ExistingDealModel
                    {
                        ShopDealID = d.ShopDealID,
                        DealName = d.DealName,
                        DealStartDate = d.DealStartDate,
                        DealEndDate = d.DealEndDate,
                        DealPrice = d.DealPrice,
                        DealPercent = d.DealPercent,
                        DealImage1 = d.DealImage1,
                        DealImage2 = d.DealImage2,
                        DealImage3 = d.DealImage3
                    })
                    .ToListAsync();

                result.ResponseData.Add(new DealListResult { Deals = deals });
                return result.Success("Deals fetched successfully");
            }
            catch (Exception ex)
            {
                return result.ErrorResponse("Failed to retrieve deals", ex);
            }
        }
        

    }
}
