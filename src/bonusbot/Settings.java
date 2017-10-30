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
	
	/** Token of the bot (Client-ID) */
	static String token;
	/** Prefix for the commands */
	public static String prefix = "!";
	/** name of the bot */
	static String name = "Bonus-Bot";
	/** playing-text of the bot */
	static String playing = "Bonus-community";
	/** name of the language-channel where you can use language-commands */
	public static String languageChannel = "";
	/** name of the audio-channel where you can use audio-commands */
	public static String audioChannel = "";
	/** name of the channel where the audio-info gets posted (embed) */
	public static String audioInfoChannel = "";
	/** name of the channel where the user joining the user get greeted */
	public static String greetUserChannel = "";
	/** name of the role which can use audio-commands */
	public static String audiobotUserRole = "";
	/** name of the english-section role (!english) */
	public static String englishRole = "";
	/** name of the german-section role (!german/!deutsch) */
	public static String germanRole = "";
	/** name of the turkish-section role (!turkish/!türkce) */
	public static String turkishRole = "";
	/** name of the what-emoji, used for some outputs */
	public static String whatEmoji = "what";
	/** name of the haha-emoji, used for some outputs */
	public static String hahaEmoji = "haha";
	/** name of the tada-emoji, used for some outputs */
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