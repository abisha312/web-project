namespace SmartAlerts.API.IOModels
{
    public class ShopDetailResponse
    {
        public long ShopDetailID { get; set; }

        public long UserID { get; set; }

        public long ShopTypeID { get; set; }

        public string ShopName { get; set; }
        public string? ShopProfilePicURL { get; set; }
    }
}
