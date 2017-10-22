import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

class Util {
	
	static void sendMessage ( IChannel channel, String message ) {
		try {
	        // This might look weird but it'll be explained in another page.
	        RequestBuffer.request(() -> {
	            try{
	                channel.sendMessage(message);
	            } catch (DiscordException e){
	            	e.printStackTrace ( Logging.getPrintWrite() );
	            }
	        });
		} catch ( Exception e ) {
	 		e.printStackTrace ( Logging.getPrintWrite() );
	 	}
    }
	
}
