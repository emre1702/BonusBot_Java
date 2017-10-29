package discordbot.commands;

import discordbot.*;
import discordbot.server.Channels;
import discordbot.server.Roles;
import discordbot.server.Emojis;
import lavaplayer.TrackScheduler;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
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
	 * Create the music-commands.
	 */
	// Load that way so Handler is first fully loaded before creating the commands.
	static void createMusicCommands () {
		
		Handler.commandMap.put ( "join", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						final IVoiceChannel channel = event.getAuthor().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							channel.join();
						} else {
							Util.sendMessage( event.getChannel(), Lang.getLang ( "You_not_in_voice_channel", event.getAuthor(), event.getGuild() )+Emojis.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", Emojis.whatcode ));
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
		Handler.commandMap.put ( "leave", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						final IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							final TrackScheduler scheduler = Util.getGuildMusicManager(event.getGuild()).getScheduler();
					         scheduler.getQueue().clear();
					         scheduler.nextTrack();
					         
					         channel.leave();
							
						} else {
							Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+Emojis.what );
							if ( Emojis.whatcode != -1 )
								event.getMessage().addReaction( ReactionEmoji.of( "what", Emojis.whatcode ));
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
		final Command playCommand = ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
			if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
				if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
					final IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();
		
		            if(botVoiceChannel == null) {
		            	final IVoiceChannel channel = event.getAuthor().getVoiceStateForGuild( event.getGuild() ).getChannel();
						
						if ( channel != null ) {
							channel.join();
						} else {
							Util.sendMessage( event.getChannel(), Lang.getLang ( "You_not_in_voice_channel", event.getAuthor(), event.getGuild() )+Emojis.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", Emojis.whatcode ));
							return;
						}
		            }
		            
		            if ( args.size() > 0 ) {
			            // Turn the args back into a string separated by space
		            	final String searchStr = String.join(" ", args);
			
		            	final boolean addtoqueue = cmd.equals( "queue" ) || cmd.equals( "qplay" ) || cmd.equals( "qyt" );
			            discordbot.Audio.loadAndPlay ( event, searchStr, addtoqueue );
		            } else {
		            	final AudioPlayer player = Util.getGuildMusicManager ( event.getGuild() ).getPlayer();
						player.setPaused( false );
						AudioInfo.changeAudioInfoStatus( event.getGuild(), "playing" );
		            }
				}
			}
        };
        Handler.commandMap.put ( "play", playCommand );
        Handler.commandMap.put ( "yt", playCommand );
        Handler.commandMap.put ( "queue", playCommand );
        Handler.commandMap.put ( "qplay", playCommand );
        Handler.commandMap.put ( "qyt", playCommand );
        
        Handler.commandMap.put( "pause", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
        	try {
        		if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						final IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							final AudioPlayer player = Util.getGuildMusicManager ( event.getGuild() ).getPlayer();
							player.setPaused( !player.isPaused() );
							AudioInfo.changeAudioInfoStatus( event.getGuild(), player.isPaused() ? "paused" : "playing" );
						} else {
							Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+Emojis.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", Emojis.whatcode ));
						}
					}
        		}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
        });
        
        Handler.commandMap.put( "stop", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
        	try {
        		if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						final IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							final TrackScheduler scheduler = Util.getGuildMusicManager(event.getGuild()).getScheduler();
							scheduler.getQueue().clear();
							scheduler.nextTrack();
							AudioInfo.changeAudioInfoStatus( event.getGuild(), "stopped" );
						} else {
							Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+Emojis.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", Emojis.whatcode ));
						}
					}
        		}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
        });
		
        Handler.commandMap.put( "skip", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
			if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
				if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
					discordbot.Audio.skipTrack(event);
				}
			}
		});
		
        Handler.commandMap.put( "volume", ( String cmd, MessageReceivedEvent event, List<String> args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						if ( args.size() > 0 ) {
							try {
								final int volume = Integer.parseInt( args.get( 0 ) );
								final AudioPlayer player = Util.getGuildMusicManager(event.getGuild()).getPlayer();
								player.setVolume( volume );
								AudioInfo.changeAudioInfoVolume( event.getGuild(), volume );
							} catch ( NumberFormatException e ) {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "first_has_to_be_int", event.getAuthor(), event.getGuild() ) );
							}
						} else {
							final AudioPlayer player = Util.getGuildMusicManager(event.getGuild()).getPlayer();
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
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						final AudioPlayer player = Util.getGuildMusicManager(event.getGuild()).getPlayer();
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
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						if ( args.size() > 0 ) {
							try {
								final double trackpospercent = Integer.parseInt( args.get( 0 ) );
								final AudioPlayer player = Util.getGuildMusicManager(event.getGuild()).getPlayer();
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
