package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import bonusbot.AudioInfo;
import bonusbot.Util;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler {
	/** queue for tracks */
	private final List<Track> queue = Collections.synchronizedList( new ArrayList<Track>() );
	private final AudioPlayer player; 
	private final IGuild guild;
	
	/**
	 * Constructor for TrackScheduler 
	 * @param player AudioPlayer for the TrackScheduler
	 */
	public TrackScheduler(final AudioPlayer player, final IGuild guild) {
		// Because we will be removing from the "head" of the queue frequently, a LinkedList is a better implementation
		// since all elements won't have to be shifted after removing. Additionally, choosing to add in between the queue
		// will similarly not cause many elements to shift and wil only require a couple of node changes.
		this.player = player;
		this.guild = guild;
	}
	
	/**
	 * Plays a track.
	 * @param track
	 * @param user
	 */
	public synchronized void play ( final AudioTrack track, final IUser user ) {
		queue.add( new Track( track, user, Util.getLocalDateTime() ) );
		AudioInfo.changeAudioInfoStatus( guild, "loading" );
		player.playTrack( track );
	}
	
	/**
	 * Add the next track to queue or play right away if nothing is in the queue.
	 *
	 * @param track The track to play or add to queue.
	 * @return If the track was started.
	 */
	public synchronized boolean queue(final AudioTrack audio, final IUser user) {
		// Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
		// something is playing, it returns false and does nothing. In that case the player was already playing so this
		// track goes to the queue instead.
		queue.add(new Track( audio, user, Util.getLocalDateTime() ));
		final boolean playing = player.startTrack(audio, true);
		
		return playing;
	}
	
	/**
	 * Removes a Track from queue.
	 * @param audio
	 */
	public synchronized void remove( final AudioTrack audio ) {
		for ( int i = queue.size() - 1; i >= 0; --i ) {
			if ( queue.get( i ).audio.equals( audio ) )
				queue.remove( i );
		}
	}
	
	/**
	 * Removes a Track from queue.
	 * @param index
	 * @return 
	 */
	public synchronized Track remove( final int index ) {
		if ( queue.size() <= index )
			return null;
		Track removedtrack = queue.get( index );
		queue.remove( index );
		return removedtrack;
	}
	
	/**
	 * Clears the queue.
	 */
	public synchronized void clearQueue() {
		queue.clear();
	}
	
	/**
	 * Starts the next track, stopping the current one if it is playing.
	 * @return The track that was stopped, null if there wasn't anything playing
	 */
	public synchronized AudioTrack nextTrack() {
		final AudioTrack currentTrack = player.getPlayingTrack();
		if ( queue.size() > 0 ) {
			final Track nextTrack = queue.get( 0 );
	
			// Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
			// giving null to startTrack, which is a valid argument and will simply stop the player.
			player.startTrack( nextTrack.audio, false );
		} else 
			player.startTrack( null, false );
		return currentTrack;
	}
	
	/**
	 * Returns the queue for this scheduler. Adding to the head of the queue (index 0) does not automatically
	 * cause it to start playing immediately. The returned collection is thread-safe and can be modified.
	 *
	 * To iterate over this queue, use a synchronized block. For example:
	 * {@code synchronize (getQueue()) { // iteration code } }
	 * 
	 * @return The queue.
	 */
	public List<Track> getQueue() {
		return this.queue;
	}

	/**
	 * Get the next Track.
	 * @return
	 */
	public Track getNext () {
		if ( queue.size() > 0 ) {
			Track track = queue.get( 0 );
			queue.remove( 0 );
			return track;
		} 
		return null;
	}
}
