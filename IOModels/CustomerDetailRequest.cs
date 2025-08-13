namespace SmartAlerts.API.IOModels
{
    public class CustomerDetailRequest
    {

        public long UserID { get; set; }

        public DateOnly CustomerDOB { get; set; }

        public DateOnly? CustomerAnniversary { get; set; }

        public string? ProfilePicURL { get; set; }
        public string? DeviceFCMToken { get; set; }
    }
}
