namespace SmartAlerts.API.IOModels
{
    public class ForgotPwResponse
    {
        public string TempPw { get; set; }
        public DateTime ExpiresIn { get; set; }
    }
}
