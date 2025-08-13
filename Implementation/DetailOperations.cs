using Azure.Core;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using SmartAlerts.API.Interfaces;
using SmartAlerts.API.IOModels;
using SmartAlerts.API.Models;
using SmartAlerts.API.Utilities;

namespace SmartAlerts.API.Implementation
{
    public class DetailOperations : IDetailOperations
    {
        private readonly SmartAlertsDbContext _dbContext1;
        private readonly IWebHostEnvironment _env;
        private readonly IHttpContextAccessor _httpContextAccessor;

        public DetailOperations(SmartAlertsDbContext dbContext, IWebHostEnvironment env, IHttpContextAccessor httpContextAccessor)
        {
            _dbContext1 = dbContext;
            _env = env;
            _httpContextAccessor = httpContextAccessor;
        }

        public async Task<APIResult<ShopDetailResponse>> AddShopDetail(ShopDetailRequest request)
        {
            var result = new APIResult<ShopDetailResponse>();
            try
            {
                if (request == null || request.UserID <= 0)
                {
                    result.ValidationResponse("Invalid shop detail request or User ID");
                    return result;
                }

                if (request.OpeningTime == TimeSpan.Zero || request.ClosingTime == TimeSpan.Zero)
                {
                    result.ValidationResponse("Please enter valid Opening and Closing Time");
                    return result;
                }

                if (string.IsNullOrEmpty(request.ShopManagerName))
                {
                    result.ValidationResponse("Please Enter Manager Name");
                    return result;
                }

                if (!string.IsNullOrEmpty(request.ShopProfilePicURL))
                {
                    try
                    {
                        string base64Data = request.ShopProfilePicURL;
                        if (base64Data.StartsWith("data:image"))
                        {
                            int commaIndex = base64Data.IndexOf(',');
                            base64Data = base64Data.Substring(commaIndex + 1);
                        }

                        // Log length and preview of base64 for debugging
                        string debugLog = $"Decoded base64 length: {base64Data.Length}\n";
                        debugLog += "Sample ending: " + base64Data.Substring(Math.Max(0, base64Data.Length - 20)) + "\n";

                        byte[] imageBytes = Convert.FromBase64String(base64Data);

                        string uploadsFolder = Path.Combine(_env.WebRootPath, "Uploads", "ProfilePics");
                        Directory.CreateDirectory(uploadsFolder);

                        string uniqueFileName = Guid.NewGuid().ToString() + ".jpg";
                        string filePath = Path.Combine(uploadsFolder, uniqueFileName);

                        await File.WriteAllBytesAsync(filePath, imageBytes);

                        var requestHost = _httpContextAccessor.HttpContext?.Request;
                        string imageUrl = $"{requestHost?.Scheme}://{requestHost?.Host}/SmartAlertsAPI/Uploads/ProfilePics/{uniqueFileName}";
                        request.ShopProfilePicURL = imageUrl;
                    }
                    catch (Exception ex)
                    {
                        result.ValidationResponse("Base64 decoding failed: " + ex.Message);
                        return result;
                    }
                }

                var validTypes = new[] { "Food", "Clothing", "Accessories", "FMCG" };
                if (!validTypes.Contains(request.ShopType, StringComparer.OrdinalIgnoreCase))
                {
                    result.ValidationResponse("Invalid shop type spelling.");
                    return result;
                }

                var user = await _dbContext1.UserMasters.FirstOrDefaultAsync(u => u.UserID == request.UserID && u.UserTypeID == 2);
                if (user == null)
                {
                    result.ValidationResponse("Invalid user");
                    return result;
                }

                var locationParts = new List<string?>
                {
                    user.Address1, user.Address2, user.City,
                    user.StateOrProvince, user.Country, user.PostalCode
                };
                string shopLocation = string.Join(", ", locationParts.Where(p => !string.IsNullOrWhiteSpace(p)));

                var shopTypeMap = new Dictionary<string, int>(StringComparer.OrdinalIgnoreCase)
                {
                    { "Food", 2 },
                    { "Clothing", 3 },
                    { "Accessories", 4 },
                    { "FMCG", 5 }
                };

                if (!shopTypeMap.TryGetValue(request.ShopType, out int shopTypeId))
                {
                    result.ValidationResponse("Invalid shop type provided.");
                    return result;
                }

                var shopDetail = new ShopDetail
                {
                    UserID = user.UserID,
                    ShopTypeID = shopTypeId,
                    ShopName = user.ShopName,
                    OpeningTime = request.OpeningTime,
                    ClosingTime = request.ClosingTime,
                    WebsiteURL = request.WebsiteURL,
                    ShopManagerName = request.ShopManagerName,
                    ShopContact = request.ShopContact,
                    ShopLocation = shopLocation,
                    ShopProfilePicURL = request.ShopProfilePicURL,
                    CreatedBy = user.UserID,
                    CreatedOn = DateTime.Now,
                    ShopDetailStatus = 1,
                    Latitude = request.Latitude,
                    Longitude = request.Longitude
                };

                _dbContext1.ShopDetails.Add(shopDetail);
                await _dbContext1.SaveChangesAsync();

                result.ResponseData.Add(new ShopDetailResponse
                {
                    ShopDetailID = shopDetail.ShopDetailID,
                    UserID = shopDetail.UserID,
                    ShopTypeID = shopDetail.ShopTypeID,
                    ShopName = shopDetail.ShopName,
                    ShopProfilePicURL = shopDetail.ShopProfilePicURL
                });

                result.Success("Shop details added successfully");
                return result;
            }
            catch (Exception ex)
            {
                result.ErrorResponse("Failed to add shop details", ex);
                return result;
            }
        }

