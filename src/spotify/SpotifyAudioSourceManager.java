
/*
 * Skybot, a multipurpose discord bot
 *      Copyright (C) 2017 - 2018  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan & Maurice R S "Sanduhr32"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package spotify;

import java.util.List;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.impl.client.HttpClientBuilder;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import org.apache.http.client.config.RequestConfig;

import bonusbot.Logging;

public class SpotifyAudioSourceManager implements AudioSourceManager, HttpConfigurable {

	private static String PROTOCOL_REGEX = "?:spotify:(track:)|(http://|https://)[a-z]+\\.";
	private static String DOMAIN_REGEX = "spotify\\.com/";
	private static String TRACK_REGEX = "track/";
	private static String ALBUM_REGEX = "album/";
	private static String PLAYLIST_REGEX = "user/(.*)/playlist/";
	private static String REST_REGEX = "(.*)";
	private static String SPOTIFY_BASE_REGEX = PROTOCOL_REGEX + DOMAIN_REGEX;

	private static Pattern SPOTIFY_TRACK_REGEX = Pattern
			.compile("^(" + SPOTIFY_BASE_REGEX + TRACK_REGEX + ")" + REST_REGEX + "$");
	private static Pattern SPOTIFY_ALBUM_REGEX = Pattern
			.compile("^(" + SPOTIFY_BASE_REGEX + ALBUM_REGEX + ")" + REST_REGEX + "$");
	private static Pattern SPOTIFY_PLAYLIST_REGEX = Pattern
			.compile("^(" + SPOTIFY_BASE_REGEX + ")" + PLAYLIST_REGEX + REST_REGEX + "$");

	private SpotifyApi api;
	private ClientCredentialsRequest clientCredentialsRequest;
	private YoutubeAudioSourceManager youtubeAudioSourceManager;
	private ScheduledExecutorService service;
	private YouTube youtube;
	private String youtubeApiKey;

	public SpotifyAudioSourceManager(String clientID, String clientSecret, String youtubeApiKey,
			YoutubeAudioSourceManager youtubeAudioSourceManager) {
		this.youtubeAudioSourceManager = youtubeAudioSourceManager;
		this.youtubeApiKey = youtubeApiKey;
		api = new SpotifyApi.Builder().setClientId(clientID).setClientSecret(clientSecret).build();
		clientCredentialsRequest = api.clientCredentials().build();
		service = Executors.newScheduledThreadPool(1, r -> new Thread(r, "Spotify-Token-Update-Thread"));
		service.scheduleAtFixedRate(this::updateAccesToken, 0, 1, TimeUnit.HOURS);
		youtube = getYouTubeService();
	}

	private YouTube getYouTubeService() {
		try {
			return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(),
					JacksonFactory.getDefaultInstance(), (unused) -> {
					}).setApplicationName("SkyBot-youtube-search").build();
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace(Logging.getPrintWrite());
			return null;
		}
	}

	private List<SearchResult> searchYoutube(String query) throws IOException {
		return youtube.search().list("id,snippet").setKey(youtubeApiKey).setQ(query).setType("video")
				.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)").setMaxResults(1L)
				.execute().getItems();
	}

	private Video getVideoById(String videoID) throws Exception {
		return youtube.videos().list("snippet,statistics,contentDetails").setId(videoID).setKey(youtubeApiKey).execute()
				.getItems().get(0);
	}

	@Override
	public String getSourceName() {
		return "spotify";
	}

	@Override
	public void configureRequests(Function<RequestConfig, RequestConfig> configurator) {
		// not needed
	}

	@Override
	public void configureBuilder(Consumer<HttpClientBuilder> configurator) {
		// also not needed
	}

	@Override
	public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {

		if (isSpotifyAlbum(reference.identifier)) {
			if (youtube == null)
				return null;
			Matcher res = SPOTIFY_ALBUM_REGEX.matcher(reference.identifier);
			if (res.matches()) {

				try {
					List<AudioTrack> playList = new ArrayList<>();
					Album album = (Album) api.getAlbum(res.group(res.groupCount())).build().executeAsync().get();
					for (TrackSimplified t : album.getTracks().getItems()) {
						List<SearchResult> results = searchYoutube(
								album.getArtists()[0].getName() + " - " + t.getName());
						playList.addAll(doThingWithPlaylist(results));
					}
					return new BasicAudioPlaylist(album.getName(), playList, playList.get(0), false);
				} catch (Exception e) {
					// logger.error("Something went wrong!", e);
					throw new FriendlyException(e.getMessage(), FriendlyException.Severity.FAULT, e);
					// return null;
				}
			}
		} else if (isSpotifyPlaylist(reference.identifier)) {
			if (youtube == null)
				return null;
			Matcher res = SPOTIFY_PLAYLIST_REGEX.matcher(reference.identifier);
			if (res.matches()) {

				try {
					List<AudioTrack> finalPlaylist = new ArrayList<>();
					Playlist spotifyPlaylist = (Playlist) api
							.getPlaylist(res.group(res.groupCount() - 1), res.group(res.groupCount())).build()
							.executeAsync().get();
					for (PlaylistTrack playlistTrack : spotifyPlaylist.getTracks().getItems()) {
						List<SearchResult> results = searchYoutube(playlistTrack.getTrack().getArtists()[0].getName()
								+ " - " + playlistTrack.getTrack().getName());
						finalPlaylist.addAll(doThingWithPlaylist(results));
					}
					return new BasicAudioPlaylist(spotifyPlaylist.getName(), finalPlaylist, finalPlaylist.get(0),
							false);
				} catch (Exception e) {
					// logger.error("Something went wrong!", e);
					throw new FriendlyException(e.getMessage(), FriendlyException.Severity.FAULT, e);
					// return null;
				}
			}
		} else if (isSpotyfyTrack(reference.identifier)) {
			if (youtube == null)
				return null;
			Matcher res = SPOTIFY_TRACK_REGEX.matcher(reference.identifier);
			if (res.matches()) {

				try {
					Track track = (Track) api.getTrack(res.group(res.groupCount())).build().executeAsync().get();
					List<SearchResult> results = searchYoutube(
							track.getArtists()[0].getName() + " - " + track.getName());
					Video v = getVideoById(results.get(0).getId().getVideoId());
					return new SpotifyAudioTrack(new AudioTrackInfo(v.getSnippet().getTitle(),
							v.getSnippet().getChannelId(), toLongDuration(v.getContentDetails().getDuration()),
							v.getId(), false, "https://youtube.com/watch?v=" + v.getId()), youtubeAudioSourceManager);
					// return
					// youtubeSearchProvider.loadSearchResult(track.getArtists().get(0).getName() +
					// " - "+ track.getName());
				} catch (Exception e) {
					// logger.error("Something went wrong!", e);
					throw new FriendlyException(e.getMessage(), FriendlyException.Severity.FAULT, e);
					// return null;
				}
			}
		}

		/*
		 * if(isSpotifyAlbum(reference.identifier)) { if(this.youtubeAudioSourceManager
		 * == null) return null; Matcher res =
		 * SPOTIFY_ALBUM_REGEX.matcher(reference.identifier); if (res.matches()) { try {
		 * List<AudioTrack> playList = new ArrayList<>(); Album album =
		 * api.getAlbum(res.group(res.groupCount())).build().get(); for(SimpleTrack t :
		 * album.getTracks().getItems()){ String fakeUrl =
		 * album.getArtists().get(0).getName() + " - "+ t.getName(); List<AudioTrack>
		 * tracks =
		 * ((AudioPlaylist)youtubeSearchProvider.loadSearchResult(fakeUrl)).getTracks();
		 * if(tracks.size() > 0) playList.add(tracks.get(0)); } return new
		 * BasicAudioPlaylist(album.getName(), playList, playList.get(0), false); }
		 * catch (Exception e) { //logger.error("Something went wrong!", e); throw new
		 * FriendlyException(e.getMessage(), FriendlyException.Severity.FAULT, e);
		 * //return null; } } } else if(isSpotifyPlaylist(reference.identifier)) {
		 * if(this.youtubeAudioSourceManager == null) return null; Matcher res =
		 * SPOTIFY_PLAYLIST_REGEX.matcher(reference.identifier); if (res.matches()) {
		 * try { List<AudioTrack> finalPlaylist = new ArrayList<>(); final
		 * Playlist spotifyPlaylist = api.getPlaylist(res.group(res.groupCount()-1),
		 * res.group(res.groupCount())).build().get(); for(PlaylistTrack playlistTrack :
		 * spotifyPlaylist.getTracks().getItems()){ String fakeUrl =
		 * playlistTrack.getTrack().getArtists().get(0).getName() + " - " +
		 * playlistTrack.getTrack().getName(); System.out.println(fakeUrl);
		 * List<AudioTrack> tracks =
		 * ((AudioPlaylist)youtubeSearchProvider.loadSearchResult(fakeUrl)).getTracks();
		 * if(tracks.size() > 0) finalPlaylist.add(tracks.get(0)); } return new
		 * BasicAudioPlaylist(spotifyPlaylist.getName(), finalPlaylist,
		 * finalPlaylist.get(0), false); } catch (Exception e) {
		 * //logger.error("Something went wrong!", e); throw new
		 * FriendlyException(e.getMessage(), FriendlyException.Severity.FAULT, e);
		 * //return null; } } } else if(isSpotyfyTrack(reference.identifier)) {
		 * if(this.youtubeAudioSourceManager == null) return null; Matcher res =
		 * SPOTIFY_TRACK_REGEX.matcher(reference.identifier); if (res.matches()) { try {
		 * Track track = api.getTrack(res.group(res.groupCount())).build().get();
		 * return
		 * youtubeSearchProvider.loadSearchResult(track.getArtists().get(0).getName() +
		 * " - "+ track.getName()); } catch (Exception e) {
		 * //logger.error("Something went wrong!", e); throw new
		 * FriendlyException(e.getMessage(), FriendlyException.Severity.FAULT, e);
		 * //return null; } } }
		 */
		return null;
	}

	@Override
	public boolean isTrackEncodable(AudioTrack track) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void encodeTrack(AudioTrack track, DataOutput output) {

	}

	@Override
	public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
		return new SpotifyAudioTrack(trackInfo, youtubeAudioSourceManager);
	}

	@Override
	public void shutdown() {
		if (this.youtubeAudioSourceManager != null)
			this.youtubeAudioSourceManager.shutdown();
		if (this.service != null)
			this.service.shutdown();

	}

	private boolean isSpotyfyTrack(String input) {
		return SPOTIFY_TRACK_REGEX.matcher(input).matches();
	}

	private boolean isSpotifyAlbum(String input) {
		return SPOTIFY_ALBUM_REGEX.matcher(input).matches();
	}

	private boolean isSpotifyPlaylist(String input) {
		return SPOTIFY_PLAYLIST_REGEX.matcher(input).matches();
	}

	private void updateAccesToken() {
		try {
			Future<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();
			ClientCredentials clientCredentials = clientCredentialsFuture.get();
			api.setAccessToken(clientCredentials.getAccessToken());
		} catch (Exception e) {
			e.printStackTrace(Logging.getPrintWrite());
		}
	}

	private List<AudioTrack> doThingWithPlaylist(List<SearchResult> results) throws Exception {
		List<AudioTrack> playList = new ArrayList<>();
		if (results.size() > 0) {
			SearchResult video = results.get(0);
			ResourceId rId = video.getId();
			if (rId.getKind().equals("youtube#video")) {
				Video v = getVideoById(video.getId().getVideoId());
				playList.add(new SpotifyAudioTrack(
						new AudioTrackInfo(v.getSnippet().getTitle(), v.getSnippet().getChannelId(),
								toLongDuration(v.getContentDetails().getDuration()), video.getId().getVideoId(), false,
								"https://youtube.com/watch?v=" + video.getId().getVideoId()),
						youtubeAudioSourceManager));
			}
		}
		return playList;
	}

	private long toLongDuration(String dur) {
		String time = dur.substring(2);
		long duration = 0L;
		Object[][] indexs = new Object[][] { { "H", 3600 }, { "M", 60 }, { "S", 1 } };
		for (Object[] index1 : indexs) {
			int index = time.indexOf((String) index1[0]);
			if (index != -1) {
				String value = time.substring(0, index);
				duration += Integer.parseInt(value) * (int) index1[1] * 1000;
				time = time.substring(value.length() + 1);
			}
		}
		return duration;
	}
}
