package com.panda;

import com.panda.commands.SlashCommand;
import com.panda.constants.EnvironmentVariables;
import com.panda.utility.CommandLoader;
import com.panda.utility.ListenerLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.tinypandas.AnnotationLoader;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Optional;

public class Bot {
    public static void main(String[] args) throws NullPointerException, LoginException, InterruptedException {
        String botToken = Optional.ofNullable(System.getenv(EnvironmentVariables.TOKEN_PATH))
                        .orElseThrow(() -> new NullPointerException("No bot token provided / found."));

        AnnotationLoader annotationLoader = new AnnotationLoader("com.panda");
        annotationLoader.onStartRegister();

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
        builder.addEventListeners(ListenerLoader.getLoadedListeners().values());

        JDA jda = builder.build();
        jda.awaitReady();

        jda.getGuilds().forEach(guild -> {
            if (guild != null) {
                setupGuild(guild);
            }
        });
    }

    private static void setupGuild(Guild guild) {
        CommandListUpdateAction commands = guild.updateCommands();

        List<SlashCommandData> slashCommandDataList = CommandLoader.getLoadedCommands().values().stream()
                .map(SlashCommand::getSlashImplementation).toList();

        commands.addCommands(slashCommandDataList).queue();
    }
}
