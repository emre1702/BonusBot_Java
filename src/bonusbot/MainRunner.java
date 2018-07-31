package bonusbot;

import java.sql.Connection;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import webhook.BonusHttpServer;

/**
 * Main-method
 * 
 * @author emre1702
 *
 */
public class MainRunner {
	
	private static void tryDatabase() {
		try (Connection conn = Database.get()) {
			if (conn != null) {
				System.out.println("[INFO] Connecting to database works!");
			}
		} catch (Exception e) {}
	}

	/**
	 * static void main
	 * 
	 * @param args
	 *            Console-args, not used
	 */
	public static void main(String[] args) {
		try {
			Settings.loadSettings();
			tryDatabase();
			if (Settings.get("webhookName") == null && Settings.get("httpServerPort") != null) {
				new BonusHttpServer(Settings.<Integer>get("httpServerPort"));
			}
			IDiscordClient client = Client.createClient(Settings.get("token"), true);

			EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new bonusbot.commands.Handler());
			dispatcher.registerListener(new EventsListener());
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}
}

// Invitation-Link://
// https://discordapp.com/api/oauth2/authorize?client_id=356578515472089089&scope=bot&permissions=70618192
// Infos: https://discordapp.com/developers/docs/topics/oauth2
// Permissions:
// https://discordapp.com/developers/docs/topics/permissions#permissions-bitwise-permission-flags