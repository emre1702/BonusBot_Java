package bonusbot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import bonusbot.guild.GuildExtends;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class contains the language-strings for english, german and turkish.
 * @author emre1702
 *
 */
public class Lang {
	
	/** map for all the language-string for the languages */
	private final static Map<String, Map<String, String>> languageMap = new HashMap<String, Map<String, String>>();
	/** add the language-strings */
	static {
		final Map<String, String> english = new HashMap<String, String>();
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
		english.put( "not_played_audio_so_far", "I haven't played audio so far." );
		english.put( "adding_to_queue", "Adding to queue: " );
		english.put( "adding_to_queue_playlist", "Adding playlist to queue: " );
		english.put( "nothing_found_by", "Nothing found by " );
		english.put( "could_not_play", "Could not play: " );
		english.put( "skipped", "Skipped this track." );
		english.put( "skipped_nothing_left", "Skipped this track, nothing left." );
		english.put( "playing", "playing" );
		english.put( "playing_playlist", "Playing playlist: " );
		english.put( "url", "url" );
		english.put( "length", "length" );
		english.put( "channel", "channel" );
		english.put( "number", "number" );
		english.put( "usage", "Usage" );
		english.put( "user", "user" );
		english.put( "reason", "reason" );
		english.put( "del_messages_for_days", "delete messages for days" );
		english.put( "amount_messages", "amount messages" );
		english.put( "user_not_found", "User not found!" );
		english.put( "got_deleted", "Messages got deleted." );
		english.put( "stopped_the_audio", "Audio stopped!" );
		english.put( "will_stop_audio_after_time", "Audio will stop after {1} second(s)!" );
		english.put( "will_pause_audio_after_time", "Audio will pause after {1} second(s)!" );
		english.put( "will_resume_audio_after_time", "Audio will resume after {1} second(s)!" );
		english.put( "how_to_add_playlist", "If you wanted to add the whole playlist, please use \"!playlist [url]\" or \"!playlistqueue [url]\"." );
		english.put( "track_removed_from_queue", "The track got removed from queue: " );
		english.put( "index_not_in_queue", "The index is not in the queue!" );
		english.put( "shuffle_mode_on", "Shuffle-mode is now on" );
		english.put( "shuffle_mode_off", "Shuffle-mode deactivated" );
		
		final Map<String, String> german = new HashMap<String, String>();
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
		german.put( "not_played_audio_so_far", "Ich habe bisher nichts abgespielt." );
		german.put( "adding_to_queue", "Füge der Warteschleife hinzu: " );
		german.put( "adding_to_queue_playlist", "Füge Playlist der Warteschleife hinzu: " );
		german.put( "nothing_found_by", "Nichts gefunden bei " );
		german.put( "could_not_play", "Konnte nicht abspielen: " );
		german.put( "skipped", "Überspringe das jetzige Audio." );
		german.put( "skipped_nothing_left", "Überspringe das jetzige Audio, nichts ist mehr übrig." );
		german.put( "playing", "Spiele ab" );
		german.put( "playing_playlist", "Spiele Playlist ab: " );
		german.put( "url", "Link" );
		german.put( "length", "Länge" );
		german.put( "channel", "Kanal" );
		german.put( "number", "Zahl" );
		german.put( "usage", "Nutzung" );
		german.put( "user", "Nutzer" );
		german.put( "reason", "Grund" );
		german.put( "del_messages_for_days", "Lösche Nachrichten seit Tage" );
		german.put( "amount_messages", "Anzahl Nachrichten" );
		german.put( "user_not_found", "Nutzer nicht gefunden!" );
		german.put( "got_deleted", "Nachrichten wurden gelöscht." );
		german.put( "stopped_the_audio", "Audio wurde gestoppt!" );
		german.put( "will_stop_audio_after_time", "Audio wird nach {1} Sekunde(n) gestoppt!" );
		german.put( "will_pause_audio_after_time", "Audio wird nach {1} Sekunde(n) pausiert!" );
		german.put( "will_resume_audio_after_time", "Audio wird nach {1} Sekunde(n) fortgesetzt!" );
		german.put( "how_to_add_playlist", "Wenn du die Playlist ganz abspielen lassen willst, nutze bitte \"!playlist [url]\" oder \"!playlistqueue [url]\"." );
		german.put( "track_removed_from_queue", "Der Track wurde von der Warteschlange entfernt: " );
		german.put( "index_not_in_queue", "Der Index ist nicht in der Warteschlange!" );
		german.put( "shuffle_mode_on", "Shuffle-Modus ist nun an" );
		german.put( "shuffle_mode_off", "Shuffle-Modus deaktiviert" );
		
		final Map<String, String> turkish = new HashMap<String, String>();
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
		turkish.put( "not_played_audio_so_far", "Simdiye kadar bisey oynatmadim." );
		turkish.put( "adding_to_queue", "Kuyruga ekliyorum: " );
		turkish.put( "adding_to_queue_playlist", "Playlisti kuyruga ekliyorum: " );
		turkish.put( "nothing_found_by", "Bisey bulunamadi: " );
		turkish.put( "could_not_play", "Oynanamadi: " );
		turkish.put( "skipped", "Bu sesi atladim" );
		turkish.put( "skipped_nothing_left", "Bu sesi atladim, baska kalmadi." );
		turkish.put( "playing", "oynaniyor" );
		turkish.put( "playing_playlist", "Playlist oynaniyor: " );
		turkish.put( "url", "url" );
		turkish.put( "length", "uzunlugu" );
		turkish.put( "channel", "kanal" );
		turkish.put( "number", "numara" );
		turkish.put( "usage", "Kullanim" );
		turkish.put( "user", "kisi" );
		turkish.put( "reason", "sebep" );
		turkish.put( "del_messages_for_days", "son X günlerin mesajlarini sil" );
		turkish.put( "amount_messages", "mesaj sayisi" );
		turkish.put( "user_not_found", "Kullanici bulunamadi!" );
		turkish.put( "got_deleted", "mesajlar silindi." );
		turkish.put( "stopped_the_audio", "Ses durduruldu!" );
		turkish.put( "will_stop_audio_after_time", "Ses {1} saniye sonra durduralacak!" );
		turkish.put( "will_pause_audio_after_time", "Sese {1} saniye sonra ara verilecek!" );
		turkish.put( "will_resume_audio_after_time", "Ses {1} saniye sonra devam edecek!" );
		turkish.put( "how_to_add_playlist", "Playlistin bütününü oynatmak istiyorsan lüften \"!playlist [url]\" yada \"!playlistqueue [url]\" kullan." );
		turkish.put( "track_removed_from_queue", "Ses kuyruktan kaldirildi: " );
		turkish.put( "index_not_in_queue", "Numara kuyrukta degil!" );
		turkish.put( "shuffle_mode_on", "Shuffle acildi" );
		turkish.put( "shuffle_mode_off", "Shuffle kapatildi" );
		
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
	public final static String getLang ( final String str, final IUser user, final IGuild guild ) {
		String language = "english";
		final List<IRole> roles = user.getRolesForGuild( guild );
		final GuildExtends guildext = GuildExtends.get( guild );
		final Long germanRoleID = guildext.getGermanRoleID(); 
		
		if ( germanRoleID != null && roles.contains( guild.getRoleByID( germanRoleID ) ) ) {
			language = "german";
		} else {
			final Long turkishRoleID = guildext.getTurkishRoleID(); 
			if ( turkishRoleID != null && roles.contains( guild.getRoleByID( turkishRoleID ) ) ) {
				language = "turkish";
			} 
		}
		return languageMap.get( language ).get( str );
	}
	
	/**
	 * Get language-string for specific user in the specific guild.
	 * Also replace {1} with replace1
	 * @param str The index for the value.
	 * @param user The user who will get the message (needed for checking the language)
	 * @param guild Your guild where you want to send the message.
	 * @param replace1 The string to replace {1}
	 * @return The string for the users language.
	 */
	public final static String getLang ( final String str, final IUser user, final IGuild guild, final String replace1 ) {
		String language = "english";
		final List<IRole> roles = user.getRolesForGuild( guild );
		final GuildExtends guildext = GuildExtends.get( guild );
		final Long germanRoleID = guildext.getGermanRoleID(); 
		
		if ( germanRoleID != null && roles.contains( guild.getRoleByID( germanRoleID ) ) ) {
			language = "german";
		} else {
			final Long turkishRoleID = guildext.getTurkishRoleID(); 
			if ( turkishRoleID != null && roles.contains( guild.getRoleByID( turkishRoleID ) ) ) {
				language = "turkish";
			} 
		}
		return languageMap.get( language ).get( str ).replaceAll( Pattern.quote( "{1}" ), replace1 );
	}
}
