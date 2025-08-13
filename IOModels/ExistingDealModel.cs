namespace SmartAlerts.API.IOModels
{
    public class ExistingDealModel
    {
        public long ShopDealID { get; set; }
        public string? DealName { get; set; }

        public DateTime DealStartDate { get; set; }

        public DateTime DealEndDate { get; set; }

        public double? DealPrice { get; set; }

        public double? DealPercent { get; set; }
        public string? DealImage1 { get; set; }
        public string? DealImage2 { get; set; }
        public string? DealImage3 { get; set; }
    }
}
