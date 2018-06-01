package bonusbot;

import java.util.List;

import bonusbot.guild.GuildExtends;
import sx.blah.discord.api.events.EventSubscriber;
//import sx.blah.discord.handle.audit.ActionType;
//import sx.blah.discord.handle.audit.entry.TargetedEntry;
import sx.blah.discord.handle.impl.events.ReadyEvent;
//import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
//import sx.blah.discord.handle.impl.events.guild.channel.message.MessageUpdateEvent;
//import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
//import sx.blah.discord.handle.impl.events.guild.member.UserPardonEvent;
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
		GuildExtends guildext = GuildExtends.get( event.getGuild() );
		IChannel greetUserChannel = guildext.getGreetUserChannel(); 
		if ( greetUserChannel != null ) {
			IGuild guild = event.getGuild();
			int amountonserver = guild.getTotalMemberCount();
			String suffix = amountonserver == 1 ? "st" : 
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
	
	@EventSubscriber
	public final void onUserLeaveGuild( UserLeaveEvent event ) {
		GuildExtends guildext = GuildExtends.get( event.getGuild() );
		IChannel logchannel = guildext.getUserLeaveLogChannel();
		if (logchannel != null) {
			String msg = event.getUser().mention() + " has left the guild.";
			Util.sendMessage(logchannel, msg);
		}
	}
	
	/*@EventSubscriber
	public final void onUserBanned( UserBanEvent event ) {
		GuildExtends guildext = GuildExtends.get( event.getGuild() );
		IChannel logchannel = guildext.getUserBanLogChannel();
		if (logchannel != null) {
			TargetedEntry auditlog = event.getGuild().getAuditLog(ActionType.MEMBER_BAN_ADD).getEntriesByTarget(event.getUser().getLongID()).get(0);
			String msg = event.getUser().mention() + " got banned by "+auditlog.getResponsibleUser().mention()+". Reason: "+auditlog.getReason().orElse("None");
			Util.sendMessage(logchannel, msg);
		}
	}
	
	@EventSubscriber
	public final void onUserUnbanned( UserPardonEvent event ) {
		GuildExtends guildext = GuildExtends.get( event.getGuild() );
		IChannel logchannel = guildext.getUserPardonLogChannel();
		if (logchannel != null) {
			TargetedEntry auditlog = event.getGuild().getAuditLog(ActionType.MEMBER_BAN_REMOVE).getEntriesByTarget(event.getUser().getLongID()).get(0);
			String msg = event.getUser().mention() + " got unbanned by "+auditlog.getResponsibleUser().mention()+".";
			Util.sendMessage(logchannel, msg);
		}
	}
	
	@EventSubscriber
	public final void onMessageEdited( MessageUpdateEvent event ) {
		if (!MainRunner.isBot(event.getAuthor())) {
			GuildExtends guildext = GuildExtends.get( event.getGuild() );
			IChannel logchannel = guildext.getMessageUpdateLogChannel();
			if (logchannel != null) {
				String msg = "Message got edited by "+event.getAuthor().mention()+" in "+event.getChannel().mention()+":\n" + event.getOldMessage().getContent();
				Util.sendMessage(logchannel, msg);
			}
		}
	}
	
	@EventSubscriber
	public final void onMessageDeleted( MessageDeleteEvent event ) {
		GuildExtends guildext = GuildExtends.get( event.getGuild() );
		IChannel logchannel = guildext.getMessageDeleteLogChannel();
		if (logchannel != null) {
			TargetedEntry auditlog = event.getGuild().getAuditLog(ActionType.MESSAGE_DELETE).getEntriesByTarget(event.getMessageID()).get(0);
			String msg = "Message from "+event.getAuthor().mention()+" got deleted by "+auditlog.getResponsibleUser().mention()+" in "+event.getChannel().mention()+":\n" + event.getMessage().getContent();
			Util.sendMessage(logchannel, msg);
		}
	}*/
}
