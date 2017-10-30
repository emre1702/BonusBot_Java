package bonusbot.commands;

import java.util.List;

import bonusbot.Logging;
import bonusbot.guild.GuildExtends;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * Language-commands.
 * Used to specify the language-section for the user.
 * That way e.g. german guys don't have to see the turkish section.
 * @author emre1702
 *
 */
public class Language {
	
	/**
	 * Create the language-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	static void createLanguageCommands () {
		
		final Command requestLanguageSectionRole = ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
			try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isLanguageChannel( event.getChannel().getLongID() ) ) {
					IRole role = null;
					switch ( cmd ) {
						case "deutsch":
						case "german":
							Long germanRoleID = guildext.getGermanRoleID();
							if ( germanRoleID != null )
								role = event.getGuild().getRoleByID( germanRoleID );
							break;
						case "türkce":
						case "turkish":
							Long turkishRoleID = guildext.getTurkishRoleID();
							if ( turkishRoleID != null )
								role = event.getGuild().getRoleByID( turkishRoleID );
							break;
						case "english":
							Long englishRoleID = guildext.getEnglishRoleID();
							if ( englishRoleID != null )
								role = event.getGuild().getRoleByID( englishRoleID );
							break;
					}
					if ( role != null )
						event.getAuthor().addRole( role );
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put( "deutsch", requestLanguageSectionRole );
		Handler.commandMap.put( "german", requestLanguageSectionRole );
		Handler.commandMap.put( "türkce", requestLanguageSectionRole );
		Handler.commandMap.put( "turkish", requestLanguageSectionRole );
		Handler.commandMap.put( "english", requestLanguageSectionRole );	
	}
}
