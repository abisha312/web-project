using Microsoft.AspNetCore.Mvc;
using SmartAlerts.API.Models;
using System.Net;
using System.Net.Mail;
using System.Security.Cryptography;
using System.Net.Mail;

namespace SmartAlerts.API.Utilities
{
    public class TempPwGenerator
    {
        private const int TemporaryPasswordLength = 12; // adjust the length as needed
        private const string AllowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+-=";
        private const int DefaultExpiryTimeInMinutes = 60;
        public string GenerateTemporaryPassword()
        {
            using (var rng = new RNGCryptoServiceProvider())
            {
                var passwordChars = new char[TemporaryPasswordLength];
                var randomBytes = new byte[4]; // 4 bytes for an integer
                for (var i = 0; i < TemporaryPasswordLength; i++)
                {
                    rng.GetBytes(randomBytes);
                    var randomIndex = BitConverter.ToInt32(randomBytes, 0) % AllowedCharacters.Length;
                    if (randomIndex < 0) // Ensure the index is not negative
                        randomIndex = -randomIndex;
                    passwordChars[i] = AllowedCharacters[randomIndex % AllowedCharacters.Length]; // Ensure the index is within bounds
                }
                var password = new string(passwordChars);
                return password;
            }
        }
        public DateTime SetExpiryTime(int expiryTimeInMinutes = DefaultExpiryTimeInMinutes)
        {
            var expiryTime = DateTime.UtcNow.AddMinutes(expiryTimeInMinutes);
            return expiryTime;
        }

        public void SendEmail(string toEmail, string tempPw, DateTime exptime)
        {
            try
            {
                // Define the SMTP settings
                string smtpServer = "smtp.gmail.com";
                int port = 587;
                string fromAddress = "abishaeunice123@gmail.com";
                string password = "wcoe ldgk bwam ykem";

                // Create a new MailMessage
                MailMessage message = new MailMessage();

                // Set the sender and recipient addresses
                message.From = new MailAddress(fromAddress);
                message.To.Add(new MailAddress(toEmail)); // Replace with the client's email

                // Set the subject and body of the email
                message.Subject = "Temporary Password";
                message.Body = "Your temporary password is:" + tempPw + " Expires at:" + exptime;

                // Create a new SmtpClient
                SmtpClient client = new SmtpClient(smtpServer, port);
                client.EnableSsl = true;
                client.Timeout = 30000;
                // Set the credentials
                client.Credentials = new NetworkCredential(fromAddress, password);

                // Send the email
                client.Send(message);
            }
            catch (Exception ex)
            {
                throw;
            }    
        }
    }
}
