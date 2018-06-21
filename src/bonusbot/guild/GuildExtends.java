package bonusbot.guild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;

import bonusbot.Audio;
import bonusbot.Logging;
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
 * 
 * @author emre1702
 *
 */
public class GuildExtends {

	/** The object for the specific guild(ID) */
	private static Map<IGuild, GuildExtends> guildExtendsObjects = new HashMap<>();
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
	 * Constructor Also creates AudioManager for the guild.
	 * 
	 * @param guild
	 *            The guild.
	 */
	public GuildExtends(IGuild guild) {
		guildExtendsObjects.put(guild, this);
		this.audiomanager = new AudioManager(guild, Audio.getPlayerManager());
		this.guild = guild;
	}

	/**
	 * Get the object for the specific guild.
	 * 
	 * @param guild
	 *            The guild whose object we want to retrieve.
	 * @return The GuildExtends object for the guild.
	 */
	public synchronized static GuildExtends get(IGuild guild) {
		GuildExtends guildext = guildExtendsObjects.get(guild);

		if (guildext == null) {
			guildext = new GuildExtends(guild);
		}

		return guildext;
	}

	/**
	 * Getter for the IGuild of a GuildExtends.
	 * 
	 * @return
	 */
	public IGuild getGuild() {
		return guild;
	}

	/**
	 * Check if the channel is the audio-channel.
	 * 
	 * @param channel
	 * @return
	 */
	public boolean isAudioChannel(IChannel channel) {
		return channel.equals(getChannel("audioChannel"));
	}

	/**
	 * Check if the channel is the roles-channel.
	 * 
	 * @param channel
	 * @return
	 */
	public boolean isRolesChannel(IChannel channel) {
		return channel.equals(getChannel("audioChannel"));
	}

	/**
	 * Check if the user got any of the admin roles. Needed for the admin-commands.
	 * 
	 * @param user
	 *            User to check.
	 * @return if the user got an admin role.
	 */
	public boolean isAdmin(IUser user) {
		String[] adminrolenames = Settings.get("admins");
		if (adminrolenames != null) {
			List<IRole> roles = guild.getRolesForUser(user);
			for (String adminrolename : adminrolenames) {
				List<IRole> adminroles = guild.getRolesByName(adminrolename);
				if (adminroles.size() > 0 && roles.contains(adminroles.get(0)))
					return true;
			}
		}
		return false;
	}

	/**
	 * Check if the user has the role to play audio or everyone can play it
	 * (audioBotUserRoleID is not set)
	 * 
	 * @param user
	 *            The user we want to check.
	 * @return If user can use audio-commands.
	 */
	public boolean canPlayAudio(IUser user) {
		IRole audiorole = this.getRole("audiobotUserRole");
		if (audiorole == null)
			return true;
		return user.hasRole(audiorole);
	}

	/**
	 * Getter for the GuildAudioManager for the guild.
	 * 
	 * @return GuildAudioManager for the guild
	 */
	public GuildAudioManager getAudioManager() {
		return this.audiomanager.manager;
	}

	/**
	 * Getter for a channel for the guild. Possible keys are in bonusbot.conf (e.g.
	 * languageChannel).
	 * 
	 * @param key
	 *            Channel key (written in bonusbot.conf)
	 * @return
	 */
	public IChannel getChannel(String key) {
		String channelname = Settings.get(key);
		if (channelname != null) {
			List<IChannel> channels = guild.getChannelsByName(channelname);
			if (!channels.isEmpty()) {
				return channels.get(0);
			}
		}
		return null;
	}

	/**
	 * Getter for a role for the guild. Possible keys are in bonusbot.conf (e.g.
	 * englishRole).
	 * 
	 * @param key
	 *            Role key (written in bonusbot.conf)
	 * @return
	 */
	public IRole getRole(String key) {
		String rolename = Settings.get(key);
		if (rolename != null) {
			List<IRole> roles = guild.getRolesByName(rolename);
			if (!roles.isEmpty()) {
				return roles.get(0);
			}
		}
		return null;
	}

	/**
	 * Getter for a emoji for the guild. Possible keys are in bonusbot.conf (e.g.
	 * whatEmoji).
	 * 
	 * @param key
	 *            Emoji key (written in bonusbot.conf)
	 * @return
	 */
	public IEmoji getEmoji(String key) {
		String emojiname = Settings.get(key);
		if (emojiname != null) {
			return guild.getEmojiByName(emojiname);
		}
		return null;
	}

	/**
	 * Stops the stop-audio-timer.
	 * 
	 * @return
	 */
	public void stopTheStopAudioTimer() {
		stopAudioTimer.cancel();
		stopAudioTimer.purge();
		stopAudioTimer = new Timer();
	}

	/**
	 * Stops the pause/resume-audio-timer.
	 * 
	 * @return
	 */
	public void stopThePauseResumeAudioTimer() {
		pauseresumeAudioTimer.cancel();
		pauseresumeAudioTimer.purge();
		pauseresumeAudioTimer = new Timer();
	}

	/**
	 * Get the user from a mention string
	 * 
	 * @param mention
	 *            The mention
	 * @return IUser The user
	 */
	public IUser getUserFromMention(String mention) {
		try {
			return guild.getUserByID(Long.parseUnsignedLong(mention.replaceAll("[^0-9]", "")));
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
		return null;
	}

	/**
	 * Send the webhook info embed object to all guild
	 * 
	 * @param obj
	 */
	public static void sendWebhookInfosToAllGuilds(List<EmbedObject> objs) {
		for (GuildExtends guildext : guildExtendsObjects.values()) {
			IChannel webhookchannel = guildext.getChannel("webhookChannel");
			if (webhookchannel != null) {
				for (int i = 0; i < objs.size(); ++i) {
					Util.sendMessage(webhookchannel, objs.get(i));
				}
			}
		}
	}

}
