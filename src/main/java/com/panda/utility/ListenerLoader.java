package com.panda.utility;

import com.panda.listeners.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinypandas.annotations.LoaderInfo;
import org.tinypandas.utility.Loader;

import java.util.HashMap;
import java.util.Map;

@LoaderInfo(isLoader = true)
public class ListenerLoader extends Loader {

    private static final Logger logger = LoggerFactory.getLogger(ListenerLoader.class.getName());

    private static final Map<String, Listener> loadedListeners = new HashMap<>();

    public static Map<String, Listener> getLoadedListeners() {
        return loadedListeners;
    }

    public <T> T registerClass(Class<T> clazz) {
        logger.info("Registering listener: " + clazz);
        try {
            logger.info("Successfully registered listener: " + clazz);
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.warn("Failed to register listener: " + clazz);
            e.printStackTrace();
            return null;
        }
    }
}
