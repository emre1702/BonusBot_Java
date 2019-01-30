package bonusbot.commands;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import bonusbot.Client;
import bonusbot.Lang;
import bonusbot.Settings;
import bonusbot.Util;
import bonusbot.guild.GuildExtends;
import bonusbot.user.Mute;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

/**
 * Commands for admins
 * 
 * @author emre1702
 */
public class Admin {
	
	private static void deleteMessage(IMessage message) {
		Util.sendMessage(message.getGuild(), "DELETING message!");
		RequestBuffer.request(() -> {
			message.delete();
		});
	}
	
	/**
	 * Delete all messages from a user in all channels.
	 * 
	 * @param guild
	 *            The guild where the messages should get deleted.
	 * @param user
	 *            The user which messages should get deleted.
	 * @return Amount of deleted messages.
	 */
	private static int deleteAllMessages(IGuild guild, IUser user, RequestBuilder reqbuilder) {
		int amountdeleted = 0;
		for (IChannel channel : guild.getChannels()) {
			amountdeleted += deleteAllMessages(channel, user, reqbuilder);
		}
		return amountdeleted;
	}
	
	/**
	 * Delete all messages from a user in a channel.
	 * 
	 * @param channel
	 *            The channel where the message should get deleted.
	 * @param user
	 *            The user which messages should get deleted.
	 * @return Amount of deleted messages.
	 */
	private static int deleteAllMessages(IChannel channel, IUser user, RequestBuilder reqbuilder) {
		try {
			int[] amountdeleted = new int[1];
			amountdeleted[0] = 0;
			MessageHistory msghist = channel.getFullMessageHistory();
			msghist.stream().filter(message -> message.getAuthor().equals(user)).forEach(message -> {
				reqbuilder.andThen(() -> { message.delete(); return true; });
				++amountdeleted[0];
			});
			return amountdeleted[0];
		} catch (Exception ex) {
			return 0;
		}
	}
	
	/**
	 * Delete last messages in a channel.
	 * 
	 * @param channel
	 *            The channel where the message should get deleted.
	 * @param amount
	 *            Amount of messages which should get deleted.
	 * @return Amount of deleted messages.
	 */
	private static int deleteLastMessages(IChannel channel, int amount) {
		MessageHistory msghist = channel.getMessageHistory();
		int[] size = new int[1];
		size[0] = 0;
		msghist.parallelStream().limit(amount).forEach(message -> {
			deleteMessage(message);
			++size[0];
		});
		return size[0];
	}

	/**
	 * Delete last messages from a user in a channel.
	 * 
	 * @param channel
	 *            The channel where the message should get deleted.
	 * @param user
	 *            The user which messages should get deleted.
	 * @param amount
	 *            Amount of messages which should get deleted.
	 * @return Amount of deleted messages.
	 */
	private static int deleteLastMessages(IChannel channel, IUser user, int amount) {
		int[] amountdeleted = new int[1];
		amountdeleted[0] = 0;
		MessageHistory history = channel.getMessageHistory();
		history.parallelStream().filter(message -> message.getAuthor().equals(user)).limit(amount).forEach(message -> {
			if (amountdeleted[0] < amount) {
				deleteMessage(message);
				++amountdeleted[0];
			}
		});
		return amountdeleted[0];
	}
		
	/**
	 * Delete last messages from a user in a channel.
	 * 
	 * @param channel
	 *            The channel where the message should get deleted.
	 * @param user
	 *            The user which messages should get deleted.
	 * @return Amount of deleted messages.
	 */
	private static int deleteLastMessages(IChannel channel, IUser user) {
		int[] amountdeleted = new int[1];
		amountdeleted[0] = 0;
		MessageHistory history = channel.getMessageHistory();
		history.parallelStream().filter(message -> message.getAuthor().equals(user)).forEach(message -> {
			deleteMessage(message);
			++amountdeleted[0];
		});
		return amountdeleted[0];
	}
	
	/**
	 * Create the admins-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	static void createAdminCommands() {

		/** Delete last messages (of a user) in the channel */
		ICommand deleteLastMessagesOfUser = (String cmd, MessageReceivedEvent event,
				List<String> args) -> {
			try {
				GuildExtends guildext = GuildExtends.get(event.getGuild());
				if (guildext.isAdmin(event.getAuthor())) {
					IGuild guild = event.getGuild();
					IChannel channel = event.getChannel();
					IUser author = event.getAuthor();
					RequestBuilder reqbuilder = new RequestBuilder(Client.get());
					reqbuilder.setAsync(true);
					reqbuilder.shouldBufferRequests(true);
					reqbuilder.doAction(() -> { return true; });  // is that needed?
					if (args.size() > 0) {
						int deleteamount = 100;
						if (args.size() > 1) {
							if (StringUtils.isNumeric(args.get(1)))
								deleteamount = Integer.parseInt(args.get(1));
							List<IUser> users = event.getMessage().getMentions();
							if (!users.isEmpty()) {
								IUser user = users.get(0);
								if (user != null) {
									int amountdeleted;
									if (cmd.equals("delmsgall")) {
										amountdeleted = deleteAllMessages(guild, user, reqbuilder);
										reqbuilder.andThen(() -> {
											Util.sendMessage(channel,
													amountdeleted + " " + Lang.getLang("got_deleted", author, guild));
											return true;
										});
										reqbuilder.execute();
									} else {
										amountdeleted = deleteLastMessages(channel, user, deleteamount);
										Util.sendMessage(channel,
												amountdeleted + " " + Lang.getLang("got_deleted", author, guild));
									}
								}
								
							} else
								Util.sendMessage(channel, Lang.getLang("user_not_found", event.getAuthor(), guild));
						} else if (StringUtils.isNumeric(args.get(0))) {
							deleteamount = Integer.parseInt(args.get(0));
							int amountdeleted = deleteLastMessages(channel, deleteamount);
							Util.sendMessage(channel, amountdeleted + " " + Lang.getLang("got_deleted", author, guild));
						} else {
							List<IUser> users = event.getMessage().getMentions();
							if (!users.isEmpty()) {
								IUser user = users.get(0);
								int amountdeleted;
								if (cmd.equals("delmsgall")) {
									amountdeleted = deleteAllMessages(guild, user, reqbuilder);
									reqbuilder.andThen(() -> {
										Util.sendMessage(channel, amountdeleted + " " + Lang.getLang("got_deleted", author, guild));
										return true;
									});
									reqbuilder.execute();
								} else {
									amountdeleted = deleteLastMessages(channel, user);
									Util.sendMessage(channel, amountdeleted + " " + Lang.getLang("got_deleted", author, guild));
								}	
							}
						}
					} else {
						Util.sendMessage(channel,
								Lang.getLang("usage", author, guild) + ": " + Settings.get("prefix") + "delete " + " [@"
										+ Lang.getLang("user", author, guild) + "]" + " ["
										+ Lang.getLang("amount_messages", author, guild) + " = 100]");
					}
				}
			} catch (Exception e) {
				LogManager.getLogger().error(e);
			}
		};
		Handler.commandMap.put("delmsg", deleteLastMessagesOfUser);
		Handler.commandMap.put("delmsgall", deleteLastMessagesOfUser);

