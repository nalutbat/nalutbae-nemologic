package com.story4g.nalutbae.nemologic;

import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URLDecoder;

/**
 * An application that parses the HTML of the quiz page
 * at <a href="http://nemonemologic.com">nemonemologic.com</a>
 * and solves the Nemonemologic puzzle on that page.
 *
 * Usage
 * <pre>
 *     $ ./gradlew build
 *     $ java -cp build/libs/nalutbae-nemologic-0.0.1-SNAPSHOT.jar com.story4g.nalutbae.nemologic.NemonemologicDotComQuizParser "http://nemonemologic.com/play_logic.php?quid=43"
 * </pre>
 *  
 * @author nalutbae
 */
public class NemonemologicDotComQuizParser {

	/**
	 * When debugging, pass `isDebug` as a java vm option.
	 * <pre>
	 *     $ java -DisDebug NemonemologicDotComQuizParser
	 * <pre/>
	 */
	public static final boolean IS_DEBUG = Boolean.parseBoolean(System.getProperty("isDebug"));

	private static final String QUIZ_URL = "http://nemonemologic.com/play_logic.php?quid=43";

	/**
	 * parse and solve quiz of  <a href="http://nemonemologic.com">nemonemologic</a> site.
	 *
	 * @param args http://nemonemologic.com/play_logic.php?quid=43 format
	 * @throws Exception any exception
	 */
	public static void main(String[] args) throws Exception {
		String link = (args != null && args.length > 0) ? args[0] : QUIZ_URL;
		Document doc = Jsoup.connect(link).get();
		Element input = doc.getElementById("data-holder");
		String encodedValue = input.attr("value");
		String decodedValue = URLDecoder.decode(encodedValue, "UTF-8");

		JSONObject valueObj = parseJson(decodedValue);
		int[][] hhintsArray = transformArray((JSONArray) valueObj.get("hhints"));
		int[][] vhintsArray = transformArray((JSONArray) valueObj.get("vhints"));

		if (IS_DEBUG) {
			System.out.println(arrayToString(hhintsArray));
			System.out.println(arrayToString(vhintsArray));
		}

		NemoLogicSolver solver = new NemoLogicSolver(hhintsArray, vhintsArray);
		solver.process();
	}

	private static JSONObject parseJson(String json) throws ParseException {
		return (JSONObject) new JSONParser().parse(json);
	}

	private static int[][] transformArray(JSONArray jsonArray) {
		int[][] transposed = new int[jsonArray.size()][];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONArray row = (JSONArray) jsonArray.get(i);
			transposed[i] = new int[row.size()];
			for (int j = 0; j < row.size(); j++) {
				transposed[i][j] = ((Long) row.get(j)).intValue();
			}
		}
		return transposed;
	}

	private static String arrayToString(int[][] array) {
		StringBuilder result = new StringBuilder("{");

		for (int i = 0; i < array.length; i++) {
			result.append("{");

			for (int j = 0; j < array[i].length; j++) {
				result.append(array[i][j]);

				if (j < array[i].length - 1) {
					result.append(", ");
				}
			}

			result.append("}");

			if (i < array.length - 1) {
				result.append(", ");
			}
		}

		result.append("}");

		return result.toString();
	}
}
