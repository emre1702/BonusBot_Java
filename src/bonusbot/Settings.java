package bonusbot;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Load and manage settings.
 * @author emre1702
 *
 */
public class Settings {
	private static Map<String, Object> settingsMap;
	private static Map<String, Object> defaultSettingsMap = new HashMap<String, Object>();
	static {
		defaultSettingsMap.put("prefix", "!");
		defaultSettingsMap.put("name", "Bonus-Bot");
		defaultSettingsMap.put("playing", "Bonus-community");
		defaultSettingsMap.put("httpServerPort", -1);
		defaultSettingsMap.put("whatEmoji", "what");
		defaultSettingsMap.put("hahaEmoji", "haha");
		defaultSettingsMap.put("hahaEmoji", "tada");
	}
	
	
	/**
	 * Loads the settings from discordbot.log.
	 * To disable a setting just change the value to "" (empty String) or delete the entry.
	 */
	@SuppressWarnings("unchecked")
	public static void loadSettings() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		try {			
			settingsMap = mapper.readValue(new FileReader("bonusbot.conf"), Map.class);
			System.out.println(settingsMap.get("token"));
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		if (settingsMap.containsKey(key)) {
			return (T)settingsMap.get(key);
		}
		if (defaultSettingsMap.containsKey(key)) {
			return (T)defaultSettingsMap.get(key);
		}
		return null;
	}
	
}