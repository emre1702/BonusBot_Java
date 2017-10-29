package discordbot;
import discordbot.commands.Handler;
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
			client.changeUsername( Settings.name );
			client.changePlayingText( Settings.playing );
			
			final EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new Handler());
			dispatcher.registerListener( new UserJoin() );
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}
}