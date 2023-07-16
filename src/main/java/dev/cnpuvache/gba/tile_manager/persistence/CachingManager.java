package dev.cnpuvache.gba.tile_manager.persistence;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class CachingManager {

    private static final File homeDirectory = new File(System.getProperty("user.home"));
    private static final File cachingDirectory = new File(homeDirectory, ".gba_tile_manager/");
    private static final File propertiesFile = new File(cachingDirectory, "gba_tile_manager.properties");
    private static final String LATEST_OPENED_PROJECT_KEY = "project.latest.opened";
    private static final String LATEST_LOADED_CALLBACK_HEADER_FILE = "project.latest.callback.header";

    private final Properties properties;

    private static CachingManager INSTANCE;

    private CachingManager() {
        if (!propertiesFile.exists()) {
            try {
                if (!cachingDirectory.mkdirs()) {
                    throw new IOException("Unable to create property file " + propertiesFile.getAbsolutePath());
                }
                if (!propertiesFile.createNewFile() || !propertiesFile.setReadable(true) || !propertiesFile.setWritable(true)) {
                    throw new IOException("Unable to create property file " + propertiesFile.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create property file " + propertiesFile.getAbsolutePath());
            }
        }
        Properties properties;
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file " + propertiesFile);
        }
        this.properties = properties;
    }

    public static CachingManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CachingManager();
        }
        return INSTANCE;
    }

    public synchronized File getLatestOpenedProject() {
        if (properties.containsKey(LATEST_OPENED_PROJECT_KEY)) {
            Object latestOpenedProject = properties.get(LATEST_OPENED_PROJECT_KEY);
            String latestOpenendProjectStringValue = String.valueOf(latestOpenedProject);
            File latestOpenedProjectFile = new File(latestOpenendProjectStringValue);
            return latestOpenedProjectFile;
        }
        return null;
    }

    public synchronized void setLatestOpenedProject(File latestOpenedProjectFile) {
        if (latestOpenedProjectFile.exists() && latestOpenedProjectFile.canRead()) {
            properties.setProperty(LATEST_OPENED_PROJECT_KEY, latestOpenedProjectFile.getAbsolutePath());
        }
        save();
    }

    private void save() {
        try (FileOutputStream fos = new FileOutputStream(propertiesFile)) {
            properties.store(fos, "cnpuvache");
        } catch (IOException e) {
            throw new RuntimeException("Unable to write to properties file.");
        }
    }

    public Optional<File> getLatestLoadedCallbackHeaderFile() {
        if (properties.containsKey(LATEST_LOADED_CALLBACK_HEADER_FILE)) {
            Object latestLoadedCallbackHeaderFile = properties.get(LATEST_LOADED_CALLBACK_HEADER_FILE);
            String latestLoadedCallbackHeaderFileName = String.valueOf(latestLoadedCallbackHeaderFile);
            return Optional.of(new File(latestLoadedCallbackHeaderFileName));
        }
        return Optional.empty();
    }

    public void setLatestLoadedCallbackHeaderFile(File headerFile) {
        if (headerFile.exists() && headerFile.canRead()) {
            properties.setProperty(LATEST_LOADED_CALLBACK_HEADER_FILE, headerFile.getAbsolutePath());
        }
        save();
    }
}
