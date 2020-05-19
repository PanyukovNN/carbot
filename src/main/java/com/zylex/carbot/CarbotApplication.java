package com.zylex.carbot;

import com.zylex.carbot.service.driver.DriverManager;
import com.zylex.carbot.service.puller.FilialPuller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

import java.time.LocalDateTime;

@SpringBootApplication
public class CarbotApplication {

    public static final LocalDateTime BOT_START_TIME = LocalDateTime.now();

    public static final boolean HEADLESS_DRIVER = false;

    public static void main(String[] args) {
//        System.getProperties().put("proxySet", "true");
//        System.getProperties().put("socksProxyHost", "127.0.0.1");
//        System.getProperties().put("socksProxyPort", "9150");
        ApiContextInitializer.init();
        SpringApplication.run(CarbotApplication.class, args);
    }
}
