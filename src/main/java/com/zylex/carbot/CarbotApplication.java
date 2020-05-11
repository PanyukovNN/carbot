package com.zylex.carbot;

import com.zylex.carbot.controller.logger.ConsoleLogger;
import com.zylex.carbot.model.Model;
import com.zylex.carbot.repository.ModelRepository;
import com.zylex.carbot.service.parser.ParseProcessor;
import com.zylex.carbot.view.View;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;

@ComponentScan
@EnableJpaRepositories
public class CarbotApplication {

    public static final LocalDateTime BOT_START_TIME = LocalDateTime.now();

    public static final boolean HEADLESS_DRIVER = false;

    public static void main(String[] args) {
        ConsoleLogger.startMessage();
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CarbotApplication.class)) {
            Model model = context.getBean(ModelRepository.class).findByName("VESTA SW CROSS");
            context.getBean(ParseProcessor.class).parse(model);
            context.getBean(View.class).printOutput(model);
        } finally {
            ConsoleLogger.endMessage();
        }
    }
}
