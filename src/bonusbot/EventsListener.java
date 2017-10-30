package bonusbot;

import java.util.List;

import bonusbot.guild.GuildExtends;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.IGuild;

class EventsListener {
	
	@EventSubscriber
	public final void onReady ( final ReadyEvent event ) {
		event.getClient().changeUsername( Settings.name );
		event.getClient().changePlayingText( Settings.playing );
		
		List<IGuild> guilds = event.getClient().getGuilds();
		for ( IGuild guild : guilds ) {
			new GuildExtends ( guild );
		}
	}
	
	@EventSubscriber
	public final void onUserJoinedGuild ( final UserJoinEvent event ) {
		final GuildExtends guildext = GuildExtends.get( event.getGuild() );
		final Long greetUserChannelID = guildext.getGreetUserChannelID(); 
		if ( greetUserChannelID != null ) {
			final IGuild guild = event.getGuild();
			final int amountonserver = guild.getTotalMemberCount();
			final String suffix = amountonserver == 1 ? "st" : 
				( amountonserver == 2 ? "nd" :
				( amountonserver == 3 ? "rd" : "th" ) );
			final String tadaemoji = guildext.getTadaEmoji() == null ? " "+ guildext.getTadaEmoji() : "!";
			Util.sendMessage( guild.getChannelByID( greetUserChannelID ), "Welcome "+event.getUser().mention()
					+"!\nYou are the "+amountonserver+suffix+" user" + tadaemoji
					+"\nPlease read 'informations' in 'important' category." );
			
		}
	}
}
