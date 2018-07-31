package bonusbot.guild;

import org.apache.logging.log4j.LogManager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import bonusbot.AudioInfo;
import bonusbot.lavaplayer.GuildAudioManager;
import bonusbot.lavaplayer.Track;
import bonusbot.lavaplayer.TrackScheduler;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.RequestBuffer;

/**
 * Manage the GuildAudioManager for a guild.
 * 
 * @author emre1702
 *
 */
public class AudioManager {
	/** GuildAudioManager for the guild */
	GuildAudioManager manager = null;

	/**
	 * Get the GuildAudioManager for the specific guild.
	 * 
	 * @param guild
	 *            The guild of which we want to get the GuildMusicManager.
	 * @param playerManager
	 *            playerManager of the guild
	 */
	AudioManager(IGuild guild, AudioPlayerManager playerManager) {
		manager = new GuildAudioManager(playerManager, guild);

		manager.getPlayer().addListener(new AudioEventAdapter() {
			/*
			 * @Override public void onPlayerPause ( AudioPlayer player ) {
			 * 
			 * }
			 * 
			 * @Override public void onPlayerResume ( AudioPlayer player ) {
			 * 
			 * }
			 */

			@Override
			public void onTrackStart(AudioPlayer player, AudioTrack track) {
				try {
					TrackScheduler scheduler = manager.getScheduler();
					Track startedTrack = scheduler.getCurrent();
					if (startedTrack != null) {
						EmbedObject object = AudioInfo.createAudioInfo(startedTrack.audio, startedTrack.user, guild,
								startedTrack.date, scheduler);
						GuildExtends guildext = GuildExtends.get(guild);
						IChannel musicinfochannel = guildext.getChannel("audioInfoChannel");
						if (musicinfochannel != null) {
							MessageHistory msghist = musicinfochannel.getFullMessageHistory();
							RequestBuffer.request(() -> {
								if (msghist.isEmpty()) {
									musicinfochannel.sendMessage(object);
								} else {
									msghist.getEarliestMessage().edit(object);
								}
							});
						}
						AudioInfo.changeAudioInfoStatus(guild, "playing");
					}
				} catch (Exception e) {
					LogManager.getLogger().error(e);
				}
			}

			@Override
			public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
				// Only start the next track if the end reason is suitable for it (FINISHED or
				// LOAD_FAILED)
				if (endReason.mayStartNext) {
					manager.getScheduler().nextTrack();
				}
				AudioInfo.changeAudioInfoStatus(guild, "ended");
			}
		});

		guild.getAudioManager().setAudioProvider(manager.getAudioProvider());
	}

}
