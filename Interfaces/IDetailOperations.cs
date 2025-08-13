using SmartAlerts.API.IOModels;

namespace SmartAlerts.API.Interfaces
{
    public interface IDetailOperations
    {
        Task<APIResult<ShopDetailResponse>> AddShopDetail(ShopDetailRequest request);
        Task<APIResult<CustomerDetailResponse>> AddCustomerDetail(CustomerDetailRequest request);


    }
}
