package com.zylex.carbot.exception;

import com.zylex.carbot.controller.logger.ConsoleLogger;

public class CarbotException extends RuntimeException {

    public CarbotException(String message) {
        super(message);
        ConsoleLogger.writeErrorMessage(message);
    }

    public CarbotException(String message, Throwable cause) {
        super(message, cause);
        ConsoleLogger.writeErrorMessage(message, cause);
    }
}
