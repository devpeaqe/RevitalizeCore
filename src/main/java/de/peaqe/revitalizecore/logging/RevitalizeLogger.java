package de.peaqe.revitalizecore.logging;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 29.11.2025 | 15:38 Uhr
 * *
 */

public class RevitalizeLogger {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    @Getter
    private final File logFolder;
    private final int maxMemoryEntries;

    private final Map<String, ModuleLogger> moduleLoggers = new HashMap<>();
    private final Deque<LogEntry> memoryLog = new ArrayDeque<>();

    public RevitalizeLogger(File dataFolder) {
        this(dataFolder, 300);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public RevitalizeLogger(File dataFolder, int maxMemoryEntries) {
        this.logFolder = new File(dataFolder, "logs");
        this.maxMemoryEntries = maxMemoryEntries;

        if (!logFolder.exists()) logFolder.mkdirs();

    }

    public ModuleLogger getLogger(String moduleName) {
        return moduleLoggers.computeIfAbsent(moduleName,
                m -> new ModuleLogger(m, this));
    }

    //@SuppressWarnings("UnstableApiUsage")
    public synchronized void log(String module, LogLevel level, String msg) {
        var now = System.currentTimeMillis();
        var entry = new LogEntry(now, module, level, msg);

        this.memoryLog.addLast(entry);
        if (this.memoryLog.size() > this.maxMemoryEntries) this.memoryLog.removeFirst();

        //Bukkit.getLogger().info(format(entry));
        this.writeToFile(entry);
    }

    private void writeToFile(LogEntry entry) {
        try {
            var fileName = sanitizeFileName(entry.module()) + ".log";
            var file = new File(logFolder, fileName);

            if (!file.exists()) {
                Files.createFile(file.toPath());
            }

            try (var fos = new FileOutputStream(file, true);
                 var writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 var bw = new BufferedWriter(writer)) {

                bw.write(format(entry));
                bw.newLine();
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[Revitalize-Logger] Failed to write log file: " + e.getMessage());
        }
    }

    private String sanitizeFileName(String module) {
        return module.replaceAll("[^a-zA-Z0-9-_.]", "_");
    }

    private String format(LogEntry entry) {
        var time = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(entry.timestampMillis()),
                ZoneId.systemDefault()
        );

        return "[" + TIME_FORMAT.format(time) + "]"
                + " [" + entry.level().name() + "]"
                + " [" + entry.module() + "] "
                + entry.message();
    }

    /**
     * Gibt die letzten Logs zurück, optional gefiltert.
     *
     * @param moduleFilter nullable – wenn != null, nur dieses Modul
     * @param levelFilter  nullable – wenn != null, nur dieses Level
     * @param limit        max Anzahl, von hinten (neueste)
     */
    public synchronized List<LogEntry> getRecentLogs(String moduleFilter,
                                                     LogLevel levelFilter,
                                                     int limit) {
        var all = new ArrayList<LogEntry>(memoryLog);
        var result = new ArrayList<LogEntry>();

        for (int i = all.size() - 1; i >= 0; i--) {
            var entry = all.get(i);

            if (moduleFilter != null && !entry.module().equalsIgnoreCase(moduleFilter))
                continue;

            if (levelFilter != null && entry.level() != levelFilter)
                continue;

            result.add(entry);
            if (result.size() >= limit) {
                break;
            }
        }

        var ordered = new ArrayList<LogEntry>();
        for (int i = result.size() - 1; i >= 0; i--) {
            ordered.add(result.get(i));
        }

        return ordered;
    }

    public synchronized List<LogEntry> getAllRecentLogs() {
        return new ArrayList<>(memoryLog);
    }

    public Set<String> getRegisteredModuleNames() {
        return this.moduleLoggers.keySet();
    }

}
