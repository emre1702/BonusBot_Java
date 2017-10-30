package discordbot;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import discordbot.server.Channels;
import discordbot.server.Emojis;
import discordbot.server.Roles;

/**
 * Load and manage settings.
 * @author emre1702
 *
 */
public class Settings {
	/**
	 * Token of the bot.
	 * Can be found here:
	 * https://discordapp.com/developers/applications/me
	 */
	static String token;
	public static String prefix = "!";
	static String name = "Bonus-Bot";
	static String playing = "Bonus-community";
	
	/**
	 * Loads the settings from discordbot.log.
	 * To disable a settings just delete it or change the value to -1.
	 */
	public final static void loadSettings ( ) {
		final JSONParser parser = new JSONParser();
		
		try {
			final Object obj = parser.parse( new FileReader ( "discordbot.conf" ) );
			
			final JSONObject jsonObject = (JSONObject) obj;
			token = (String) jsonObject.get( "token" );
			
			if ( jsonObject.containsKey( "prefix" ) )
				prefix = (String) jsonObject.get( "prefix" );
			if ( jsonObject.containsKey( "name" ) ) 
				name = (String) jsonObject.get( "name" );
			if ( jsonObject.containsKey( "playing" ) ) 
				playing = (String) jsonObject.get( "playing" );
			
			if ( jsonObject.containsKey( "languageChannelID" ) )
				Channels.languageChannelID = (Long) jsonObject.get( "languageChannelID" );
			if ( jsonObject.containsKey( "audioChannelID" ) )
				Channels.audioChannelID = (Long) jsonObject.get( "audioChannelID" );
			if ( jsonObject.containsKey( "audioChannelID" ) )
				Channels.audioInfoChannelID = (Long) jsonObject.get( "audioInfoChannelID" );
			if ( jsonObject.containsKey( "greetUserChannelID" ) )
				Channels.greetUserChannelID = (Long) jsonObject.get( "greetUserChannelID" );
			if ( jsonObject.containsKey( "audiobotUserID" ) )
				Roles.audiobotUserID = (Long) jsonObject.get( "audiobotUserID" );
			if ( jsonObject.containsKey( "germanRoleID" ) )
				Roles.germanRoleID = (Long) jsonObject.get( "germanRoleID" );
			if ( jsonObject.containsKey( "englishRoleID" ) )
				Roles.englishRoleID = (Long) jsonObject.get( "englishRoleID" );
			if ( jsonObject.containsKey( "turkishRoleID" ) )
				Roles.turkishRoleID = (Long) jsonObject.get( "turkishRoleID" );
			
			if ( jsonObject.containsKey( "whatEmojiID" ) ) {
				Emojis.whatcode = (Long) jsonObject.get( "whatEmojiID" );
				Emojis.what  = "<:what:"+Emojis.whatcode+">";
			}
			if ( jsonObject.containsKey( "hahaEmojiID" ) ) {
				Emojis.hahacode = (Long) jsonObject.get( "hahaEmojiID" );
				Emojis.haha = "<:haha:"+Emojis.hahacode+">";
			}
			if ( jsonObject.containsKey( "tadaEmojiID" ) ) {
				Emojis.tada = "<:tada:"+ (Long) jsonObject.get( "tadaEmojiID" ) +">";
			}
			
		} catch ( Exception e ) {
			e.printStackTrace ( Logging.getPrintWrite() );
		}
	}
}
