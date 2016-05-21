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
        if (data.length > 0) {
            Path learnPath = getLearnPath();
            if (learnPath == null) {
                throw new IOException("Learn path has not been set. Could not save data.");
            }

            Path folder = learnPath.getParent();
            if (!Files.exists(folder)) {
                logger.debug("Folder does not exist: {}", folder.toAbsolutePath());
                Files.createDirectory(folder);
                logger.debug("Folder created: {}", folder.toAbsolutePath());
            }
            logger.debug("Writing learn data to {}", learnPath.toAbsolutePath());
            Files.write(learnPath, data);
        }
    }

    public Path getLearnPath() {
        return learnPath;
    }

    public void setLearnPath(Path learnPath) {
        this.learnPath = learnPath;
        logger.debug("learn path set to {}", learnPath.toAbsolutePath());
    }

}
