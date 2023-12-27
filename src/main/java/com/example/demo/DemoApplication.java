package com.example.demo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	final long oneMB = 1024 * 1024; // 1MB in bytes
	final long targetSizePerMinute = 100 * oneMB; // 100MB
	final long targetSizePerSecond = targetSizePerMinute / 60; // 分割 by 60 seconds

	// 例として、ログメッセージは約100KBとする
	final int messageSize = 100 * 1024; // 100KB
	final String message = String.format("%0" + messageSize + "d", 0).replace("0", "x"); // Dummy log message

	// 秒間に必要なログ数を計算
	final int logsPerSecond = (int) (targetSizePerSecond / messageSize);

	private static boolean isRunning = false;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		generateLogs();
	}

	private static final AtomicInteger counter = new AtomicInteger(0);

	public synchronized void generateLogs() {
		if (isRunning) {
			return;
		}
		isRunning = true;
		executor.scheduleAtFixedRate(() -> {
			int current = counter.incrementAndGet();
			System.out.print(current);
			System.out.println(":");
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < logsPerSecond; i++) {
				System.out.println(message);
			}
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			System.out.print(current);
			System.out.println("...Elapsed time: " + elapsedTime + " milliseconds");
		}, 0, 1, java.util.concurrent.TimeUnit.SECONDS);
        executor.schedule(() -> {
            executor.shutdown();
            System.out.println("Log generation completed.");
			isRunning = false;
		}, 10, TimeUnit.MINUTES);
	}
}
