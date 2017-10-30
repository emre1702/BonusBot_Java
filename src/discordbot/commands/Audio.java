package discordbot.commands;

import discordbot.*;
import discordbot.guild.GuildExtends;
import lavaplayer.TrackScheduler;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/** 
 * Commands for audio
 * @author emre1702
 *
 */
public class Audio {
	
	/**
	 * Create the audio-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	static void createAudioCommands () {
		
		Handler.commandMap.put ( "join", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
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
		} );
		
		Handler.commandMap.put ( "leave", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
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
		} );
		
		final Command playCommand = ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
			try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						final IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();
			
			            if(botVoiceChannel == null) {
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
								return;
							}
			            }
			            
			            if ( args.size() > 0 ) {
				            // Turn the args back into a string separated by space
			            	final String searchStr = String.join(" ", args);
				
			            	final boolean addtoqueue = cmd.equals( "queue" ) || cmd.equals( "qplay" ) || cmd.equals( "qyt" );
				            discordbot.Audio.loadAndPlay ( event, searchStr, addtoqueue );
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
        Handler.commandMap.put ( "play", playCommand );
        Handler.commandMap.put ( "yt", playCommand );
        Handler.commandMap.put ( "queue", playCommand );
        Handler.commandMap.put ( "qplay", playCommand );
        Handler.commandMap.put ( "qyt", playCommand );
        
        Handler.commandMap.put( "pause", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
        	try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						final IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							final AudioPlayer player = GuildExtends.get(event.getGuild()).getAudioManager().getPlayer();
							player.setPaused( !player.isPaused() );
							AudioInfo.changeAudioInfoStatus( event.getGuild(), player.isPaused() ? "paused" : "playing" );
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
        });
        
        Handler.commandMap.put( "stop", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
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
        });
		
        Handler.commandMap.put( "skip", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
        	try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						discordbot.Audio.skipTrack(event);
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		});
		
        Handler.commandMap.put( "volume", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
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
		});
		
        Handler.commandMap.put( "playing", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
        	try {
				final GuildExtends guildext = GuildExtends.get( event.getGuild() );
				if ( guildext.isAudioChannel ( event.getChannel().getLongID() ) ) {
					if ( guildext.canPlayAudio ( event.getAuthor() ) ) {
						final AudioPlayer player = GuildExtends.get(event.getGuild()).getAudioManager().getPlayer();
						final AudioTrack track = player.getPlayingTrack();
						if ( track != null ) {
							final AudioTrackInfo info = track.getInfo();
							final String length = (int) ( Math.floor( info.length / 60000 ) ) + ":" + (int) ( Math.floor( info.length / 1000 ) % 60 );
							Util.sendMessage( event.getChannel(), "**"+Lang.getLang ( "playing", event.getAuthor(), event.getGuild() ) +":**\n"+info.title+" - "+info.author+"\n"
							 		+ "**"+Lang.getLang ( "url", event.getAuthor(), event.getGuild() ) +":**\n"+ info.uri + "\n"
							 		+ "**"+Lang.getLang ( "length", event.getAuthor(), event.getGuild() ) +":**\n" + length + "\n" );
						} else {
							Util.sendMessage( event.getChannel(), Lang.getLang ( "not_playing_audio", event.getAuthor(), event.getGuild() ) );
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		});
		
        Handler.commandMap.put("position", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
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
		});
	}
}
