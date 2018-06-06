package bonusbot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IUser;
import webhook.BonusHttpServer;

/**
 * Main-method
 * @author emre1702
 *
 */
public class MainRunner {
	
	/**
	 * static void main
	 * @param args Console-args, not used
	 */
	public static void main ( final String[] args ) {
		try {
			Settings.loadSettings();
			if (Settings.webhookName.equals("") && Settings.httpServerPort != -1) {
				new BonusHttpServer(Settings.httpServerPort);
			}
			IDiscordClient client = Client.createClient(Settings.token, true);
			
			final EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new bonusbot.commands.Handler());
			dispatcher.registerListener( new EventsListener() );
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}
	
	public static boolean isBot(IUser user) {
		return Client.get().getOurUser().equals(user);
	}
}

// Invitation-Link://
// https://discordapp.com/api/oauth2/authorize?client_id=356578515472089089&scope=bot&permissions=70618192
// Infos: https://discordapp.com/developers/docs/topics/oauth2
// Permissions: https://discordapp.com/developers/docs/topics/permissions#permissions-bitwise-permission-flags