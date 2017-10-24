import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException; 

class Client {
	
	/**
	 * Creates the client for the bot.
	 * @param token Token of the bot created on discord-website.
	 * @param login If the bot should login.
	 * @return The client of the bot.
	 */
	static IDiscordClient createClient ( String token, boolean login ) {
        try {
        	ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
            clientBuilder.withToken(token); // Adds the login info to the builder
            if (login) {
                return clientBuilder.login(); // Creates the client instance and logs the client in
            } else {
                return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
            }
        } catch (DiscordException e) { // This is thrown if there was a problem building the client
            e.printStackTrace ( Logging.getPrintWrite() );
            return null;
        }
	}
	
}
