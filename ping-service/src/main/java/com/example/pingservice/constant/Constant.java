package com.example.pingservice.constant;

/**
 * Ping Constant
 *
 * @author yangjinde
 * @date 2024/8/21
 */
public class Constant {

    public static final String SAY_CONTENT = "Hello";

    /**
     * User Home Path
     */
    public static final String USER_HOME = System.getProperty("user.home");

    /**
     * Log Complete Path
     */
    public static final String LOG_PATH = USER_HOME + "/ping-pong-service/ping.log";

    /**
     * lock file path
     */
    public static final String LOCK_FILE_PATH = "data/ping.service.lock";

    /**
     * date format
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 1 second
     */
    public static final long ONE_SECOND = 1;

    /**
     * Maximum Request Limit
     */
    public static final int MAX_REQ_NUM = 2;

}
