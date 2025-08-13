namespace SmartAlerts.API.IOModels
{
    public class CustomerDetailResponse
    {
        public long UserID { get; set; }

        public DateOnly CustomerDOB { get; set; }
    }
}
