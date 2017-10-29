package discordbot;
import discordbot.server.Channels;
import discordbot.server.Emojis;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Handler for user-join to the guild.
 * @author emre1702
 *
 */
class UserJoin implements IListener<UserJoinEvent> {

	@Override
	/**
	 * handle when the user joins the guild.
	 */
	public void handle ( UserJoinEvent event ) {
		if ( Channels.greetUserChannelID != -1 ) {
			final IGuild guild = event.getGuild();
			final int amountonserver = guild.getTotalMemberCount();
			final String suffix = amountonserver == 1 ? "st" : 
				( amountonserver == 2 ? "nd" :
				( amountonserver == 3 ? "rd" : "th" ) );
			Util.sendMessage( guild.getChannelByID( Channels.greetUserChannelID ), "Welcome "+event.getUser().mention()
					+"!\nYou are the "+amountonserver+suffix+" user "+Emojis.tada
					+"\nPlease read 'informations' in 'important' category." );
			
		}
		
	}

}
