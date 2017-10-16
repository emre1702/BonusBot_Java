import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class Logging {
	
	static PrintWriter getPrintWrite ( ) {
		FileWriter filewriter = null;
		try {
			filewriter = new FileWriter ( "discordbot.log", true );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return new PrintWriter ( filewriter );
	}
}
