package discordbot;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import discordbot.server.Channels;
import lavaplayer.GuildMusicManager;
import lavaplayer.TrackScheduler;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.RequestBuffer;

public class Util {
	private static final Map<Long, GuildMusicManager> musicManagers  = new HashMap<>();
	static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	
	static {
		AudioSourceManagers.registerRemoteSources( playerManager );
		AudioSourceManagers.registerLocalSource( playerManager );
	}
	
	/**
	 * Get LocalDateTime now for Europe/Paris timezone.
	 * @return LocalDateTime.now for Europe/Paris timezone.
	 */
	static LocalDateTime getLocalDateTime ( ) {
		return LocalDateTime.now( ZoneId.of( "Europe/Paris" ) );
	}
	
	/**
	 * Get custom timestamp for now.
	 * @return timestamp at current time.
	 */
	private static String getTimestamp ( ) {
		return getLocalDateTime().format( DateTimeFormatter.ofPattern( "HH:mm:ss - dd.MM.yyyy" ) ).toString();
	}
	
	/**
	 * Get custom timestamp for a specific LocalDateTime.
	 * @param datetime The LocalDateTime we want to get the timestamp of.
	 * @return timestamp at the time of the LocalDateTime.
	 */
	private static String getTimestamp ( LocalDateTime datetime ) {
		return datetime.format( DateTimeFormatter.ofPattern( "HH:mm:ss - dd.MM.yyyy" ) ).toString();
	}
	
	
	/**
	 * Send a message to the specific channel.
	 * Uses RequestBuffer.
	 * @param channel The channel where we want to have the message.
	 * @param message The message we want to send.
	 */
	public static void sendMessage ( IChannel channel, String message ) {
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
	
	/**
	 * Send a EmbedObject to the specific channel.
	 * Uses RequestBuffer.
	 * @param channel The channel where we want to have the EmbedObject.
	 * @param object The EmbedObject we want to send.
	 */
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
	
	/**
	 * Get the GuildMusicManager for the specific guild.
	 * @param guild The guild of which we want to get the GuildMusicManager.
	 * @return The GuildMusicManager of the guild.
	 */
	public static synchronized GuildMusicManager getGuildMusicManager(IGuild guild) {
        long guildId = guild.getLongID();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
            
            final GuildMusicManager managerForInnerScope = musicManager;
            
            musicManager.getPlayer().addListener(new AudioEventAdapter() {
            	/*@Override
            	public void onPlayerPause ( AudioPlayer player ) {
            		
            	}
            	
            	@Override
            	public void onPlayerResume ( AudioPlayer player ) {
            		
            	}*/
            	
            	@Override
            	public void onTrackStart ( AudioPlayer player, AudioTrack track ) {
            		try {
	            		if ( Channels.musicInfoChannelID != -1 ) {
	            			TrackScheduler scheduler = managerForInnerScope.getScheduler();
	            			EmbedObject object = Util.getMusicInfo ( track, scheduler.userqueue.poll(), guild, scheduler.datequeue.poll() );
	            			IChannel musicinfochannel = guild.getChannelByID( Channels.musicInfoChannelID );
	            			MessageHistory msghist = musicinfochannel.getFullMessageHistory();
	            			if ( msghist.isEmpty() ) {
	            				Util.sendMessage( musicinfochannel, object );
	            			} else {
	            				msghist.getEarliestMessage().edit( object );
	            			}
	            		}
            		} catch ( Exception e ) {
            	 		e.printStackTrace ( Logging.getPrintWrite() );
            	 	}
            	}
            	
            	@Override
            	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
            		if(endReason.mayStartNext) {
            			managerForInnerScope.getScheduler().nextTrack();
            		}
            		if ( Channels.musicInfoChannelID != -1 ) {
            			Util.changeMusicInfoStatus( guild, "ended" );
            		}
            	}
            } );
        }

        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }
	
	/**
	 * Gets the EmbedObject with informations the audiotrack for music-info channel.
	 * @param audiotrack The audiotrack we want to get the infos of.
	 * @param user User who added the audiotrack.
	 * @param guild Guild where all happenes.
	 * @param dateadded Date when the audio got added.
	 * @return EmbedObject with infos for the music-info channel.
	 */
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
			builder.withTitle( info.title );
			builder.withUrl( info.uri );
			int minutes = (int) (Math.floor( info.length / 60000 ));
			int seconds = (int)(Math.floor( info.length / 1000 ) % 60 );
			builder.appendField( "Status:", "playing", false );
			builder.appendField( "Volume:", String.valueOf( getGuildMusicManager(guild).getPlayer().getVolume() ), false );
			builder.appendField( "Length:", minutes + ":" + ( seconds >= 10 ? seconds : "0"+seconds ), false );
			builder.appendField( "Added:", getTimestamp ( dateadded ), false );
			
			EmbedObject obj = builder.build();
			
			obj.timestamp = getTimestamp();
			
			return builder.build();
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
	public static void changeMusicInfoStatus ( IGuild guild, String status ) {
		if ( Channels.musicInfoChannelID != -1 ) {
			IChannel musicinfochannel = guild.getChannelByID( Channels.musicInfoChannelID );
			if ( musicinfochannel != null ) {
				IMessage msg = musicinfochannel.getFullMessageHistory().getEarliestMessage();
				if ( msg != null ) {
					IEmbed embed = msg.getEmbeds().get ( 0 );
					if ( embed != null ) {
						EmbedObject obj = new EmbedObject ( embed );
						if ( obj.fields.length > 0 ) {
							obj.fields[0].value = status;
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
	 * @param status The new volume-info.
	 */
	public static void changeMusicInfoVolume ( IGuild guild, int volume ) {
		if ( Channels.musicInfoChannelID != -1 ) {
			IChannel musicinfochannel = guild.getChannelByID( Channels.musicInfoChannelID );
			if ( musicinfochannel != null ) {
				IMessage msg = musicinfochannel.getFullMessageHistory().getEarliestMessage();
				if ( msg != null ) {
					IEmbed embed = msg.getEmbeds().get ( 0 );
					if ( embed != null ) {
						EmbedObject obj = new EmbedObject ( embed );
						if ( obj.fields.length > 0 ) {
							obj.fields[1].value = String.valueOf( volume );
							msg.edit( obj );
						}
					}
				}
			}
		}
	}
}
