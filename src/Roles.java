import java.util.List;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

class Roles {
	
	static long germanRoleID;
	static long englishRoleID;
	static long turkishRoleID;
	static long musicbotUserID;
	
	static boolean canPlayMusic ( IUser user, IGuild guild ) {
		List<IRole> roles = user.getRolesForGuild( guild );
		if ( roles.contains( guild.getRoleByID( musicbotUserID ) ) )
			return true;
		else
			return false;
	}
}