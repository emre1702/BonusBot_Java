package bonusbot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

/**
 * This class creates the discord-client for the bot.
 * 
 * @author emre1702
 *
 */
public class Client {
	private static IDiscordClient client;

	/**
	 * Creates the client for the bot.
	 * 
	 * @param token
	 *            Token of the bot created on discord-website.
	 * @param login
	 *            If the bot should login.
	 * @return The client of the bot.
	 */
	static IDiscordClient createClient(String token, boolean login) {
		try {
			ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
			clientBuilder.withToken(token); // Adds the login info to the builder
			if (login) {
				client = clientBuilder.login(); // Creates the client instance and logs the client in
				return client;
			} else {
				client = clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you
												// would have to call client.login() yourself
				return client;
			}
		} catch (DiscordException e) { // This is thrown if there was a problem building the client
			e.printStackTrace(Logging.getPrintWrite());
			return null;
		}
	}

	public static IDiscordClient get() {
		return client;
	}
}
