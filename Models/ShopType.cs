using System;
using System.Collections.Generic;

namespace SmartAlerts.API.Models;

public partial class ShopType
{
    public long ShopTypeID { get; set; }

    public string ShopTypeName { get; set; } = null!;

    public long CreatedBy { get; set; }

    public DateTime CreatedOn { get; set; }

    public long? LastModifiedBy { get; set; }

    public DateTime? LastModifiedOn { get; set; }

    public int ShopTypeStatus { get; set; }

    public virtual ICollection<NotificationSetting> NotificationSettings { get; set; } = new List<NotificationSetting>();

    public virtual ICollection<ShopDeal> ShopDeals { get; set; } = new List<ShopDeal>();

    public virtual ICollection<ShopDetail> ShopDetails { get; set; } = new List<ShopDetail>();
}
