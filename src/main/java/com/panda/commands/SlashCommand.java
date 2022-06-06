package com.panda.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SlashCommand {

    private final String name;
    private final String description;
    private final List<OptionData> optionsDataList;

    public SlashCommand(String name, String description) {
        this(name, description, new ArrayList<>());
    }

    public SlashCommand(String name, String description, @NotNull List<OptionData> optionsDataList) {
        this.name = name;
        this.description = description;
        this.optionsDataList = optionsDataList;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<OptionData> getOptionsDataList() {
        return optionsDataList;
    }

    public void addOptionData(OptionData optionData) {
        optionsDataList.add(optionData);
    }

    public SlashCommandData getSlashImplementation() {
        return Commands.slash(getName(), getDescription())
                .addOptions(optionsDataList);
    }

    public abstract void execute(SlashCommandInteractionEvent event);

    @Override
    public String toString() {
        return "SlashCommand{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", optionsDataList=" + optionsDataList +
                '}';
    }
}
