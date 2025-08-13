using SmartAlerts.API.IOModels;

namespace SmartAlerts.API.Interfaces
{
    public interface IUserOperation
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="userId"></param>
        /// <returns></returns>
        Task<APIResult<UserModel>> GetProfile(long userId);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="request"></param>
        /// <returns></returns>
        Task<APIResult<UserModel>> UpdateProfile(UserModel request);
    }
}
