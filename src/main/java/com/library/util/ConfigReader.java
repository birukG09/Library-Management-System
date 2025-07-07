package com.library.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton utility class for reading configuration from properties file
 * Demonstrates file I/O operations using BufferedReader
 */
public class ConfigReader {
    private static ConfigReader instance;
    private final Properties properties;
    private final String configFileName;
    
    private ConfigReader() {
        this.properties = new Properties();
        this.configFileName = "config/database.properties";
        loadConfiguration();
    }
    
    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }
    
    private void loadConfiguration() {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFileName))) {
            properties.load(reader);
            FileLogger.getInstance().log("INFO", "Configuration loaded from: " + configFileName);
        } catch (IOException e) {
            FileLogger.getInstance().log("WARNING", "Could not load configuration file: " + configFileName + 
                ". Using default values. Error: " + e.getMessage());
            loadDefaultConfiguration();
        }
    }
    
    private void loadDefaultConfiguration() {
        // Set default values if config file is not available
        properties.setProperty("db.name", "library.db");
        properties.setProperty("db.driver", "org.sqlite.JDBC");
        properties.setProperty("app.name", "Library Management System");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("log.level", "INFO");
        properties.setProperty("backup.enabled", "true");
        properties.setProperty("backup.interval.hours", "24");
    }
    
    // Method overloading for getting properties
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            FileLogger.getInstance().log("WARNING", "Invalid integer value for property: " + key);
            return defaultValue;
        }
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    public void reloadConfiguration() {
        properties.clear();
        loadConfiguration();
        FileLogger.getInstance().log("INFO", "Configuration reloaded");
    }
    
    public void displayConfiguration() {
        System.out.println("=== Configuration Settings ===");
        for (String key : properties.stringPropertyNames()) {
            System.out.println(key + " = " + properties.getProperty(key));
        }
        System.out.println("==============================");
    }
    
    // Get all properties for debugging
    public Properties getAllProperties() {
        return new Properties(properties);
    }
}
