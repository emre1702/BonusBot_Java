import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

class Language {
	
	private static Map<String, Map<String, String>> languageMap = new HashMap<String, Map<String, String>>();
	static {
		Map<String, String> english = new HashMap<String, String>();
		english.put( "yes", "Yes" );
		english.put( "no", "No" );
		english.put( "not_sure", "Not sure" );
		english.put( "maybe", "Maybe" );
		english.put( "nah", "Nah, don't think so" );
		english.put( "absolutely", "Absolutely" );
		english.put( "stupid_question", "Too stupid question " );
		english.put( "ask_again", "Ask again" );
		english.put( "what_is_question", "And what exactly is the question? Dafuq " );
		english.put( "You_not_in_voice_channel", "You aren't even in a voice-channel " );
		english.put( "I_not_in_voice_channel", "I'm not even in a voice-channel " );
		english.put( "first_has_to_be_int", "The first argument has to be an integer!" );
		english.put( "first_has_to_be_floating_number", "The first argument has to be a decimal number!" );
		english.put( "current_volume", "The current volume is: " );
		english.put( "volume_usage_1", "Use '" );
		english.put( "volume_usage_2", "volume [0-200]' to change the volume!" );
		english.put( "not_playing_audio", "I'm not playing audio right now." );
		english.put( "adding_to_queue", "Adding to queue: " );
		english.put( "first_track_of_playlist", "first track of playlist" );
		english.put( "nothing_found_by", "Nothing found by " );
		english.put( "could_not_play", "Could not play: " );
		english.put( "skipped", "Skipped this track." );
		english.put( "skipped_nothing_left", "Skipped this track, nothing left." );
		english.put( "playing", "playing" );
		english.put( "url", "url" );
		english.put( "length", "length" );
		
		Map<String, String> german = new HashMap<String, String>();
		german.put( "yes", "Ja" );
		german.put( "no", "Nein" );
		german.put( "not_sure", "Nicht sicher" );
		german.put( "maybe", "Vielleicht" );
		german.put( "nah", "Nee, denke nicht" );
		german.put( "absolutely", "Absolut" );
		german.put( "stupid_question", "Zu dumme Frage " );
		german.put( "ask_again", "Frag nochmal" );
		german.put( "what_is_question", "Und was genau ist die Frage? Dafuq " );
		german.put( "You_not_in_voice_channel", "Bist nicht einmal in einem Channel " );
		german.put( "I_not_in_voice_channel", "Bin nicht einmal in einem Channel " );
		german.put( "first_has_to_be_int", "Das erste Argument muss eine ganze Zahl sein!" );
		german.put( "first_has_to_be_floating_number", "Das erste Argument muss eine Dezimalzahl sein!" );
		german.put( "current_volume", "Die jetzige Lautstärke ist: " );
		german.put( "volume_usage_1", "Benutze '" );
		german.put( "volume_usage_2", "volume [0-200]' um die Lautstärke zu veränden." );
		german.put( "not_playing_audio", "Ich spiele nicht mal Sound ab." );
		german.put( "adding_to_queue", "Füge der Warteschleife hinzu: " );
		german.put( "first_track_of_playlist", "Erster Song der Playliste" );
		german.put( "nothing_found_by", "Nichts gefunden bei " );
		german.put( "could_not_play", "Konnte nicht abspielen: " );
		german.put( "skipped", "Überspringe das jetzige Audio." );
		german.put( "skipped_nothing_left", "Überspringe das jetzige Audio, nichts ist mehr übrig." );
		german.put( "playing", "Spiele ab" );
		german.put( "url", "Link" );
		german.put( "length", "Länge" );
		
		Map<String, String> turkish = new HashMap<String, String>();
		turkish.put( "yes", "Evet" );
		turkish.put( "no", "Hayir" );
		turkish.put( "not_sure", "Emin degilim" );
		turkish.put( "maybe", "Belki" );
		turkish.put( "nah", "Yok, öyle düsünmüyorum" );
		turkish.put( "absolutely", "kesinlikle" );
		turkish.put( "stupid_question", "Cok aptalca soru " );
		turkish.put( "ask_again", "Yeniden sor" );
		turkish.put( "what_is_question", "Ve soru ne? Amk " );
		turkish.put( "You_not_in_voice_channel", "Bir kanal'da bile degilsin " );
		turkish.put( "I_not_in_voice_channel", "Bir kanal'da bile degilim" );
		turkish.put( "first_has_to_be_int", "Tam sayi olmasi lazim!" );
		turkish.put( "first_has_to_be_floating_number", "Ondalik sayi olmasi lazim!" );
		turkish.put( "current_volume", "Simdiki hacim: " );
		turkish.put( "volume_usage_1", "Hacim'i degistirmek icin '" );
		turkish.put( "volume_usage_2", "volume [0-200]' kullan!" );
		turkish.put( "not_playing_audio", "Ses oynamiyorum." );
		turkish.put( "adding_to_queue", "Kuyruga ekliyorum: " );
		turkish.put( "first_track_of_playlist", "calma listesinin ilk sesi" );
		turkish.put( "nothing_found_by", "Bisey bulunamadi: " );
		turkish.put( "could_not_play", "Oynanamadi: " );
		turkish.put( "skipped", "Bu sesi atladim" );
		turkish.put( "skipped_nothing_left", "Bu sesi atladim, baska kalmadi." );
		turkish.put( "playing", "oynaniyor" );
		turkish.put( "url", "url" );
		turkish.put( "length", "uzunlugu" );
		
		languageMap.put( "english", english );
		languageMap.put( "german", german );
		languageMap.put( "turkish", turkish );

	}
	
	/**
	 * Get language-string for specific user in the specific guild.
	 * @param str The index for the value.
	 * @param user The user who will get the message (needed for checking the language)
	 * @param guild Your guild where you want to send the message.
	 * @return The string for the users language.
	 */
	final static String getLang ( String str, IUser user, IGuild guild ) {
		String language = "english";
		List<IRole> roles = user.getRolesForGuild( guild );
		IRole germanrole = guild.getRoleByID( Roles.germanRoleID );
		
		if ( roles.contains( germanrole ) ) {
			language = "german";
		} else {
			IRole turkishrole = guild.getRoleByID( Roles.turkishRoleID );
			if ( roles.contains( turkishrole ) ) {
				language = "turkish";
			} 
		}
		return languageMap.get( language ).get( str );
	}
}
