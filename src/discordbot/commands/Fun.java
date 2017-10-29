package discordbot.commands;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import discordbot.Lang;
import discordbot.Logging;
import discordbot.Util;
import discordbot.server.Emoji;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class Fun {
	
	/**
	 * Create the music-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	static void createFunCommands ( ) {	
		
		Handler.commandMap.put ( "8ball", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
			try { 
				final IChannel channel = event.getChannel();
				if ( args.size() > 0 ) {
					final int rnd = ThreadLocalRandom.current().nextInt( 1, 12 + 1 );
					
					if ( rnd <= 3 ) 
						Util.sendMessage ( channel, Lang.getLang ( "yes", event.getAuthor(), event.getGuild() ) );
					else if ( rnd <= 6 )
						Util.sendMessage( channel, Lang.getLang ( "no", event.getAuthor(), event.getGuild() ) );
					else if ( rnd == 7 )
						Util.sendMessage( channel, Lang.getLang ( "not_sure", event.getAuthor(), event.getGuild() ) );
					else if ( rnd == 8 )
						Util.sendMessage( channel, Lang.getLang ( "maybe", event.getAuthor(), event.getGuild() ) );
					else if ( rnd == 9 )
						Util.sendMessage( channel, Lang.getLang ( "nah", event.getAuthor(), event.getGuild() ) );
					else if ( rnd == 10 )
						Util.sendMessage( channel, Lang.getLang ( "absolutely", event.getAuthor(), event.getGuild() ) );
					else if ( rnd == 11 ) {
						Util.sendMessage( channel, Lang.getLang ( "stupid_question", event.getAuthor(), event.getGuild() )+Emoji.haha );
						event.getMessage().addReaction( ReactionEmoji.of( "haha", Emoji.hahacode ));
					} else 
						Util.sendMessage( channel, Lang.getLang ( "ask_again", event.getAuthor(), event.getGuild() ) );		
				} else {
					Util.sendMessage( channel, Lang.getLang ( "what_is_question", event.getAuthor(), event.getGuild() )+Emoji.what );
					event.getMessage().addReaction( ReactionEmoji.of( "what", Emoji.whatcode ));
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );		
	}
}
