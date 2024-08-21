package com.example.pingservice.util;

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
 * Ping速率控制器
 *
 * @author yangjinde
 * @date 2024/8/15
 */
public class PingRateLimiter {

    // 定义一个文件用于实现文件锁和计数器存储
    private static final String LOCK_FILE_PATH = "data/ping.service.lock";

    /**
     * 1秒钟
     */
    private static final long ONE_SECOND = 1;

    /**
     * 最大请求限制
     */
    private static final int MAX_REQ_NUM = 2;

    static {
        checkLockFilePath(LOCK_FILE_PATH);
    }

    /**
     * 校验文件锁目录是否存在，不存在则创建
     *
     * @param lockFilePath 文件锁路径
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
     * 初始化文件内容
     *
     * @param raf RandomAccessFile
     * @throws IOException IOException
     */
    private static void initializeFile(RandomAccessFile raf) throws IOException {
        if (raf.length() == 0) {
            raf.writeLong(System.currentTimeMillis()); // 写入当前时间戳
            raf.writeInt(0); // 写入初始请求计数器
        }
    }

    /**
     * 速率控制，使用默认锁文件
     *
     * @return 通过返回true, 不通过返回false
     */
    public static boolean checkRateLimit() {
        return checkRateLimit(LOCK_FILE_PATH);
    }

    /**
     * 速率控制
     *
     * @param lockFilePath 文件锁路径
     * @return 通过返回true, 不通过返回false
     */
    public static boolean checkRateLimit(String lockFilePath) {
        lockFilePath = StringUtils.hasText(lockFilePath) ? lockFilePath : LOCK_FILE_PATH;
        checkLockFilePath(lockFilePath);
        try (RandomAccessFile raf = new RandomAccessFile(lockFilePath, "rw"); FileChannel channel = raf.getChannel(); FileLock lock = channel.lock()) {
            // 如果无法获取文件锁，说明已有其它实例在操作，返回false
            if (lock == null) {
                return false;
            }

            // 初始化文件内容
            PingRateLimiter.initializeFile(raf);

            // 同步计数器和时间窗口
            synchronized (PingRateLimiter.class) {
                // 读取当前计数器和时间
                raf.seek(0);
                long startTime = raf.readLong();
                int requestCount = raf.readInt();

                //yyyyMMddHHmmss
                long currentTime = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
                //log.info("startTime:{}, currentTime:{}, requestCount:{}", startTime, currentTime, requestCount);

                boolean pass;
                if (currentTime - startTime >= ONE_SECOND) {// 检查是否超过1秒钟，如果是，重置计数器和时间窗口
                    //log.info("间隔大于1秒：{}", currentTime - startTime);
                    raf.seek(0);//把文件指针重置到文件的起始位置，以便后续的读写操作从文件的开头开始进行
                    raf.writeLong(currentTime);//记录时间为当前时间
                    raf.writeInt(1);//记录请求次数为1
                    pass = true;
                } else if (requestCount >= MAX_REQ_NUM) {// 如果在同1秒钟且请求数大于等于限制数，直接返回false
                    //log.info("间隔小于1秒:{}, requestCount且大于2:{}", currentTime - startTime, requestCount);
                    pass = false;
                } else {// 如果在同1秒钟且请求数小于限制数，更新请求次数+1，并返回true
                    //log.info("间隔小于1秒:{}, requestCount且小于2:{}", currentTime - startTime, requestCount);
                    raf.seek(0);//把文件指针重置到文件的起始位置，以便后续的读写操作从文件的开头开始进行
                    raf.writeLong(startTime);//记录时间，使用原来的时间
                    raf.writeInt(requestCount + 1);//记录请求次数+1
                    pass = true;
                }
                return pass;
            }

        } catch (Exception e) {
            return false; // 出现异常时，返回false
        }
    }
}
