import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class Settings {
	static String token;
	static String prefix = "!";
	
	/**
	 * Loads the settings from discordbot.log.
	 * To disable a settings just delete it or change the value to 0.
	 */
	static void loadSettings ( ) {
		JSONParser parser = new JSONParser();
		
		try {
			Object obj = parser.parse( new FileReader ( "discordbot.conf" ) );
			
			JSONObject jsonObject = (JSONObject) obj;
			token = (String) jsonObject.get( "token" );
			
			if ( jsonObject.containsKey( "prefix" ) )
				prefix = (String) jsonObject.get( "prefix" );
			
			if ( jsonObject.containsKey( "languageChannelID" ) )
				Channels.languageChannelID = (Long) jsonObject.get( "languageChannelID" );
			if ( jsonObject.containsKey( "musicChannelID" ) )
				Channels.musicChannelID = (Long) jsonObject.get( "musicChannelID" );
			if ( jsonObject.containsKey( "greetUserChannelID" ) )
				Channels.greetUserChannelID = (Long) jsonObject.get( "greetUserChannelID" );
			if ( jsonObject.containsKey( "musicbotUserID" ) )
				Roles.musicbotUserID = (Long) jsonObject.get( "musicbotUserID" );
			if ( jsonObject.containsKey( "germanRoleID" ) )
				Roles.germanRoleID = (Long) jsonObject.get( "germanRoleID" );
			if ( jsonObject.containsKey( "englishRoleID" ) )
				Roles.englishRoleID = (Long) jsonObject.get( "englishRoleID" );
			if ( jsonObject.containsKey( "turkishRoleID" ) )
				Roles.turkishRoleID = (Long) jsonObject.get( "turkishRoleID" );
			
			ServerEmoji.whatcode = (Long) jsonObject.get( "whatEmojiID" );
			ServerEmoji.what  = "<:what:"+ServerEmoji.whatcode+">";
			ServerEmoji.hahacode = (Long) jsonObject.get( "hahaEmojiID" );
			ServerEmoji.haha = "<:haha:"+ServerEmoji.hahacode+">";
			ServerEmoji.tadacode = (Long) jsonObject.get( "tadaEmojiID" );
			ServerEmoji.tada = "<:tada:"+ServerEmoji.tadacode+">";
			
		} catch ( Exception e ) {
			e.printStackTrace ( Logging.getPrintWrite() );
		}
	}
}
