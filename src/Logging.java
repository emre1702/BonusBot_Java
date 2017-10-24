import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

class Logging {
	
	/**
	 * PrintStream for Exception.printStackTrace, 
	 * so the error gets written in discordbot.log file
	 * @return The PrintStream for printStackTrace
	 */
	static PrintStream getPrintWrite ( ) {
		try {
			File f = new File ( "discordbot.log" );
			if ( !f.exists() ) {
				f.createNewFile();
			}
			return new PrintStream ( f );
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return null;
	}
}
