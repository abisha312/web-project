using System;
using System.Collections.Generic;

namespace SmartAlerts.API.Models;

public partial class ShopDetail
{
    public long ShopDetailID { get; set; }

    public long UserID { get; set; }

    public long ShopTypeID { get; set; }

    public string ShopName { get; set; } = null!;

    public string? WebsiteURL { get; set; }

    public string ShopManagerName { get; set; } = null!;

    public string ShopContact { get; set; }

    public string ShopLocation { get; set; } = null!;

    public string Latitude { get; set; } = null!;

    public string Longitude { get; set; } = null!;

    public long CreatedBy { get; set; }

    public DateTime CreatedOn { get; set; }

    public long? LastModifiedBy { get; set; }

    public DateTime? LastModifiedOn { get; set; }

    public int ShopDetailStatus { get; set; }
    public string? ShopProfilePicURL { get; set; }
    public TimeSpan OpeningTime { get; set; } 

    public TimeSpan ClosingTime { get; set; } 

    public virtual ICollection<CustomerReview> CustomerReviews { get; set; } = new List<CustomerReview>();

    public virtual ICollection<ShopDeal> ShopDeals { get; set; } = new List<ShopDeal>();

    public virtual ShopType ShopType { get; set; } = null!;

    public virtual UserMaster User { get; set; } = null!;
}
