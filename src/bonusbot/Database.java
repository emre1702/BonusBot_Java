package bonusbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import com.google.gson.Gson;

import bonusbot.guild.GuildExtends;
import bonusbot.user.Mute;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class Database {
	
	private static String connectionString;
	
	private static Connection get() {
		if (connectionString == null) {
			try {
				Class.forName("org.mariadb.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				LogManager.getLogger().error(e);
			}
			String dbhost = Settings.get("mariaDBHost");
			int dbport = Settings.get("mariaDBPort");
			String dbname = Settings.get("mariaDBName");
			String dbuser = Settings.get("mariaDBUser");
			String dbpassword = Settings.get("mariaDBPassword");
			if (dbhost != null && dbuser != null && dbpassword != null && dbname != null && dbport != -1) {
				connectionString = String.format("jdbc:mariadb://%s:%d/%s?user=%s&password=%s", dbhost, dbport, dbname, dbuser, dbpassword);
			} else {
				connectionString = "-";
				return null;
			}
		} else if (connectionString.equals("-")) {
			return null;
		}
		try {
			Connection conn = DriverManager.getConnection(connectionString);
			return conn;
		} catch (SQLException e) {
			LogManager.getLogger().error(e);
			return null;
		}
	}
	
	public static void check() {
		try (Connection conn = Database.get()) {
			if (conn != null) {
				LogManager.getLogger().info("Connecting to database works!");
			}
		} catch (Exception e) {
		}
	}
	
	public static void updateUserRoles(IUser user, IGuild guild) {
		List<IRole> roles = guild.getRolesForUser(user);
		Long[] rolesarray = new Long[roles.size()];	
		int i = 0;
		for (IRole role : roles) {
			rolesarray[i++] = role.getLongID();
		}
		String json = new Gson().toJson(rolesarray);
		String query = "INSERT INTO user (id, guildid, otherroles) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE otherroles = ?";
		try (Connection conn = get(); PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setLong(1, user.getLongID());
			statement.setLong(2, guild.getLongID());
			statement.setString(3, json);
			statement.setString(4, json);
			statement.execute();
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}
	
	public static void updateUserLastTimeInGuild(IUser user, IGuild guild) {
		// only update because the function gets called after updateUserRoles
		String query = "UPDATE user SET lasttimeinguild = NOW() WHERE id = ? AND guildid = ?";
		try (Connection conn = get(); PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setLong(1, user.getLongID());
			statement.setLong(2, guild.getLongID());
			statement.execute();
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}
	
	public static void loadUserDataOnGuildJoin(IUser user, GuildExtends guildext) {
		String query = "SELECT mutetime, otherroles FROM user WHERE id = ? AND guildid = ?";
		try (Connection conn = get(); PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setLong(1, user.getLongID());
			statement.setLong(2, guildext.getGuild().getLongID());
			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					long mutetime = rs.getLong("mutetime");
					String otherrolesjson = rs.getString("otherroles");
					if (otherrolesjson == null) {
						otherrolesjson = "[]";
					}
					Long[] otherroles = new Gson().fromJson(otherrolesjson, Long[].class);
					IGuild guild = guildext.getGuild();
					for (Long id : otherroles) {
						IRole role = guild.getRoleByID(id);
						if (role != null) {
							user.addRole(role);
						}
					}
					Mute.setUserMute(user, guildext, mutetime, false);
				}
			}
			
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}
	
	public static void saveUserMute(IUser user, IGuild guild, long mutetime) {
		String query;
		if (mutetime != 0) {
			String mutetimequery = mutetime == -1 ? "0" : "1";
			String muteendquery = "DATE_ADD(NOW(), INTERVAL "+mutetime+" MINUTE)" ;
			query = "INSERT INTO user (id, guildid, mutetime, muteend) VALUES (?, ?, "+mutetimequery+", "+mutetimequery+") ON DUPLICATE KEY UPDATE mutetime = "+mutetimequery+", muteend = "+muteendquery;
		} else {
			query = "UPDATE user SET mutetime = 0, muteend = NULL WHERE id = ? AND guildid = ?";
		}
		try (Connection conn = get(); PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setLong(1, user.getLongID());
			statement.setLong(2, guild.getLongID());
			statement.execute();
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}
}
