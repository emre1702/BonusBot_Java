package bonusbot;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import bonusbot.guild.GuildExtends;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Manage the audio-info embed for !playing and the audio-info channel.
 * @author emre1702
 *
 */
public class AudioInfo {
	/** map for last created embedobject in guild */
	private final static Map<Long, EmbedObject> guildlastembed = new HashMap<Long, EmbedObject>();
	
	/**
	 * Creates the EmbedObject with informations the audiotrack for audio-info channel.
	 * @param audiotrack The audiotrack we want to get the infos of.
	 * @param user User who added the audiotrack.
	 * @param guild Guild where all happenes.
	 * @param dateadded Date when the audio got added.
	 * @return EmbedObject with infos for the audio-info channel.
	 */
	public final static EmbedObject createAudioInfo ( final AudioTrack audiotrack, final IUser user, final IGuild guild, final LocalDateTime dateadded ) {
		try {
			final EmbedBuilder builder = new EmbedBuilder();
			final AudioTrackInfo info = audiotrack.getInfo();
			
			builder.withAuthorName( user.getDisplayName( guild ) );
			builder.withAuthorIcon( user.getAvatarURL() );
			builder.withColor( 0, 0, 150 );
			builder.withDescription( info.uri );
			builder.withFooterText( "Audio-info" );
			//builder.withImage( "https://www.youtube.com/yts/img/yt_1200-vfl4C3T0K.png" );
			builder.withThumbnail( "https://lh3.googleusercontent.com/Ned_Tu_ge6GgJZ_lIO_5mieIEmjDpq9kfgD05wapmvzcInvT4qQMxhxq_hEazf8ZsqA=w300" );
			builder.withTitle( info.title );
			builder.withUrl( info.uri );
			final int minutes = (int) (Math.floor( info.length / 60000 ));
			final int seconds = (int)(Math.floor( info.length / 1000 ) % 60 );
			builder.appendField( "Status:", "playing", false );
			builder.appendField( "Volume:", String.valueOf( GuildExtends.get ( guild ).getAudioManager().getPlayer().getVolume() ), false );
			builder.appendField( "Length:", minutes + ":" + ( seconds >= 10 ? seconds : "0"+seconds ), false );
			builder.appendField( "Added:", Util.getTimestamp ( dateadded ), false );
			
			EmbedObject obj = builder.build();
			
			refreshLastChangedTimestamp ( obj );
			
			guildlastembed.put( guild.getLongID(), obj );
			
			return obj;
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 		return null;
	 	}
	}
	
	/**
	 * Refresh last-changed info in embed
	 * @param obj EmbedObject
	 */
	private final static void refreshLastChangedTimestamp ( final EmbedObject obj ) {
		obj.timestamp = Util.getTimestampForDiscord();
	}
	
	/**
	 * Change the status in the EmbedObject.
	 * If the audio-info is available, refresh it there.
	 * @param guild Guild where you want to change the EmbedObject.
	 * @param status The new status.
	 */
	public final static void changeAudioInfoStatus ( final IGuild guild, final String status ) {
		final EmbedObject obj = getLastAudioInfo ( guild );
		if ( obj != null && obj.fields.length > 0 ) {
			obj.fields[0].value = status;
			refreshLastChangedTimestamp ( obj );
			
			final GuildExtends guildext = GuildExtends.get( guild );
			final Long audioinfochannelID = guildext.getAudioInfoChannelID(); 
			if ( audioinfochannelID != null ) {
				final IChannel audioinfochannel = guild.getChannelByID( audioinfochannelID );
				if ( audioinfochannel != null ) {
					final IMessage msg = audioinfochannel.getFullMessageHistory().getEarliestMessage();
					if ( msg != null ) {
						msg.edit( obj );
					}
				}
			}
		}
	}
	
	/**
	 * Change the volume-info in the EmbedObject.
	 * If the audio-info is available, refresh it there.
	 * @param guild Guild where you want to change the EmbedObject.
	 * @param volume The new volume-info.
	 */
	public final static void changeAudioInfoVolume ( final IGuild guild, final int volume ) {
		final EmbedObject obj = getLastAudioInfo ( guild );
		if ( obj != null && obj.fields.length > 1 ) {
			obj.fields[1].value = String.valueOf( volume );
			refreshLastChangedTimestamp ( obj );
			
			final GuildExtends guildext = GuildExtends.get( guild );
			final Long audioinfochannelID = guildext.getAudioInfoChannelID(); 
			if ( audioinfochannelID != null ) {
				final IChannel audioinfochannel = guild.getChannelByID( audioinfochannelID );
				if ( audioinfochannel != null ) {
					final IMessage msg = audioinfochannel.getFullMessageHistory().getEarliestMessage();
					if ( msg != null ) {
						msg.edit( obj );
					}
				}
			}
		}
	}
	
	/**
	 * Getter for the last created audio-info-embed for the guild.
	 * If there is none in the map, check the audio-info channel.
	 * @param guild The guild whose audio-info we want to retrieve.
	 * @return The EmbedObject.
	 */
	public final static EmbedObject getLastAudioInfo ( IGuild guild ) {
		EmbedObject obj = guildlastembed.get( guild.getLongID() );
		if ( obj == null ) {
			final GuildExtends guildext = GuildExtends.get( guild );
			final Long audioinfochannelID = guildext.getAudioInfoChannelID(); 
			if ( audioinfochannelID != null ) {
				final IChannel audioinfochannel = guild.getChannelByID( audioinfochannelID );
				if ( audioinfochannel != null ) {
					final IMessage msg = audioinfochannel.getFullMessageHistory().getEarliestMessage();
					if ( msg != null ) {
						List<IEmbed> embeds = msg.getEmbeds();
						if ( !embeds.isEmpty() ) {
							obj = new EmbedObject ( embeds.get( 0 ) );
						}
					}
				}
			}
		}
		return obj;
	}
}
