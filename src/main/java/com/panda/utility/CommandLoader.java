package com.panda.utility;

import com.panda.annotations.Command;
import com.panda.annotations.LoaderInfo;
import com.panda.commands.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@LoaderInfo(isLoader = true)
public class CommandLoader extends Loader {

    private static final Logger logger = LoggerFactory.getLogger(CommandLoader.class.getName());
    private static final Map<String, SlashCommand> loadedCommands = new HashMap<>();

    public static Map<String, SlashCommand> getLoadedCommands() {
        return loadedCommands;
    }

    public <T> T registerClass(Class<T> clazz) {
        logger.info("Registering command: " + clazz);
        try {
            Command annotation = clazz.getAnnotation(Command.class);
            logger.info("Successfully registered command: " + clazz);
            return clazz.getDeclaredConstructor(String.class, String.class)
                    .newInstance(annotation.name(), annotation.desc());
        } catch (Exception e) {
            logger.warn("Failed to register command: " + clazz);
            e.printStackTrace();
            return null;
        }
    }
}
