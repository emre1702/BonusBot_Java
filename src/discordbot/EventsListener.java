package discordbot;

import discordbot.server.Channels;
import discordbot.server.Emojis;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.IGuild;

class EventsListener {
	
	@EventSubscriber
	public final void onReady ( final ReadyEvent event ) {
		event.getClient().changeUsername( Settings.name );
		event.getClient().changePlayingText( Settings.playing );
	}
	
	@EventSubscriber
	public final void onUserJoinedGuild ( final UserJoinEvent event ) {
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
