using Azure;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using SmartAlerts.API.Interfaces;
using SmartAlerts.API.IOModels;

namespace SmartAlerts.API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AccountController : ControllerBase
    {
        IAccountOperations _accountOperations;

        public AccountController(IAccountOperations accountOperations)
        {
            _accountOperations = accountOperations;
        }

        [HttpPost("authenticate")]
        public async Task<IActionResult> Authenticate(SigninRequest request)
        {
            var result = await _accountOperations.AuthenticateUser(request);
            return Ok(result);
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register(SignupRequest request)
        {
            var result = await _accountOperations.RegisterUser(request);
            return Ok(result);
        }
        [HttpPost("forgotpassword")]
        public async Task<IActionResult> ForgotPassword(ForgotPwRequest request)
        {
            var result = await _accountOperations.ForgotPassword(request);
            return Ok(result);
        }
        [HttpPost("resetpassword")]
        public async Task<IActionResult> ResetPassword(ResetPwRequest request)
        {
            var result = await _accountOperations.ResetPassword(request);
            return Ok(result);
        }

    }
}
