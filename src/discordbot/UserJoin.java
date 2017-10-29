package discordbot;
import discordbot.server.Channels;
import discordbot.server.Emoji;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.IGuild;

class UserJoin implements IListener<UserJoinEvent> {

	@Override
	public void handle ( UserJoinEvent event ) {
		if ( Channels.greetUserChannelID != -1 ) {
			IGuild guild = event.getGuild();
			int amountonserver = guild.getTotalMemberCount();
			String suffix = amountonserver == 1 ? "st" : 
				( amountonserver == 2 ? "nd" :
				( amountonserver == 3 ? "rd" : "th" ) );
			Util.sendMessage( guild.getChannelByID( Channels.greetUserChannelID ), "Welcome "+event.getUser().mention()
					+"!\nYou are the "+amountonserver+suffix+" user "+Emoji.tada
					+"\nPlease read 'informations' in 'important' category." );
			
		}
		
	}

}
