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
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import lavaplayer.*;


class CommandHandler {
	
	private static Map<String, Command> commandMap = new HashMap<String, Command>();
	
	private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private static final Map<Long, GuildMusicManager> musicManagers  = new HashMap<>();

	
	static {
		AudioSourceManagers.registerRemoteSources( playerManager );
		AudioSourceManagers.registerLocalSource( playerManager );
		
		commandMap.put ( "8ball", ( cmd, event, args ) -> {
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
		
		
		commandMap.put ( "join", ( cmd, event, args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						IVoiceChannel channel = event.getAuthor().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							channel.join();
						} else {
							Util.sendMessage( event.getChannel(), "You aren't even in a voice-channel "+ServerEmoji.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
		commandMap.put ( "leave", ( cmd, event, args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
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
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
		Command playCommand = (cmd, event, args) -> {
			if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
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
		            
		            if ( args.size() > 0 ) {
			            // Turn the args back into a string separated by space
			            String searchStr = String.join(" ", args);
			
			            boolean addtoqueue = cmd == "queue" || cmd == "qplay" || cmd == "qyt";
			            loadAndPlay ( event.getChannel(), searchStr, addtoqueue );
		            } else {
		            	AudioPlayer player = getGuildAudioPlayer ( event.getGuild() ).getPlayer();
						player.setPaused( false );
		            }
				}
			}
        };
        commandMap.put ( "play", playCommand );
        commandMap.put ( "yt", playCommand );
        commandMap.put ( "queue", playCommand );
        commandMap.put ( "qplay", playCommand );
        commandMap.put ( "qyt", playCommand );
        
        commandMap.put( "pause", ( cmd, event, args ) -> {
        	try {
        		if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							AudioPlayer player = getGuildAudioPlayer ( event.getGuild() ).getPlayer();
							player.setPaused( !player.isPaused() );
						} else {
							Util.sendMessage( event.getChannel(), "I'm not even in a voice-channel "+ServerEmoji.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
						}
					}
        		}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
        });
        
        commandMap.put( "stop", ( cmd, event, args ) -> {
        	try {
        		if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							TrackScheduler scheduler = getGuildAudioPlayer(event.getGuild()).getScheduler();
							scheduler.getQueue().clear();
							scheduler.nextTrack();
						} else {
							Util.sendMessage( event.getChannel(), "I'm not even in a voice-channel "+ServerEmoji.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
						}
					}
        		}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
        });
		
		commandMap.put( "skip", (cmd, event, args) -> {
			if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
				if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
					skipTrack(event.getChannel());
				}
			}
		});
		
		commandMap.put( "volume", (cmd, event, args) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
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
							Util.sendMessage( event.getChannel(), "The current volume is: "+player.getVolume()+".\nUse "+Settings.prefix+"volume [0-100] to change the volume!" );
							
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		});
		
		commandMap.put( "playing", ( cmd, event, args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						try {
							AudioPlayer player = getGuildAudioPlayer(event.getGuild()).getPlayer();
							AudioTrack track = player.getPlayingTrack();
							if ( track != null ) {
								 AudioTrackInfo info = track.getInfo();
								 String length = (int) ( Math.floor( info.length / 60000 ) ) + ":" + (int) ( Math.floor( info.length / 1000 ) % 60 );
								 Util.sendMessage( event.getChannel(), "**playing:**\n"+info.title+" - "+info.author+"\n"
								 		+ "**url:**\n"+ info.uri + "\n"
								 		+ "**length:**\n" + length + "\n" );
							} else {
								Util.sendMessage( event.getChannel(), "I'm not playing audio right now." );
							}
						} catch ( NumberFormatException e ) {
							Util.sendMessage( event.getChannel(), "The first argument has to be an integer!" );
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		});
		
		
		// Language-Manager //
		Command requestLanguageSectionRole = ( cmd, event, args ) -> {
			try {
				if ( Channels.isLanguageChannel( event.getChannel().getLongID() ) ) {
					IRole role = null;
					switch ( cmd ) {
						case "deutsch":
							role = event.getGuild().getRoleByID( Roles.germanRoleID );
							break;
						case "german":
							role = event.getGuild().getRoleByID( Roles.germanRoleID );
							break;
						case "türkce":
							role = event.getGuild().getRoleByID( Roles.turkishRoleID );
							break;
						case "turkish":
							role = event.getGuild().getRoleByID( Roles.turkishRoleID );
							break;
						case "english":
							role = event.getGuild().getRoleByID( Roles.englishRoleID );
							break;
					}
					if ( role != null )
						event.getAuthor().addRole( role );
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		commandMap.put( "deutsch", requestLanguageSectionRole );
		commandMap.put( "german", requestLanguageSectionRole );
		commandMap.put( "türkce", requestLanguageSectionRole );
		commandMap.put( "turkish", requestLanguageSectionRole );
		commandMap.put( "english", requestLanguageSectionRole );
		
		
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
	
	private static void loadAndPlay(final IChannel channel, final String trackUrl, final boolean queue ) {
	    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

	    AudioLoadResultHandler handler = new AudioLoadResultHandler() {
		      @Override
		      public void trackLoaded(AudioTrack track) {
		        Util.sendMessage(channel, "Adding to queue " + track.getInfo().title);
		        
		        if ( queue ) {
		        	queue(channel.getGuild(), musicManager, track);
		        } else {
		        	play(channel.getGuild(), musicManager, track);
		        }
		        
		      }

		      @Override
		      public void playlistLoaded(AudioPlaylist playlist) {
		        AudioTrack firstTrack = playlist.getSelectedTrack();

		        if (firstTrack == null) {
		          firstTrack = playlist.getTracks().get(0);
		        }

		        Util.sendMessage(channel, "Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")");
		        
		        if ( queue ) {
		        	queue(channel.getGuild(), musicManager, firstTrack);
		        } else {
		        	play(channel.getGuild(), musicManager, firstTrack);
		        }
		        
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
		musicManager.getScheduler().getQueue().clear();
	    musicManager.getScheduler().queue(track);
	  }
	  
	  private static void queue(IGuild guild, GuildMusicManager musicManager, AudioTrack track) {
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
		            commandMap.get(commandStr).runCommand(commandStr, event, argsList);
		 	} catch ( Exception e ) {
		 		e.printStackTrace ( Logging.getPrintWrite() );
		 	}

	    }

}

