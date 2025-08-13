using Azure.Core;
using System.Text.RegularExpressions;

namespace SmartAlerts.API.Utilities
{
    public class PasswordValidityCheck
    {
        public string PasswordCheck(string password)
        {
            const int minPasswordLength = 8;
            const int maxPasswordLength = 10;
            const string passwordRegexPattern = @"^(?=.[a-z])(?=.[A-Z])(?=.\d)(?=.[@$!%?&])[A-Za-z\d@$!%?&]{8,10}$";

            if (password.Length < minPasswordLength || password.Length > maxPasswordLength)
            {
                return "Password must be between 8 and 10 characters";
            }

            if (Regex.IsMatch(password, passwordRegexPattern))
            {
                return "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character";
            }
            return "OK";
        }
    }
}
