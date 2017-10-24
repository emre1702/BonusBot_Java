import java.util.List;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

class Roles {
	
	static long germanRoleID = -1;
	static long englishRoleID = -1;
	static long turkishRoleID = -1;
	static long musicbotUserID = -1;
	
	/**
	 * Checks if the specific user in the guild has the right to play music.
	 * @param user The user which we want to check.
	 * @param guild The guild where the user wants to play music.
	 * @return If the user has the right to play music. Returns true if musicbotUserID is not defined in discordbot.conf.
	 */
	static boolean canPlayMusic ( IUser user, IGuild guild ) {
		if ( musicbotUserID != -1 ) {
			List<IRole> roles = user.getRolesForGuild( guild );
			if ( roles.contains( guild.getRoleByID( musicbotUserID ) ) )
				return true;
			else
				return false;
		} else 
			return true;
	}
}