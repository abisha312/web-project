using Azure.Core;
using Microsoft.AspNetCore.Mvc;
using SmartAlerts.API.Interfaces;
using SmartAlerts.API.IOModels;

namespace SmartAlerts.API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class SaleController : ControllerBase
    {
        ISaleOperations _saleOperations;
        public SaleController(ISaleOperations saleoperations)
        {
            _saleOperations = saleoperations;
        }

        [HttpPost("AddDeal")]
        public async Task<IActionResult> AddDeal([FromBody] DealModel request)
        {
            var result = await _saleOperations.AddDeal(request);
            return Ok(result);
        }

        [HttpPost("UpdateDeal")]
        public async Task<IActionResult> UpdateDeal(ExistingDealModel request)
        {
            var result = await _saleOperations.UpdateDeal(request);
            return Ok(result);
        }

        [HttpPost("DeleteDeal")]
        public async Task<IActionResult> DeleteDeal(long shopDealId)
        {
            var result = await _saleOperations.DeleteDeal(shopDealId);
            return Ok(result);
        }

        [HttpPost("ViewDeals")]
        public async Task<IActionResult> ViewDeals(long shopId)
        {
            var result = await _saleOperations.ViewDeals(shopId);
            return Ok(result);
        }

    }
}

