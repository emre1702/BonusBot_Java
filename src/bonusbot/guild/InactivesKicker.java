package bonusbot.guild;

import java.util.Timer;
import java.util.TimerTask;

import sx.blah.discord.handle.obj.IGuild;

public class InactivesKicker {
	
	private IGuild theGuild;
	private Timer theTimer;
	private int kickAfterDays;
	
	public InactivesKicker(IGuild guild, int kickafterdays) {
		if (kickafterdays > 0) {
			kickAfterDays = kickafterdays;
			theGuild = guild;
			theTimer = new Timer();
			theTimer.schedule(new Task(), 1000, 24*60*60*1000);
		}
	}
	
	private class Task extends TimerTask {

		@Override
		public void run() {
			theGuild.pruneUsers(kickAfterDays);
		}
	}

}
