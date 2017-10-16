import java.util.List;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

class Rights {
	
	private static final long musicbotuserID = 355744179554615297l;
	
	static boolean canPlayMusic ( IUser user, IGuild guild ) {
		List<IRole> roles = user.getRolesForGuild( guild );
		if ( roles.contains( guild.getRoleByID( musicbotuserID ) ) )
			return true;
		else
			return false;
	}
}
