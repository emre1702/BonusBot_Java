package discordbot;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Here is the logger.
 * @author emre1702
 *
 */
public class Logging {
	
	/**
	 * PrintStream for Exception.printStackTrace, 
	 * so the error gets written in discordbot.log file
	 * @return The PrintStream for printStackTrace
	 */
	public final static PrintWriter getPrintWrite ( ) {
		try {
			final FileWriter f = new FileWriter ( "discordbot.log", true );
			f.write( Util.getTimestamp()+": " );
			return new PrintWriter ( new BufferedWriter ( f ) );
		} catch ( FileNotFoundException e ) {
			e.printStackTrace ();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return null;
	}
}
