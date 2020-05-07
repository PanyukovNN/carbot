package com.zylex.carbot;

import com.zylex.carbot.controller.logger.ConsoleLogger;
import com.zylex.carbot.model.Car;
import com.zylex.carbot.repository.CarRepository;
import com.zylex.carbot.service.driver.DriverManager;
import com.zylex.carbot.service.parser.ParseProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@ComponentScan
@EnableJpaRepositories
public class CarbotApplication {

    public static final LocalDateTime BOT_START_TIME = LocalDateTime.now();

    public static final boolean HEADLESS_DRIVER = false;

    public static void main(String[] args) {
        ConsoleLogger.startMessage();
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CarbotApplication.class)) {
//            context.getBean(DriverManager.class).initiateDriver();
//            context.getBean(ParseProcessor.class).parse();

            Set<String> links = new LinkedHashSet<>();
            List<Car> cars = context.getBean(CarRepository.class).findAll();
            for (Car car : cars) {
                String url = "https://" + car.getFilial().getDealer().getLink() + "/ds/cars/vesta/sw-cross/prices.html";
                if (!car.getFilial().getCode().isEmpty()) {
                    url += "?dealer=" + car.getFilial().getCode();
                }
                links.add(url);
            }
            System.out.println();
            links.forEach(System.out::println);
        } finally {
            ConsoleLogger.endMessage();
        }
    }
}
