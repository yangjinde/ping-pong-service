package com.example.pingservice.util;

import com.example.pingservice.constant.Constant;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Ping rate controller
 *
 * @author yangjinde
 * @date 2024/8/15
 */
public class PingRateLimiter {

    static {
        checkLockFilePath(Constant.LOCK_FILE_PATH);
    }

    /**
     * Rate control, using default lock file
     *
     * @return pass return true, else return false
     */
    public static FileLock checkRateLimit() {
        return checkRateLimit(Constant.LOCK_FILE_PATH);
    }

    /**
     * rate control
     *
     * @param lockFilePath lock file path
     * @return if get the FileLock ,return FileLock, else return null
     */
    public static FileLock checkRateLimit(String lockFilePath) {
        lockFilePath = StringUtils.hasText(lockFilePath) ? lockFilePath : Constant.LOCK_FILE_PATH;

        // if the lock file directory not exists, create
        checkLockFilePath(lockFilePath);

        //try get the file lock
        try {
            RandomAccessFile raf = new RandomAccessFile(lockFilePath, "rw");
            FileChannel channel = raf.getChannel();
            FileLock fileLock = channel.lock();
            // if not exist the count file, init
            PingRateLimiter.initializeFile(raf);

            raf.seek(0);

            //read time and count from the lock file
            long startTime = raf.readLong();
            int requestCount = raf.readInt();

            //yyyyMMddHHmmss
            long currentTime = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));

            //interval exceeds 1 second, reset time and count, return FileLock
            if (currentTime - startTime >= Constant.ONE_SECOND) {
                raf.seek(0);
                raf.writeLong(currentTime);
                raf.writeInt(1);
                return fileLock;
            }
            // if in a second request count more than the limit, return null
            else if (requestCount >= Constant.MAX_REQ_NUM) {
                releaseFileLock(fileLock);
                return null;
            }
            // if in a second request count less than the limit, count + 1, return FileLock
            else {
                raf.seek(0);
                raf.writeLong(startTime);
                //count + 1
                raf.writeInt(requestCount + 1);
                return fileLock;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verify if the file lock directory exists, create if it does not exist
     *
     * @param lockFilePath the lock file path
     */
    private static void checkLockFilePath(String lockFilePath) {
        Path path = Paths.get(lockFilePath);
        if (!Files.exists(path)) {
            Path parent = path.getParent();
            try {
                Files.createDirectories(parent);
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Initialize file content
     *
     * @param raf RandomAccessFile
     * @throws IOException IOException
     */
    private static void initializeFile(RandomAccessFile raf) throws IOException {
        if (raf.length() == 0) {
            long currentTime = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
            // Write the current time
            raf.writeLong(currentTime);
            // Write the count 0
            raf.writeInt(0);
        }
    }

    /**
     * release fileLock
     *
     * @param fileLock fileLock
     */
    public static void releaseFileLock(FileLock fileLock) {
        try {
            if (null != fileLock) {
                fileLock.release();
            }
        } catch (IOException ignored) {

        }
    }
}
