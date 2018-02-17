package bonusbot.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import bonusbot.Lang;
import bonusbot.Logging;
import bonusbot.Settings;
import bonusbot.Util;
import bonusbot.guild.GuildExtends;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;

/**
 *  Commands for admins 
 *  @author emre1702
*/
public class Admin {
	
	/**
	 * Delete last messages in a channel.
	 * @param channel The channel where the message should get deleted.
	 * @param amount Amount of messages which should get deleted.
	 * @return Amount of deleted messages.
	 */
	private final static int deleteLastMessages ( IChannel channel, int amount ) {
		MessageHistory history = channel.getMessageHistory( amount ); 
		history.bulkDelete();
		return history.size();
		
	}
	
	/**
	 * Delete last messages from a user in a channel.
	 * @param channel The channel where the message should get deleted.
	 * @param user The user which messages should get deleted.
	 * @param amount Amount of messages which should get deleted.
	 * @return Amount of deleted messages.
	 */
	private final static int deleteLastMessages ( IChannel channel, IUser user, int amount ) {
		int amountdeleted = 0;
		List<IMessage> messagelist = new ArrayList<IMessage>();
		MessageHistory history = channel.getMessageHistory( amount > 400 ? amount : 400 ); 
		Iterator<IMessage> messages = history.iterator();
		while ( messages.hasNext() && amountdeleted < amount ) {
			IMessage message = messages.next();
			if ( message.getAuthor() == user ) {
				messagelist.add( message );
				++amountdeleted;
			}
		}
		channel.bulkDelete( messagelist );
		return amountdeleted;
	}
	
	/**
	 * Create the admins-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	final static void createAdminCommands () {
		
		/** Delete last messages (of a user) in the channel */
		final ICommand deleteLastMessagesOfUserInChannel = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
			try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAdmin ( event.getAuthor() ) ) {
					IGuild guild = event.getGuild();
					IChannel channel = event.getChannel();
					IUser author = event.getAuthor();
					if ( args.size() > 0 ) {
						int deleteamount = 100;
						if ( args.size() > 1 ) {
							if ( StringUtils.isNumeric( args.get( 1 ) ) )
								deleteamount = Integer.parseInt( args.get( 1 ) );
							List<IUser> users = guild.getUsersByName( args.get( 0 ) );
							if ( users.size() > 0 ) {
								IUser user = users.get( 0 );
								int amountdeleted = deleteLastMessages ( channel, user, deleteamount );
								Util.sendMessage( channel, amountdeleted + " " + Lang.getLang( "got_deleted", author, guild ) );
							} else 
								Util.sendMessage( channel, Lang.getLang( "user_not_found", event.getAuthor(), guild ) );
						} else {
							if ( StringUtils.isNumeric( args.get( 0 ) ) )
								deleteamount = Integer.parseInt( args.get( 0 ) );
							int amountdeleted = deleteLastMessages ( channel, deleteamount );
							Util.sendMessage( channel, amountdeleted + " " + Lang.getLang( "got_deleted", author, guild ) );
						}
					} else {
						Util.sendMessage( channel, Lang.getLang( "usage", author, guild ) + ": "+Settings.prefix+"delete "
								+ " ["+Lang.getLang( "user", author, guild ) +"]" 
								+ " ["+Lang.getLang( "amount_messages", author, guild )+" = 100]" );
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put ( "delmsg", deleteLastMessagesOfUserInChannel );
		
	
	}
	
}
