package com.example.pongservice.logger;

import com.example.pongservice.constant.Constant;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Customize date format：yyyy-MM-dd HH:mm:ss
 *
 * @author yangjinde
 * @date 2024/8/21
 */
class LoggerFormatter extends Formatter {

    /**
     * Customize record format
     *
     * @param record the log record to be formatted.
     * @return date format
     */
    @Override
    public String format(LogRecord record) {
        Date date = new Date(record.getMillis());
        return String.format("%s [%s] %s%n",
                LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Constant.DATE_FORMAT)),
                record.getLevel().getName(),
                record.getMessage());
    }
}
