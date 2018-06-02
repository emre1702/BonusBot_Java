package bonusbot.commands;

import lavaplayer.Track;
import lavaplayer.TrackScheduler;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import bonusbot.*;
import bonusbot.guild.GuildExtends;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/** 
 * Commands for audio
 * @author emre1702
 *
 */
public class Audio {
	private final static String musicFolderPath = "music/";
	private final static int maxFolderMusicInQueue = 40;
	
	private final static List<String> musicFolders = new ArrayList<String>();
	
	private final static boolean joinVoiceChannel ( MessageReceivedEvent event ) {
		final IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();
		
        if(botVoiceChannel == null) {
        	final IVoiceChannel channel = event.getAuthor().getVoiceStateForGuild( event.getGuild() ).getChannel();
        	
			if ( channel != null ) {
				channel.join();
			} else {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				final IEmoji whatemoji = guildext.getWhatEmoji();
				if ( whatemoji == null ) {
					Util.sendMessage( event.getChannel(), Lang.getLang ( "You_not_in_voice_channel", event.getAuthor(), event.getGuild() ) );
				} else {
					Util.sendMessage( event.getChannel(), Lang.getLang ( "You_not_in_voice_channel", event.getAuthor(), event.getGuild() )+Util.getEmojiString( whatemoji ) );
					event.getMessage().addReaction( ReactionEmoji.of ( whatemoji ));
				}
				return false;
			}
        }
        return true;
	}
	
	/**
	 * Plays the folder recursively.
	 * @param path The path of start-folder.
	 * @param event Event for loadAndPlay function.
	 * @param amountfiles The number of the file - used for safety, so you can't load too many files.
	 * @throws IOException 
	 */
	private final static void playFolderRecursive ( final String path, final MessageReceivedEvent event, AtomicReference<Integer> amountfiles ) throws IOException {
		File currentDir = new File ( path );
    	File[] files = currentDir.listFiles();
    	for ( File file : files ) {
    		if ( amountfiles.get().intValue() < maxFolderMusicInQueue ) {
	    		if ( !file.isDirectory() ) {
	    			amountfiles.set( amountfiles.get() + 1 );
	    			bonusbot.Audio.loadAndPlay ( event, file.getCanonicalPath(), true, false );
	    		} else 
	    			playFolderRecursive ( file.getPath(), event, amountfiles );
    		}
    	}
    }
	
