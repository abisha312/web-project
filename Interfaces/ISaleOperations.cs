using SmartAlerts.API.IOModels;

namespace SmartAlerts.API.Interfaces
{
    public interface ISaleOperations
    {
        Task<APIResult<ExistingDealModel>> AddDeal(DealModel request);
        Task<APIResult<ExistingDealModel>> UpdateDeal(ExistingDealModel request);
        Task<APIResult<string>> DeleteDeal(long shopDealId);
        //Task<APIResult<DealModel>> DeleteDeal(DealModel request);

        Task<APIResult<DealListResult>> ViewDeals(long shopId);
    }
}
