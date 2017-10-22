import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

public class MainRunner {
  
	public static void main ( String[] args ) {
		try {
			Settings.loadSettings();
			IDiscordClient client = Client.createClient(Settings.token, true);
			EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new CommandHandler());
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}
}