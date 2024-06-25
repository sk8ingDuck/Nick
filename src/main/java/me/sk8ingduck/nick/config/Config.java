package me.sk8ingduck.nick.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class Config {

    private final File file;
    protected final FileConfiguration fileConfiguration;
    protected String comment;
    public Config(String name, File path) {
        file = new File(path, name);

        if (!file.exists()) {
            path.mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        fileConfiguration = new YamlConfiguration();

        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    protected File getFile() {
        return file;
    }

    protected FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    protected void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void reload() {
        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    protected void saveComment() {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty() || !lines.get(0).startsWith("#")) {
                lines = new LinkedList<>(lines);
                lines.add(0, this.comment);

                // Write all lines back to the file
                try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Object getPathOrSet(String path, Object defaultValue) {
        return getPathOrSet(path, defaultValue, true);
    }
    public Object getPathOrSet(String path, Object defaultValue, boolean translateColors) {
        if (fileConfiguration.get(path) == null) {
            fileConfiguration.set(path, defaultValue);
            save();
        }

        return translateColors ? translateColors(fileConfiguration.get(path)) : fileConfiguration.get(path);
    }

    private Object translateColors(Object value) {
        if (value instanceof String) {
            return ((String) value).replaceAll("&", "§");
        }

        return value;
    }
}
