package com.example.pongservice.logger;

import com.example.pongservice.constant.Constant;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Custom Log
 *
 * @author yangjinde
 * @date 2024/8/21
 */
public class MyLogger {

    private static final Logger LOGGER = Logger.getLogger(MyLogger.class.getName());

    static {
        initLogFile(Constant.LOG_PATH);
    }

    /**
     * write info message
     *
     * @param message message
     */
    public static void info(String message) {
        LOGGER.info(message);
    }

    /**
     * write warn message
     *
     * @param message message
     */
    public static void warn(String message) {
        LOGGER.warning(message);
    }

    /**
     * write error message
     *
     * @param message message
     */
    public static void error(String message) {
        LOGGER.severe(message);
    }

    /**
     * initLogFile
     *
     * @param logPath the log file path
     */
    public static boolean initLogFile(String logPath) {
        logPath = StringUtils.hasLength(logPath) ? logPath : Constant.LOG_PATH;
        try {
            // Check and create directories and files
            createFileIfNotExists(logPath);
            // Config FileHandler
            FileHandler fileHandler = new FileHandler(Constant.LOG_PATH, true);
            // Set custom log format
            fileHandler.setFormatter(new LoggerFormatter());
            LOGGER.addHandler(fileHandler);
            return true;
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize logger handler: " + e.getMessage());
            return false;
        }
    }

    /**
     * Determine if the file does not exist for creation
     *
     * @param filePath the log file path
     */
    public static void createFileIfNotExists(String filePath) {
        File logFile = new File(filePath);
        if (!logFile.exists()) {
            try {
                // 创建父目录
                logFile.getParentFile().mkdirs();
                // 创建文件
                logFile.createNewFile();
            } catch (Exception ignored) {
                throw new RuntimeException();
            }
        }
    }
}