        public async Task<APIResult<CustomerDetailResponse>> AddCustomerDetail(CustomerDetailRequest request)
        {
            var result = new APIResult<CustomerDetailResponse>();

            try
            {
                if (request == null || request.UserID <= 0)
                {
                    result.ValidationResponse("Invalid customer detail request or User ID");
                    return result;
                }

                if (request.CustomerDOB == null || request.CustomerDOB == default)
                {
                    result.ValidationResponse("Please enter a valid Date of Birth");
                    return result;
                }

                if (request.CustomerAnniversary != null && request.CustomerAnniversary == default)
                {
                    request.CustomerAnniversary = null;
                }

                if (!string.IsNullOrEmpty(request.ProfilePicURL) && request.ProfilePicURL.StartsWith("data:image"))
                {
                    try
                    {
                        string base64Data = request.ProfilePicURL.Substring(request.ProfilePicURL.IndexOf(",") + 1);

                        // Optional debug log
                        string debugLog = $"Customer base64 length: {base64Data.Length}\n";
                        debugLog += "Sample ending: " + base64Data.Substring(Math.Max(0, base64Data.Length - 20)) + "\n";

                        byte[] imageBytes = Convert.FromBase64String(base64Data);

                        string uploadsFolder = Path.Combine(_env.WebRootPath, "Uploads", "CustomerProfile");
                        Directory.CreateDirectory(uploadsFolder); // Ensures it exists

                        string uniqueFileName = $"customer_{request.UserID}_{Guid.NewGuid()}.jpg";
                        string filePath = Path.Combine(uploadsFolder, uniqueFileName);
                        await File.WriteAllBytesAsync(filePath, imageBytes);

                        var requestHost = _httpContextAccessor.HttpContext?.Request;
                        string imageUrl = $"{requestHost?.Scheme}://{requestHost?.Host}/SmartAlertsAPI/Uploads/CustomerProfile/{uniqueFileName}";
                        request.ProfilePicURL = imageUrl;
                    }
                    catch (Exception ex)
                    {
                        // Log and continue without failing the request
                        Console.WriteLine("⚠️ Customer image save failed: " + ex.Message);
                        request.ProfilePicURL = null;
                    }
                }


                var user = await _dbContext1.UserMasters.FirstOrDefaultAsync(u => u.UserID == request.UserID && u.UserTypeID == 3);
                if (user == null)
                {
                    result.ValidationResponse("Invalid user");
                    return result;
                }

                var customerDetail = new CustomerDetail
                {
                    UserID = user.UserID,
                    CustomerDOB = request.CustomerDOB,
                    CustomerAnniversary = request.CustomerAnniversary,
                    ProfilePicURL = request.ProfilePicURL,
                    DeviceType = "Android",
                    TimeZone = TimeZoneInfo.Local.Id,
                    DeviceFCMToken = request.DeviceFCMToken,
                    CreatedBy = user.UserID,
                    CreatedOn = DateTime.UtcNow,
                    CustomerDetailStatus = 1
                };

                _dbContext1.CustomerDetails.Add(customerDetail);
                await _dbContext1.SaveChangesAsync();

                result.ResponseData.Add(new CustomerDetailResponse
                {
                    UserID = customerDetail.UserID,
                    CustomerDOB = customerDetail.CustomerDOB
                });

                result.Success("Customer detail added successfully");
                return result;
            }
            catch (Exception ex)
            {
                result.ErrorResponse("Failed to add customer detail", ex);
                return result;
            }
        }
    }
}
