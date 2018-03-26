package bonusbot;

import java.util.List;

import bonusbot.guild.GuildExtends;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.StatusType;

/**
 * Listener for Discord4J events.
 * @author EmreKara
 *
 */
class EventsListener {
	
	/**
	 * When the bot is ready.
	 * @param event ReadyEvent from Discord4J
	 */
	@EventSubscriber
	public final void onReady ( final ReadyEvent event ) {
		event.getClient().changeUsername( Settings.name );
		event.getClient().changePresence( StatusType.ONLINE, ActivityType.PLAYING, Settings.playing );
		
		final List<IGuild> guilds = event.getClient().getGuilds();
		for ( IGuild guild : guilds ) {
			new GuildExtends ( guild );
		}
	}
	
	/**
	 * When a user joined the guild (first time).
	 * @param event UserJoinEvent from Discord4J
	 */
	@EventSubscriber
	public final void onUserJoinedGuild ( final UserJoinEvent event ) {
		final GuildExtends guildext = GuildExtends.get( event.getGuild() );
		final IChannel greetUserChannel = guildext.getGreetUserChannel(); 
		if ( greetUserChannel != null ) {
			final IGuild guild = event.getGuild();
			final int amountonserver = guild.getTotalMemberCount();
			final String suffix = amountonserver == 1 ? "st" : 
				( amountonserver == 2 ? "nd" :
				( amountonserver == 3 ? "rd" : "th" ) );
			String welcomemsg = "Welcome "+event.getUser().mention()
					+"!\nYou are the "+amountonserver+suffix+" user 🎉";
			IChannel channel = guildext.getInformationsChannel();
			if ( channel != null )
				welcomemsg += "\nPlease read " + channel.mention() + " in 'important' category!";
			Util.sendMessage( greetUserChannel, welcomemsg ); 
		}
	}
}
