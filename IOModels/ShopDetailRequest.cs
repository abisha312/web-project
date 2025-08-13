namespace SmartAlerts.API.IOModels
{
    public class ShopDetailRequest 
    {

        public long UserID { get; set; }

        public string ShopType { get; set; }

        public TimeSpan OpeningTime { get; set; }

        public TimeSpan ClosingTime { get; set; }

        public string? WebsiteURL { get; set; }

        public string ShopManagerName { get; set; }

        public string ShopContact { get; set; } 
        public string? ShopProfilePicURL { get; set; }
        public string Latitude { get; set; } 

        public string Longitude { get; set; }
    }
}
