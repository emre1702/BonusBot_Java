package discordbot.commands;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import discordbot.Lang;
import discordbot.Logging;
import discordbot.Util;
import discordbot.guild.GuildExtends;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * Commands for fun-things.
 * @author emre1702
 *
 */
public class Fun {
	
	/**
	 * Create the music-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	static void createFunCommands ( ) {	
		
		Handler.commandMap.put ( "8ball", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
			try { 
				final IChannel channel = event.getChannel();
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
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
						final IEmoji hahaemoji = guildext.getWhatEmoji();
						if ( hahaemoji != null ) {
							Util.sendMessage( channel, Lang.getLang ( "stupid_question", event.getAuthor(), event.getGuild() )+Util.getEmojiString( hahaemoji ) );
							event.getMessage().addReaction( ReactionEmoji.of( hahaemoji ));
						} else 
							Util.sendMessage( channel, Lang.getLang ( "stupid_question", event.getAuthor(), event.getGuild() ) );
							
					} else 
						Util.sendMessage( channel, Lang.getLang ( "ask_again", event.getAuthor(), event.getGuild() ) );		
				} else {
					final IEmoji whatemoji = guildext.getWhatEmoji();
					if ( whatemoji != null ) {
						Util.sendMessage( channel, Lang.getLang ( "what_is_question", event.getAuthor(), event.getGuild() )+Util.getEmojiString( whatemoji ) );
						event.getMessage().addReaction( ReactionEmoji.of( whatemoji ));
					} else 
						Util.sendMessage( channel, Lang.getLang ( "what_is_question", event.getAuthor(), event.getGuild() ) );
						
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );		
	}
}
