package loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class add support logger logback which configured with levels: 'DEBUG' 'INFO' 'ERROR'
 * All logs saves in directory 'logs' which will create if not exists in 'lessons/cache'
 * Max log files size - 1 MB, when size reach limit
 * automatically creates archive where sets currently file debug level and creates new file log.
 * When archive reaches 10 MB than creates new archive.
 *
 * ALL LOGS LIVE 24 HOURS
 */
public class CustomLogger {
    static Logger loggerDebug = LoggerFactory.getLogger("logger.debug");
    static Logger loggerError = LoggerFactory.getLogger("logger.error");
    static Logger loggerInfo = LoggerFactory.getLogger("logger.info");

    /**
     * Method send string to logger debug which take configuration from appender logger.debug
     * @param str - message which need to log
     */
    public static void logDebug(String str) {
        loggerDebug.debug(str);
    }

    /**
     * Method send string to logger info which take configuration from appender logger.info
     * @param str - message which need to log
     */
    public static void logInfo(String str) {
        loggerInfo.info(str);
    }

    /**
     * Method send string to logger error which take configuration from appender logger.error
     * @param str - message which need to log
     */
    public static void logError(String str) {
        loggerError.error(str);
    }
}
