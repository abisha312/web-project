namespace SmartAlerts.API.IOModels
{
    public class UserModel
    {
        /// <summary>
        /// Gets or Sets the User Id
        /// </summary>
        public long UserId { get; set; }
        /// <summary>
        /// First name of the user.
        /// </summary>
        public required string FirstName { get; set; }

        /// <summary>
        /// Last name of the user.
        /// </summary>
        public required string LastName { get; set; }

        /// <summary>
        /// Email address of the user.
        /// </summary>
        public required string Email { get; set; }

        public long UserTypeId { get; set; }

        /// <summary>
        /// Name of the user's shop.
        /// </summary>
        public string? ShopName { get; set; }

        /// <summary>
        /// First line of the user's address.
        /// </summary>
        public required string Address1 { get; set; }

        /// <summary>
        /// Second line of the user's address.
        /// </summary>
        public string? Address2 { get; set; }

        /// <summary>
        /// City of the user's address.
        /// </summary>
        public required string City { get; set; }

        /// <summary>
        /// Region or state of the user's address.
        /// </summary>
        public required string Region { get; set; }

        /// <summary>
        /// Postal code of the user's address.
        /// </summary>
        public required string PostalCode { get; set; }

        /// <summary>
        /// Country of the user's address.
        /// </summary>
        public required string Country { get; set; }

        /// <summary>
        /// Phone number of the user.
        /// </summary>
        public required string Phone { get; set; }

        public required string? ProfilePicUrl { get; set; }
    }
}
