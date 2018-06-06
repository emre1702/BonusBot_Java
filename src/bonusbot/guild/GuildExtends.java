package bonusbot.guild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;

import bonusbot.Audio;
import bonusbot.Settings;
import bonusbot.Util;
import lavaplayer.GuildAudioManager;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

/**
 * Manage more informations for the specific guild.
 * @author emre1702
 *
 */
public class GuildExtends {

	/** The object for the specific guild(ID) */
	private final static Map<Long, GuildExtends> guildExtendsObjects = new HashMap<>();
	/** The AudioManager for the guild */
	private AudioManager audiomanager;
	/** The guild */
	private IGuild guild;
	/** The found numbers of !ytsearch for !ytplay */
	public AudioPlaylist ytsearchlist;
	/** Timer to stop the audio on e.g. !stop 5 */
	public Timer stopAudioTimer = new Timer();
	/** Timer to pause/resume the audio on e.g. !pause 5 */
	public Timer pauseresumeAudioTimer = new Timer();
	
	/**
	 * Constructor 
	 * Create AudioManager for the guild.
	 * @param guild The guild.
	 */
	public GuildExtends ( final IGuild guild ) {
		guildExtendsObjects.put( guild.getLongID(), this );
		this.audiomanager = new AudioManager ( guild, Audio.getPlayerManager() );
		this.guild = guild;
	}
	
	/**
	 * Get the object for the specific guild.
	 * @param guild The guild whose object we want to retrieve.
	 * @return The GuildExtends object for the guild.
	 */
	public synchronized final static GuildExtends get ( final IGuild guild ) {
		final long guildId = guild.getLongID();
		GuildExtends guildext = guildExtendsObjects.get ( guildId );

        if ( guildext == null ) {
        	guildext = new GuildExtends ( guild );
        }

        return guildext;
	}
	
	/**
	 * Getter for the IGuild of a GuildExtends.
	 * @return
	 */
	public IGuild getGuild() {
		return guild;
	}
	
	/**
	 * Check if the audio-channel is not set and the ID is it. 
	 * Needed to check if we can use audio-commmands in this channel.
	 * @param ID Long-ID of the channel we want to check.
	 * @return if the audio-channel is not set or the ID is the audio-channel.
	 */
	public final boolean isAudioChannel ( final Long ID ) {
		final Long audiochannelID = this.getAudioChannelID();
		return ( audiochannelID == null || audiochannelID.equals( ID ) );
	}
	
	/**
	 * Check if the roles-channel is not set and the ID is it. 
	 * Needed to check if we can use roles-commands in this channel.
	 * @param ID Long-ID of the channel we want to check.
	 * @return if the roles-channel is not set or the ID is the roles-channel.
	 */
	public final boolean isRolesChannel ( final Long ID ) {
		final Long roleschannelID = this.getRolesChannelID();
		return ( roleschannelID == null || roleschannelID.equals( ID ) );
	} 
	
	/**
	 * Check if the user got any of the admin roles.
	 * Needed for the admin-commands.
	 * @param user User to check.
	 * @return if the user got an admin role.
	 */
	public final boolean isAdmin ( IUser user ) {
		List<IRole> roles = guild.getRolesForUser( user );
		for ( String adminrolename : Settings.admins ) {
			List<IRole> adminroles = guild.getRolesByName( adminrolename );
			if ( adminroles.size() > 0 && roles.contains( adminroles.get( 0 ) ) )
				return true;
		}
		return false;
	}
	
	/**
	 * Check if the user has the role to play audio or everyone can play it (audioBotUserRoleID is not set)
	 * @param user The user we want to check.
	 * @return If user can use audio-commands.
	 */
	public final boolean canPlayAudio ( IUser user ) {
		final Long audioroleID = this.getAudioBotUserRoleID();
		if ( audioroleID == null )
			return true;
		final List<IRole> roles = guild.getRolesForUser( user );
		return roles.contains( guild.getRoleByID( audioroleID ) );
	}
	
