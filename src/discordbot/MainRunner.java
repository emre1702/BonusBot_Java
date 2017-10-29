package discordbot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

/**
 * Main-method
 * @author emre1702
 *
 */
public class MainRunner {
  
	public static void main ( String[] args ) {
		try {
			Settings.loadSettings();
			final IDiscordClient client = Client.createClient(Settings.token, true);
			
			final EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new discordbot.commands.Handler());
			dispatcher.registerListener( new EventsListener() );
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}
}