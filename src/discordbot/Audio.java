package discordbot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lavaplayer.GuildMusicManager;
import lavaplayer.TrackScheduler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * Audio-class.
 * @author emre1702
 *
 */
public class Audio {
	
	/**
	 * Loads an audio and plays it afterwards.
	 * @param event The event to determine the channel (information-output), guild and the author (for language).
	 * @param trackUrl URL of the audio the author want to have played.
	 * @param queue If it should be added to the queue or played right now.
	 */
	public final static void loadAndPlay(final MessageReceivedEvent event, final String trackUrl, final boolean queue ) {
		final IChannel channel = event.getChannel();
		final GuildMusicManager musicManager = Util.getGuildMusicManager(channel.getGuild());

		final AudioLoadResultHandler handler = new AudioLoadResultHandler() {
		      @Override
		      public void trackLoaded(AudioTrack track) {
		        
		        if ( queue ) {
		        	Util.sendMessage(channel, Lang.getLang ( "adding_to_queue", event.getAuthor(), event.getGuild() ) + track.getInfo().title);
		        	queue(musicManager, track, event.getAuthor());
		        } else {
		        	Util.sendMessage(channel, Lang.getLang ( "playing", event.getAuthor(), event.getGuild() ) + ": "+ track.getInfo().title);
		        	play(musicManager, track, event.getAuthor());
		        }
		        
		      }

		      @Override
		      public void playlistLoaded(AudioPlaylist playlist) {
		        AudioTrack firstTrack = playlist.getSelectedTrack();

		        if (firstTrack == null) {
		          firstTrack = playlist.getTracks().get(0);
		        }

		        
		        if ( queue ) {
		        	Util.sendMessage(channel, Lang.getLang ( "adding_to_queue", event.getAuthor(), event.getGuild() ) + firstTrack.getInfo().title 
			        		+ " ("+ Lang.getLang ( "first_track_of_playlist", event.getAuthor(), event.getGuild() )+ " " + playlist.getName() + ")");
		        	queue(musicManager, firstTrack, event.getAuthor());
		        } else {
		        	Util.sendMessage(channel, Lang.getLang ( "playing", event.getAuthor(), event.getGuild() ) + ": " + firstTrack.getInfo().title 
			        		+ " ("+ Lang.getLang ( "first_track_of_playlist", event.getAuthor(), event.getGuild() )+" " + playlist.getName() + ")");
		        	play(musicManager, firstTrack, event.getAuthor());
		        }
		        
		      }

		      @Override
		      public void noMatches() {
		    	  Util.sendMessage(channel, Lang.getLang ( "nothing_found_by", event.getAuthor(), event.getGuild() ) + trackUrl);
		      }

		      @Override
		      public void loadFailed(FriendlyException exception) {
		    	  Util.sendMessage(channel, Lang.getLang ( "could_not_play", event.getAuthor(), event.getGuild() ) + exception.getMessage());
		      }
		      
		};
	    Util.playerManager.loadItemOrdered(musicManager, trackUrl, handler );
	  }

	/**
	 * Play the AudioTrack
	 * @param musicManager The GuildMusicManager of the guild.
	 * @param track AudioTrack the user want to have played.
	 * @param user User who added the track (used later for language).
	 */
	private final static void play ( final GuildMusicManager musicManager, final AudioTrack track, final IUser user ) {
		try {
			final TrackScheduler scheduler = musicManager.getScheduler();
			scheduler.userqueue.add( user );
			scheduler.datequeue.add( Util.getLocalDateTime() );
			musicManager.getPlayer().playTrack( track );
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 	}
	}
	
	/**
	 * Puts the AudioTrack into the queue.
	 * @param musicManager The GuildMusicManager of the guild.
	 * @param track AudioTrack the user want to have played.
	 * @param user User who added the track (used later for language).
	 */
	private final static void queue ( final GuildMusicManager musicManager, final AudioTrack track, final IUser user ) {
		try {
			final TrackScheduler scheduler = musicManager.getScheduler();
			scheduler.userqueue.add( user );
			scheduler.datequeue.add( Util.getLocalDateTime() );
			scheduler.queue(track);
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 	}
	}
	
	/**
	 * Skips the current track.
	 * @param event Event from command, used for channel (output), guild and author (language).
	 */
	public final static void skipTrack ( final MessageReceivedEvent event ) {
		final IChannel channel = event.getChannel();
		final GuildMusicManager musicManager = Util.getGuildMusicManager(channel.getGuild());
		final int size = musicManager.getScheduler().getQueue().size();
		final AudioTrack oldtrack = musicManager.getScheduler().nextTrack();
	    
	    if ( oldtrack != null || size > 0 ) {
		    if ( size > 0 ) 
		    	Util.sendMessage ( channel, Lang.getLang ( "skipped", event.getAuthor(), event.getGuild() ) );
		    else {
		    	Util.sendMessage ( channel, Lang.getLang ( "skipped_nothing_left", event.getAuthor(), event.getGuild() ) );
		    	AudioInfo.changeMusicInfoStatus( event.getGuild(), "skipped" );
		    }
	    }
	}
	
}
