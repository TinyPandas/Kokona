package com.panda.utility;

import com.panda.annotations.LoaderAnnotation;
import com.panda.listeners.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@LoaderAnnotation(path = "com.panda.listeners")
public class ListenerLoader extends Loader {

    private static final Logger logger = LoggerFactory.getLogger(CommandLoader.class.getName());

    private static final Map<String, Listener> loadedListeners = new HashMap<>();

    public static Map<String, Listener> getLoadedListeners() {
        return loadedListeners;
    }

    public <T> T registerClass(Class<T> commandClass) {
        logger.info("Registering listener: " + commandClass);
        try {
            logger.info("Successfully registered listener: " + commandClass);
            return commandClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.warn("Failed to register listener: " + commandClass);
            e.printStackTrace();
            return null;
        }
    }
}
