package bonusbot.user;

import bonusbot.Database;
import bonusbot.Settings;
import bonusbot.guild.GuildExtends;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class Mute {
	
	public static void setUserMute(IUser user, GuildExtends guildext, long mutetime, boolean saveindb) {
		IRole muterole = guildext.getRole("muteRole");
		if (muterole != null) {
			boolean userhasrole = user.hasRole(muterole);
			if (mutetime != 0 && !userhasrole) {
				user.addRole(muterole);
			} else if (userhasrole) {
				user.removeRole(muterole);
			}
		}
		if (saveindb) {
			Database.saveUserMute(user, guildext.getGuild(), mutetime);
		}
	}
}
