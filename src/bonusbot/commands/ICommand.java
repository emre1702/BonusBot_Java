package bonusbot.commands;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import java.util.List;

/**
 * Interface for Command
 */
interface ICommand {

	/**
	 * Interface for commands.
	 * 
	 * @param commandName
	 *            Name of the command
	 * @param event
	 *            MessageReceivedEvent
	 * @param args
	 *            Arguments used at command-use
	 */
	void runCommand(String commandName, MessageReceivedEvent event, List<String> args);

}