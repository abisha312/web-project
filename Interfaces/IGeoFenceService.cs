using SmartAlerts.API.IOModels;

namespace SmartAlerts.API.Interfaces
{
    public interface IGeoFenceService
    {
        Task<APIResult<string>> CheckAndSendNotification(GeoFenceRequest request);
    }
}
