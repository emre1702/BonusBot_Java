package discordbot.commands;

import java.util.concurrent.ThreadLocalRandom;

import discordbot.Lang;
import discordbot.Logging;
import discordbot.ServerEmoji;
import discordbot.Util;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;

public class Fun {
	
	static {	
		Handler.commandMap.put ( "8ball", ( cmd, event, args ) -> {
			try { 
				IChannel channel = event.getChannel();
				if ( args.size() > 0 ) {
					int rnd = ThreadLocalRandom.current().nextInt( 1, 12 + 1 );
					
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
						Util.sendMessage( channel, Lang.getLang ( "stupid_question", event.getAuthor(), event.getGuild() )+ServerEmoji.haha );
						event.getMessage().addReaction( ReactionEmoji.of( "haha", ServerEmoji.hahacode ));
					} else 
						Util.sendMessage( channel, Lang.getLang ( "ask_again", event.getAuthor(), event.getGuild() ) );		
				} else {
					Util.sendMessage( channel, Lang.getLang ( "what_is_question", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
					event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );		
	}
}
