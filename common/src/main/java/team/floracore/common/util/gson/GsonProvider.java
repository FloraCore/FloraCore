package team.floracore.common.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public final class GsonProvider {

	private static final Gson NORMAL = new GsonBuilder().disableHtmlEscaping().create();
	private static final Gson PRETTY_PRINTING = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
	private static final JsonParser NORMAL_PARSER = new JsonParser();

	private GsonProvider() {
		throw new AssertionError();
	}

	public static Gson normal() {
		return NORMAL;
	}

	public static Gson prettyPrinting() {
		return PRETTY_PRINTING;
	}

	public static JsonParser parser() {
		return NORMAL_PARSER;
	}

}
