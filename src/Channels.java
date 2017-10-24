class Channels {
	
	/* The channel where you will be able to add yourself the rights 
	 * to read, write and talk in your language channels */
	static long languageChannelID = 0;
	static long musicChannelID = 0;
	static long greetUserChannelID = 0;
	
	static boolean isLanguageChannel ( final long ID ) {
		return ( languageChannelID == 0 || ID == languageChannelID );
	}
	
	static boolean isMusicChannel ( final long ID ) {
		return ( musicChannelID == 0 || ID == musicChannelID );
	}
}
