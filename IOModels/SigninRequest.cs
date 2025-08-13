namespace SmartAlerts.API.IOModels
{
    public class SigninRequest
    {
        /// <summary>
        /// Get or sets the user login email
        /// </summary>
        public string Email { get; set; }
        public string Password { get; set; }
    }
}