	private final static void loadMusicFolders() {
		try {
			File dir = new File ( musicFolderPath );
			File[] files = dir.listFiles();
			if ( files != null ) {
				for ( File file : files ) {
					if ( file.isDirectory() ) {
						musicFolders.add( file.getPath().split( "/" )[1] );
					}
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace ( Logging.getPrintWrite() );
		}
	}
	
	/**
	 * Stops the music
	 * @param guild
	 */
	private final static void stopAudio( IGuild guild ) {
		GuildExtends.get( guild ).stopTheStopAudioTimer();
		final TrackScheduler scheduler = GuildExtends.get(guild).getAudioManager().getScheduler();
		scheduler.getQueue().clear();
		scheduler.nextTrack();
		AudioInfo.changeAudioInfoStatus( guild, "stopped" );
		AudioInfo.refreshAudioInfoQueue( guild, scheduler );
	}
	
	private final static void pauseresumeAudio( IGuild guild, boolean paused ) {
		GuildExtends guildext = GuildExtends.get(guild);
		final AudioPlayer player = guildext.getAudioManager().getPlayer();
		guildext.stopThePauseResumeAudioTimer();
		player.setPaused( paused );
		AudioInfo.changeAudioInfoStatus( guild, paused ? "paused" : "playing" );
	}
	
	/**
	 * Create the audio-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	final static void createAudioCommands () {
		
		final Map<Character, Integer> timeEndingMultiplicator = new HashMap<Character, Integer>();
        timeEndingMultiplicator.put( 's', 1 );
        timeEndingMultiplicator.put( 'm', 60 );
        timeEndingMultiplicator.put( 'h', 3600 );
		
		loadMusicFolders();
		Handler.commandMap.put ( "refreshfolder", ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
			loadMusicFolders();
		} );
		
		/** Bot joins the voice-channel. */
		final ICommand joinChannel = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
			try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						final IVoiceChannel channel = event.getAuthor().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							channel.join();
						} else {
							final IEmoji whatemoji = guildext.getWhatEmoji();
							if ( whatemoji == null ) {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "You_not_in_voice_channel", event.getAuthor(), event.getGuild() ) );
							} else {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "You_not_in_voice_channel", event.getAuthor(), event.getGuild() )+Util.getEmojiString( whatemoji ) );
								event.getMessage().addReaction( ReactionEmoji.of ( whatemoji ));
							}
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put ( "join", joinChannel );
		
		/** Bot leaves the voice-channel. */
		final ICommand leaveChannel = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
			try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						final IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							final TrackScheduler scheduler = GuildExtends.get(event.getGuild()).getAudioManager().getScheduler();
					         scheduler.getQueue().clear();
					         scheduler.nextTrack();
					         
					         channel.leave();
							
						} else {
							final IEmoji whatemoji = guildext.getWhatEmoji();
							if ( whatemoji == null ) {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() ) );
							} else {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+Util.getEmojiString( whatemoji ) );
								event.getMessage().addReaction( ReactionEmoji.of ( whatemoji ));
							}
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put ( "leave", leaveChannel );
		
		/** Plays audio from a source (URL). */
		final ICommand playAudio = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
			try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {         
						if ( args.size() > 0 ) {
							if ( joinVoiceChannel ( event ) ) {		
					            // Turn the args back into a string separated by space
				            	final String searchStr = String.join(" ", args);
					
				            	final boolean addtoqueue = cmd.equals( "queue" ) || cmd.equals( "qplay" ) || cmd.equals( "qyt" );
					            bonusbot.Audio.loadAndPlay ( event, searchStr, addtoqueue, false );
							}
			            } else {
			            	final AudioPlayer player = GuildExtends.get(event.getGuild()).getAudioManager().getPlayer();
							player.setPaused( false );
							AudioInfo.changeAudioInfoStatus( event.getGuild(), "playing" );
			            }
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace( Logging.getPrintWrite() );
			}
        };
        Handler.commandMap.put ( "play", playAudio );
        Handler.commandMap.put ( "yt", playAudio );
        Handler.commandMap.put ( "queue", playAudio );
        Handler.commandMap.put ( "qplay", playAudio );
        Handler.commandMap.put ( "qyt", playAudio );
        
        /** Plays a playlist from a source (URL). */
		final ICommand playAudioPlaylist = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
			try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {         
						if ( args.size() > 0 ) {
							if ( joinVoiceChannel ( event ) ) {		
					            // Turn the args back into a string separated by space
				            	final String searchStr = String.join(" ", args);
					
				            	final boolean addtoqueue = cmd.equals( "playlistqueue" ) || cmd.equals( "queueplaylist" ) || cmd.equals( "plqueue" ) || cmd.equals( "queuepl" );
					            bonusbot.Audio.loadAndPlay ( event, searchStr, addtoqueue, true );
							}
			            }
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace( Logging.getPrintWrite() );
			}
        };
        Handler.commandMap.put ( "playplaylist", playAudioPlaylist );
        Handler.commandMap.put ( "playlist", playAudioPlaylist );
        Handler.commandMap.put ( "pl", playAudioPlaylist );
        Handler.commandMap.put ( "playlistqueue", playAudioPlaylist );
        Handler.commandMap.put ( "queueplaylist", playAudioPlaylist );
        Handler.commandMap.put ( "plqueue", playAudioPlaylist );
        Handler.commandMap.put ( "queuepl", playAudioPlaylist );
        
        /** Plays a whole folder */
        final ICommand playAudioFolder = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
        	try {
        		final GuildExtends guildext = GuildExtends.get( event.getGuild() );
        		if ( guildext.isAudioChannel( event.getChannel().getLongID() ) ) {
        			if ( guildext.canPlayAudio( event.getAuthor() ) ) {
        				if ( args.size() > 0 ) {
        					final String searchStr = String.join(" ", args);
        					if ( musicFolders.contains( searchStr ) ) {
	        					if ( joinVoiceChannel ( event ) ) {		
						            // Turn the args back into a string separated by space
					            	
					            	AtomicReference<Integer> amountfiles = new AtomicReference<Integer>();
					            	amountfiles.set( 0 );
					            	playFolderRecursive ( musicFolderPath+searchStr, event, amountfiles );
								}
        					}
        				} else {
        					Util.sendMessage( event.getChannel(), "Folders:" );
        					for ( String file : musicFolders ) {
        						Util.sendMessage( event.getChannel(), file );
        					}
        				}
        					
        			}
        		}
        	} catch ( Exception e ) {
				e.printStackTrace( Logging.getPrintWrite() );
			}
        };
        Handler.commandMap.put( "playfolder", playAudioFolder );
        
        /** Pauses or resumes the player */
        final ICommand pauseResumePlayer = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
        	try {
        		IGuild guild = event.getGuild();
				final GuildExtends guildext = GuildExtends.get( guild );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						final IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							if ( args.size() > 0 ) {
								String aftertimestr = args.get( 0 );
								char aftertimeending = aftertimestr.charAt( aftertimestr.length() - 1 );
								try {
									int multiplicator = 1;
									if ( timeEndingMultiplicator.containsKey( aftertimeending ) ) {
										aftertimestr = aftertimestr.substring( 0, aftertimestr.length() - 1);
										multiplicator = timeEndingMultiplicator.get( aftertimeending );
									} 
									int aftertime = Integer.parseInt( aftertimestr ) * multiplicator;
									if ( aftertime > 0 ) {
										guildext.stopThePauseResumeAudioTimer();
										boolean paused = cmd.equals( "pause" );
										guildext.pauseresumeAudioTimer.schedule( new TimerTask() {
								        	@Override
								        	public void run() {
								        		pauseresumeAudio( guild, paused );
								        	}
										}, aftertime * 1000 );
										Util.sendMessage( event.getChannel(), Lang.getLang ( paused ? "will_pause_audio_after_time" : "will_resume_audio_after_time", event.getAuthor(), event.getGuild(), String.valueOf( aftertime ) ) );
										return;
									}  
								} catch ( NumberFormatException e ) {
									Util.sendMessage( event.getChannel(), Lang.getLang ( "first_has_to_be_int", event.getAuthor(), guild ) );
								}
							} 
							pauseresumeAudio( guild, cmd.equals( "pause" ) );
						} else {
							final IEmoji whatemoji = guildext.getWhatEmoji();
							if ( whatemoji == null ) {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), guild ) );
							} else {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), guild )+Util.getEmojiString( whatemoji ) );
								event.getMessage().addReaction( ReactionEmoji.of ( whatemoji ));
							}
						}
					}
        		}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
        };
        Handler.commandMap.put ( "pause", pauseResumePlayer );
        Handler.commandMap.put ( "resume", pauseResumePlayer );

        /** Stops the player */
        final ICommand stopPlayer = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
        	try {
        		IGuild guild = event.getGuild();
				final GuildExtends guildext = GuildExtends.get( guild );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						final IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( guild ).getChannel();
						if ( channel != null ) {
							if ( args.size() > 0 ) {
								String aftertimestr = args.get( 0 );
								char aftertimeending = aftertimestr.charAt( aftertimestr.length() - 1 );
								try {
									int multiplicator = 1;
									if ( timeEndingMultiplicator.containsKey( aftertimeending ) ) {
										aftertimestr = aftertimestr.substring( 0, aftertimestr.length() - 1);
										multiplicator = timeEndingMultiplicator.get( aftertimeending );
									} 
									int aftertime = Integer.parseInt( aftertimestr ) * multiplicator;
									if ( aftertime > 0 ) {
										guildext.stopTheStopAudioTimer();
										guildext.stopAudioTimer.schedule( new TimerTask() {
								        	@Override
								        	public void run() {
								        		stopAudio( guild );
								        	}
										}, aftertime * 1000 );
										Util.sendMessage( event.getChannel(), Lang.getLang ( "will_stop_audio_after_time", event.getAuthor(), event.getGuild(), String.valueOf( aftertime ) ) );
										return;
									}  
								} catch ( NumberFormatException e ) {
									Util.sendMessage( event.getChannel(), Lang.getLang ( "first_has_to_be_int", event.getAuthor(), guild ) );
								}
							} 
							Util.sendMessage( event.getChannel(), Lang.getLang ( "stopped_the_audio", event.getAuthor(), event.getGuild() ) );
							stopAudio( guild );
						} else {
							final IEmoji whatemoji = guildext.getWhatEmoji();
							if ( whatemoji == null ) {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() ) );
							} else {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+Util.getEmojiString( whatemoji ) );
								event.getMessage().addReaction( ReactionEmoji.of ( whatemoji ));
							}
						}
					}
        		}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
        };
        Handler.commandMap.put( "stop", stopPlayer );
        
        /** Stops the player */
        final ICommand removeFromQueue = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
        	try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						if ( args.size() > 0 ) {
							final IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
							if ( channel != null ) {
								try {
									final int queueindex = Integer.parseInt( args.get( 0 ) ) - 1;
									final TrackScheduler scheduler = GuildExtends.get(event.getGuild()).getAudioManager().getScheduler();
									Track deletedsong = scheduler.remove( queueindex );
									if ( deletedsong != null ) {
										Util.sendMessage( event.getChannel(), Lang.getLang( "track_removed_from_queue", event.getAuthor(), event.getGuild() ) + deletedsong.audio.getInfo().title );
										AudioInfo.refreshAudioInfoQueue( event.getGuild(), scheduler );
									} else 
										Util.sendMessage( event.getChannel(), Lang.getLang( "index_not_in_queue", event.getAuthor(), event.getGuild() ) );
								} catch ( NumberFormatException e ) {
									Util.sendMessage( event.getChannel(), Lang.getLang ( "first_has_to_be_int", event.getAuthor(), event.getGuild() ) );
								}
							} else {
								final IEmoji whatemoji = guildext.getWhatEmoji();
								if ( whatemoji == null ) {
									Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() ) );
								} else {
									Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+Util.getEmojiString( whatemoji ) );
									event.getMessage().addReaction( ReactionEmoji.of ( whatemoji ));
								}
							}
						}
					}
        		}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
        };
        Handler.commandMap.put( "delqueue", removeFromQueue );
        Handler.commandMap.put( "removequeue", removeFromQueue );
        Handler.commandMap.put( "entqueue", removeFromQueue );
		
        /** Skips the current playing audio */
        final ICommand skipAudio = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
        	try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						bonusbot.Audio.skipTrack(event);
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put( "skip", skipAudio );
		Handler.commandMap.put( "next", skipAudio );
		
		/** Sets the volume of the player */
        final ICommand setVolume = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
        	try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						if ( args.size() > 0 ) {
							try {
								final int volume = Integer.parseInt( args.get( 0 ) );
								final AudioPlayer player = GuildExtends.get(event.getGuild()).getAudioManager().getPlayer();
								player.setVolume( volume );
								AudioInfo.changeAudioInfoVolume( event.getGuild(), volume );
							} catch ( NumberFormatException e ) {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "first_has_to_be_int", event.getAuthor(), event.getGuild() ) );
							}
						} else {
							final AudioPlayer player = GuildExtends.get(event.getGuild()).getAudioManager().getPlayer();
							Util.sendMessage( event.getChannel(), Lang.getLang ( "current_volume", event.getAuthor(), event.getGuild() )+player.getVolume()
								+".\n" + Lang.getLang ( "volume_usage_1", event.getAuthor(), event.getGuild() ) 
								+ Settings.prefix+Lang.getLang ( "volume_usage_2", event.getAuthor(), event.getGuild() ) );
							
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put( "volume", setVolume );
		
		/** Shows whats getting played */
        final ICommand showPlaying = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
        	try {
				final EmbedObject obj = AudioInfo.getLastAudioInfo( event.getGuild() );
				if ( obj != null ) {
					Util.sendMessage( event.getChannel(), obj );
				} else {
					Util.sendMessage( event.getChannel(), Lang.getLang ( "not_played_audio_so_far", event.getAuthor(), event.getGuild() ) );
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put( "playing", showPlaying );
		
		/** Sets the position of the current audio */
        final ICommand setAudioPosition = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
        	try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						if ( args.size() > 0 ) {
							try {
								final double trackpospercent = Integer.parseInt( args.get( 0 ) );
								final AudioPlayer player = GuildExtends.get(event.getGuild()).getAudioManager().getPlayer();
								final AudioTrack track = player.getPlayingTrack();
								if ( track != null ) {
									track.setPosition( (long) ( track.getDuration() * ( trackpospercent / 100 ) ) );
								}
							} catch ( NumberFormatException e ) {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "first_has_to_be_floating_number", event.getAuthor(), event.getGuild() ) );
							}
						}
					}
				}	
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put("position", setAudioPosition );
		
		/** search YouTube output the list */
		final ICommand searchYoutube = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
			try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						if ( args.size() > 0 ) {	
							int showamount = 5;
							if ( Util.isNumeric( args.get( args.size() - 1 ) ) ) {
								showamount = Integer.parseInt( args.get( args.size() - 1 ) );
								args.remove( args.size() - 1 );
							}
							showamount = showamount > 15 ? 15 : showamount;
							String argstr = String.join( " ", args );
							AudioPlaylist item = (AudioPlaylist) bonusbot.Audio.getYoutubeSearchProvider().loadSearchResult( argstr );
							guildext.ytsearchlist = item;
							Util.sendMessage( event.getChannel(), AudioInfo.getAudioListInfo( event.getGuild(), event.getAuthor(), argstr, item.getTracks(), showamount ) );
						}
					}
				}	
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put( "ytsearch", searchYoutube );
		
		/** plays/queues the YouTube number of ytsearch numbers */
		final ICommand playQueueYoutube = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
			try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						if ( joinVoiceChannel ( event ) ) {		
							if ( guildext.ytsearchlist != null ) {
								int number = 1;
								if ( args.size() > 0 && Util.isNumeric ( args.get( 0 ) ) ) {
									number = Integer.parseInt( args.get( 0 ) );
								}
								List<AudioTrack> tracks = guildext.ytsearchlist.getTracks();
								IChannel channel = event.getChannel();
								IUser author = event.getAuthor();
								if ( number < tracks.size() ) {
									AudioTrack track = tracks.get( number - 1 );
									// clone the track, else you can't play it twice //
									if ( cmd.equals( "ytplay" )) {
										Util.sendMessage(channel, Lang.getLang ( "playing", author, event.getGuild() ) + ": "+ track.getInfo().title);
										bonusbot.Audio.play( guildext.getAudioManager(), track.makeClone(), author );
									} else {
										Util.sendMessage(channel, Lang.getLang ( "adding_to_queue", author, event.getGuild() ) + track.getInfo().title);
										bonusbot.Audio.queue( event.getGuild(), guildext.getAudioManager(), track.makeClone(), author, true );
									}
								}
							}
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		};
		Handler.commandMap.put( "ytplay", playQueueYoutube );
		Handler.commandMap.put( "ytqueue", playQueueYoutube );
		
		final ICommand toggleShuffleMode = (String cmd, MessageReceivedEvent event, List<String> args) -> {
			final GuildExtends guildext = GuildExtends.get( event.getGuild() );
			if (guildext.canPlayAudio(event.getAuthor())) {
				boolean newshuffle = guildext.getAudioManager().getScheduler().toggleShuffle();
				Util.sendMessage(event.getChannel(), Lang.getLang("shuffle_mode_"+(newshuffle ? "on" : "off"), event.getAuthor(), event.getGuild()));
			}
		};
		Handler.commandMap.put("shuffle", toggleShuffleMode);
	}
}
