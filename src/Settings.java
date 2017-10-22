import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class Settings {
	static String token;
	static String prefix;
	
	static void loadSettings ( ) {
		JSONParser parser = new JSONParser();
		
		try {
			Object obj = parser.parse( new FileReader ( "discordbot.conf" ) );
			
			JSONObject jsonObject = (JSONObject) obj;
			token = (String) jsonObject.get( "token" );
			prefix = (String) jsonObject.get( "prefix" );
			
			Channels.languageChannelID = (Long) jsonObject.get( "languageChannelID" );
			Roles.musicbotUserID = (Long) jsonObject.get( "musicbotUserID" );
			Roles.germanRoleID = (Long) jsonObject.get( "germanRoleID" );
			Roles.englishRoleID = (Long) jsonObject.get( "englishRoleID" );
			Roles.turkishRoleID = (Long) jsonObject.get( "turkishRoleID" );
			ServerEmoji.whatcode = (Long) jsonObject.get( "whatEmojiID" );
			ServerEmoji.what  = "<:what:"+ServerEmoji.whatcode+">";
			ServerEmoji.hahacode = (Long) jsonObject.get( "hahaEmojiID" );
			ServerEmoji.haha = "<:haha:"+ServerEmoji.hahacode+">";
			
		} catch ( Exception e ) {
			e.printStackTrace ( Logging.getPrintWrite() );
		}
	}
}
