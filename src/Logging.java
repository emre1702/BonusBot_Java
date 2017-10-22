import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

class Logging {
	
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
