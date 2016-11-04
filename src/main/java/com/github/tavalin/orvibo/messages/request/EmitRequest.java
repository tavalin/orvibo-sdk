package com.github.tavalin.orvibo.messages.request;

import com.github.tavalin.orvibo.messages.OrviboRequest;

public class EmitRequest extends OrviboRequest {
    
    private String code;

	public EmitRequest(String deviceId, String code) {
		setDeviceId(deviceId);
		setCode(code);
	}
  
  public void setCode(String code) {
      this.code = code;
  }
  
  public String getCode() {
      return code;
  }
  

}
