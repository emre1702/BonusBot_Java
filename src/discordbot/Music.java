package discordbot;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lavaplayer.GuildMusicManager;
import lavaplayer.TrackScheduler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class Music {
	
	public static void loadAndPlay(final MessageReceivedEvent event, final String trackUrl, final boolean queue ) {
		final IChannel channel = event.getChannel();
	    GuildMusicManager musicManager = Util.getGuildMusicManager(channel.getGuild());

	    AudioLoadResultHandler handler = new AudioLoadResultHandler() {
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

	private static void play(GuildMusicManager musicManager, AudioTrack track, IUser user ) {
		try {
			TrackScheduler scheduler = musicManager.getScheduler();
			scheduler.userqueue.add( user );
			scheduler.datequeue.add( LocalDateTime.now( ZoneId.of( "Europe/Paris" ) ) );
			musicManager.getPlayer().playTrack( track );
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 	}
	}
	  
	private static void queue(GuildMusicManager musicManager, AudioTrack track, IUser user ) {
		try {
			TrackScheduler scheduler = musicManager.getScheduler();
			scheduler.userqueue.add( user );
			scheduler.datequeue.add( LocalDateTime.now( ZoneId.of( "Europe/Paris" ) ) );
			scheduler.queue(track);
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 	}
	}
	
	public static void skipTrack ( final MessageReceivedEvent event ) {
		final IChannel channel = event.getChannel();
		GuildMusicManager musicManager = Util.getGuildMusicManager(channel.getGuild());
	    int size = musicManager.getScheduler().getQueue().size();
	    AudioTrack oldtrack = musicManager.getScheduler().nextTrack();
	    
	    if ( oldtrack != null || size > 0 ) {
		    if ( size > 0 ) 
		    	Util.sendMessage ( channel, Lang.getLang ( "skipped", event.getAuthor(), event.getGuild() ) );
		    else {
		    	Util.sendMessage ( channel, Lang.getLang ( "skipped_nothing_left", event.getAuthor(), event.getGuild() ) );
		    	Util.changeMusicInfoStatus( event.getGuild(), "skipped" );
		    }
	    }
	}
	
}
