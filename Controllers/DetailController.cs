using Microsoft.AspNetCore.Mvc;
using SmartAlerts.API.Interfaces;
using SmartAlerts.API.IOModels;

namespace SmartAlerts.API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class DetailController : ControllerBase
    {
        IDetailOperations _detailOperations;

        public DetailController(IDetailOperations detailOperations)
        {
            _detailOperations = detailOperations;
        }

        [HttpPost("addshopdetail")]
        public async Task<IActionResult> AddShopDetail([FromBody] ShopDetailRequest request)
        {
            var result = await _detailOperations.AddShopDetail(request);
            return Ok(result);
        }

        [HttpPost("addcustomerdetail")]
        public async Task<IActionResult> AddCustomerDetail([FromBody] CustomerDetailRequest request)
        {
            var result = await _detailOperations.AddCustomerDetail(request);
            return Ok(result);
        }
    }
}
