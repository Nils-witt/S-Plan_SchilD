/*
 * Copyright (c) 2021.
 */

package de.nils_witt.splan;

import de.nils_witt.splan.connectors.Api;
import de.nils_witt.splan.connectors.ConfigConnector;
import de.nils_witt.splan.connectors.FileSystemConnector;
import de.nils_witt.splan.models.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CliApplication implements Runnable {
    private Config config;
    private XLSXFileHandler xlsxFileHandler;
    private Api api;
    private final Logger logger = LogManager.getLogger(CliApplication.class);

    public static void main(String[] args) {
        try {
            CliApplication cliApplication = new CliApplication();
            Thread thread = new Thread(cliApplication);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        initApplication();
        if (xlsxFileHandler != null) {
            xlsxFileHandler.processFile();
        }
    }

    /**
     * Initialises the CLI Application parameters
     */
    private void initApplication() {

        FileSystemConnector.createDataDirs();
        if (logger == null) return;

        config = ConfigConnector.loadConfig(logger);
        if (config == null) {
            try {
                ConfigConnector.copyDefaultConfig();
                logger.info("Created default config.json");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("Failed to create default config.json");
            }
            return;
        }

        if (!Api.verifyBearer(logger, config.getBearer(), config.getUrl())) {
            logger.warn("Api token invalid");
            return;
        }
        api = new Api(config);
        xlsxFileHandler = new XLSXFileHandler(FileSystemConnector.getWorkingDir().toString().concat("/data/Students.xlsx"), api);

        logger.info("Init Complete");
    }
}
