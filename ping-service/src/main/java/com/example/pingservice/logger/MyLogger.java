package com.example.pingservice.logger;

import com.example.pingservice.constant.Constant;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * 自定义日志
 *
 * @author yangjinde
 * @date 2024/8/21
 */
public class MyLogger {

    private static final Logger LOGGER = Logger.getLogger(MyLogger.class.getName());

    static {
        initLogFile(Constant.LOG_PATH);
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warning(message);
    }

    public static void error(String message) {
        LOGGER.severe(message);
    }

    /**
     * 初始化日志文件
     */
    public static void initLogFile(String logPath) {
        logPath = StringUtils.hasLength(logPath) ? logPath : Constant.LOG_PATH;
        try {
            // 检查并创建目录和文件
            createFileIfNotExists(logPath);
            // 配置FileHandler
            FileHandler fileHandler = new FileHandler(Constant.LOG_PATH, true);
            // 设置自定义日志格式
            fileHandler.setFormatter(new LoggerFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.severe("Failed to initialize logger handler: " + e.getMessage());
        }
    }

    /**
     * 判断如果文件不存在着创建
     */
    public static void createFileIfNotExists(String filePath) {
        File logFile = new File(filePath);
        if (!logFile.exists()) {
            // 创建父目录
            logFile.getParentFile().mkdirs();
            // 创建文件
            try {
                logFile.createNewFile();
            } catch (IOException ignored) {
            }
        }
    }
}

