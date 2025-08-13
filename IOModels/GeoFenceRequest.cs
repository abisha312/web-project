namespace SmartAlerts.API.IOModels
{
    public class GeoFenceRequest
    {
        public int UserId { get; set; }         // Customer ID
        public double UserLat { get; set; }
        public double UserLon { get; set; }
        public string DeviceToken { get; set; }
    }

}
