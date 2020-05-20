package com.zylex.carbot;

import com.zylex.carbot.controller.logger.ConsoleLogger;
import com.zylex.carbot.model.Model;
import com.zylex.carbot.model.ParsingTime;
import com.zylex.carbot.repository.ModelRepository;
import com.zylex.carbot.repository.ParsingTimeRepository;
import com.zylex.carbot.service.parser.ParseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScheduledParsingTask extends Thread {

    private final ParseProcessor parseProcessor;

    private final ParsingTimeRepository parsingTimeRepository;

    private final ModelRepository modelRepository;

    @Autowired
    public ScheduledParsingTask(ParseProcessor parseProcessor,
                                ParsingTimeRepository parsingTimeRepository,
                                ModelRepository modelRepository) {
        this.parseProcessor = parseProcessor;
        this.parsingTimeRepository = parsingTimeRepository;
        this.modelRepository = modelRepository;
    }

    @Override
    public void run() {
        try {
            Model model = modelRepository.findByName("VESTA SW CROSS");
            parseProcessor.parse(model);
            parsingTimeRepository.save(new ParsingTime(LocalDateTime.now()));
        } catch (Throwable t) {
            ConsoleLogger.writeErrorMessage(t.getMessage(), t);
        }
    }
}
