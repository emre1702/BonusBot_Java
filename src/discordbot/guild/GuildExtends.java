package discordbot.guild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import discordbot.Client;
import discordbot.Settings;
import lavaplayer.GuildAudioManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class GuildExtends {

	private final static Map<Long, GuildExtends> guildExtendsObjects = new HashMap<>();
	private AudioManager audiomanager;
	private IGuild guild;
	
	public GuildExtends ( final IGuild guild ) {
		guildExtendsObjects.put( guild.getLongID(), this );
		this.audiomanager = new AudioManager ( guild, Client.getPlayerManager() );
		this.guild = guild;
	}
	
	public synchronized final static GuildExtends get ( final IGuild guild ) {
		final long guildId = guild.getLongID();
		GuildExtends guildext = guildExtendsObjects.get ( guildId );

        if ( guildext == null ) {
        	guildext = new GuildExtends ( guild );
        }

        return guildext;
	}
	
	public final boolean isAudioChannel ( final Long ID ) {
		final Long audiochannelID = this.getAudioChannelID();
		return ( audiochannelID == null || audiochannelID.equals( ID ) );
	}
	
	public final boolean isLanguageChannel ( final Long ID ) {
		final Long languagechannelID = this.getLanguageChannelID();
		return ( languagechannelID == null || languagechannelID.equals( ID ) );
	} 
	
	public final boolean canPlayAudio ( IUser user ) {
		final Long audioroleID = this.getAudioBotUserRoleID();
		if ( audioroleID == null )
			return true;
		final List<IRole> roles = guild.getRolesForUser( user );
		return roles.contains( guild.getRoleByID( audioroleID ) );
	}
	
	public final GuildAudioManager getAudioManager () {
		return this.audiomanager.manager;
	}
	
	public final Long getLanguageChannelID() {
		if ( Settings.languageChannel != "" ) {
			final List<IChannel> channel = guild.getChannelsByName( Settings.languageChannel );
			if ( !channel.isEmpty() ) {
				return channel.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	public final Long getAudioChannelID() {
		if ( Settings.audioChannel != "" ) {
			final List<IChannel> channel = guild.getChannelsByName( Settings.audioChannel );
			if ( !channel.isEmpty() ) {
				return channel.get( 0 ).getLongID();
			}
		}
		return null;
	}

	public final Long getAudioInfoChannelID() {
		if ( Settings.audioInfoChannel != "" ) {
			final List<IChannel> channel = guild.getChannelsByName( Settings.audioInfoChannel );
			if ( !channel.isEmpty() ) {
				return channel.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	public final Long getGreetUserChannelID() {
		if ( Settings.greetUserChannel != "" ) {
			final List<IChannel> channel = guild.getChannelsByName( Settings.greetUserChannel );
			if ( !channel.isEmpty() ) {
				return channel.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	public final Long getAudioBotUserRoleID() {
		if ( Settings.audiobotUserRole != "" ) {
			final List<IRole> roles = guild.getRolesByName( Settings.audiobotUserRole );
			if ( !roles.isEmpty() ) {
				return roles.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	public final Long getEnglishRoleID() {
		if ( Settings.englishRole != "" ) {
			final List<IRole> roles = guild.getRolesByName( Settings.englishRole );
			if ( !roles.isEmpty() ) {
				return roles.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	public final Long getGermanRoleID() {
		if ( Settings.germanRole != "" ) {
			final List<IRole> roles = guild.getRolesByName( Settings.germanRole );
			if ( !roles.isEmpty() ) {
				return roles.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	public final Long getTurkishRoleID() {
		if ( Settings.turkishRole != "" ) {
			final List<IRole> roles = guild.getRolesByName( Settings.turkishRole );
			if ( !roles.isEmpty() ) {
				return roles.get( 0 ).getLongID();
			}
		}
		return null;
	}
	
	public final IEmoji getWhatEmoji() {
		if ( Settings.whatEmoji != "" ) {
			return guild.getEmojiByName( Settings.whatEmoji );
		}
		return null;
	}
	
	public final IEmoji getHahaEmoji() {
		if ( Settings.hahaEmoji != "" ) {
			return guild.getEmojiByName( Settings.hahaEmoji );
		}
		return null;
	}
	
	public final IEmoji getTadaEmoji() {
		if ( Settings.tadaEmoji != "" ) {
			return guild.getEmojiByName( Settings.tadaEmoji );
		}
		return null;
	}
	

}
