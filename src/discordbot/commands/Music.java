package discordbot.commands;

import discordbot.*;
import lavaplayer.TrackScheduler;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IVoiceChannel;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public class Music {
	
	static {
		Handler.commandMap.put ( "join", ( cmd, event, args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						IVoiceChannel channel = event.getAuthor().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							channel.join();
						} else {
							Util.sendMessage( event.getChannel(), Lang.getLang ( "You_not_in_voice_channel", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
						}
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
		} );
		
		Handler.commandMap.put ( "leave", ( cmd, event, args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							 TrackScheduler scheduler = Util.getGuildMusicManager(event.getGuild()).getScheduler();
					         scheduler.getQueue().clear();
					         scheduler.nextTrack();
					         
					         channel.leave();
							
						} else {
							Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
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
							Util.sendMessage( event.getChannel(), Lang.getLang ( "You_not_in_voice_channel", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
							return;
						}
		            }
		            
		            if ( args.size() > 0 ) {
			            // Turn the args back into a string separated by space
			            String searchStr = String.join(" ", args);
			
			            boolean addtoqueue = cmd.equals( "queue" ) || cmd.equals( "qplay" ) || cmd.equals( "qyt" );
			            discordbot.Music.loadAndPlay ( event, searchStr, addtoqueue );
		            } else {
		            	AudioPlayer player = Util.getGuildMusicManager ( event.getGuild() ).getPlayer();
						player.setPaused( false );
						Util.changeMusicInfoStatus( event.getGuild(), "playing" );
		            }
				}
			}
        };
        Handler.commandMap.put ( "play", playCommand );
        Handler.commandMap.put ( "yt", playCommand );
        Handler.commandMap.put ( "queue", playCommand );
        Handler.commandMap.put ( "qplay", playCommand );
        Handler.commandMap.put ( "qyt", playCommand );
        
        Handler.commandMap.put( "pause", ( cmd, event, args ) -> {
        	try {
        		if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							AudioPlayer player = Util.getGuildMusicManager ( event.getGuild() ).getPlayer();
							player.setPaused( !player.isPaused() );
							Util.changeMusicInfoStatus( event.getGuild(), player.isPaused() ? "paused" : "playing" );
						} else {
							Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
						}
					}
        		}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
        });
        
        Handler.commandMap.put( "stop", ( cmd, event, args ) -> {
        	try {
        		if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						IVoiceChannel channel = event.getClient().getOurUser().getVoiceStateForGuild( event.getGuild() ).getChannel();
						if ( channel != null ) {
							TrackScheduler scheduler = Util.getGuildMusicManager(event.getGuild()).getScheduler();
							scheduler.getQueue().clear();
							scheduler.nextTrack();
							Util.changeMusicInfoStatus( event.getGuild(), "stopped" );
						} else {
							Util.sendMessage( event.getChannel(), Lang.getLang ( "I_not_in_voice_channel", event.getAuthor(), event.getGuild() )+ServerEmoji.what );
							event.getMessage().addReaction( ReactionEmoji.of( "what", ServerEmoji.whatcode ));
						}
					}
        		}
			} catch ( Exception e ) {
				e.printStackTrace ( Logging.getPrintWrite() );
			}
        });
		
        Handler.commandMap.put( "skip", (cmd, event, args) -> {
			if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
				if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
					discordbot.Music.skipTrack(event);
				}
			}
		});
		
        Handler.commandMap.put( "volume", (cmd, event, args) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						if ( args.size() > 0 ) {
							try {
								int volume = Integer.parseInt( args.get( 0 ) );
								AudioPlayer player = Util.getGuildMusicManager(event.getGuild()).getPlayer();
								player.setVolume( volume );
								Util.changeMusicInfoVolume( event.getGuild(), volume );
							} catch ( NumberFormatException e ) {
								Util.sendMessage( event.getChannel(), Lang.getLang ( "first_has_to_be_int", event.getAuthor(), event.getGuild() ) );
							}
						} else {
							AudioPlayer player = Util.getGuildMusicManager(event.getGuild()).getPlayer();
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
		
        Handler.commandMap.put( "playing", ( cmd, event, args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						AudioPlayer player = Util.getGuildMusicManager(event.getGuild()).getPlayer();
						AudioTrack track = player.getPlayingTrack();
						if ( track != null ) {
							 AudioTrackInfo info = track.getInfo();
							 String length = (int) ( Math.floor( info.length / 60000 ) ) + ":" + (int) ( Math.floor( info.length / 1000 ) % 60 );
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
		
        Handler.commandMap.put("position", ( cmd, event, args ) -> {
			try {
				if ( Channels.isMusicChannel ( event.getChannel().getLongID() ) ) {
					if ( Roles.canPlayMusic ( event.getAuthor(), event.getGuild() ) ) {
						if ( args.size() > 0 ) {
							try {
								double trackpospercent = Integer.parseInt( args.get( 0 ) );
								AudioPlayer player = Util.getGuildMusicManager(event.getGuild()).getPlayer();
								AudioTrack track = player.getPlayingTrack();
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
