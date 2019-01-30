package bonusbot.user;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bonusbot.Database;
import bonusbot.guild.GuildExtends;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class Mute {
	private static Timer muteUpdater;
	
	private static void changeUserRolesToUnmuted(IUser user, IGuild guild) {
		List<IRole> mutedroles = user.getRolesForGuild(guild);
		for (IRole role : mutedroles) {
			String rolename = role.getName();
			if (!rolename.endsWith("Muted"))
				continue;
			List<IRole> normalroles = guild.getRolesByName(rolename.substring(0, rolename.length() - 5));
			if (!normalroles.isEmpty()) {
				IRole normalrole = normalroles.get(0);
				user.removeRole(role);
				user.addRole(normalrole);
			}
		}
	}
	
	private static void changeUserRolesToMuted(IUser user, IGuild guild) {
		List<IRole> roles = user.getRolesForGuild(guild);
		for (IRole role : roles) {
			String rolename = role.getName();
			if (rolename.endsWith("Muted"))
				continue;
			List<IRole> mutedroles = guild.getRolesByName(rolename + "Muted");
			if (!mutedroles.isEmpty()) {
				IRole mutedrole = mutedroles.get(0);
				user.removeRole(role);
				user.addRole(mutedrole);
			}
		}
	}
	
	public static void setUserMute(IUser user, GuildExtends guildext, long mutetime, boolean saveindb) {
		IRole muterole = guildext.getRole("muteRole");
		if (muterole != null) {
			boolean userhasrole = user.hasRole(muterole);
			if (mutetime != 0 && !userhasrole) {
				user.addRole(muterole);
				changeUserRolesToMuted(user, guildext.getGuild());
			} else if (userhasrole) {
				user.removeRole(muterole);
				changeUserRolesToUnmuted(user, guildext.getGuild());
			}
		}
		if (saveindb) {
			Database.saveUserMute(user, guildext.getGuild(), mutetime);
		}
	}
	
	public static void startMuteUpdater() {
		muteUpdater = new Timer();
		muteUpdater.schedule(new MuteUpdateTask(), 1000, 60*1000);
	}
	
	private static class MuteUpdateTask extends TimerTask {

		@Override
		public void run() {
			Database.updateMutes();
		}
	}
}
