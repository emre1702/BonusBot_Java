package bonusbot.commands;

import java.util.List;

import org.apache.logging.log4j.LogManager;

import bonusbot.guild.GuildExtends;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * Language-commands. Used to specify the language-section for the user. That
 * way e.g. german guys don't have to see the turkish section.
 * 
 * @author emre1702
 *
 */
public class Language {

	/**
	 * Create the language-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	static void createLanguageCommands() {

		/**
		 * Request language-section role. Deactivated when languageChannel doesn't
		 * exist/isn't set.
		 */
		ICommand requestLanguageSectionRole = (String cmd, MessageReceivedEvent event, List<String> args) -> {
			try {
				GuildExtends guildext = GuildExtends.get(event.getGuild());
				if (guildext.isRolesChannel(event.getChannel())) {
					IRole role = null;
					IUser user = event.getAuthor();
					switch (cmd) {
					case "deutsch":
					case "german":
						IRole germanRole = guildext.getLanguageRole("germanRole", user);
						if (germanRole != null) {
							role = germanRole;
						}
						break;
					case "türkce":
					case "turkish":
						IRole turkishRole = guildext.getLanguageRole("turkishRole", user);
						if (turkishRole != null) {
							role = turkishRole;
						}
						break;
					case "english":
						IRole englishRole = guildext.getLanguageRole("englishRole", user);
						if (englishRole != null) {
							role = englishRole;
						}
						break;
					}
					if (role != null)
						user.addRole(role);
				}
			} catch (Exception e) {
				LogManager.getLogger().error(e);
			}
		};
		Handler.commandMap.put("deutsch", requestLanguageSectionRole);
		Handler.commandMap.put("german", requestLanguageSectionRole);
		Handler.commandMap.put("türkce", requestLanguageSectionRole);
		Handler.commandMap.put("turkish", requestLanguageSectionRole);
		Handler.commandMap.put("english", requestLanguageSectionRole);
	}
}
