package discordbot;
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

/**
 * Some important and useful stuff.
 * @author emre1702
 *
 */
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
	final static LocalDateTime getLocalDateTime ( ) {
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
	 * Get the GuildMusicManager for the specific guild.
	 * @param guild The guild of which we want to get the GuildMusicManager.
	 * @return The GuildMusicManager of the guild.
	 */
	public final static synchronized GuildMusicManager getGuildMusicManager ( final IGuild guild ) {
		final long guildId = guild.getLongID();
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
	            			EmbedObject object = AudioInfo.getMusicInfo ( track, scheduler.userqueue.poll(), guild, scheduler.datequeue.poll() );
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
            			AudioInfo.changeMusicInfoStatus( guild, "ended" );
            		}
            	}
            } );
        }

        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }

}
