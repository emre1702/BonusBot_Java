package bonusbot.webhook;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;

import com.sun.net.httpserver.HttpServer;

/**
 * Class to create our HTTP server
 * 
 * @author EmreKara
 */
public class BonusHttpServer {
	private HttpServer server;

	public BonusHttpServer(int port) {
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext("/", new Handlers.RootHandler());
			//server.createContext("/echoHeader", new Handlers.EchoHeaderHandler());
			//server.createContext("/echoGet", new Handlers.EchoGetHandler());
			server.createContext("/echoPost", new Handlers.EchoPostHandler());
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			LogManager.getLogger().error(e);
		}
	}

	/**
	 * Stop the server.
	 */
	public void Stop() {
		server.stop(0);
	}
}
