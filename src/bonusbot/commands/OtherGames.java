package bonusbot.commands;

import java.util.List;

import bonusbot.Logging;
import bonusbot.guild.GuildExtends;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * Other-games-roles-commands.
 * Used to specify the games-section for the user.
 * That way e.g. guys don't have to see the PUBG section when they don't play it.
 * @author emre1702
 *
 */
public class OtherGames {
	
	/**
	 * Create the language-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	static void createOtherGamesCommands () {
		
		/** 
		 * Request language-section role.
		 * Deactivated when languageChannel doesn't exist/isn't set.
		 */
		final ICommand requestOtherGamesSectionRole = ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
			try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isRolesChannel( event.getChannel().getLongID() ) ) {
					IRole role = null;
					switch ( cmd ) {
						case "pubg":
							Long pubgRoleID = guildext.getPUBGRoleID();
							if ( pubgRoleID != null )
								role = event.getGuild().getRoleByID( pubgRoleID );
							break;
						case "rocketleague":
							Long rocketleagueRoleID = guildext.getRocketLeagueRoleID();
							if ( rocketleagueRoleID != null )
								role = event.getGuild().getRoleByID( rocketleagueRoleID );
							break;
					}
					if ( role != null )
						event.getAuthor().addRole( role );
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put( "pubg", requestOtherGamesSectionRole );
		Handler.commandMap.put( "rocketleague", requestOtherGamesSectionRole );
	}
}
