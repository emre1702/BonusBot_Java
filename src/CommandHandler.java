import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IVoiceChannel;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavaplayer.*;


class CommandHandler {
	
	private static Map<String, Command> commandMap = new HashMap<String, Command>();
	
	private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private static final Map<Long, GuildMusicManager> musicManagers  = new HashMap<>();

	
	static {
		AudioSourceManagers.registerRemoteSources( playerManager );
		AudioSourceManagers.registerLocalSource( playerManager );
		
		commandMap.put ( "8ball", ( event, args ) -> {
			try { 
				IChannel channel = event.getChannel();
				if ( args.size() > 0 ) {
					int rnd = ThreadLocalRandom.current().nextInt( 1, 12 + 1 );
					
					if ( rnd <= 3 ) 
						Util.sendMessage ( channel, "Yes" );
					else if ( rnd <= 6 )
						Util.sendMessage( channel, "No" );
					else if ( rnd == 7 )
						Util.sendMessage( channel, "Not sure" );
					else if ( rnd == 8 )
						Util.sendMessage( channel, "Maybe" );
					else if ( rnd == 9 )
						Util.sendMessage( channel, "Nah, don't think so" );
					else if ( rnd == 10 )
						Util.sendMessage( channel, "Absolutely" );
					else if ( rnd == 11 ) {
						Util.sendMessage( channel, "Too stupid question "+ServerEmoji.haha );
						event.getMessage().addReaction( ReactionEmoji.of( "haha", ServerEmoji.hahacode ));
					} else 
						Util.sendMessage( channel, "Ask again" );		
				} else {
					Util.sendMessage( channel, "And what exactly is the question? Dafuq "+ServerEmoji.what );
					event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
		
		commandMap.put ( "join", ( event, args ) -> {
			try {
				if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
					IVoiceChannel channel = event.getAuthor().getVoiceStateForGuild( event.getGuild() ).getChannel();
					if ( channel != null ) {
						channel.join();
					} else {
						Util.sendMessage( event.getChannel(), "You aren't even in a voice-channel "+ServerEmoji.what );
						event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
		commandMap.put ( "leave", ( event, args ) -> {
			try {
				if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
					IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
					if ( channel != null ) {
						 TrackScheduler scheduler = getGuildAudioPlayer(event.getGuild()).getScheduler();
				         scheduler.getQueue().clear();
				         scheduler.nextTrack();
				         
				         channel.leave();
						
					} else {
						Util.sendMessage( event.getChannel(), "I'm not even in a voice-channel "+ServerEmoji.what );
						event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
		Command playCommand = (event, args) -> {
			if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
	            IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();
	
	            if(botVoiceChannel == null) {
	            	IVoiceChannel channel = event.getAuthor().getVoiceStateForGuild( event.getGuild() ).getChannel();
					
					if ( channel != null ) {
						channel.join();
					} else {
						Util.sendMessage( event.getChannel(), "You aren't even in a voice-channel "+ServerEmoji.what );
						event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
						return;
					}
	            }
	
	            // Turn the args back into a string separated by space
	            String searchStr = String.join(" ", args);
	
	            loadAndPlay(event.getChannel(), searchStr);
			}
        };
        commandMap.put ( "play", playCommand );
        commandMap.put ( "yt", playCommand );
		
		commandMap.put("skip", (event, args) -> {
			if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
				skipTrack(event.getChannel());
			}
		});
		
		commandMap.put( "volume", (event, args) -> {
			try {
				if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
					if ( args.size() > 0 ) {
						try {
							int volume = Integer.parseInt( args.get( 0 ) );
							AudioPlayer player = getGuildAudioPlayer(event.getGuild()).getPlayer();
							player.setVolume( volume );
						} catch ( NumberFormatException e ) {
							Util.sendMessage( event.getChannel(), "The first argument has to be an integer!" );
						}
					} else {
						AudioPlayer player = getGuildAudioPlayer(event.getGuild()).getPlayer();
						Util.sendMessage( event.getChannel(), "The current volume is: "+player.getVolume()+". Use "+Settings.prefix+"volume [0-100] to change the volume!" );
						
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		});
		
		
		// Language-Manager //
		commandMap.put( "deutsch", ( event, args ) -> {
			try {
				if ( event.getChannel().getLongID() == Channels.languageChannelID ) {
					IRole role = event.getGuild().getRoleByID( Roles.germanRoleID );
					event.getAuthor().addRole( role );
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
		commandMap.put( "türkce", ( event, args ) -> {
			try {
				if ( event.getChannel().getLongID() == Channels.languageChannelID ) {
					IRole role = event.getGuild().getRoleByID( Roles.turkishRoleID );
					event.getAuthor().addRole( role );
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
		commandMap.put( "english", ( event, args ) -> {
			try {
				if ( event.getChannel().getLongID() == Channels.languageChannelID ) {
					IRole role = event.getGuild().getRoleByID( Roles.englishRoleID );
					event.getAuthor().addRole( role );
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
	}
	
	
	private static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
        long guildId = guild.getLongID();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }
	
	private static void loadAndPlay(final IChannel channel, final String trackUrl) {
	    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

	    AudioLoadResultHandler handler = new AudioLoadResultHandler() {
		      @Override
		      public void trackLoaded(AudioTrack track) {
		        Util.sendMessage(channel, "Adding to queue " + track.getInfo().title);

		        play(channel.getGuild(), musicManager, track);
		      }

		      @Override
		      public void playlistLoaded(AudioPlaylist playlist) {
		        AudioTrack firstTrack = playlist.getSelectedTrack();

		        if (firstTrack == null) {
		          firstTrack = playlist.getTracks().get(0);
		        }

		        Util.sendMessage(channel, "Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")");

		        play(channel.getGuild(), musicManager, firstTrack);
		      }

		      @Override
		      public void noMatches() {
		    	  Util.sendMessage(channel, "Nothing found by " + trackUrl);
		      }

		      @Override
		      public void loadFailed(FriendlyException exception) {
		    	  Util.sendMessage(channel, "Could not play: " + exception.getMessage());
		      }
		};
	    playerManager.loadItemOrdered(musicManager, trackUrl, handler );
	  }

	  private static void play(IGuild guild, GuildMusicManager musicManager, AudioTrack track) {
	    musicManager.getScheduler().queue(track);
	  }

	  private static void skipTrack(IChannel channel) {
	    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
	    musicManager.getScheduler().nextTrack();

	    Util.sendMessage(channel, "Skipped to next track.");
	  }
	
	
	 @EventSubscriber
	    public void onMessageReceived ( MessageReceivedEvent event ) {
		 	try {

		        // Note for error handling, you'll probably want to log failed commands with a logger or sout
		        // In most cases it's not advised to annoy the user with a reply incase they didn't intend to trigger a
		        // command anyway, such as a user typing ?notacommand, the bot should not say "notacommand" doesn't exist in
		        // most situations. It's partially good practise and partially developer preference
	
		        // Given a message "/test arg1 arg2", argArray will contain ["/test", "arg1", "arg"]
		        String[] argArray = event.getMessage().getContent().split(" ");
	
		        // First ensure at least the command and prefix is present, the arg length can be handled by your command func
		        if(argArray.length == 0)
		            return;
	
		        // Check if the first arg (the command) starts with the prefix defined in the utils class
		        if(!argArray[0].startsWith(Settings.prefix))
		            return;
	
		        // Extract the "command" part of the first arg out by just ditching the first character
		        String commandStr = argArray[0].substring(1);
	
		        // Load the rest of the args in the array into a List for safer access
		        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
		        argsList.remove(0); // Remove the command
	
		        // Instead of delegating the work to a switch, automatically do it via calling the mapping if it exists
		        if(commandMap.containsKey(commandStr))
		            commandMap.get(commandStr).runCommand(event, argsList);
		 	} catch ( Exception e ) {
		 		e.printStackTrace ( Logging.getPrintWrite() );
		 	}

	    }

}

