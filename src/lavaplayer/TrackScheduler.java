package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import sx.blah.discord.handle.obj.IUser;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler {
  private final List<AudioTrack> queue;
  public final List<IUser> userqueue;
  public final List<LocalDateTime> datequeue;
  private final AudioPlayer player;

  public TrackScheduler(AudioPlayer player) {
    // Because we will be removing from the "head" of the queue frequently, a LinkedList is a better implementation
    // since all elements won't have to be shifted after removing. Additionally, choosing to add in between the queue
    // will similarly not cause many elements to shift and wil only require a couple of node changes.
    queue = Collections.synchronizedList(new LinkedList<>());
    userqueue = Collections.synchronizedList( new LinkedList<>() );
    datequeue = Collections.synchronizedList( new LinkedList<>() );
    this.player = player;
  }

  /**
   * Add the next track to queue or play right away if nothing is in the queue.
   *
   * @param track The track to play or add to queue.
   */
  public synchronized boolean queue(AudioTrack track) {
    // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
    // something is playing, it returns false and does nothing. In that case the player was already playing so this
    // track goes to the queue instead.
    boolean playing = player.startTrack(track, true);

    if(!playing) {
      queue.add(track);
    }

    return playing;
  }

  /**
   * Starts the next track, stopping the current one if it is playing.
   * @return The track that was stopped, null if there wasn't anything playing
   */
  public synchronized AudioTrack nextTrack() {
    AudioTrack currentTrack = player.getPlayingTrack();
    AudioTrack nextTrack = queue.isEmpty() ? null : queue.remove(0);

    // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
    // giving null to startTrack, which is a valid argument and will simply stop the player.
    player.startTrack(nextTrack, false);
    return currentTrack;
  }

  /**
   * Returns the queue for this scheduler. Adding to the head of the queue (index 0) does not automatically
   * cause it to start playing immediately. The returned collection is thread-safe and can be modified.
   *
   * @apiNote To iterate over this queue, use a synchronized block. For example:
   * {@code synchronize (getQueue()) { // iteration code } }
   */
  public List<AudioTrack> getQueue() {
    return this.queue;
  }
}
