package bonusbot.commands;
import java.util.*;

import bonusbot.Logging;
import bonusbot.Settings;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;


/** 
 * ICommand-handler.
 * @author emre1702
 *
 */
public class Handler {
	
	/** map for commands */
	final static Map<String, ICommand> commandMap = new HashMap<String, ICommand>();
	
	/**
	 * Create all the commands.
	 * Use this handler for the clients dispatcher as a listener.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	public Handler () {
		Fun.createFunCommands();
		Language.createLanguageCommands();
		Audio.createAudioCommands();
		OtherGames.createOtherGamesCommands();
		Admin.createAdminCommands();
	}
	
	/** 
	 * When a message is received 
	 * @param event MessageReceivedEvent from Discord4J
	 */
	 @EventSubscriber
	 public void onMessageReceived ( MessageReceivedEvent event ) {
		 	try {

		        // Note for error handling, you'll probably want to log failed commands with a logger or sout
		        // In most cases it's not advised to annoy the user with a reply incase they didn't intend to trigger a
		        // command anyway, such as a user typing ?notacommand, the bot should not say "notacommand" doesn't exist in
		        // most situations. It's partially good practise and partially developer preference
	
		        // Given a message "/test arg1 arg2", argArray will contain ["/test", "arg1", "arg"]
		 		final String[] argArray = event.getMessage().getContent().split(" ");
	
		        // First ensure at least the command and prefix is present, the arg length can be handled by your command func
		        if(argArray.length == 0)
		            return;
	
		        // Check if the first arg (the command) starts with the prefix defined in the utils class
		        if(!argArray[0].startsWith(Settings.get("prefix")))
		            return;
	
		        // Extract the "command" part of the first arg out by just ditching the first character
		        final String commandStr = argArray[0].substring(1);
	
		        // Load the rest of the args in the array into a List for safer access
		        final List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
		        argsList.remove(0); // Remove the command
	
		        // Instead of delegating the work to a switch, automatically do it via calling the mapping if it exists
		        if(commandMap.containsKey(commandStr))
		            commandMap.get(commandStr).runCommand(commandStr, event, argsList);
		 	} catch ( Exception e ) {
		 		e.printStackTrace ( Logging.getPrintWrite() );
		 	}

	    }

}

