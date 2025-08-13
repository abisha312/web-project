using SmartAlerts.API.IOModels;

namespace SmartAlerts.API.Interfaces
{
    public interface IAccountOperations
    {
        Task<APIResult<SigninResponse>> AuthenticateUser(SigninRequest request);
        Task<APIResult<SignupResponse>> RegisterUser(SignupRequest request);
        Task<APIResult<ForgotPwResponse>> ForgotPassword(ForgotPwRequest request);
        Task<APIResult<ResetPwResponse>> ResetPassword(ResetPwRequest request);
    }
}