	/**
	 * Getter for the GuildAudioManager for the guild.
	 * @return GuildAudioManager for the guild
	 */
	public final GuildAudioManager getAudioManager () {
		return this.audiomanager.manager;
	}
	
	/** 
	 * Getter for the Infomations-channel in this guild.
	 * @return IChannel The informations-channel or null if not exists.
	 */
	public final IChannel getInformationsChannel () {
		if ( Settings.infoChannel != "" ) {
			List<IChannel> channels = guild.getChannelsByName( Settings.infoChannel );
			if ( !channels.isEmpty() ) {
				return channels.get( 0 );
			}
		}
		return null;
	}
	
	/**
	 * Getter for the ID of the roles-channel in this guild.
	 * @return LongID of the roles-channel or null if not exists.
	 */
	public final Long getRolesChannelID() {
		if ( Settings.rolesChannel != "" ) {
			final List<IChannel> channel = guild.getChannelsByName( Settings.rolesChannel );
			if ( !channel.isEmpty() ) {
				return channel.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	/**
	 * Getter for the ID of the audio-channel in this guild.
	 * @return LongID of the audio-channel or null if not exists.
	 */
	public final Long getAudioChannelID() {
		if ( Settings.audioChannel != "" ) {
			final List<IChannel> channel = guild.getChannelsByName( Settings.audioChannel );
			if ( !channel.isEmpty() ) {
				return channel.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	/**
	 * Getter for the ID of the audioinfo-channel in this guild.
	 * @return LongID of the audioinfo-channel or null if not exists.
	 */
	public final Long getAudioInfoChannelID() {
		if ( Settings.audioInfoChannel != "" ) {
			final List<IChannel> channel = guild.getChannelsByName( Settings.audioInfoChannel );
			if ( !channel.isEmpty() ) {
				return channel.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	private IChannel getChannel(String name) {
		if (name != "") {
			final List<IChannel> channels = guild.getChannelsByName(name);
			if ( !channels.isEmpty() ) {
				return channels.get( 0 );
			}
		}
		return null;
	}
	
	/**
	 * Getter for the channel where we want to greet new user in this guild.
	 * @return IChannel of the greet-user-channel or null if not exists.
	 */
	public final IChannel getGreetUserChannel() {
		return getChannel(Settings.greetUserChannel);
	}
	
	/**
	 * Getter for the channel where we want to log when a user leaves the channel
	 * @return IChannel
	 */
	public IChannel getUserLeaveLogChannel() {
		return getChannel(Settings.userLeaveLogChannel);
	}
	
	/**
	 * Getter for the channel where we want to log when a message gets edited
	 * @return IChannel
	 */
	public IChannel getMessageUpdateLogChannel() {
		return getChannel(Settings.messageUpdateLogChannel);
	}
	
	/**
	 * Getter for the channel where we want to log when a message gets deleted
	 * @return IChannel
	 */
	public IChannel getMessageDeleteLogChannel() {
		return getChannel(Settings.messageDeleteLogChannel);
	}
	
	/**
	 * Getter for the channel where we want to log when a user gets banned
	 * @return IChannel
	 */
	public IChannel getUserBanLogChannel() {
		return getChannel(Settings.userBanLogChannel);
	}
	
	/**
	 * Getter for the channel where we want to log when a user gets unbanned
	 * @return IChannel
	 */
	public IChannel getUserPardonLogChannel() {
		return getChannel(Settings.userPardonLogChannel);
	}
	
	/**
	 * Getter for the channel where the webhook is active.
	 * @return IChannel
	 */
	public IChannel getWebhookChannel() {
		return getChannel(Settings.webhookChannel);
	}
	
	/**
	 * Getter for the ID of the role who should be able to use audio-commands.
	 * @return LongID of the audioBotUser role or null if not exists
	 */
	public final Long getAudioBotUserRoleID() {
		if ( Settings.audiobotUserRole != "" ) {
			final List<IRole> roles = guild.getRolesByName( Settings.audiobotUserRole );
			if ( !roles.isEmpty() ) {
				return roles.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	/**
	 * Getter for the ID of the role for the english-section.
	 * @return LongID of the english-section role or null if not exists
	 */
	public final Long getEnglishRoleID() {
		if ( Settings.englishRole != "" ) {
			final List<IRole> roles = guild.getRolesByName( Settings.englishRole );
			if ( !roles.isEmpty() ) {
				return roles.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	/**
	 * Getter for the ID of the role for the german-section.
	 * @return LongID of the german-section role or null if not exists
	 */
	public final Long getGermanRoleID() {
		if ( Settings.germanRole != "" ) {
			final List<IRole> roles = guild.getRolesByName( Settings.germanRole );
			if ( !roles.isEmpty() ) {
				return roles.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	/**
	 * Getter for the ID of the role for the turkish-section.
	 * @return LongID of the turkish-section role or null if not exists
	 */
	public final Long getTurkishRoleID() {
		if ( Settings.turkishRole != "" ) {
			final List<IRole> roles = guild.getRolesByName( Settings.turkishRole );
			if ( !roles.isEmpty() ) {
				return roles.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	/**
	 * Getter for the ID of the role for the RocketLeague-section.
	 * @return LongID of the rocketleague-section role or null if not exists
	 */
	public final Long getRocketLeagueRoleID() {
		if ( Settings.rocketleagueRole != "" ) {
			final List<IRole> roles = guild.getRolesByName( Settings.rocketleagueRole );
			if ( !roles.isEmpty() ) {
				return roles.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	/**
	 * Getter for the ID of the role for the PUBG-section.
	 * @return LongID of the PUBG-section role or null if not exists
	 */
	public final Long getPUBGRoleID() {
		if ( Settings.pubgRole != "" ) {
			final List<IRole> roles = guild.getRolesByName( Settings.pubgRole );
			if ( !roles.isEmpty() ) {
				return roles.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	/**
	 * Getter for the ID of the what-emoji
	 * @return LongID of the what-emoji or null if not exists
	 */
	public final IEmoji getWhatEmoji() {
		if ( Settings.whatEmoji != "" ) {
			return guild.getEmojiByName( Settings.whatEmoji );
		}
		return null;
	}
	
	/**
	 * Getter for the ID of the haha-emoji
	 * @return LongID of the haha-emoji or null if not exists
	 */
	public final IEmoji getHahaEmoji() {
		if ( Settings.hahaEmoji != "" ) {
			return guild.getEmojiByName( Settings.hahaEmoji );
		}
		return null;
	}
	
	/** 
	 * Stops the stop-audio-timer.
	 * @return 
	 */
	public final void stopTheStopAudioTimer() {
		stopAudioTimer.cancel();
		stopAudioTimer.purge();
		stopAudioTimer = new Timer();
	}
	
	/** 
	 * Stops the pause/resume-audio-timer.
	 * @return 
	 */
	public final void stopThePauseResumeAudioTimer() {
		pauseresumeAudioTimer.cancel();
		pauseresumeAudioTimer.purge();
		pauseresumeAudioTimer = new Timer();
	}
	
	/**
	 * Get the user from a mention string
	 * @param mention The mention
	 * @return IUser The user
	 */
	public IUser getUserFromMention ( String mention ) {
		try {
			return guild.getUserByID( Long.parseUnsignedLong(mention.replaceAll("[^0-9]", "") ) );
		} catch ( Exception e ) {}
		return null;
	}
	
	/**
	 * Send the webhook info embed object to all guild
	 * @param obj
	 */
	public static void sendWebhookInfosToAllGuilds(List<EmbedObject> objs) {
		for (GuildExtends guildext : guildExtendsObjects.values()) {
			IChannel webhookchannel = guildext.getWebhookChannel();
			if (webhookchannel != null) {
				for (int i=0; i < objs.size(); ++i) {
					Util.sendMessage(webhookchannel, objs.get(i));
				}
			}
		}
	}

}
