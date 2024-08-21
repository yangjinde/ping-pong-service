package com.example.pingservice.logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * 自定义日期格式：yyyy-MM-dd HH:mm:ss
 *
 * @author yangjinde
 * @date 2024/8/21
 */
class LoggerFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        Date date = new Date(record.getMillis());
        return String.format("%s [%s] %s%n",
                LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                record.getLevel().getName(),
                record.getMessage());
    }
}
