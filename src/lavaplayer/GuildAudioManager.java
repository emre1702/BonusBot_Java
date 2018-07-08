package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;

import sx.blah.discord.handle.obj.IGuild;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildAudioManager {
	/** AudioPlayer from LavalPlayer */
	private AudioPlayer player;
	/** AudioProvider */
	private AudioProvider provider;
	/** TrackScheduler */
	private TrackScheduler scheduler;

	/**
	 * Creates a player and a track scheduler.
	 * 
	 * @param manager
	 *            Audio player manager to use for creating the player.
	 * @param guild
	 *            Guild for the GuildAudioManager
	 */
	public GuildAudioManager(AudioPlayerManager manager, IGuild guild) {
		player = manager.createPlayer();
		player.setVolume(5);
		provider = new AudioProvider(player);
		scheduler = new TrackScheduler(player, guild);
	}

	/**
	 * Adds a listener to be registered for audio events.
	 * 
	 * @param listener
	 *            The AudioEventListener you want to add.
	 */
	public void addAudioListener(AudioEventListener listener) {
		player.addListener(listener);
	}

	/**
	 * Removes a listener that was registered for audio events.
	 * 
	 * @param listener
	 *            The AudioEventListener you want to remove.
	 */
	public void removeAudioListener(AudioEventListener listener) {
		player.removeListener(listener);
	}

	/**
	 * @return The scheduler for AudioTracks.
	 */
	public TrackScheduler getScheduler() {
		return this.scheduler;
	}

	/**
	 * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
	 */
	public AudioProvider getAudioProvider() {
		return provider;
	}

	/**
	 * Get the AudioPlayer.
	 * 
	 * @return AudioPlayer
	 */
	public AudioPlayer getPlayer() {
		return this.player;
	}
}