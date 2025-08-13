using System;
using System.Collections.Generic;

namespace SmartAlerts.API.Models;

public partial class UserMaster
{
    public long UserID { get; set; }

    public string FirstName { get; set; } = null!;

    public string LastName { get; set; } = null!;

    public string Email { get; set; } = null!;

    public string LoginPassword { get; set; } = null!;

    public string? TempPassword { get; set; }

    public string? ShopName { get; set; }

    public string Address1 { get; set; } = null!;

    public string? Address2 { get; set; }

    public string City { get; set; } = null!;

    public string StateOrProvince { get; set; } = null!;

    public string Country { get; set; } = null!;

    public string PostalCode { get; set; } = null!;

    public long UserTypeID { get; set; }

    public string ContactNo { get; set; } = null!;

    public long CreatedBy { get; set; }

    public DateTime CreatedOn { get; set; }

    public long? LastModifiedBy { get; set; }

    public DateTime? LastModifiedOn { get; set; }

    public int UserStatus { get; set; }

    public virtual ICollection<CustomerDetail> CustomerDetails { get; set; } = new List<CustomerDetail>();

    public virtual ICollection<ShopDetail> ShopDetails { get; set; } = new List<ShopDetail>();

    public virtual UserTypeMaster UserType { get; set; } = null!;
}
