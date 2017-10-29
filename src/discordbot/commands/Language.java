package discordbot.commands;

import discordbot.Logging;
import discordbot.server.Channels;
import discordbot.server.Roles;
import sx.blah.discord.handle.obj.IRole;

public class Language {
	
	static {
		Command requestLanguageSectionRole = ( cmd, event, args ) -> {
			try {
				if ( Channels.isLanguageChannel( event.getChannel().getLongID() ) ) {
					IRole role = null;
					switch ( cmd ) {
						case "deutsch":
							if ( Roles.germanRoleID != -1 )
								role = event.getGuild().getRoleByID( Roles.germanRoleID );
							break;
						case "german":
							if ( Roles.germanRoleID != -1 )
								role = event.getGuild().getRoleByID( Roles.germanRoleID );
							break;
						case "türkce":
							if ( Roles.turkishRoleID != -1 )
								role = event.getGuild().getRoleByID( Roles.turkishRoleID );
							break;
						case "turkish":
							if ( Roles.turkishRoleID != -1 )
								role = event.getGuild().getRoleByID( Roles.turkishRoleID );
							break;
						case "english":
							if ( Roles.englishRoleID != -1 )
								role = event.getGuild().getRoleByID( Roles.englishRoleID );
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
