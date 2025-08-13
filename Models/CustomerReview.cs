using System;
using System.Collections.Generic;

namespace SmartAlerts.API.Models;

public partial class CustomerReview
{
    public long ReviewID { get; set; }

    public string? Review { get; set; }

    public long CustomerDetailID { get; set; }

    public long ShopID { get; set; }

    public long CreatedBy { get; set; }

    public DateTime CreatedOn { get; set; }

    public long? LastModifiedBy { get; set; }

    public DateTime? LastModifiedOn { get; set; }

    public int CustomerReviewStatus { get; set; }

    public virtual CustomerDetail CustomerDetail { get; set; } = null!;

    public virtual ShopDetail Shop { get; set; } = null!;
}
