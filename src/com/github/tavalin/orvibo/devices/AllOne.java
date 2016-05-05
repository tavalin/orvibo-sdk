package com.github.tavalin.orvibo.devices;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.commands.CommandFactory;
import com.github.tavalin.orvibo.protocol.Message;


public class AllOne extends OrviboDevice {
    
    private String filename = null;
	
	public AllOne() {
        super(DeviceType.ALLONE);
    }

    public void emit(String emitFile) throws IOException {
		Message message = CommandFactory.createEmitCommand(this, emitFile);
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(message);
	}
	
	public void learn() {
		Message message = CommandFactory.createLearnCommand(this);
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(message);
	}
	
	public void setLearnName(String filename) {
	    setFilename(filename);
	}
	
    public void saveIrData(byte[] data) throws IOException {
        String filename = getFilename();
        if (filename != null && !"".equals(filename)) {
            Files.write(Paths.get(filename), data);
       }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
