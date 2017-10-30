package bonusbot;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Here is the logger.
 * @author emre1702
 *
 */
public class Logging {
	
	/**
	 * PrintStream for Exception.printStackTrace, 
	 * so the error gets written in discordbot.log file
	 * @return The PrintWriter for printStackTrace
	 */
	public final static PrintStream getPrintWrite ( ) {
		try {
			final FileOutputStream f = new FileOutputStream ( "bonusbot.log", true );
			PrintStream stream = new PrintStream ( f );
			stream.print( Util.getTimestamp()+": " );
			return stream;
		} catch ( FileNotFoundException e ) {
			e.printStackTrace ();
		}
		return null;
	}
}
