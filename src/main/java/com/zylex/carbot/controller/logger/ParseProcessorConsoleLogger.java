package com.zylex.carbot.controller.logger;

import com.zylex.carbot.service.parser.ParseProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class ParseProcessorConsoleLogger extends ConsoleLogger {

    private final static Logger LOG = LoggerFactory.getLogger(ParseProcessor.class);

    private static int totalFilials;

    private static final AtomicInteger processedFilials = new AtomicInteger(0);

    private static final AtomicInteger errorFilials = new AtomicInteger(0);

    public static void startLog(int totalFilials) {
        ParseProcessorConsoleLogger.totalFilials = totalFilials;
        writeInLine("\n");
        LOG.info("Parsing filials started.");
    }

    public static synchronized void logFilial() {
        String output = String.format("Parsing filials: %d/%d (%s%%)",
                processedFilials.incrementAndGet(),
                totalFilials,
                new DecimalFormat("#0.0").format(((double) processedFilials.get() / (double) totalFilials) * 100).replace(",", "."));
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
        if (processedFilials.get() + errorFilials.get() == totalFilials) {
            processedFilials.set(0);
            errorFilials.set(0);
            writeLineSeparator();
            writeInLine("\nDuring parsing occurred " + errorFilials.get() + " errors.");
            LOG.info("Parsing completed.");
        }
    }

    public static void logFilialError() {
        errorFilials.incrementAndGet();
    }

    public static void endLog() {
        String output = "Parsing finished.";
        writeInLine("\n" + output);
        LOG.info(output);
    }
}
