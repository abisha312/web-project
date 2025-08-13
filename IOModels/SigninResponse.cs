namespace SmartAlerts.API.IOModels
{
    public class SigninResponse
    {
        public long UserID { get; set; }
        public string UserName { get; set; }
        public string Email { get; set; }
        public long UserTypeID { get; set; }
        public string AccessToken { get; set; }
        public long UserSpecificID { get; set; }
        public string FCMToken { get; set; }
    }
}
