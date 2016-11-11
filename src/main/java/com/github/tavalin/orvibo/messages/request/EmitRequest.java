package com.github.tavalin.orvibo.messages.request;

import com.github.tavalin.orvibo.messages.OrviboRequest;

public class EmitRequest extends OrviboRequest {
    
    private byte[] code;

	public EmitRequest(String deviceId, byte[] code) {
		setDeviceId(deviceId);
		setCode(code);
	}
  
  public void setCode(byte[] code) {
      this.code = code;
  }
  
  public byte[] getCode() {
      return code;
  }
  

}
