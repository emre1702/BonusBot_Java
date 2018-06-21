package bonusbot.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import bonusbot.Lang;
import bonusbot.Logging;
import bonusbot.Settings;
import bonusbot.Util;
import bonusbot.guild.GuildExtends;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;

/**
 * Commands for admins
 * 
 * @author emre1702
 */
public class Admin {

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
		MessageHistory history = channel.getMessageHistory(amount);
		history.bulkDelete();
		return history.size();

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
		int amountdeleted = 0;
		List<IMessage> messagelist = new ArrayList<IMessage>();
		MessageHistory history = channel.getMessageHistory(amount > 400 ? amount : 400);
		Iterator<IMessage> messages = history.iterator();
		while (messages.hasNext() && amountdeleted < amount) {
			IMessage message = messages.next();
			if (message.getAuthor() == user) {
				messagelist.add(message);
				++amountdeleted;
			}
		}
		channel.bulkDelete(messagelist);
		return amountdeleted;
	}

	/**
	 * Create the admins-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	static void createAdminCommands() {

		/** Delete last messages (of a user) in the channel */
		ICommand deleteLastMessagesOfUserInChannel = (String cmd, MessageReceivedEvent event,
				List<String> args) -> {
			try {
				GuildExtends guildext = GuildExtends.get(event.getGuild());
				if (guildext.isAdmin(event.getAuthor())) {
					IGuild guild = event.getGuild();
					IChannel channel = event.getChannel();
					IUser author = event.getAuthor();
					if (args.size() > 0) {
						int deleteamount = 100;
						if (args.size() > 1) {
							if (StringUtils.isNumeric(args.get(1)))
								deleteamount = Integer.parseInt(args.get(1));
							IUser user = guildext.getUserFromMention(args.get(0));
							if (user != null) {
								int amountdeleted = deleteLastMessages(channel, user, deleteamount);
								Util.sendMessage(channel,
										amountdeleted + " " + Lang.getLang("got_deleted", author, guild));
							} else
								Util.sendMessage(channel, Lang.getLang("user_not_found", event.getAuthor(), guild));
						} else {
							if (StringUtils.isNumeric(args.get(0)))
								deleteamount = Integer.parseInt(args.get(0));
							int amountdeleted = deleteLastMessages(channel, deleteamount);
							Util.sendMessage(channel, amountdeleted + " " + Lang.getLang("got_deleted", author, guild));
						}
					} else {
						Util.sendMessage(channel,
								Lang.getLang("usage", author, guild) + ": " + Settings.get("prefix") + "delete " + " [@"
										+ Lang.getLang("user", author, guild) + "]" + " ["
										+ Lang.getLang("amount_messages", author, guild) + " = 100]");
					}
				}
			} catch (Exception e) {
				e.printStackTrace(Logging.getPrintWrite());
			}
		};
		Handler.commandMap.put("delmsg", deleteLastMessagesOfUserInChannel);

		ICommand banUser = (String cmd, MessageReceivedEvent event, List<String> args) -> {
			GuildExtends guildext = GuildExtends.get(event.getGuild());
			if (guildext.isAdmin(event.getAuthor())) {
				IUser author = event.getAuthor();
				IGuild guild = event.getGuild();
				IChannel channel = event.getChannel();
				if (args.size() > 1) {
					int deletemessagesfordays = 0;
					if (args.size() > 2 && StringUtils.isNumeric(args.get(2))) {
						deletemessagesfordays = Integer.parseInt(args.get(2));
					}
					IUser user = guildext.getUserFromMention(args.get(0));
					if (user != null) {
						String reason = args.get(1);
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
									+ Lang.getLang("reason", author, guild) + "]" + " ["
									+ Lang.getLang("del_messages_for_days", author, guild) + " = 0]");
				}
			}
		};
		Handler.commandMap.put("ban", banUser);
	}

}
