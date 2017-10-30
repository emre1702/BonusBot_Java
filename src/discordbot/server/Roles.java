package discordbot.server;
import java.util.List;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

/**
 * Server-roles informations
 * @author emre1702
 *
 */
public class Roles {
	
	public static long germanRoleID = -1;
	public static long englishRoleID = -1;
	public static long turkishRoleID = -1;
	public static long audiobotUserID = -1;
	
	/**
	 * Checks if the specific user in the guild has the right to play music.
	 * @param user The user which we want to check.
	 * @param guild The guild where the user wants to play music.
	 * @return If the user has the right to play music. Returns true if musicbotUserID is not defined in discordbot.conf.
	 */
	public final static boolean canPlayAudio ( IUser user, IGuild guild ) {
		if ( audiobotUserID != -1 ) {
			final List<IRole> roles = user.getRolesForGuild( guild );
			if ( roles.contains( guild.getRoleByID( audiobotUserID ) ) )
				return true;
			else
				return false;
		} else 
			return true;
	}
}