		ICommand banUser = (String cmd, MessageReceivedEvent event, List<String> args) -> {
			GuildExtends guildext = GuildExtends.get(event.getGuild());
			if (guildext.isAdmin(event.getAuthor())) {
				IUser author = event.getAuthor();
				IGuild guild = event.getGuild();
				IChannel channel = event.getChannel();
				if (args.size() > 1) {
					List<IUser> users = event.getMessage().getMentions();
					if (!users.isEmpty()) {
						IUser user = users.get(0);
						int deletemessagesfordays = 0;
						int reasonstartindex = 2;
						if (StringUtils.isNumeric(args.get(1))) {
							deletemessagesfordays = Integer.parseInt(args.get(1));
						} else {
							--reasonstartindex;
						}
						String reason = args.get(reasonstartindex);
						for (int i = reasonstartindex+1; i < args.size(); ++i) {
							reason += " " + args.get(i);
						}
						String adminname = Util.getUniqueName(author);
						user.getOrCreatePMChannel().sendMessage(
								"You got banned by " + adminname + " from " + guild.getName() + ". Reason: " + reason);
						guild.banUser(user, adminname + " - " + reason, deletemessagesfordays);
					} else
						Util.sendMessage(channel, Lang.getLang("user_not_found", event.getAuthor(), guild));
				} else {
					Util.sendMessage(channel,
							Lang.getLang("usage", author, guild) + ": " + Settings.get("prefix") + "ban " + " [@"
									+ Lang.getLang("user", author, guild) + "]" + " ["
									+ Lang.getLang("del_messages_for_days", author, guild) + " = 0]" + " ["
									+ Lang.getLang("reason", author, guild)+"]");
				}
			}
		};
		Handler.commandMap.put("ban", banUser);
		
		ICommand muteUser = (String cmd, MessageReceivedEvent event, List<String> args) -> {
			IGuild guild = event.getGuild();
			GuildExtends guildext = GuildExtends.get(guild);
			IUser author = event.getAuthor();
			if (guildext.isAdmin(author)) {
				IChannel channel = event.getChannel();
				if (args.size() > 2) {
					List<IUser> users = event.getMessage().getMentions();
					if (!users.isEmpty()) {
						IUser user = users.get(0);
						if (StringUtils.isNumeric(args.get(1))) {
							long mutetime = Long.parseLong(args.get(1));
							String adminname = Util.getUniqueName(author);
							String reason = args.get(2);
							for (int i = 3; i < args.size(); ++i) {
								reason += " " + args.get(i); 
							}
							if (mutetime == -1) {
								user.getOrCreatePMChannel().sendMessage(
										"You got permanently muted by " + adminname + " in " + guild.getName() + ". Reason: " + reason);
							} else if (mutetime == 0) {
								user.getOrCreatePMChannel().sendMessage(
										"You got unmuted by " + adminname + " in " + guild.getName() + ". Reason: " + reason);
							} else {
								user.getOrCreatePMChannel().sendMessage(
										"You got muted by " + adminname + " in " + guild.getName() + " for "+mutetime+" minutes. Reason: " + reason);
							}
							Mute.setUserMute(user, guildext, mutetime, true);
						} else 
							Util.sendMessage(channel,
									Lang.getLang("usage", author, guild) + ": " + Settings.get("prefix") + "mute " + " [@"
											+ Lang.getLang("user", author, guild) + "]" + " ["
											+ Lang.getLang("minutes", author, guild) + "]" + " ["
											+ Lang.getLang("reason", author, guild) + "]");
					} else
						Util.sendMessage(channel, Lang.getLang("user_not_found", event.getAuthor(), guild));
				} else
					Util.sendMessage(channel,
							Lang.getLang("usage", author, guild) + ": " + Settings.get("prefix") + "mute " + " [@"
									+ Lang.getLang("user", author, guild) + "]" + " ["
									+ Lang.getLang("minutes", author, guild) + "]" + " ["
									+ Lang.getLang("reason", author, guild) + "]");
			}
		};
		Handler.commandMap.put("mute", muteUser);
	}

}
