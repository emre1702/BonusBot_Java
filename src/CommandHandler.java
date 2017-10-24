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
						Util.sendMessage ( channel, Language.getLang ( "yes", event.getAuthor(), event.getGuild() ) );
					else if ( rnd <= 6 )
						Util.sendMessage( channel, Language.getLang ( "no", event.getAuthor(), event.getGuild() ) );
					else if ( rnd == 7 )
						Util.sendMessage( channel, Language.getLang ( "not_sure", event.getAuthor(), event.getGuild() ) );
					else if ( rnd == 8 )
						Util.sendMessage( channel, Language.getLang ( "maybe", event.getAuthor(), event.getGuild() ) );
					else if ( rnd == 9 )
						Util.sendMessage( channel, Language.getLang ( "nah", event.getAuthor(), event.getGuild() ) );
					else if ( rnd == 10 )
						Util.sendMessage( channel, Language.getLang ( "absolutely", event.getAuthor(), event.getGuild() ) );
					else if ( rnd == 11 ) {
						Util.sendMessage( channel, Language.getLang ( "stupid_question", event.getAuthor(), event.getGuild() )+ServerEmoji.haha );
						event.getMessage().addReaction( ReactionEmoji.of( "haha", ServerEmoji.hahacode ));
					} else 
						Util.sendMessage( channel, Language.getLang ( "ask_again", event.getAuthor(), event.getGuild() ) );		
				} else {
					Util.sendMessage( channel, Language.getLang ( "what_is_question", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
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
							Util.sendMessage( event.getChannel(), Language.getLang ( "You_not_in_voice_channel", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
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
							Util.sendMessage( event.getChannel(), Language.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
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
							Util.sendMessage( event.getChannel(), Language.getLang ( "You_not_in_voice_channel", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
							return;
						}
		            }
		            
		            if ( args.size() > 0 ) {
			            // Turn the args back into a string separated by space
			            String searchStr = String.join(" ", args);
			
			            boolean addtoqueue = cmd.equals( "queue" ) || cmd.equals( "qplay" ) || cmd.equals( "qyt" );
			            loadAndPlay ( event, searchStr, addtoqueue );
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
							Util.sendMessage( event.getChannel(), Language.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
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
							Util.sendMessage( event.getChannel(), Language.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
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
					skipTrack(event);
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
								Util.sendMessage( event.getChannel(), Language.getLang ( "first_has_to_be_int", event.getAuthor(), event.getGuild() ) );
							}
						} else {
							AudioPlayer player = getGuildAudioPlayer(event.getGuild()).getPlayer();
							Util.sendMessage( event.getChannel(), Language.getLang ( "current_volume", event.getAuthor(), event.getGuild() )+player.getVolume()
								+".\n" + Language.getLang ( "volume_usage_1", event.getAuthor(), event.getGuild() ) 
								+ Settings.prefix+Language.getLang ( "volume_usage_2", event.getAuthor(), event.getGuild() ) );
							
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
						AudioPlayer player = getGuildAudioPlayer(event.getGuild()).getPlayer();
						AudioTrack track = player.getPlayingTrack();
						if ( track != null ) {
							 AudioTrackInfo info = track.getInfo();
							 String length = (int) ( Math.floor( info.length / 60000 ) ) + ":" + (int) ( Math.floor( info.length / 1000 ) % 60 );
							 Util.sendMessage( event.getChannel(), "**"+Language.getLang ( "playing", event.getAuthor(), event.getGuild() ) +":**\n"+info.title+" - "+info.author+"\n"
							 		+ "**"+Language.getLang ( "url", event.getAuthor(), event.getGuild() ) +":**\n"+ info.uri + "\n"
							 		+ "**"+Language.getLang ( "length", event.getAuthor(), event.getGuild() ) +":**\n" + length + "\n" );
						} else {
							Util.sendMessage( event.getChannel(), Language.getLang ( "not_playing_audio", event.getAuthor(), event.getGuild() ) );
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		});
		
		commandMap.put("position", ( cmd, event, args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						if ( args.size() > 0 ) {
							try {
								double trackpospercent = Integer.parseInt( args.get( 0 ) );
								AudioPlayer player = getGuildAudioPlayer(event.getGuild()).getPlayer();
								AudioTrack track = player.getPlayingTrack();
								if ( track != null ) {
									track.setPosition( (long) ( track.getDuration() * ( trackpospercent / 100 ) ) );
								}
							} catch ( NumberFormatException e ) {
								Util.sendMessage( event.getChannel(), Language.getLang ( "first_has_to_be_floating_number", event.getAuthor(), event.getGuild() ) );
							}
						}
					}
				}	
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		});
		
		/*commandMap.put( "testit", ( cmd, event, args ) -> {
			try {
				if ( args.size() > 0 ) {
					if ( args.get ( 0 ).equals( "infos" ) ) {
						Util.sendMessage( event.getChannel(), event.getAuthor().getAvatar() );
						Util.sendMessage( event.getChannel(), event.getAuthor().getAvatarURL() );
						Util.sendMessage( event.getChannel(), event.getAuthor().getClient().getApplicationName() );
						Util.sendMessage( event.getChannel(), event.getAuthor().getClient().getApplicationDescription() );
						Util.sendMessage( event.getChannel(), event.getAuthor().getDiscriminator() );
						Util.sendMessage( event.getChannel(), event.getAuthor().mention() );
					} else 
						Util.sendMessage( event.getChannel(), "Nothing to test here" );
				} else 
					Util.sendMessage( event.getChannel(), "No args" );
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		});*/
		
		
		// Language-Manager //
		Command requestLanguageSectionRole = ( cmd, event, args ) -> {
			try {
				if ( Channels.isLanguageChannel( event.getChannel().getLongID() ) ) {
					IRole role = null;
					switch ( cmd ) {
						case "deutsch":
							if ( Roles.germanRoleID != -1 )
								role = event.getGuild().getRoleByID( Roles.germanRoleID );
							break;
						case "german":
							if ( Roles.germanRoleID != -1 )
								role = event.getGuild().getRoleByID( Roles.germanRoleID );
							break;
						case "türkce":
							if ( Roles.turkishRoleID != -1 )
								role = event.getGuild().getRoleByID( Roles.turkishRoleID );
							break;
						case "turkish":
							if ( Roles.turkishRoleID != -1 )
								role = event.getGuild().getRoleByID( Roles.turkishRoleID );
							break;
						case "english":
							if ( Roles.englishRoleID != -1 )
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
	
	private static void loadAndPlay(final MessageReceivedEvent event, final String trackUrl, final boolean queue ) {
		final IChannel channel = event.getChannel();
	    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

	    AudioLoadResultHandler handler = new AudioLoadResultHandler() {
		      @Override
		      public void trackLoaded(AudioTrack track) {
		        
		        if ( queue ) {
		        	Util.sendMessage(channel, Language.getLang ( "adding_to_queue", event.getAuthor(), event.getGuild() ) + track.getInfo().title);
		        	queue(musicManager, track);
		        } else {
		        	Util.sendMessage(channel, Language.getLang ( "playing", event.getAuthor(), event.getGuild() ) + ": "+ track.getInfo().title);
		        	play(musicManager, track);
		        }
		        
		      }

		      @Override
		      public void playlistLoaded(AudioPlaylist playlist) {
		        AudioTrack firstTrack = playlist.getSelectedTrack();

		        if (firstTrack == null) {
		          firstTrack = playlist.getTracks().get(0);
		        }

		        
		        if ( queue ) {
		        	Util.sendMessage(channel, Language.getLang ( "adding_to_queue", event.getAuthor(), event.getGuild() ) + firstTrack.getInfo().title 
			        		+ " ("+ Language.getLang ( "first_track_of_playlist", event.getAuthor(), event.getGuild() )+ " " + playlist.getName() + ")");
		        	queue(musicManager, firstTrack);
		        } else {
		        	Util.sendMessage(channel, Language.getLang ( "playing", event.getAuthor(), event.getGuild() ) + ": " + firstTrack.getInfo().title 
			        		+ " ("+ Language.getLang ( "first_track_of_playlist", event.getAuthor(), event.getGuild() )+" " + playlist.getName() + ")");
		        	play(musicManager, firstTrack);
		        }
		        
		      }

		      @Override
		      public void noMatches() {
		    	  Util.sendMessage(channel, Language.getLang ( "nothing_found_by", event.getAuthor(), event.getGuild() ) + trackUrl);
		      }

		      @Override
		      public void loadFailed(FriendlyException exception) {
		    	  Util.sendMessage(channel, Language.getLang ( "could_not_play", event.getAuthor(), event.getGuild() ) + exception.getMessage());
		      }
		      
		};
	    playerManager.loadItemOrdered(musicManager, trackUrl, handler );
	  }

	  private static void play(GuildMusicManager musicManager, AudioTrack track) {
	    musicManager.getPlayer().playTrack( track );
	  }
	  
	  private static void queue(GuildMusicManager musicManager, AudioTrack track) {
		  musicManager.getScheduler().queue(track);
	  }

	  private static void skipTrack ( final MessageReceivedEvent event ) {
		final IChannel channel = event.getChannel();
	    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
	    int size = musicManager.getScheduler().getQueue().size();
	    AudioTrack oldtrack = musicManager.getScheduler().nextTrack();
	    
	    if ( oldtrack != null || size > 0 ) {
		    if ( size > 0 ) 
		    	Util.sendMessage ( channel, Language.getLang ( "skipped", event.getAuthor(), event.getGuild() ) );
		    else 
		    	Util.sendMessage ( channel, Language.getLang ( "skipped_nothing_left", event.getAuthor(), event.getGuild() ) );
	    }
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

