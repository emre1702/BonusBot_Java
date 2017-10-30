package discordbot.server;

/**
 * Server-channel informations
 * @author emre1702
 *
 */
public class Channels {
	
	/* The channel where you will be able to add yourself the rights 
	 * to read, write and talk in your language channels */
	public static long languageChannelID = -1;
	public static long audioChannelID = -1;
	public static long audioInfoChannelID = -1;
	public static long greetUserChannelID = -1;
	
	/**
	 * Checks if the ID is the ID of the language-channel.
	 * @param ID The ID to be checked.
	 * @return If the ID is the languageChannelID. Returns true if languageChannelID is not defined in discordconf.log.
	 */
	public final static boolean isLanguageChannel ( final long ID ) {
		return ( languageChannelID == -1 || ID == languageChannelID );
	}
	
	/**
	 * Checks if the ID is the ID of the music-channel.
	 * @param ID The ID to be checked.
	 * @return If the ID is the musicChannelID. Returns true if musicChannelID is not defined in discordconf.log.
	 */
	public final static boolean isAudioChannel ( final long ID ) {
		return ( audioChannelID == -1 || ID == audioChannelID );
	}
}
