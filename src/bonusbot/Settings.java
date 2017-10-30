package bonusbot;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * Load and manage settings.
 * @author emre1702
 *
 */
public class Settings {

	static String token;
	public static String prefix = "!";
	static String name = "Bonus-Bot";
	static String playing = "Bonus-community";
	public static String languageChannel = "";
	public static String audioChannel = "";
	public static String audioInfoChannel = "";
	public static String greetUserChannel = "";
	public static String audiobotUserRole = "";
	public static String englishRole = "";
	public static String germanRole = "";
	public static String turkishRole = "";
	public static String whatEmoji = "what";
	public static String hahaEmoji = "haha";
	public static String tadaEmoji = "";
	
	/**
	 * Loads the settings from discordbot.log.
	 * To disable a settings just change the value to "" (empty String).
	 * Don't disable "token"!
	 */
	public final static void loadSettings ( ) {
		final JSONParser parser = new JSONParser();
		
		try {
			final Object obj = parser.parse( new FileReader ( "bonusbot.conf" ) );
			
			final JSONObject jsonObject = (JSONObject) obj;
			token = (String) jsonObject.get( "token" );
			
			if ( jsonObject.containsKey( "prefix" ) )
				prefix = (String) jsonObject.get( "prefix" );
			if ( jsonObject.containsKey( "name" ) ) 
				name = (String) jsonObject.get( "name" );
			if ( jsonObject.containsKey( "playing" ) ) 
				playing = (String) jsonObject.get( "playing" );
			
			if ( jsonObject.containsKey( "languageChannel" ) )
				languageChannel = (String) jsonObject.get( "languageChannel" );
			if ( jsonObject.containsKey( "audioChannel" ) )
				audioChannel = (String) jsonObject.get( "audioChannel" );
			if ( jsonObject.containsKey( "audioInfoChannel" ) )
				audioInfoChannel = (String) jsonObject.get( "audioInfoChannel" );
			if ( jsonObject.containsKey( "greetUserChannel" ) )
				greetUserChannel = (String) jsonObject.get( "greetUserChannel" );
			
			if ( jsonObject.containsKey( "audiobotUserRole" ) )
				audiobotUserRole = (String) jsonObject.get( "audiobotUserRole" );
			if ( jsonObject.containsKey( "englishRole" ) )
				englishRole = (String) jsonObject.get( "englishRole" );
			if ( jsonObject.containsKey( "germanRole" ) )
				germanRole = (String) jsonObject.get( "germanRole" );
			if ( jsonObject.containsKey( "turkishRole" ) )
				turkishRole = (String) jsonObject.get( "turkishRole" );
			
			if ( jsonObject.containsKey( "whatEmoji" ) )
				whatEmoji = (String) jsonObject.get( "whatEmoji" );
			if ( jsonObject.containsKey( "hahaEmoji" ) )
				hahaEmoji = (String) jsonObject.get( "hahaEmoji" );
			if ( jsonObject.containsKey( "tadaEmoji" ) )
				tadaEmoji = (String) jsonObject.get( "tadaEmoji" );
			
		} catch ( Exception e ) {
			e.printStackTrace ( Logging.getPrintWrite() );
		}
	}
}



/**
 * Loads the informations from the server.
 * To disable a settings just set it to -1 (or disable it)
 * Don't disable "token"!
 */