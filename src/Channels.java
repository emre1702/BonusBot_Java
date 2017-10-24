class Channels {
	
	/* The channel where you will be able to add yourself the rights 
	 * to read, write and talk in your language channels */
	static long languageChannelID = 0;
	static long musicChannelID = 0;
	static long greetUserChannelID = 0;
	
	/**
	 * Checks if the ID is the ID of the language-channel.
	 * @param ID The ID to be checked.
	 * @return If the ID is the languageChannelID. Returns true when there is no language-channel.
	 */
	static boolean isLanguageChannel ( final long ID ) {
		return ( languageChannelID == 0 || ID == languageChannelID );
	}
	
	/**
	 * Checks if the ID is the ID of the music-channel.
	 * @param ID The ID to be checked.
	 * @return If the ID is the musicChannelID. Returns true when there is no music-channel.
	 */
	static boolean isMusicChannel ( final long ID ) {
		return ( musicChannelID == 0 || ID == musicChannelID );
	}
}
