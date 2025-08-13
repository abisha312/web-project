using Azure.Core;
using Microsoft.AspNetCore.DataProtection;
using Microsoft.AspNetCore.Identity.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using SmartAlerts.API.Interfaces;
using SmartAlerts.API.IOModels;
using SmartAlerts.API.Models;
using SmartAlerts.API.Utilities;
using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace SmartAlerts.API.Implementation
{
    public class AccountOperations : IAccountOperations
    {

        SmartAlertsDbContext _dbContext1;

        private JwtServices _jwtServices;
        public AccountOperations(SmartAlertsDbContext dbContext, JwtServices jwtServices)
        {
            _dbContext1 = dbContext;
            _jwtServices = jwtServices;
        }

        public async Task<APIResult<SigninResponse>> AuthenticateUser(SigninRequest request)
        {
            var result = new APIResult<SigninResponse>();
            try
            {
                var userInfo = _dbContext1.UserMasters.FirstOrDefault(u => u.Email == request.Email && (u.LoginPassword == request.Password||u.TempPassword==request.Password));
                if (userInfo == null)
                {
                    result.ValidationResponse("Invalid Credentials");
                    return result;
                }
                long userSpecificId = -1;
                string FCMToken= null; 
                if (userInfo.UserTypeID == 2)
                {
                    var shop = _dbContext1.ShopDetails.FirstOrDefault(u => u.UserID == userInfo.UserID);
                    userSpecificId = shop.ShopDetailID;
                }
                if (userInfo.UserTypeID == 3)
                {
                    var shop = _dbContext1.CustomerDetails.FirstOrDefault(u => u.UserID == userInfo.UserID);
                    userSpecificId = shop.CustomerDetailID;
                    FCMToken = shop.DeviceFCMToken;
                }

                //var keyBytes = new byte[32]; // 256 bits / 8 bits per byte = 32 bytes
                //using (var rng = new RNGCryptoServiceProvider())
                //{
                //    rng.GetBytes(keyBytes);
                //}

                //// Generate JWT token
                //var tokenHandler = new JwtSecurityTokenHandler();
                //var tokenDescriptor = new SecurityTokenDescriptor
                //{
                //    Expires = DateTime.UtcNow.AddMinutes(30), // adjust the expiration time as needed
                //    SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(keyBytes), SecurityAlgorithms.HmacSha256Signature)
                //};
                //var token = tokenHandler.CreateToken(tokenDescriptor);


                var loginResponse = new SigninResponse
                {
                    UserID = userInfo.UserID,
                    UserName = userInfo.FirstName,
                    Email = userInfo.Email,
                    UserTypeID = userInfo.UserTypeID,
                    AccessToken = "",// tokenHandler.WriteToken(token) // assign the JWT token to AccessToken  2. JWT Token creation
                    UserSpecificID = userSpecificId,
                    FCMToken = FCMToken
                };
                loginResponse.AccessToken = _jwtServices.GenerateToken(loginResponse);
                result.ResponseData.Add(loginResponse);
                result.Success("Login successful");
                return result;
            }
            catch (Exception ex)
            {
                result.ErrorResponse("Error authenticating user", ex);
                return result;
            }
        }

        public async Task<APIResult<ForgotPwResponse>> ForgotPassword(ForgotPwRequest request)
        {
            //throw new NotImplementedException();
            var result = new APIResult<ForgotPwResponse>();
            try
            {
                var userInfo = _dbContext1.UserMasters.FirstOrDefault(u => u.Email == request.Email);
                if (userInfo == null)
                {
                    result.ValidationResponse("Unregistered Email");
                    return result;
                }
                var tempPwGenerator = new TempPwGenerator();
                userInfo.TempPassword = tempPwGenerator.GenerateTemporaryPassword();
                await _dbContext1.SaveChangesAsync();
                var exptime = tempPwGenerator.SetExpiryTime();
                //tempPwGenerator.SendEmail(userInfo.Email,userInfo.TempPassword,exptime);
                var forgotPwResponse = new ForgotPwResponse
                {
                    TempPw = userInfo.TempPassword,
                    ExpiresIn = exptime
                };
                
                result.ResponseData.Add(forgotPwResponse);

                result.Success("Temporary Password Generated Successfully");
                tempPwGenerator.SendEmail(request.Email,userInfo.TempPassword,exptime);
                return result;
            }
            catch (Exception ex)
            {
                result.ErrorResponse("Error authenticating user", ex);
                return result;
            }
        }

        public async Task<APIResult<ResetPwResponse>> ResetPassword(ResetPwRequest request)
        {
            var result = new APIResult<ResetPwResponse>();
            try 
            {
                var userInfo = _dbContext1.UserMasters.FirstOrDefault(u => u.Email == request.Email && u.LoginPassword == request.OldPw);
                if (userInfo == null)
                {
                    result.ValidationResponse("Check your Credentials");
                    return result;
                }
                if (string.IsNullOrEmpty(request.Email))
                {
                    result.ValidationResponse("Please Provide Your Email");
                    return result;
                }
                if (string.IsNullOrEmpty(request.OldPw))
                {
                    result.ValidationResponse("Please Provide Your Old Password");
                    return result;
                }
                if (string.IsNullOrEmpty(request.NewPw))
                {
                    result.ValidationResponse("Please Enter a New Password");
                    return result;
                }

                var pwValCheck = new PasswordValidityCheck();
                var pwCheck = pwValCheck.PasswordCheck(request.NewPw);
                if (pwCheck != "OK")
                {
                    result.ValidationResponse(pwCheck);
                }

                userInfo.LoginPassword = request.NewPw;
                await _dbContext1.SaveChangesAsync();

                var resetPwResponse = new ResetPwResponse
                {
                    successmsg = "Password Reset Successful"
                };
                result.ResponseData.Add(resetPwResponse);
                result.Success("User registered successfully");
                return result;
            }
            catch (Exception ex)
            {
                result.ErrorResponse("Error authenticating user", ex);
                return result;
            }
        }

        public async Task<APIResult<SignupResponse>> RegisterUser(SignupRequest request)
        {
            var result = new APIResult<SignupResponse>();
            try
            {
                if (request == null)
                {
                    result.ValidationResponse("Please Provide Valid Credentials");
                    return result;
                }
                if (string.IsNullOrEmpty(request.FirstName))
                {
                    result.ValidationResponse("Please Provide Your First Name");
                    return result;
                }
                if (string.IsNullOrEmpty(request.LastName))
                {
                    result.ValidationResponse("Please Provide Your Last Name");
                    return result;
                }
                if (string.IsNullOrEmpty(request.Address1))
                {
                    result.ValidationResponse("Please Provide Your Address");
                    return result;
                }
                if (string.IsNullOrEmpty(request.Email))
                {
                    result.ValidationResponse("Please Provide Your Email");
                    return result;
                }
                if (string.IsNullOrEmpty(request.Password))
                {
                    result.ValidationResponse("Please Enter a Password");
                    return result;
                }
                var pwValCheck = new PasswordValidityCheck();
                var pwCheck = pwValCheck.PasswordCheck(request.Password);
                if(pwCheck != "OK")
                {
                    result.ValidationResponse(pwCheck);
                }

                var newUser = new UserMaster
                {
                    ShopName = request.ShopName,
                    Address1 = request.Address1,
                    Address2 = request.Address2,
                    City = request.City,
                    StateOrProvince = request.StateOrProvince,
                    PostalCode = request.PostalCode,
                    Country = request.Country,
                    ContactNo = request.ContactNo,
                    FirstName = request.FirstName,
                    LastName = request.LastName,
                    Email = request.Email,
                    LoginPassword = request.Password
                };
                if (newUser.ShopName == "")
                {
                    newUser.UserTypeID = 3;
                }
                else
                {
                    newUser.UserTypeID = 2;
                }
                if (newUser.Address2 == "")
                {
                    newUser.Address2 = null;
                }
                newUser.CreatedOn = DateTime.Now;
                _dbContext1.UserMasters.Add(newUser);
                await _dbContext1.SaveChangesAsync();

                var signupResponse = new SignupResponse
                {
                    UserID = newUser.UserID,
                    FirstName = newUser.FirstName,
                    LastName = newUser.LastName,
                    Email = newUser.Email
                };
                result.ResponseData.Add(signupResponse);
                result.Success("User registered successfully");
                return result;
            }
            catch (Exception ex)
            {
                result.ErrorResponse("Error registering user", ex);
                return result;
            }
        }
    }
}