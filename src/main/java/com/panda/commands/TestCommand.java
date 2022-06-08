package com.panda.commands;

import com.panda.annotations.Command;
import com.panda.annotations.LoaderInfo;
import com.panda.utility.CommandLoader;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "test", desc = "Some test command.")
@LoaderInfo(loader = CommandLoader.class)
public class TestCommand extends SlashCommand {

    public TestCommand(String name, String description) {
        super(name, description);
    }

    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Testing command, will be removed in the future.").queue();
    }
}
