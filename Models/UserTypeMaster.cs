using System;
using System.Collections.Generic;

namespace SmartAlerts.API.Models;

public partial class UserTypeMaster
{
    public long UserTypeId { get; set; }

    public string UserType { get; set; } = null!;

    public string? UserTypeDescription { get; set; }

    public int UserTypeStatus { get; set; }

    public virtual ICollection<UserMaster> UserMasters { get; set; } = new List<UserMaster>();
}
