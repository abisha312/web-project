using Microsoft.IdentityModel.Tokens;
using SmartAlerts.API.IOModels;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace SmartAlerts.API.Utilities
{
    /// <summary>
    /// 
    /// </summary>
    public class JwtServices
    {
        private IConfiguration _configuration;
        private IHttpContextAccessor _contextAccessor;
        private string _sigingKey = string.Empty;
        private string _encrptionKey = string.Empty;

        public JwtServices(IConfiguration configuration, IHttpContextAccessor contextAccessor)
        {
            _configuration = configuration;
            _contextAccessor = contextAccessor;
            _sigingKey = _configuration["JWT:SigingKey"] ?? "";
            _encrptionKey = _configuration["JWT:EncryptionKey"] ?? "";
        }

        /// <summary>
        /// Generates a JWT Token
        /// </summary>
        /// <param name="userInfo"></param>
        /// <returns></returns>
        public string GenerateToken(SigninResponse userInfo)
        {
            var symmetricSigningkey = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(_sigingKey));
            var symmetricEncKey = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(_encrptionKey));

            var objClaims = new List<Claim>
            {
                new Claim("UserId",userInfo.UserID.ToString()),
                new Claim("UserTypeId", userInfo.UserTypeID.ToString()),
                new Claim("Email", userInfo.Email),

                new Claim(JwtRegisteredClaimNames.Iss, _configuration["JWT:Issuer"] ?? "SmartAlerts"),
                new Claim(JwtRegisteredClaimNames.Aud, _configuration["JWT:Audience"] ?? "SmartAlertsUsers")
            };

            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(objClaims),
                Expires = DateTime.UtcNow.AddHours(6),
                IssuedAt = DateTime.UtcNow,
                Audience = _configuration["JWT:Audience"],
                Issuer = _configuration["JWT:Issuer"],
                SigningCredentials = new SigningCredentials(symmetricSigningkey, SecurityAlgorithms.HmacSha256Signature),
                EncryptingCredentials = new EncryptingCredentials(symmetricEncKey, JwtConstants.DirectKeyUseAlg, SecurityAlgorithms.Aes128CbcHmacSha256),
            };

            var tokenHandler = new JwtSecurityTokenHandler();
            var token = tokenHandler.CreateJwtSecurityToken(tokenDescriptor);
            var tokenString = tokenHandler.WriteToken(token);

            return tokenString;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public ClaimData GetClaimData()
        {
            ClaimData claimData = new ClaimData();
            try
            {
                var symmetricSigningkey = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(_sigingKey));
                var symmetricEncKey = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(_encrptionKey));

                SecurityToken securityToken;
                JwtSecurityTokenHandler handler = new JwtSecurityTokenHandler();

                TokenValidationParameters validationParameters = new TokenValidationParameters()
                {
                    ValidAudience = _configuration["JWT:Audience"],
                    ValidIssuer = _configuration["JWT:Issuer"],
                    ValidateLifetime = true,
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = symmetricSigningkey,
                    TokenDecryptionKey = symmetricEncKey
                };

                var httpContext = _contextAccessor.HttpContext;
                var authorizationHeader = httpContext?.Request.Headers["Authorization"].FirstOrDefault();
                var tokenString = authorizationHeader?.Replace("Bearer", "").Trim();
                ClaimsPrincipal claimsPrincipal = handler.ValidateToken(tokenString,validationParameters,out securityToken);
                claimData = new ClaimData
                {
                    UserId = Convert.ToInt64(claimsPrincipal.Claims.First(c => c.Type == "UserId").Value),
                    UserTypeId = Convert.ToInt64(claimsPrincipal.Claims.First(c => c.Type == "UserTypeId").Value),
                    Email = claimsPrincipal.Claims.First(c => c.Type == "Email").Value
                };

                return claimData;

            }
            catch (Exception ex)
            {
                throw;
            }
        }
    }

}
