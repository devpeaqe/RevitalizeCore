package de.peaqe.revitalizecore.logging;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 29.11.2025 | 15:53 Uhr
 * *
 */

public class ModuleLogger {

    private final String moduleName;
    private final RevitalizeLogger parent;

    public ModuleLogger(String moduleName, RevitalizeLogger parent) {
        this.moduleName = moduleName;
        this.parent = parent;
    }

    public void info(String msg) {
        parent.log(moduleName, LogLevel.INFO, msg);
    }

    public void warn(String msg) {
        parent.log(moduleName, LogLevel.WARN, msg);
    }

    public void error(String msg) {
        parent.log(moduleName, LogLevel.ERROR, msg);
    }

    public void debug(String msg) {
        parent.log(moduleName, LogLevel.DEBUG, msg);
    }

}
