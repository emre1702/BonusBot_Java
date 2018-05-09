package bonusbot;
import java.time.LocalDateTime;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

/**
 * Some important and useful stuff.
 * @author emre1702
 *
 */
public class Util {
	
	/**
	 * Get LocalDateTime now for Europe/Paris timezone.
	 * @return LocalDateTime.now for Europe/Paris timezone.
	 */
	public final static LocalDateTime getLocalDateTime ( ) {
		return LocalDateTime.now( ZoneId.of( "Europe/Paris" ) );
	}
	
	/**
	 * Get custom timestamp for now.
	 * @return timestamp at current time.
	 */
	final static String getTimestamp ( ) {
		return getLocalDateTime().format( DateTimeFormatter.ofPattern( "HH:mm:ss - dd.MM.yyyy" ) );
	}
	
	/**
	 * Get custom timestamp to use in the embed in Discord.
	 * Discord only accepts ISO-8601 format.
	 * @return timestamp at current time for embed.
	 */
	final static String getTimestampForDiscord ( ) {
		return LocalDateTime.now ( ZoneId.of( "Z" )).toString();
	}
	
	/**
	 * Get custom timestamp for a specific LocalDateTime.
	 * @param datetime The LocalDateTime we want to get the timestamp of.
	 * @return timestamp at the time of the LocalDateTime.
	 */
	final static String getTimestamp ( LocalDateTime datetime ) {
		return datetime.format( DateTimeFormatter.ofPattern( "HH:mm:ss - dd.MM.yyyy" ) ).toString();
	}
	
	
	/**
	 * Send a message to the specific channel.
	 * Uses RequestBuffer.
	 * @param channel The channel where we want to have the message.
	 * @param message The message we want to send.
	 */
	public final static void sendMessage ( final IChannel channel, final String message ) {
        RequestBuffer.request(() -> {
            try{
                channel.sendMessage(message);
            } catch (DiscordException e){
            	e.printStackTrace ( Logging.getPrintWrite() );
            }
        });
    }
	
	/**
	 * Edits a message with RequestBuffer.
	 * @param msg
	 * @param obj
	 */
	public final static void editMessage ( IMessage msg, EmbedObject obj ) {
		RequestBuffer.request(() -> {
            try{
            	msg.edit( obj );
            } catch (DiscordException e){
            	e.printStackTrace ( Logging.getPrintWrite() );
            }
        });
	}
	
	/**
	 * Send a EmbedObject to the specific channel.
	 * Uses RequestBuffer.
	 * @param channel The channel where we want to have the EmbedObject.
	 * @param object The EmbedObject we want to send.
	 */
	public final static void sendMessage ( final IChannel channel, final EmbedObject object ) {
		try {
	        // This might look weird but it'll be explained in another page.
	        RequestBuffer.request(() -> {
	            try{
	                channel.sendMessage(object);
	            } catch (DiscordException e){
	            	e.printStackTrace ( Logging.getPrintWrite() );
	            }
	        });
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 	}
	}
	
	/**
	 * Send a message to the audio channel of the guild.
	 * @param guild The guild.
	 * @param message The message.
	 */
	public final static void sendMessage ( final IGuild guild, final String message ) {
		try {
	        // This might look weird but it'll be explained in another page.
	        RequestBuffer.request(() -> {
	            try{
	            	guild.getChannelsByName( Settings.audioChannel ).get( 0 ).sendMessage(message);
	            } catch (DiscordException e){
	            	e.printStackTrace ( Logging.getPrintWrite() );
	            }
	        });
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 	}
	}
	
	/**
	 * Send a object to the audio channel of the guild.
	 * @param guild The guild.
	 * @param object The object.
	 */
	public final static void sendMessage ( final IGuild guild, final EmbedObject object ) {
		try {
	        // This might look weird but it'll be explained in another page.
	        RequestBuffer.request(() -> {
	            try{
	            	guild.getChannelsByName( Settings.audioChannel ).get( 0 ).sendMessage(object);
	            } catch (DiscordException e){
	            	e.printStackTrace ( Logging.getPrintWrite() );
	            }
	        });
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 	}
	}
	
	/**
	 * Returns the first non-null value.
	 * Equivalent to ?? from C#.
	 * @param one First one to check
	 * @param two Second one to check
	 * @param <T> a type
	 * @return The first value of both not being null.
	 */
	public final static <T> T firstNonNull ( T one, T two ) {
		return one != null ? one : two;
	}
	
	/**
	 * Get string to use in the discord-message by the emoji.
	 * @param emoji Emoji whose string we want to retrieve.
	 * @return String to use in Discord
	 */
	public final static String getEmojiString ( IEmoji emoji ) {
		return "<:"+emoji.getName()+":"+emoji.getStringID()+">";
	}
	
	/**
	 * Checks if a string is numeric.
	 * @param str The string to check.
	 * @return if the string is numeric
	 */
	public final static boolean isNumeric ( String str ) {  
	    return str.matches("[-+]?\\d*\\.?\\d+");  
	} 

}
