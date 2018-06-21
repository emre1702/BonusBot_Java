package bonusbot.commands;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import bonusbot.Lang;
import bonusbot.Logging;
import bonusbot.Util;
import bonusbot.guild.GuildExtends;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * Commands for fun-things.
 * 
 * @author emre1702
 *
 */
public class Fun {

	/**
	 * Create the music-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	static void createFunCommands() {

		ICommand askTheBot = (String cmd, MessageReceivedEvent event, List<String> args) -> {
			try {
				IChannel channel = event.getChannel();
				GuildExtends guildext = GuildExtends.get(event.getGuild());
				if (args.size() > 0) {
					int rnd = ThreadLocalRandom.current().nextInt(1, 12 + 1);

					if (rnd <= 3)
						Util.sendMessage(channel, Lang.getLang("yes", event.getAuthor(), event.getGuild()));
					else if (rnd <= 6)
						Util.sendMessage(channel, Lang.getLang("no", event.getAuthor(), event.getGuild()));
					else if (rnd == 7)
						Util.sendMessage(channel, Lang.getLang("not_sure", event.getAuthor(), event.getGuild()));
					else if (rnd == 8)
						Util.sendMessage(channel, Lang.getLang("maybe", event.getAuthor(), event.getGuild()));
					else if (rnd == 9)
						Util.sendMessage(channel, Lang.getLang("nah", event.getAuthor(), event.getGuild()));
					else if (rnd == 10)
						Util.sendMessage(channel, Lang.getLang("absolutely", event.getAuthor(), event.getGuild()));
					else if (rnd == 11) {
						IEmoji hahaemoji = guildext.getEmoji("what");
						if (hahaemoji != null) {
							Util.sendMessage(channel,
									Lang.getLang("stupid_question", event.getAuthor(), event.getGuild())
											+ Util.getEmojiString(hahaemoji));
							event.getMessage().addReaction(ReactionEmoji.of(hahaemoji));
						} else
							Util.sendMessage(channel,
									Lang.getLang("stupid_question", event.getAuthor(), event.getGuild()));

					} else
						Util.sendMessage(channel, Lang.getLang("ask_again", event.getAuthor(), event.getGuild()));
				} else {
					IEmoji whatemoji = guildext.getEmoji("what");
					if (whatemoji != null) {
						Util.sendMessage(channel, Lang.getLang("what_is_question", event.getAuthor(), event.getGuild())
								+ Util.getEmojiString(whatemoji));
						event.getMessage().addReaction(ReactionEmoji.of(whatemoji));
					} else
						Util.sendMessage(channel,
								Lang.getLang("what_is_question", event.getAuthor(), event.getGuild()));

				}
			} catch (Exception e) {
				e.printStackTrace(Logging.getPrintWrite());
			}
		};
		Handler.commandMap.put("8ball", askTheBot);
		Handler.commandMap.put("ask", askTheBot);
	}
}
