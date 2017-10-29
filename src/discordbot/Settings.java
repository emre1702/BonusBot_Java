package discordbot;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import discordbot.server.Channels;
import discordbot.server.Emoji;
import discordbot.server.Roles;

public class Settings {
	static String token;
	public static String prefix = "!";
	
	/**
	 * Loads the settings from discordbot.log.
	 * To disable a settings just delete it or change the value to -1.
	 */
	public static void loadSettings ( ) {
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
			if ( jsonObject.containsKey( "musicInfoChannelID" ) )
				Channels.musicInfoChannelID = (Long) jsonObject.get( "musicInfoChannelID" );
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
			
			Emoji.whatcode = (Long) jsonObject.get( "whatEmojiID" );
			Emoji.what  = "<:what:"+Emoji.whatcode+">";
			Emoji.hahacode = (Long) jsonObject.get( "hahaEmojiID" );
			Emoji.haha = "<:haha:"+Emoji.hahacode+">";
			Emoji.tadacode = (Long) jsonObject.get( "tadaEmojiID" );
			Emoji.tada = "<:tada:"+Emoji.tadacode+">";
			
		} catch ( Exception e ) {
			e.printStackTrace ( Logging.getPrintWrite() );
		}
	}
}
