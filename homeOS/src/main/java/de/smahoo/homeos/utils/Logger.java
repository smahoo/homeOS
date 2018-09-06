package de.smahoo.homeos.utils;

/**
 * Created by Matze on 22.09.16.
 */
public interface Logger {

    void info(String message);
    void error(String message);
    void error(String message, Throwable throwable);
    void warn(String message);
    void debug(String message);
}
