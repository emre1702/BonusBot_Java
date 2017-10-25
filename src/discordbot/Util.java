package discordbot;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

class Util {
	
	/**
	 * Send a message to the specific channel.
	 * Uses RequestBuffer.
	 * @param channel The channel where we want to have the message.
	 * @param message The message we want to send.
	 */
	static void sendMessage ( IChannel channel, String message ) {
		try {
	        // This might look weird but it'll be explained in another page.
	        RequestBuffer.request(() -> {
	            try{
	                channel.sendMessage(message);
	            } catch (DiscordException e){
	            	e.printStackTrace ( Logging.getPrintWrite() );
	            }
	        });
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 	}
    }
	
	public static void sendMessage ( IChannel channel, EmbedObject object ) {
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
	
	static EmbedObject getMusicInfo ( AudioTrack audiotrack, IUser user, IGuild guild, LocalDateTime dateadded ) {
		try {
			EmbedBuilder builder = new EmbedBuilder();
			AudioTrackInfo info = audiotrack.getInfo();
			
			builder.withAuthorName( user.getDisplayName( guild ) );
			builder.withAuthorIcon( user.getAvatarURL() );
			builder.withColor( 0, 0, 150 );
			builder.withDescription( info.uri );
			builder.withFooterText( "Music-info" );
			//builder.withImage( "https://www.youtube.com/yts/img/yt_1200-vfl4C3T0K.png" );
			builder.withThumbnail( "https://lh3.googleusercontent.com/Ned_Tu_ge6GgJZ_lIO_5mieIEmjDpq9kfgD05wapmvzcInvT4qQMxhxq_hEazf8ZsqA=w300" );
			builder.withTimestamp( Timestamp.valueOf( LocalDateTime.now( ZoneId.of( "Europe/Paris" ) ) ).getTime() );
			builder.withTitle( info.title );
			builder.withUrl( info.uri );
			int minutes = (int) (Math.floor( info.length / 60000 ));
			int seconds = (int)(Math.floor( info.length / 1000 ) % 60 );
			builder.appendField( "Length:", minutes + ":" + ( seconds >= 10 ? seconds : "0"+seconds ), false );
			builder.appendField( "Added:", dateadded.format( DateTimeFormatter.ofPattern( "HH:mm:ss - dd.MM.yyyy" ) ).toString(), false );
			builder.appendField( "Status:", "playing", false );
			
			return builder.build();
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 		return null;
	 	}
	}
	
	static void changeMusicInfoStatus ( IGuild guild, String status ) {
		IChannel musicinfochannel = guild.getChannelByID( Channels.musicInfoChannelID );
		IMessage msg = musicinfochannel.getFullMessageHistory().getEarliestMessage();
		IEmbed embed = msg.getEmbeds().get ( 0 );
		EmbedObject obj = new EmbedObject ( embed );
		obj.fields[2].value = status;
		msg.edit( obj );
	}

	
	
}
