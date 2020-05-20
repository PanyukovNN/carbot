package com.zylex.carbot;

import com.zylex.carbot.repository.EquipmentRepository;
import com.zylex.carbot.view.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class CarbotApplication implements CommandLineRunner {

    public static final LocalDateTime BOT_START_TIME = LocalDateTime.now();

    public static final boolean HEADLESS_DRIVER = false;

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "9150");
        ApiContextInitializer.init();
        SpringApplication.run(CarbotApplication.class, args);
    }

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    View view;

    @Override
    public void run(String... args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdownNow));
        Thread parsingTask = context.getBean(ScheduledParsingTask.class);
        scheduler.scheduleAtFixedRate(parsingTask, 0, 2, TimeUnit.HOURS);
    }
}
