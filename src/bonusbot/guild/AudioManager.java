package bonusbot.guild;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import bonusbot.AudioInfo;
import bonusbot.Logging;
import lavaplayer.GuildAudioManager;
import lavaplayer.Track;
import lavaplayer.TrackScheduler;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.RequestBuffer;

/**
 * Manage the GuildAudioManager for a guild.
 * @author emre1702
 *
 */
public class AudioManager {
	/** GuildAudioManager for the guild */
	GuildAudioManager manager = null;
	
	/**
	 * Get the GuildAudioManager for the specific guild.
	 * @param guild The guild of which we want to get the GuildMusicManager.
	 * @return The GuildMusicManager of the guild.
	 */
	AudioManager ( final IGuild guild, final AudioPlayerManager playerManager ) {
		manager = new GuildAudioManager( playerManager, guild );
        
		manager.getPlayer().addListener(new AudioEventAdapter() {
        	/*@Override
        	public void onPlayerPause ( AudioPlayer player ) {
        		
        	}
        	
        	@Override
        	public void onPlayerResume ( AudioPlayer player ) {
        		
        	}*/
        	
        	@Override
        	public void onTrackStart ( AudioPlayer player, AudioTrack track ) {
        		try {
        			TrackScheduler scheduler = manager.getScheduler();
        			Track startedTrack = scheduler.getNext();
        			if ( startedTrack != null ) {
	        			EmbedObject object = AudioInfo.createAudioInfo ( startedTrack.audio, startedTrack.user, guild, startedTrack.date, scheduler );
	        			final GuildExtends guildext = GuildExtends.get( guild );
	        			final Long audioInfoChannelID = guildext.getAudioInfoChannelID();
	        	    	if ( audioInfoChannelID != null ) {
	        				IChannel musicinfochannel = guild.getChannelByID( audioInfoChannelID );
	        				MessageHistory msghist = musicinfochannel.getFullMessageHistory();
	        				RequestBuffer.request(() -> {
        		            	if (msghist.isEmpty()) {
        		            		musicinfochannel.sendMessage(object);
    	        				} else {
    	        					msghist.getEarliestMessage().edit( object );
    	        				}
	        		        });
	        			}
	                	AudioInfo.changeAudioInfoStatus( guild, "playing" );
        			}
        		} catch ( Exception e ) {
        	 		e.printStackTrace ( Logging.getPrintWrite() );
        	 	}
        	}
        	
        	@Override
        	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        		if(endReason.mayStartNext) {
        			manager.getScheduler().nextTrack();
        		}
        		AudioInfo.changeAudioInfoStatus( guild, "ended" );
        	}
        } );
        
        guild.getAudioManager().setAudioProvider(manager.getAudioProvider());
    }

}
