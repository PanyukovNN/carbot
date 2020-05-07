package com.zylex.carbot.controller.logger;

import com.zylex.carbot.CarbotApplication;
import com.zylex.carbot.exception.ConsoleLoggerException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static com.zylex.carbot.CarbotApplication.BOT_START_TIME;

/**
 * Base class for loggers.
 */
@SuppressWarnings("WeakerAccess")
public abstract class ConsoleLogger {

    private final static Logger LOG = LoggerFactory.getLogger(CarbotApplication.class);

    /**
     * Writes start message
     */
    public static void startMessage() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        String startMessage = String.format("BetBot started at %s", BOT_START_TIME.format(formatter));
        writeInLine(startMessage);
        LOG.info(startMessage);
        writeLineSeparator('~');
    }

    static void writeLineSeparator() {
        String line = "\n" + StringUtils.repeat("-", 50);
        writeInLine(line);
    }

    @SuppressWarnings("SameParameterValue")
    static void writeLineSeparator(char delimiter) {
        String line = "\n" + StringUtils.repeat(delimiter, 50);
        writeInLine(line);
    }

    public synchronized static void writeErrorMessage(String message, Throwable cause) {
        System.err.print(message);
        LOG.error(message, cause);
    }

    public synchronized static void writeErrorMessage(String message) {
        System.err.print(message);
        LOG.error(message);
    }

    public synchronized static void writeInLine(String line) {
        System.out.print(line);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    synchronized void pressAnyButton() {
        try {
            System.in.read();
        } catch (IOException e) {
            throw new ConsoleLoggerException(e.getMessage(), e);
        }
    }

    public synchronized static void endMessage() {
        writeLineSeparator('~');
        String output = "Bot work completed in " + computeTime(BOT_START_TIME);
        writeInLine("\n" + output);
        LOG.info(output);
    }

    @SuppressWarnings("SameParameterValue")
    static String computeTime(LocalDateTime startTime) {
        long seconds = ChronoUnit.SECONDS.between(startTime, LocalDateTime.now());
        String time = String.format("%02d min. %02d sec.",
                TimeUnit.SECONDS.toMinutes(seconds) % TimeUnit.HOURS.toMinutes(1),
                seconds % TimeUnit.MINUTES.toSeconds(1));
        long hours = TimeUnit.SECONDS.toHours(seconds);
        return hours > 0
                ? String.format("%02d h. ", hours) + time
                : time;
    }
}
