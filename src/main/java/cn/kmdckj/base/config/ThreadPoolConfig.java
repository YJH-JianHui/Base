package cn.kmdckj.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类。
 * 配置异步任务 (@Async) 专用的线程池参数。
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    /**
     * 核心线程数：CPU核心数
     */
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数：CPU核心数 * 2
     */
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;

    /**
     * 队列容量
     */
    private static final int QUEUE_CAPACITY = 1000;

    /**
     * 线程空闲时间（秒）
     */
    private static final int KEEP_ALIVE_SECONDS = 60;

    /**
     * 异步任务线程池
     */
    @Bean(name = "asyncTaskExecutor")
    public Executor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数
        executor.setCorePoolSize(CORE_POOL_SIZE);

        // 最大线程数
        executor.setMaxPoolSize(MAX_POOL_SIZE);

        // 队列容量
        executor.setQueueCapacity(QUEUE_CAPACITY);

        // 线程空闲时间
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);

        // 线程名称前缀
        executor.setThreadNamePrefix("async-task-");

        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }

    /**
     * 日志记录线程池
     * 专门用于异步记录日志，避免影响主业务
     */
    @Bean(name = "logTaskExecutor")
    public Executor logTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("log-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}