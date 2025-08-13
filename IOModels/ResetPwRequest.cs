namespace SmartAlerts.API.IOModels
{
    public class ResetPwRequest
    {
        public string Email { get; set; }
        public string OldPw { get; set; }
        public string NewPw { get; set; }
    }
}
