package de.peaqe.revitalizecore.modules.chat.config;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.modules.chat.ChatModule;
import de.peaqe.revitalizecore.modules.chat.utils.FilterMode;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 14:09 Uhr
 * *
 */

public class ChatConfig {

    private final ChatModule chatModule;
    private final RevitalizeCore revitalizeCore;

    private final File file;
    private final FileConfiguration fileConfiguration;

    public ChatConfig(ChatModule chatModule) {
        this.chatModule = chatModule;
        this.revitalizeCore = chatModule.getRevitalizeCore();
        this.file = new File(this.revitalizeCore.getDataFolder(), "chat.yml");
        this.fileConfiguration = YamlConfiguration.loadConfiguration(file);

        this.chatModule.getLogger().info("ChatConfig loaded from " + file.getName());
    }

    public final String getPrefix() {
        var value = this.getString("prefix");
        this.chatModule.getLogger().debug("getPrefix() → " + value);
        return value;
    }

    public final String getTextColor() {
        var value = this.getString("textColor");
        this.chatModule.getLogger().debug("getTextColor() → " + value);
        return value;
    }

    public final String getHighlightColor() {
        var value = this.getString("highlightColor");
        this.chatModule.getLogger().debug("getHighlightColor() → " + value);
        return value;
    }

    private String getString(String path) {
        var string = this.fileConfiguration.getString(path);

        if (string == null) {
            this.chatModule.getLogger().warn("Missing string config at path: " + path);
            return "";
        }

        return string.replace('&', '§');
    }

    public boolean getEnabled() {
        var value = this.fileConfiguration.getBoolean("filter.enabled");
        this.chatModule.getLogger().debug("getEnabled() → " + value);
        return value;
    }

    @SneakyThrows
    public void setEnabled(boolean enabled) {
        this.chatModule.getLogger().info("setEnabled(" + enabled + ")");
        this.fileConfiguration.set("filter.enabled", enabled);
        this.fileConfiguration.save(this.file);
    }

    public String getReplacement() {
        var value = this.getString("filter.replacement");
        this.chatModule.getLogger().debug("getReplacement() → " + value);
        return value;
    }

    public FilterMode getFilterMode() {
        var filterMode = this.fileConfiguration.getString("filter.mode");

        if (filterMode == null) {
            this.chatModule.getLogger().warn("filter.mode missing, defaulting to REPLACE");
            return FilterMode.REPLACE;
        }

        var mode = FilterMode.fromString(filterMode);
        this.chatModule.getLogger().debug("getFilterMode() → " + mode);
        return mode;
    }

    @SneakyThrows
    public void setFilterMode(FilterMode filterMode) {
        this.chatModule.getLogger().info("setFilterMode(" + filterMode.name() + ")");
        this.fileConfiguration.set("filter.mode", filterMode.name());
        this.fileConfiguration.save(this.file);
    }

    public String getBlockMessage() {
        var value = this.getString("block-message");
        this.chatModule.getLogger().debug("getBlockMessage() → " + value);
        return value;
    }

    public String getStaffNotifyText() {
        var value = this.getString("staff.notify.text");
        this.chatModule.getLogger().debug("getStaffNotifyText() → " + value);
        return value;
    }

    public boolean getStaffNotifyEnabled() {
        var value = this.fileConfiguration.getBoolean("staff.notify.enabled");
        this.chatModule.getLogger().debug("getStaffNotifyEnabled() → " + value);
        return value;
    }

    @SneakyThrows
    public void setStaffNotifyEnabled(boolean enabled) {
        this.chatModule.getLogger().info("setStaffNotifyEnabled(" + enabled + ")");
        this.fileConfiguration.set("staff.notify.enabled", enabled);
        this.fileConfiguration.save(this.file);
    }

    public boolean getStaffSplitterEnabled() {
        var value = this.fileConfiguration.getBoolean("staff.splitter.enabled");
        this.chatModule.getLogger().debug("getStaffSplitterEnabled() → " + value);
        return value;
    }

    @SneakyThrows
    public void setStaffSplitterEnabled(boolean enabled) {
        this.chatModule.getLogger().info("setStaffSplitterEnabled(" + enabled + ")");
        this.fileConfiguration.set("staff.splitter.enabled", enabled);
        this.fileConfiguration.save(this.file);
    }

    public String getStaffSplitterText() {
        var value = this.getString("staff.splitter.text");
        this.chatModule.getLogger().debug("getStaffSplitterText() → " + value);
        return value;
    }

    @SneakyThrows
    public void setStaffSplitterText(String splitter) {
        this.chatModule.getLogger().info("setStaffSplitterText(" + splitter + ")");
        this.fileConfiguration.set("staff.splitter.text", splitter);
        this.fileConfiguration.save(this.file);
    }

    public List<String> getBadWords() {
        var value = this.fileConfiguration.getStringList("filter.badwords");
        this.chatModule.getLogger().debug("getBadWords() → " + value);
        return value;
    }

    @SneakyThrows
    public void addBadWord(String badWord) {
        this.chatModule.getLogger().info("addBadWord(" + badWord + ")");
        var badWords = this.getBadWords();
        badWords.add(badWord);
        this.fileConfiguration.set("filter.badwords", badWords);
        this.fileConfiguration.save(this.file);
    }

    @SneakyThrows
    public void removeBadWord(String badWord) {
        this.chatModule.getLogger().info("removeBadWord(" + badWord + ")");
        var badWords = this.getBadWords();
        badWords.remove(badWord);
        this.fileConfiguration.set("filter.badwords", badWords);
        this.fileConfiguration.save(this.file);
    }

}
