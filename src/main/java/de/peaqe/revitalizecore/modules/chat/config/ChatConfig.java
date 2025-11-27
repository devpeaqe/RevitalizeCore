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
    }

    // -------------------------------------------------------------------

    // String configurations

    public final String getPrefix() {
        return this.getString("prefix");
    }

    public final String getTextColor() {
        return this.getString("textColor");
    }

    public final String getHighlightColor() {
        return this.getString("highlightColor");
    }

    private String getString(String path) {
        var string = this.fileConfiguration.getString(path);
        if (string == null) return "";

        return string.replace('&', '§');
    }

    // String configurations

    // -------------------------------------------------------------------

    // Filter configuration

    public boolean getEnabled() {
        return this.fileConfiguration.getBoolean("filter.enabled");
    }

    @SneakyThrows
    public void setEnabled(boolean enabled) {
        this.fileConfiguration.set("filter.enabled", enabled);
        this.fileConfiguration.save(this.file);
    }

    public String getReplacement() {
        return this.getString("filter.replacement");
    }

    public FilterMode getFilterMode() {
        var filterMode = this.fileConfiguration.getString("filter.mode");
        if (filterMode == null) return FilterMode.REPLACE;

        return FilterMode.fromString(filterMode);
    }

    @SneakyThrows
    public void setFilterMode(FilterMode filterMode) {
        this.fileConfiguration.set("filter.mode", filterMode.name());
        this.fileConfiguration.save(this.file);
    }

    public String getBlockMessage() {
        return this.getString("block-message");
    }

    // -------------------------------------------------------------------

    // STAFF

    // -------------------------------------------------------------------

    public String getStaffNotifyText() {
        return this.getString("staff.notify.text");
    }

    public boolean getStaffNotifyEnabled() {
        return this.fileConfiguration.getBoolean("staff.notify.enabled");
    }

    @SneakyThrows
    public void setStaffNotifyEnabled(boolean enabled) {
        this.fileConfiguration.set("staff.notify.enabled", enabled);
        this.fileConfiguration.save(this.file);
    }

    public boolean getStaffSplitterEnabled() {
        return this.fileConfiguration.getBoolean("staff.splitter.enabled");
    }

    @SneakyThrows
    public void setStaffSplitterEnabled(boolean enabled) {
        this.fileConfiguration.set("staff.splitter.enabled", enabled);
        this.fileConfiguration.save(this.file);
    }

    public String getStaffSplitterText() {
        return this.getString("staff.splitter.text");
    }

    @SneakyThrows
    public void setStaffSplitterText(String splitter) {
        this.fileConfiguration.set("staff.splitter.text", splitter);
        this.fileConfiguration.save(this.file);
    }

    // -------------------------------------------------------------------

    // STAFF

    // -------------------------------------------------------------------

    public List<String> getBadWords() {
        return this.fileConfiguration.getStringList("filter.badwords");
    }

    @SneakyThrows
    public void addBadWord(String badWord) {
        var badWords = this.getBadWords();
        badWords.add(badWord);
        this.fileConfiguration.set("filter.badwords", badWords);
        this.fileConfiguration.save(this.file);
    }

    @SneakyThrows
    public void removeBadWord(String badWord) {
        var badWords = this.getBadWords();
        badWords.remove(badWord);
        this.fileConfiguration.set("filter.badwords", badWords);
        this.fileConfiguration.save(this.file);
    }

}
