package com.panda;

import com.panda.annotations.CommandAnnotation;
import com.panda.annotations.ListenerAnnotation;
import com.panda.commands.SlashCommand;
import com.panda.constants.EnvironmentVariables;
import com.panda.listeners.Listener;
import com.panda.listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;
import java.util.*;

public class Bot {

    private static final Reflections reflections = new Reflections("com.panda");

    private static final Map<String, SlashCommand> loadedCommands = new HashMap<>();
    private static final Map<String, Listener> loadedListeners = new HashMap<>();

    public static void main(String[] args) throws NullPointerException, LoginException, InterruptedException {
        String botToken = Optional.ofNullable(System.getenv(EnvironmentVariables.TOKEN_PATH))
                        .orElseThrow(() -> new NullPointerException("No bot token provided / found."));

        registerCommands();
        registerListeners();

        JDABuilder builder = JDABuilder.createDefault(botToken)
                //Enable the bulk delete event.
                .setBulkDeleteSplittingEnabled(false)
                // Only cache online members or owner of the guild.
                .setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.OWNER))
                // Disable member chunking on startup.
                .setChunkingFilter(ChunkingFilter.NONE)
                // Consider guilds with more than 100 members as "large".
                // Large guilds will only provide online members in setup.
                .setLargeThreshold(100)
                // Set the event manager to annotated methods.
                .setEventManager(new AnnotatedEventManager())
                .setActivity(Activity.playing("v0.2_alpha"));

        // TODO: Determine Saving Method (Check if EnvironmentVariables.DB_URI exists, or default: json)
        builder.addEventListeners(new MessageListener());

        JDA jda = builder.build();
        jda.awaitReady();

        jda.getGuilds().forEach(guild -> {
            if (guild != null) {
                setupGuild(guild);
            }
        });
    }

    private static void registerCommands() {
        Set<Class<?>> commandClasses = reflections.getTypesAnnotatedWith(CommandAnnotation.class);
        commandClasses.forEach(commandClass -> {
            String name = commandClass.getAnnotation(CommandAnnotation.class).name();
            SlashCommand registeredCommand = (SlashCommand) registerCommand(commandClass);

            System.out.println("Registered Command: " + name);
            loadedCommands.put(name, registeredCommand);
        });

        loadedCommands.forEach((name, command) -> System.out.println(command.toString()));
    }

    private static void registerListeners() {
        Set<Class<?>> listenerClasses = reflections.getTypesAnnotatedWith(ListenerAnnotation.class);
        listenerClasses.forEach(listenerClass -> {
            String name = listenerClass.getAnnotation(ListenerAnnotation.class).name();
            Listener registeredListener = (Listener) registerListener(listenerClass);

            System.out.println("Registered Listener: " + name);
            loadedListeners.put(name, registeredListener);
        });

        loadedListeners.forEach((name, listener) -> System.out.println(listener.toString()));
    }

    private static <T> T registerCommand(Class<T> commandClass) {
        try {
            CommandAnnotation annotation = commandClass.getAnnotation(CommandAnnotation.class);
            return commandClass.getDeclaredConstructor(String.class, String.class)
                    .newInstance(annotation.name(), annotation.desc());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T> T registerListener(Class<T> commandClass) {
        try {
            return commandClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void setupGuild(Guild guild) {
        CommandListUpdateAction commands = guild.updateCommands();

        List<SlashCommandData> slashCommandDataList = loadedCommands.values().stream()
                .map(SlashCommand::getSlashImplementation).toList();

        commands.addCommands(slashCommandDataList).queue();
    }
}
