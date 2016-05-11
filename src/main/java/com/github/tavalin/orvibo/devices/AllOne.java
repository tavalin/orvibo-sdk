package com.github.tavalin.orvibo.devices;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.commands.CommandFactory;
import com.github.tavalin.orvibo.protocol.Message;

public class AllOne extends OrviboDevice {

    /** The Constant logger. */
    private final Logger logger = LoggerFactory.getLogger(AllOne.class);

    private Path learnPath = null;
    
    public AllOne() {
        super(DeviceType.ALLONE);
    }

    public void emit(Path file) throws IOException {
        Message message = CommandFactory.createEmitCommand(this, file);
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(message);
    }

    public void learn(Path file) {
        Message message = CommandFactory.createLearnCommand(this);
        setLearnPath(file);
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(message);
    }

    public void saveLearnedData(byte[] data) throws IOException {
        Path path = getLearnPath();
        if (path == null) {
            throw new IOException("Learn path has not been set. Could not save data.");
        }
        logger.debug("writing learn data to {}",path.getFileName());
        Files.write(path, data);
    }

     public Path getLearnPath() {
        return learnPath;
    }

    public void setLearnPath(Path learnPath) {
        this.learnPath = learnPath;
        logger.debug("learn path set to {}", learnPath.getFileName());
    }
  

}
