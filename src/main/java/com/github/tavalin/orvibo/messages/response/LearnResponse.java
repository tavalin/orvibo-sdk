package com.github.tavalin.orvibo.messages.response;

import com.github.tavalin.orvibo.messages.OrviboMessage;

public class LearnResponse extends OrviboMessage {
    
    private String code;
    
 public void setCode(String code) {
     this.code = code;
 }
 
 public String getCode() {
     return code;
 }
 

}
