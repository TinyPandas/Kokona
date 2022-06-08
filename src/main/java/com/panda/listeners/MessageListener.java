package com.panda.listeners;

import com.panda.annotations.ListenerAnnotation;
import com.panda.annotations.LoaderAnnotation;
import com.panda.utility.ListenerLoader;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

@ListenerAnnotation(name = "message")
@LoaderAnnotation(loader = ListenerLoader.class)
public class MessageListener extends Listener {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("ping")) {
            event.getMessage().reply("pong").queue();
        }
    }
}
