package com.rdfarango.constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final String NAME = "config.properties";
    public static final Properties properties;

    public static class Keys {
        public static final String GRAPH_LITERALS_START_KEY = "graph.literalsCollection.start_key";
    }

    static {
        Properties fallback = new Properties();
        fallback.put(Keys.GRAPH_LITERALS_START_KEY, "1");
        properties = new Properties(fallback);

        try (InputStream input = new FileInputStream(NAME)) {
            // load a properties file
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int GetGraphLiteralsStartKey(){
        return Integer.valueOf(properties.getProperty(Keys.GRAPH_LITERALS_START_KEY));
    }
}
