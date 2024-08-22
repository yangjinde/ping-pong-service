package com.example.pongservice.constant;

/**
 * Pong Constant
 *
 * @author yangjinde
 * @date 2024/8/21
 */
public class Constant {

    // lock file path
    public static final String LOCK_FILE_PATH = "data/pong.service.lock";

    /**
     * 1 second
     */
    public static final long ONE_SECOND = 1;

    /**
     * Maximum Request Limit
     */
    public static final int MAX_REQ_NUM = 1;

    /**
     * User Home Path
     */
    public static final String USER_HOME = System.getProperty("user.home");

    /**
     * Log Complete Path
     */
    public static final String LOG_PATH = USER_HOME + "/ping-pong-service/pong.log";

    /**
     * date format
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";


}
