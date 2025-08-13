using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using SmartAlerts.API.Interfaces;
using SmartAlerts.API.IOModels;
using SmartAlerts.API.Models;
using System.Diagnostics.Metrics;
using System.Numerics;
using System.Text.RegularExpressions;

namespace SmartAlerts.API.Implementation
{
    public class UserOperation : IUserOperation
    {
        SmartAlertsDbContext _dbContext1;
        public UserOperation(SmartAlertsDbContext dbContext)
        {
            _dbContext1 = dbContext;
        }
        /// <inheritdoc/>
        public async Task<APIResult<UserModel>> GetProfile(long userId)
        {
            var result = new APIResult<UserModel>();

            try
            {
                var userProfile = _dbContext1.UserMasters.FirstOrDefault(u => u.UserID == userId);

                if (userProfile == null)
                {
                    result.ValidationResponse("Invalid User");
                    return result;
                }

                string profilePicUrl = null;

                if (userProfile.UserTypeID == 2) // Business
                {
                    var shopDetail = _dbContext1.ShopDetails.FirstOrDefault(s => s.UserID == userProfile.UserID);
                    profilePicUrl = shopDetail?.ShopProfilePicURL;
                }
                else if (userProfile.UserTypeID == 3) // Customer
                {
                    var customerDetail = _dbContext1.CustomerDetails.FirstOrDefault(c => c.UserID == userProfile.UserID);
                    profilePicUrl = customerDetail?.ProfilePicURL;
                }

                var getProfileResponse = new UserModel
                {
                    UserId = userProfile.UserID,
                    FirstName = userProfile.FirstName,
                    LastName = userProfile.LastName,
                    Email = userProfile.Email,
                    UserTypeId = userProfile.UserTypeID,
                    ShopName = userProfile.ShopName,
                    Address1 = userProfile.Address1,
                    Address2 = userProfile.Address2,
                    City = userProfile.City,
                    Region = userProfile.StateOrProvince,
                    Country = userProfile.Country,
                    PostalCode = userProfile.PostalCode,
                    Phone = userProfile.ContactNo,
                    ProfilePicUrl = profilePicUrl
                };

                result.ResponseData.Add(getProfileResponse);
                result.Success("Profile Displayed Successfully");
            }
            catch (Exception ex)
            {
                result.ErrorResponse("Error fetching profile", ex);
            }

            return result;
        }


        /// <inheritdoc/>
        public async Task<APIResult<UserModel>> UpdateProfile(UserModel request)
        {
            var result = new APIResult<UserModel>();
            try
            {
                var userProfile = _dbContext1.UserMasters.FirstOrDefault(u => u.UserID == request.UserId);
                if (userProfile == null)
                {
                    result.ValidationResponse("Invalid User");
                    return result;
                }

                // 🔧 Update UserMaster fields
                if (!string.IsNullOrEmpty(request.FirstName)) userProfile.FirstName = request.FirstName;
                if (!string.IsNullOrEmpty(request.LastName)) userProfile.LastName = request.LastName;
                if (!string.IsNullOrEmpty(request.ShopName)) userProfile.ShopName = request.ShopName;
                if (!string.IsNullOrEmpty(request.Address1)) userProfile.Address1 = request.Address1;
                if (!string.IsNullOrEmpty(request.Address2)) userProfile.Address2 = request.Address2;
                if (!string.IsNullOrEmpty(request.City)) userProfile.City = request.City;
                if (!string.IsNullOrEmpty(request.Region)) userProfile.StateOrProvince = request.Region;
                if (!string.IsNullOrEmpty(request.Country)) userProfile.Country = request.Country;
                if (!string.IsNullOrEmpty(request.PostalCode)) userProfile.PostalCode = request.PostalCode;
                if (!string.IsNullOrEmpty(request.Phone)) userProfile.ContactNo = request.Phone;

                userProfile.LastModifiedBy = request.UserId;
                userProfile.LastModifiedOn = DateTime.Now;

                string profilePicUrl = null;

                // 🔄 Update Profile Pic in ShopDetails or CustomerDetails
                if (userProfile.UserTypeID == 2) // Business
                {
                    var shopDetail = _dbContext1.ShopDetails.FirstOrDefault(s => s.UserID == request.UserId);
                    if (shopDetail != null && !string.IsNullOrEmpty(request.ProfilePicUrl))
                    {
                        shopDetail.ShopProfilePicURL = request.ProfilePicUrl;
                        profilePicUrl = shopDetail.ShopProfilePicURL;
                    }
                }
                else if (userProfile.UserTypeID == 3) // Customer
                {
                    var customerDetail = _dbContext1.CustomerDetails.FirstOrDefault(c => c.UserID == request.UserId);
                    if (customerDetail != null && !string.IsNullOrEmpty(request.ProfilePicUrl))
                    {
                        customerDetail.ProfilePicURL = request.ProfilePicUrl;
                        profilePicUrl = customerDetail.ProfilePicURL;
                    }
                }

                await _dbContext1.SaveChangesAsync();

                // ✅ Prepare response
                var updateprofileResponse = new UserModel
                {
                    UserId = userProfile.UserID,
                    FirstName = userProfile.FirstName,
                    LastName = userProfile.LastName,
                    Email = userProfile.Email,
                    UserTypeId = userProfile.UserTypeID,
                    ShopName = userProfile.ShopName,
                    Address1 = userProfile.Address1,
                    Address2 = userProfile.Address2,
                    City = userProfile.City,
                    Region = userProfile.StateOrProvince,
                    Country = userProfile.Country,
                    PostalCode = userProfile.PostalCode,
                    Phone = userProfile.ContactNo,
                    ProfilePicUrl = profilePicUrl
                };

                result.ResponseData.Add(updateprofileResponse);
                result.Success("Profile updated successfully");
                return result;
            }
            catch (Exception ex)
            {
                result.ErrorResponse("Error updating profile", ex);
                return result;
            }
        }

    }
}
