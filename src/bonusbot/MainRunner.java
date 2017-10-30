package bonusbot;
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
			dispatcher.registerListener(new bonusbot.commands.Handler());
			dispatcher.registerListener( new EventsListener() );
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}
}

// Invitation-Link://
// https://discordapp.com/api/oauth2/authorize?client_id=356578515472089089&scope=bot&permissions=70618192
// Infos: https://discordapp.com/developers/docs/topics/oauth2
// Permissions: https://discordapp.com/developers/docs/topics/permissions#permissions-bitwise-permission-flags