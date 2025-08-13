namespace SmartAlerts.API.IOModels
{

    /// <summary>
    /// 
    /// </summary>
    /// <typeparam name="T"></typeparam>
    public class APIResult<T>
    {
        public APIResult()
        {
            ResponseCode = ResponseCodes.Exception;
            ResponseMessage = string.Empty;
            ErrorDetail = string.Empty;
            ResponseData = new List<T>();
        }
        /// <summary>
        /// Gets or sets the Response code for the current request
        /// </summary>
        public ResponseCodes ResponseCode { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string ResponseMessage { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string ErrorDetail { get; set; }
        public List<T> ResponseData { get; set; }


    }

    public static class APIResultExtensions
    {
        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="result"></param>
        /// <param name="message"></param>
        /// <returns></returns>
        public static APIResult<T> Success<T>(this APIResult<T> result, string message)
        {
            result.ResponseCode = ResponseCodes.Success;
            result.ResponseMessage = message;
            return result;
        }
        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="result"></param>
        /// <param name="message"></param>
        /// <returns></returns>
        public static APIResult<T> ValidationResponse<T>(this APIResult<T> result, string message)
        {
            result.ResponseCode = ResponseCodes.ValidationError;
            result.ResponseMessage = message;
            return result;
        }
        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="result"></param>
        /// <param name="message"></param>
        /// <param name="ex"></param>
        /// <returns></returns>
        public static APIResult<T> ErrorResponse<T>(this APIResult<T> result, string message, Exception ex)
        {
            result.ResponseCode = ResponseCodes.Exception;
            result.ResponseMessage = message;
            result.ErrorDetail = ex.ToString();
            return result;
        }
    }

}
