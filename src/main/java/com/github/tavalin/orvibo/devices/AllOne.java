package com.github.tavalin.orvibo.devices;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.commands.CommandFactory;
import com.github.tavalin.orvibo.exceptions.OrviboException;
import com.github.tavalin.orvibo.protocol.Message;

public class AllOne extends OrviboDevice {

    private String learnFilename = null;
    private String emitFilename = null;
    private String rootFolder = File.pathSeparator;

    public AllOne() {
        super(DeviceType.ALLONE);
    }

    public void emit() throws IOException {
        Message message = CommandFactory.createEmitCommand(this);
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(message);
    }

    public void learn() {
        Message message = CommandFactory.createLearnCommand(this);
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(message);
    }

    public void saveIrData(byte[] data) throws IOException, OrviboException {
        String filename = getLearnFilename();
        Files.write(Paths.get(filename), data);
    }

    public String getLearnFilename() throws OrviboException {
        if (StringUtils.isBlank(learnFilename) || learnFilename.indexOf(".") < 0) {
            throw new OrviboException("Learn filename has not been set.");
        }
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
