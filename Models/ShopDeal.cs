using System;
using System.Collections.Generic;

namespace SmartAlerts.API.Models;

public partial class ShopDeal
{
    public long ShopDealID { get; set; }

    public long ShopTypeID { get; set; }

    public string? DealName { get; set; }

    public DateTime DealStartDate { get; set; }

    public DateTime DealEndDate { get; set; }

    public double? DealPrice { get; set; }

    public double? DealPercent { get; set; }

    public long ShopID { get; set; }

    public string ShopLocation { get; set; } = null!;

    public long CreatedBy { get; set; }

    public DateTime CreatedOn { get; set; }

    public long? LastModifiedBy { get; set; }

    public DateTime? LastModifiedOn { get; set; }

    public int ShopDealStatus { get; set; }

    public virtual ShopDetail Shop { get; set; } = null!;

    public virtual ShopType ShopType { get; set; } = null!;
    public string? DealImage1 { get; set; } = null!;
    public string? DealImage2 { get; set; }
    public string? DealImage3 { get; set; }
}
