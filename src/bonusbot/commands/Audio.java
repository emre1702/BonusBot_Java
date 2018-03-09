package bonusbot.commands;

import lavaplayer.TrackScheduler;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
	    			bonusbot.Audio.loadAndPlay ( event, file.getCanonicalPath(), true );
	    		} else 
	    			playFolderRecursive ( file.getPath(), event, amountfiles );
    		}
    	}
    }
	
	private final static void loadMusicFolders() {
		try {
			File dir = new File ( musicFolderPath );
			File[] files = dir.listFiles();
			for ( File file : files ) {
				if ( file.isDirectory() ) {
					musicFolders.add( file.getPath().split( "/" )[1] );
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace ( Logging.getPrintWrite() );
		}
	}
	
	/**
	 * Create the audio-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	final static void createAudioCommands () {
		
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
					            bonusbot.Audio.loadAndPlay ( event, searchStr, addtoqueue );
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
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						final IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							final AudioPlayer player = GuildExtends.get(event.getGuild()).getAudioManager().getPlayer();
							boolean paused = cmd.equals( "pause" );
							player.setPaused( paused );
							AudioInfo.changeAudioInfoStatus( event.getGuild(), paused ? "paused" : "playing" );
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
        Handler.commandMap.put ( "pause", pauseResumePlayer );
        Handler.commandMap.put ( "resume", pauseResumePlayer ); 
        
        /** Stops the player */
        final ICommand stopPlayer = ( final String cmd, final MessageReceivedEvent event, final List<String> args ) -> {
        	try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						final IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							final TrackScheduler scheduler = GuildExtends.get(event.getGuild()).getAudioManager().getScheduler();
							scheduler.getQueue().clear();
							scheduler.nextTrack();
							AudioInfo.changeAudioInfoStatus( event.getGuild(), "stopped" );
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
										bonusbot.Audio.queue( guildext.getAudioManager(), track.makeClone(), author );
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
	}
}
