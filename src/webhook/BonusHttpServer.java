package webhook;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import bonusbot.Logging;

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
			e.printStackTrace(Logging.getPrintWrite());
		}
	}

	/**
	 * Stop the server.
	 */
	public void Stop() {
		server.stop(0);
	}
}
