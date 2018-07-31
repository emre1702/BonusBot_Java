package bonusbot;

import java.time.LocalDateTime;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

/**
 * Some important and useful stuff.
 * 
 * @author emre1702
 *
 */
public class Util {

	/**
	 * Get LocalDateTime now for Europe/Paris timezone.
	 * 
	 * @return LocalDateTime.now for Europe/Paris timezone.
	 */
	public static LocalDateTime getLocalDateTime() {
		return LocalDateTime.now(ZoneId.of("Europe/Paris"));
	}

	/**
	 * Get custom timestamp for now.
	 * 
	 * @return timestamp at current time.
	 */
	static String getTimestamp() {
		return getLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd.MM.yyyy"));
	}

	/**
	 * Get custom timestamp to use in the embed in Discord. Discord only accepts
	 * ISO-8601 format.
	 * 
	 * @return timestamp at current time for embed.
	 */
	static String getTimestampForDiscord() {
		return LocalDateTime.now(ZoneId.of("Z")).toString();
	}

	/**
	 * Get custom timestamp for a specific LocalDateTime.
	 * 
	 * @param datetime
	 *            The LocalDateTime we want to get the timestamp of.
	 * @return timestamp at the time of the LocalDateTime.
	 */
	static String getTimestamp(LocalDateTime datetime) {
		return datetime.format(DateTimeFormatter.ofPattern("HH:mm:ss - dd.MM.yyyy")).toString();
	}

	/**
	 * Send a message to the specific channel. Uses RequestBuffer.
	 * 
	 * @param channel
	 *            The channel where we want to have the message.
	 * @param message
	 *            The message we want to send.
	 */
	public static void sendMessage(IChannel channel, String message) {
		RequestBuffer.request(() -> {
			try {
				channel.sendMessage(message);
			} catch (DiscordException e) {
				LogManager.getLogger().error(e);
			}
		});
	}

	/**
	 * Edits a message with RequestBuffer.
	 * 
	 * @param msg
	 *            The message to edit.
	 * @param obj
	 *            The object to insert into the message.
	 */
	public static void editMessage(IMessage msg, EmbedObject obj) {
		RequestBuffer.request(() -> {
			try {
				msg.edit(obj);
			} catch (DiscordException e) {
				LogManager.getLogger().error(e);
			}
		});
	}

	/**
	 * Send a EmbedObject to the specific channel. Uses RequestBuffer.
	 * 
	 * @param channel
	 *            The channel where we want to have the EmbedObject.
	 * @param object
	 *            The EmbedObject we want to send.
	 */
	public static void sendMessage(IChannel channel, EmbedObject object) {
		try {
			// This might look weird but it'll be explained in another page.
			RequestBuffer.request(() -> {
				try {
					channel.sendMessage(object);
				} catch (DiscordException e) {
					LogManager.getLogger().error(e);
				}
			});
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}

	/**
	 * Send a message to the guilds bot message channel of the guild.
	 * 
	 * @param guild
	 *            The guild.
	 * @param message
	 *            The message.
	 */
	public static void sendMessage(IGuild guild, String message) {
		try {
			String guildsBotMessageChannelName = Settings.get("guildsBotMessageChannel");
			if (guildsBotMessageChannelName != null) {
				// This might look weird but it'll be explained in another page.
				List<IChannel> channels = guild.getChannelsByName(guildsBotMessageChannelName);
				if (!channels.isEmpty()) {
					RequestBuffer.request(() -> {
						try {
							channels.get(0).sendMessage(message);
						} catch (DiscordException e) {
							LogManager.getLogger().error(e);
						}
					});
				}
			}
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}

	/**
	 * Send a object to the guilds bot message channel of the guild.
	 * 
	 * @param guild
	 *            The guild.
	 * @param object
	 *            The object.
	 */
	public static void sendMessage(IGuild guild, EmbedObject object) {
		try {
			String guildsBotMessageChannelName = Settings.get("guildsBotMessageChannel");
			if (guildsBotMessageChannelName != null) {
				List<IChannel> channels = guild.getChannelsByName(guildsBotMessageChannelName);
				if (!channels.isEmpty()) {
					RequestBuffer.request(() -> {
						try {
							channels.get(0).sendMessage(object);
						} catch (DiscordException e) {
							LogManager.getLogger().error(e);
						}
					});
				}
			}
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}

	/**
	 * Returns the first non-null value. Equivalent to ?? from C#.
	 * 
	 * @param one
	 *            First one to check
	 * @param two
	 *            Second one to check
	 * @param <T>
	 *            a type
	 * @return The first value of both not being null.
	 */
	public static <T> T firstNonNull(T one, T two) {
		return one != null ? one : two;
	}

	/**
	 * Get string to use in the discord-message by the emoji.
	 * 
	 * @param emoji
	 *            Emoji whose string we want to retrieve.
	 * @return String to use in Discord
	 */
	public static String getEmojiString(IEmoji emoji) {
		return "<:" + emoji.getName() + ":" + emoji.getStringID() + ">";
	}

	/**
	 * Checks if a string is numeric.
	 * 
	 * @param str
	 *            The string to check.
	 * @return if the string is numeric
	 */
	public static boolean isNumeric(String str) {
		return str.matches("[-+]?\\d*\\.?\\d+");
	}

	/**
	 * Get a users unique name (Name#Discriminator)
	 * 
	 * @param user
	 *            The user
	 * @return unique name of the user
	 */
	public static String getUniqueName(IUser user) {
		return String.format("%s#%s", user.getName(), user.getDiscriminator());
	}

	/**
	 * Get a random integer number
	 * 
	 * @param min min. integer
	 * @param max max. integer
	 * @return random number between min and max
	 */
	public static int random(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}

}
