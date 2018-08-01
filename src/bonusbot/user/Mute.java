package bonusbot.user;

import java.util.Timer;
import java.util.TimerTask;

import bonusbot.Database;
import bonusbot.guild.GuildExtends;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class Mute {
	private static Timer muteUpdater;
	
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
