package discordbot;

import java.time.LocalDateTime;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import discordbot.server.Channels;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class AudioInfo {
	
	/**
	 * Gets the EmbedObject with informations the audiotrack for music-info channel.
	 * @param audiotrack The audiotrack we want to get the infos of.
	 * @param user User who added the audiotrack.
	 * @param guild Guild where all happenes.
	 * @param dateadded Date when the audio got added.
	 * @return EmbedObject with infos for the music-info channel.
	 */
	static final EmbedObject getMusicInfo ( final AudioTrack audiotrack, final IUser user, final IGuild guild, final LocalDateTime dateadded ) {
		try {
			final EmbedBuilder builder = new EmbedBuilder();
			final AudioTrackInfo info = audiotrack.getInfo();
			
			builder.withAuthorName( user.getDisplayName( guild ) );
			builder.withAuthorIcon( user.getAvatarURL() );
			builder.withColor( 0, 0, 150 );
			builder.withDescription( info.uri );
			builder.withFooterText( "Music-info" );
			//builder.withImage( "https://www.youtube.com/yts/img/yt_1200-vfl4C3T0K.png" );
			builder.withThumbnail( "https://lh3.googleusercontent.com/Ned_Tu_ge6GgJZ_lIO_5mieIEmjDpq9kfgD05wapmvzcInvT4qQMxhxq_hEazf8ZsqA=w300" );
			builder.withTitle( info.title );
			builder.withUrl( info.uri );
			final int minutes = (int) (Math.floor( info.length / 60000 ));
			final int seconds = (int)(Math.floor( info.length / 1000 ) % 60 );
			builder.appendField( "Status:", "playing", false );
			builder.appendField( "Volume:", String.valueOf( Util.getGuildMusicManager(guild).getPlayer().getVolume() ), false );
			builder.appendField( "Length:", minutes + ":" + ( seconds >= 10 ? seconds : "0"+seconds ), false );
			builder.appendField( "Added:", Util.getTimestamp ( dateadded ), false );
			
			final EmbedObject obj = builder.build();
			
			obj.timestamp = Util.getTimestampForDiscord();
			
			return obj;
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 		return null;
	 	}
	}
	
	/**
	 * Change the status in the EmbedObject in music-info channel.
	 * @param guild Guild where you want to change the EmbedObject.
	 * @param status The new status.
	 */
	public final static void changeMusicInfoStatus ( final IGuild guild, final String status ) {
		if ( Channels.musicInfoChannelID != -1 ) {
			final IChannel musicinfochannel = guild.getChannelByID( Channels.musicInfoChannelID );
			if ( musicinfochannel != null ) {
				final IMessage msg = musicinfochannel.getFullMessageHistory().getEarliestMessage();
				if ( msg != null ) {
					final IEmbed embed = msg.getEmbeds().get ( 0 );
					if ( embed != null ) {
						final EmbedObject obj = new EmbedObject ( embed );
						if ( obj.fields.length > 0 ) {
							obj.fields[0].value = status;
							obj.timestamp = Util.getTimestampForDiscord();
							msg.edit( obj );
						}
					}
				}
			}
		}
	}
	
	/**
	 * Change the volume-info in the EmbedObject in music-info channel.
	 * @param guild Guild where you want to change the EmbedObject.
	 * @param volume The new volume-info.
	 */
	public final static void changeMusicInfoVolume ( final IGuild guild, final int volume ) {
		if ( Channels.musicInfoChannelID != -1 ) {
			final IChannel musicinfochannel = guild.getChannelByID( Channels.musicInfoChannelID );
			if ( musicinfochannel != null ) {
				final IMessage msg = musicinfochannel.getFullMessageHistory().getEarliestMessage();
				if ( msg != null ) {
					final IEmbed embed = msg.getEmbeds().get ( 0 );
					if ( embed != null ) {
						final EmbedObject obj = new EmbedObject ( embed );
						if ( obj.fields.length > 0 ) {
							obj.fields[1].value = String.valueOf( volume );
							obj.timestamp = Util.getTimestampForDiscord();
							msg.edit( obj );
						}
					}
				}
			}
		}
	}
}
