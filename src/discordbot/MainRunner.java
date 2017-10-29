package discordbot;
import discordbot.commands.Handler;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

public class MainRunner {
  
	public static void main ( String[] args ) {
		try {
			Settings.loadSettings();
			final IDiscordClient client = Client.createClient(Settings.token, true);
			final EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new Handler());
			dispatcher.registerListener( new UserJoin() );
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}
}