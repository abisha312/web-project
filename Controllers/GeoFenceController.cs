using Microsoft.AspNetCore.Mvc;
using SmartAlerts.API.IOModels;
using SmartAlerts.API.Utilities;
using SmartAlerts.API.Interfaces;


namespace SmartAlerts.API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class GeoFenceController : Controller
    {
        private readonly IGeoFenceService _geoFenceService;

        public GeoFenceController(IGeoFenceService geoFenceService)
        {
            _geoFenceService = geoFenceService;
        }

        [HttpPost("CheckProximity")]
        public async Task<IActionResult> CheckProximity([FromBody] GeoFenceRequest request)
        {
            var result = await _geoFenceService.CheckAndSendNotification(request);
            return Ok(result);
        }
    }
}
