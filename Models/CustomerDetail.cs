using System;
using System.Collections.Generic;

namespace SmartAlerts.API.Models;

public partial class CustomerDetail
{
    public long CustomerDetailID { get; set; }

    public long UserID { get; set; }

    public DateOnly CustomerDOB { get; set; }

    public DateOnly? CustomerAnniversary { get; set; }

    public string DeviceType { get; set; } = null!;

    public string? TimeZone { get; set; }

    public string? ProfilePicURL { get; set; }

    public string? DeviceFCMToken { get; set; }

    public long CreatedBy { get; set; }

    public DateTime CreatedOn { get; set; }

    public long? LastModifiedBy { get; set; }

    public DateTime? LastModifiedOn { get; set; }

    public int CustomerDetailStatus { get; set; }

    public virtual ICollection<CustomerReview> CustomerReviews { get; set; } = new List<CustomerReview>();

    public virtual UserMaster User { get; set; } = null!;
}
