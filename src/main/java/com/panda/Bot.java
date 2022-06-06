package com.panda;

import com.panda.annotations.CommandAnnotation;
import com.panda.commands.SlashCommand;
import com.panda.constants.EnvironmentVariables;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;
import java.util.*;

public class Bot {

    private static final Map<String, SlashCommand> loadedCommands = new HashMap<>();

    public static void main(String[] args) throws NullPointerException, LoginException, InterruptedException {
        String botToken = Optional.ofNullable(System.getenv(EnvironmentVariables.TOKEN_PATH))
                        .orElseThrow(() -> new NullPointerException("No bot token provided / found."));

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
                .setActivity(Activity.playing("v0.1_alpha"));

        // TODO: Determine Saving Method (Check if EnvironmentVariables.DB_URI exists, or default: json)
        // TODO: Add listeners

        JDA jda = builder.build();
        jda.awaitReady();

        registerCommands();

        jda.getGuilds().forEach(guild -> {
            if (guild != null) {
                setupGuild(guild);
            }
        });
    }

    private static void registerCommands() {
        Reflections reflections = new Reflections("com.panda");

        Set<Class<?>> commandClasses = reflections.getTypesAnnotatedWith(CommandAnnotation.class);
        commandClasses.forEach(commandClass -> {
            String name = commandClass.getAnnotation(CommandAnnotation.class).name();
            SlashCommand registeredCommand = (SlashCommand) registerCommand(commandClass);

            System.out.println("Registered: " + name);
            loadedCommands.put(name, registeredCommand);
        });

        loadedCommands.forEach((name, command) -> System.out.println(command.toString()));
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

    private static void setupGuild(Guild guild) {
        CommandListUpdateAction commands = guild.updateCommands();

        List<SlashCommandData> slashCommandDataList = loadedCommands.values().stream()
                .map(SlashCommand::getSlashImplementation).toList();

        commands.addCommands(slashCommandDataList).queue();
    }
}
