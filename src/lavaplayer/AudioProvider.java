package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import sx.blah.discord.handle.audio.AudioEncodingType;
import sx.blah.discord.handle.audio.IAudioProvider;

/**
 * This is a wrapper around AudioPlayer which makes it behave as an
 * IAudioProvider for D4J. As D4J calls canProvide before every call to
 * provide(), we pull the frame in canProvide() and use the frame we already
 * pulled in provide().
 */
public class AudioProvider implements IAudioProvider {
	/** AudioPlayer for LavaPlayer */
	private AudioPlayer audioPlayer;
	/** Last frame of AudioFrame */
	private AudioFrame lastFrame;

	/**
	 * @param audioPlayer
	 *            Audio player to wrap.
	 */
	public AudioProvider(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
	}

	/** When bot is ready. */
	@Override
	public boolean isReady() {
		if (lastFrame == null) {
			lastFrame = audioPlayer.provide();
		}

		return lastFrame != null;
	}

	/** Provide **/
	@Override
	public byte[] provide() {
		if (lastFrame == null) {
			lastFrame = audioPlayer.provide();
		}

		byte[] data = lastFrame != null ? lastFrame.data : null;
		lastFrame = null;

		return data;
	}

	/** get channels */
	@Override
	public int getChannels() {
		return 2;
	}

	/**
	 * get audio-encoding type Use Opus for Discord
	 */
	@Override
	public AudioEncodingType getAudioEncodingType() {
		return AudioEncodingType.OPUS;
	}
}