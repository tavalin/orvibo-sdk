package com.github.tavalin.orvibo.devices;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.commands.CommandFactory;
import com.github.tavalin.orvibo.protocol.Message;


public class AllOne extends OrviboDevice {
    
    private String learnFilename = null;
    private String emitFilename = null;
    private String rootFolder = "";
	
	public AllOne() {
        super(DeviceType.ALLONE);
    }

    public void emit() throws IOException  {
		Message message = CommandFactory.createEmitCommand(this);
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(message);
	}
	
	public void learn() {
		Message message = CommandFactory.createLearnCommand(this);
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(message);
	}
	
	
    public void saveIrData(byte[] data) throws IOException {
        String filename = getLearnFilename();
        if (filename != null && !"".equals(filename)) { // TODO: StringUtils?
            Files.write(Paths.get(filename), data);
       }
    }

    public String getLearnFilename() {
        return Paths.get(getRootFolder(), learnFilename).toString();
    }

    public void setLearnFilename(String learnFilename) {
        this.learnFilename = learnFilename;
    }

    public String getEmitFilename() {
        return Paths.get(getRootFolder(), emitFilename).toString();
    }
    
    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public void setEmitFilename(String emitFilename) {
        this.emitFilename = emitFilename;
    }

    public String getRootFolder() {
        return rootFolder;
    }



}
