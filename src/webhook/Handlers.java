package webhook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import bonusbot.guild.GuildExtends;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Class to handle data from HTTP.
 * 
 * @author EmreKara
 */
public class Handlers {
	public static class RootHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			String response = "<h1>Server start success</h1>";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());

			os.close();
		}
	}

	/*public static class EchoHeaderHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			Headers headers = he.getRequestHeaders();
			Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
			String response = "";
			for (Map.Entry<String, List<String>> entry : entries)
				response += entry.toString() + "\n";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
		}
	}

	public static class EchoGetHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();
			URI requestedUri = he.getRequestURI();
			String query = requestedUri.getRawQuery();
			parseQuery(query, parameters);
			// send response
			String response = "";
			for (String key : parameters.keySet())
				response += key + " = " + parameters.get(key) + "\n";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();

		}

	}*/

	public static class EchoPostHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("Served by /echoPost handler...");
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			parseQuery(query, parameters);
			List<EmbedObject> objs = getEmbedObject(parameters);
			GuildExtends.sendWebhookInfosToAllGuilds(objs);
			// send response
			String response = "";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();

		}
	}

	private static Gson gson = new Gson();

	/** Define in these classes, what you want to retrieve from the informations */
	class WhatWeWant {
		SenderInfos sender;
		String ref;
		RepositoryInfo repository;
		HeadCommit head_commit;
		CommitInfo[] commits;
	}

	class SenderInfos {
		String login;
		String avatar_url;
		String url;
	}

	class RepositoryInfo {
		String name;
	}

	class HeadCommit {
		String url;
	}

	class CommitInfo {
		String id;
		String message;
		String url;
		CommitterInfo committer;
	}

	class CommitterInfo {
		String name;
	}

	/**
	 * Used to create the EmbedObject from the informations.
	 * 
	 * @param parameters
	 * @return
	 */
	public static List<EmbedObject> getEmbedObject(Map<String, Object> parameters) {
		List<EmbedObject> list = new ArrayList<EmbedObject>();
		try {
			WhatWeWant obj = gson.fromJson((String) parameters.get("payload"), WhatWeWant.class);

			EmbedBuilder builder = new EmbedBuilder();
			builder.withAuthorIcon(obj.sender.avatar_url);
			builder.withAuthorName(obj.sender.login);
			builder.withAuthorUrl(obj.sender.url);
			builder.withColor(0, 0, 150);

			String[] splitedref = obj.ref.split(Pattern.quote("/"));
			builder.withTitle("[" + obj.repository.name + ":" + splitedref[splitedref.length - 1] + "] "
					+ obj.commits.length + " new commit(s).");
			builder.withUrl(obj.head_commit.url);

			String content = "";
			for (int i = 0; i < obj.commits.length; ++i) {
				CommitInfo commit = obj.commits[i];
				String msg = "[`" + commit.id.substring(0, 7) + "`](" + commit.url + ") " + commit.message + " - "
						+ commit.committer.name + "\n";
				if ((content + msg).length() > EmbedBuilder.DESCRIPTION_CONTENT_LIMIT) {
					builder.withDescription(content);
					list.add(builder.build());
					content = "";
				}
				content = content + msg;
			}
			if (!content.isEmpty()) {
				builder.withDescription(content);
				list.add(builder.build());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return list;
	}

	/** Don't touch it */
	@SuppressWarnings("unchecked")
	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
}
