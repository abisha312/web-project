package com.smart.alerts.models;

import java.util.ArrayList;
import java.util.List;

public class APIResult<T> {
    public APIResult()
    {
        ResponseCode = ResponseCodes.Exception;
        ResponseMessage = "";
        ErrorDetail = "";
        ResponseData = new ArrayList<>();
    }
    /// <summary>
/// Gets or sets the Response code for the current request
/// </summary>
    public ResponseCodes ResponseCode;
    /// <summary>
///
/// </summary>
    public String ResponseMessage;
    /// <summary>
///
/// </summary>
    public String ErrorDetail;
    public List<T> ResponseData;

    public boolean getSuccess() {
        return ResponseCode == ResponseCodes.Success;
    }
}
