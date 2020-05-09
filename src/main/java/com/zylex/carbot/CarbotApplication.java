package com.zylex.carbot;

import com.zylex.carbot.controller.logger.ConsoleLogger;
import com.zylex.carbot.model.Car;
import com.zylex.carbot.model.CarStatus;
import com.zylex.carbot.model.Filial;
import com.zylex.carbot.repository.CarRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ComponentScan
@EnableJpaRepositories
public class CarbotApplication {

    public static final LocalDateTime BOT_START_TIME = LocalDateTime.now();

    public static final boolean HEADLESS_DRIVER = false;

    public static void main(String[] args) {
        ConsoleLogger.startMessage();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CarbotApplication.class)) {
//            context.getBean(ParseProcessor.class).parse();

            List<String> colors = Arrays.asList("Ледниковый", "Марс", "Сердолик", "Ангкор", "Карфаген", "Дайвинг", "Фантом", "Плутон", "Маэстро", "Платина");

            Set<Filial> totalFilials = new HashSet<>();
            for (String color : colors) {
                List<Car> cars = context.getBean(CarRepository.class).findByColorContaining(color).stream()
                        .filter(car -> car.getStatus().equals(CarStatus.NEW.toString()))
                        .collect(Collectors.toList());
                Set<Filial> filials = new LinkedHashSet<>();
                for (Car car : cars) {
                    filials.add(car.getFilial());
                }
                totalFilials.addAll(filials);
                System.out.println("\nАвтомобили цвета \"" + color + "\":");
                int i = 0;
                for (Filial filial : filials) {
                    System.out.println(++i + ") " + filial.getAddress());
                    String url = "https://" + filial.getDealer().getLink() + "/ds/cars/vesta/sw-cross/prices.html";
                    if (!filial.getCode().isEmpty()) {
                        url += "?dealer=" + filial.getCode();
                    }
                    System.out.println(url);
                }
                if (i == 0) {
                    System.out.println("Нет доступных автомобилей");
                }
            }

            System.out.println("\nВсего автомобилей "
                    + context.getBean(CarRepository.class).findAll().stream()
                    .filter(car -> car.getStatus().equals(CarStatus.NEW.toString()))
                    .count()
                    + " у "
                    + totalFilials.size()
                    + " дилеров.");
        } finally {
            ConsoleLogger.endMessage();
        }
    }
}
