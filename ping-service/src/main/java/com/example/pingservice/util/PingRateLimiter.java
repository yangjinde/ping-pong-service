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
    public static boolean checkRateLimit() {
        return checkRateLimit(Constant.LOCK_FILE_PATH);
    }

    /**
     * rate control
     *
     * @param lockFilePath lock file path
     * @return Judging by the form of obtaining the file lock, if the lock is obtained, return true else return false
     */
    public static boolean checkRateLimit(String lockFilePath) {
        lockFilePath = StringUtils.hasText(lockFilePath) ? lockFilePath : Constant.LOCK_FILE_PATH;

        // if the lock file directory not exists, create
        checkLockFilePath(lockFilePath);

        //try get the file lock
        try (RandomAccessFile raf = new RandomAccessFile(lockFilePath, "rw"); FileChannel channel = raf.getChannel(); FileLock lock = channel.lock()) {
            // if can not get the lock ，return false
            if (lock == null) {
                return false;
            }

            // if not exist the count file, init
            PingRateLimiter.initializeFile(raf);

            raf.seek(0);

            //read time and count from the lock file
            long startTime = raf.readLong();
            int requestCount = raf.readInt();

            //yyyyMMddHHmmss
            long currentTime = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));

            boolean pass;
            //interval exceeds 1 second, reset time and count, return true
            if (currentTime - startTime >= Constant.ONE_SECOND) {
                raf.seek(0);
                raf.writeLong(currentTime);
                raf.writeInt(1);
                pass = true;
            }
            // if in a second request count more than the limit, return false
            else if (requestCount >= Constant.MAX_REQ_NUM) {
                pass = false;
            }
            // if in a second request count less than the limit, count + 1, return true
            else {
                raf.seek(0);
                raf.writeLong(startTime);
                //count + 1
                raf.writeInt(requestCount + 1);
                pass = true;
            }
            return pass;
        } catch (Exception e) {
            return false;
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
}
