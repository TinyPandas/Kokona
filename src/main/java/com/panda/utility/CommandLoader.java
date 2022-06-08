package com.panda.utility;

import com.panda.annotations.CommandAnnotation;
import com.panda.annotations.LoaderAnnotation;
import com.panda.commands.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@LoaderAnnotation(path = "com.panda.commands")
public class CommandLoader extends Loader {

    private static final Logger logger = LoggerFactory.getLogger(CommandLoader.class.getName());
    private static final Map<String, SlashCommand> loadedCommands = new HashMap<>();

    public static Map<String, SlashCommand> getLoadedCommands() {
        return loadedCommands;
    }

    public <T> T registerClass(Class<T> commandClass) {
        logger.info("Registering command: " + commandClass);
        try {
            CommandAnnotation annotation = commandClass.getAnnotation(CommandAnnotation.class);
            logger.info("Successfully registered command: " + commandClass);
            return commandClass.getDeclaredConstructor(String.class, String.class)
                    .newInstance(annotation.name(), annotation.desc());
        } catch (Exception e) {
            logger.warn("Failed to register command: " + commandClass);
            e.printStackTrace();
            return null;
        }
    }
}
