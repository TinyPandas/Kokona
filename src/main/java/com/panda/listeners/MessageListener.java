package com.panda.listeners;

import com.panda.annotations.Listener;
import com.panda.utility.ListenerLoader;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.tinypandas.annotations.LoaderInfo;

@Listener(name = "message")
@LoaderInfo(loader = ListenerLoader.class)
public class MessageListener extends com.panda.listeners.Listener {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("ping")) {
            event.getMessage().reply("pong").queue();
        }
    }
}
