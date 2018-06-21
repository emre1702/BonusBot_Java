package bonusbot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import bonusbot.guild.GuildExtends;
import lavaplayer.GuildAudioManager;
import lavaplayer.TrackScheduler;
import spotify.SpotifyAudioSourceManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

/**
 * Audio-class.
 * 
 * @author emre1702
 *
 */
public class Audio {

	/** AudioPlayerManager for LavaPlayer */
	private static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private static YoutubeSearchProvider youtubeSearch;

	/** Register sources of audio for LavaPlayer and load YoutubeSearchProvider */
	static {
		YoutubeAudioSourceManager youtubemanger = new YoutubeAudioSourceManager();

		playerManager.registerSourceManager(youtubemanger);
		String spotifyClientID = Settings.get("spotifyClientID");
		String spotifyClientSecret = Settings.get("spotifyClientSecret");
		String youtubeAPIKey = Settings.get("youtubeAPIKey");
		if (spotifyClientID != null && spotifyClientSecret != null && youtubeAPIKey != null) {
			playerManager.registerSourceManager(
					new SpotifyAudioSourceManager(spotifyClientID, spotifyClientSecret, youtubeAPIKey, youtubemanger));
		}
		playerManager.registerSourceManager(new HttpAudioSourceManager());
		playerManager.registerSourceManager(new VimeoAudioSourceManager());
		playerManager.registerSourceManager(new BandcampAudioSourceManager());
		playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
		playerManager.registerSourceManager(new BeamAudioSourceManager());
		playerManager.registerSourceManager(new SoundCloudAudioSourceManager());

		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
		youtubeSearch = new YoutubeSearchProvider(playerManager.source(YoutubeAudioSourceManager.class));
	}

	/**
	 * Get the AudioPlayerManager for the bot from LavaPlayer.
	 * 
	 * @return AudioPlayerManager
	 */
	public static AudioPlayerManager getPlayerManager() {
		return playerManager;
	}

	/**
	 * Get the YoutubeSearchProvider to be able to search for Youtube-Videos (from
	 * LavaPlayer).
	 * 
	 * @return YoutubeSearchProvider
	 */
	public static YoutubeSearchProvider getYoutubeSearchProvider() {
		return youtubeSearch;
	}

	/**
	 * Loads an audio and plays it afterwards.
	 * 
	 * @param event
	 *            The event to determine the channel (information-output), guild and
	 *            the author (for language).
	 * @param trackUrl
	 *            URL of the audio the author want to have played.
	 * @param queue
	 *            If it should be added to the queue or played right now.
	 */
	public static void loadAndPlay(MessageReceivedEvent event, String trackUrl, boolean isqueue, boolean isplaylist) {
		IChannel channel = event.getChannel();
		GuildAudioManager audioManager = GuildExtends.get(channel.getGuild()).getAudioManager();
		IGuild guild = event.getGuild();
		IUser author = event.getAuthor();

		AudioLoadResultHandler handler = new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				if (isqueue) {
					Util.sendMessage(channel,
							Lang.getLang("adding_to_queue", event.getAuthor(), guild) + track.getInfo().title);
					queue(guild, audioManager, track, author, true);
				} else {
					Util.sendMessage(channel, Lang.getLang("playing", author, guild) + ": " + track.getInfo().title);
					play(audioManager, track, author);
				}
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				if (isplaylist) {
					int i = 0;
					if (isqueue) {
						for (AudioTrack track : playlist.getTracks()) {
							queue(guild, audioManager, track, author, false);
							if (i == 100)
								break;
							++i;
						}
					} else {
						audioManager.getScheduler().clearQueue();
						for (AudioTrack track : playlist.getTracks()) {
							if (i == 0)
								play(audioManager, track, author);
							else
								queue(guild, audioManager, track, author, false);
							if (i == 100)
								break;
							++i;
						}
					}
					Util.sendMessage(channel,
							Lang.getLang(isqueue ? "adding_to_queue_playlist" : "playing_playlist", author, guild)
									+ playlist.getName());
					AudioInfo.refreshAudioInfoQueue(guild, audioManager.getScheduler());
				} else {
					AudioTrack selectedTrack = playlist.getSelectedTrack();

					if (selectedTrack == null) {
						selectedTrack = playlist.getTracks().get(0);
					}
					trackLoaded(selectedTrack);
					Util.sendMessage(channel, Lang.getLang("how_to_add_playlist", author, guild));
				}
			}

			@Override
			public void noMatches() {
				Util.sendMessage(channel, Lang.getLang("nothing_found_by", author, guild) + trackUrl);
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				Util.sendMessage(channel, Lang.getLang("could_not_play", author, guild) + exception.getMessage());
			}

		};
		playerManager.loadItemOrdered(audioManager, trackUrl, handler);
	}

	/**
	 * Play the AudioTrack
	 * 
	 * @param audioManager
	 *            The GuildAudioManager of the guild.
	 * @param track
	 *            AudioTrack the user want to have played.
	 * @param user
	 *            User who added the track (used later for language).
	 */
	public static void play(GuildAudioManager audioManager, AudioTrack track, IUser user) {
		try {
			TrackScheduler scheduler = audioManager.getScheduler();
			scheduler.play(track, user);
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}

	/**
	 * Puts the AudioTrack into the queue.
	 * 
	 * @param audioManager
	 *            The GuildAudioManager of the guild.
	 * @param track
	 *            AudioTrack the user want to have played.
	 * @param user
	 *            User who added the track (used later for language).
	 */
	public static void queue(IGuild guild, GuildAudioManager audioManager, AudioTrack track, IUser user,
			boolean refreshAudioInfo) {
		try {
			TrackScheduler scheduler = audioManager.getScheduler();
			scheduler.queue(track, user);
			if (refreshAudioInfo)
				AudioInfo.refreshAudioInfoQueue(guild, scheduler);
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}

	/**
	 * Skips the current track.
	 * 
	 * @param event
	 *            Event from command, used for channel (output), guild and author
	 *            (language).
	 */
	public static void skipTrack(MessageReceivedEvent event) {
		IChannel channel = event.getChannel();
		GuildAudioManager audioManager = GuildExtends.get(event.getGuild()).getAudioManager();
		int size = audioManager.getScheduler().getQueue().size();
		AudioTrack oldtrack = audioManager.getScheduler().nextTrack();

		if (oldtrack != null || size > 0) {
			if (size > 0)
				Util.sendMessage(channel, Lang.getLang("skipped", event.getAuthor(), event.getGuild()));
			else {
				Util.sendMessage(channel, Lang.getLang("skipped_nothing_left", event.getAuthor(), event.getGuild()));
				AudioInfo.changeAudioInfoStatus(event.getGuild(), "skipped");
			}
		}
	}

}
