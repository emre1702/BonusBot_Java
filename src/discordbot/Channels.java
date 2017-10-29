package discordbot;

public class Channels {
	
	/* The channel where you will be able to add yourself the rights 
	 * to read, write and talk in your language channels */
	static long languageChannelID = -1;
	static long musicChannelID = -1;
	public static long musicInfoChannelID = -1;
	static long greetUserChannelID = -1;
	
	/**
	 * Checks if the ID is the ID of the language-channel.
	 * @param ID The ID to be checked.
	 * @return If the ID is the languageChannelID. Returns true if languageChannelID is not defined in discordconf.log.
	 */
	public static boolean isLanguageChannel ( final long ID ) {
		return ( languageChannelID == -1 || ID == languageChannelID );
	}
	
	/**
	 * Checks if the ID is the ID of the music-channel.
	 * @param ID The ID to be checked.
	 * @return If the ID is the musicChannelID. Returns true if musicChannelID is not defined in discordconf.log.
	 */
	public static boolean isMusicChannel ( final long ID ) {
		return ( musicChannelID == -1 || ID == musicChannelID );
	}
}
