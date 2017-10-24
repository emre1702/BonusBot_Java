package discordbot;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import java.util.List;

interface Command {

    // Interface for a command to be implemented in the command map
    void runCommand( String commandName, MessageReceivedEvent event, List<String> args );

}