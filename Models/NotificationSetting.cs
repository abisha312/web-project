using System;
using System.Collections.Generic;

namespace SmartAlerts.API.Models;

public partial class NotificationSetting
{
    public long NotificationID { get; set; }

    public string LocationRadius { get; set; } = null!;

    public long ShopTypeID { get; set; }

    public DateTime NotificationExpiration { get; set; }

    public long CreatedBy { get; set; }

    public DateTime CreatedOn { get; set; }

    public long? LastModifiedBy { get; set; }

    public DateTime? LastModifiedOn { get; set; }

    public int NotificationSettingStatus { get; set; }

    public virtual ShopType ShopType { get; set; } = null!;
}
