package utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.hus.nlp.tagger.VietnameseMaxentTagger;
import vn.hus.nlp.tagger.VietnameseMaxentTaggerProvider;
import crawler.PostCrawler;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class ParseStopWord {
	private List<String> symbols;
	private Set<String> stopWordsEng;
	private Set<String> stopWordsVN;
	private Set<String> stopWordsVN1;

	private Logger logger;
	private VietnameseMaxentTagger tagger;

	public ParseStopWord() {
		symbols = new ArrayList<String>();
		stopWordsEng = new HashSet<String>();
		stopWordsVN = new HashSet<String>();
		stopWordsVN1 = new HashSet<String>();
		logger = LoggerFactory.getLogger(ParseStopWord.class.getName());
		tagger = new VietnameseMaxentTagger();
	}

	public void initSymbols() {
		symbols.add("!");
		symbols.add("@");
		symbols.add("#");
		symbols.add("$");
		symbols.add("%");
		symbols.add("^");
		symbols.add("&");
		symbols.add("*");
		symbols.add("(");
		symbols.add(")");
		symbols.add(",");
		symbols.add(".");
		symbols.add("/");
		symbols.add(";");
		symbols.add("'");
		symbols.add("[");
		symbols.add("]");
		symbols.add("{");
		symbols.add("}");
		symbols.add("\\");
		symbols.add("|");
		symbols.add(":");
		symbols.add("\"");
		symbols.add(":");
		symbols.add("-");
		symbols.add("_");
		symbols.add("+");
		symbols.add("=");
		symbols.add("?");
	}

	public void initStopWordsEng(String pathStopWords) {
		try (InputStreamReader isr = new InputStreamReader(new FileInputStream(
				pathStopWords), "UTF-8")) {
			BufferedReader reader = new BufferedReader(isr);

			String line = reader.readLine();
			while (line != null) {
				stopWordsEng.add(line);
				line = reader.readLine();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void initStopWordsVN(String pathStopWords) {
		try (InputStreamReader isr = new InputStreamReader(new FileInputStream(
				pathStopWords), "UTF-8")) {
			BufferedReader reader = new BufferedReader(isr);

			String line = reader.readLine();
			while (line != null) {
				stopWordsVN1.add(line.trim());
				StringTokenizer strToken = new StringTokenizer(line);
				String tmp;
				while (strToken.hasMoreTokens()) {
					tmp = strToken.nextToken();
					stopWordsVN.add(tmp);
				}

				line = reader.readLine();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void parseStringWithTagger(HashMap<String, String> hm) {
		Iterator<String> it = hm.keySet().iterator();
		String line = "";
		String id = "";
		while (it.hasNext()) {
			id = it.next();
			line = hm.get(id);
			line = parseWithTagger(line);

			String[] inputs = line.split("\\/(CC|[RLMECITYZX]|Np|Nc|Nu)\\s*");
			String result = "";

			for (int i = 0; i < inputs.length; i++) {
				// System.out.println("Phase 1:" + inputs[i]);
				// Check word is Noun/Verb/Adjective/Pronoun
				if (inputs[i].contains("/N") || inputs[i].contains("/V")
						|| inputs[i].contains("/P") || inputs[i].contains("/A")) {
					String tmp = inputs[i];
					int index = tmp.lastIndexOf("/");

					// xung đột/N quân sự/N // dẫn/V tới
					tmp = tmp.substring(0, index + 2);
					// System.out.println("Phase 2:" + tmp);

					String[] refineInputs = tmp.split("\\/[NVAP]\\s*");
					for (int j = 0; j < refineInputs.length; j++) {
						// System.out.println("Phase 3:" + refineInputs[j]);

						if (!stopWordsVN
								.contains(refineInputs[j].toLowerCase())) {
							if (refineInputs[j].equals("")
									|| refineInputs[j].equals(" ")) {
								continue;
							}

							result += " " + refineInputs[j].toLowerCase();
						}
					}
				}
			} // end for
		}

	}

	private String parseWithTagger(String line) {
		// Remove special symbols
		for (int i = 0; i < symbols.size(); i++) {
			line = line.replace(symbols.get(i), "");
		}

		// remove break line
		line = line.replaceAll("\r|\n", "");
		line = tagger.tagText(line);

		return line;
	}

	public void parseString(HashMap<String, String> hm) {

		Iterator<String> it = hm.keySet().iterator();
		String line = "";
		String id = "";
		while (it.hasNext()) {
			id = it.next();
			line = hm.get(id);
			line = parse(line);

			ExportDataToFile.exportToFile(id, line);
		}

	}

	private String parse(String line) {
		// Remove special symbols
		for (int i = 0; i < symbols.size(); i++) {
			line = line.replace(symbols.get(i), "");
		}

		// remove break line
		line = line.replaceAll("\r|\n", " ");

		// for stopWordsVN1
		Iterator<String> it = stopWordsVN1.iterator();
		while (it.hasNext()) {
			String stopWord = it.next();
			String regex1 = String.format("^%s ", stopWord);
			String regex2 = String.format(" %s ", stopWord);
			String regex3 = String.format(" %s$", stopWord);
			String regex4 = String.format("https?[^\\s]+", "");

			line = line.toLowerCase().replaceAll(regex1, "");
			line = line.toLowerCase().replaceAll(regex2, " ");
			line = line.toLowerCase().replaceAll(regex3, "");
			line = line.toLowerCase().replaceAll(regex4, " ");
		}

		String result = "";
		String tmp = "";

		StringTokenizer strToken = new StringTokenizer(line);
		while (strToken.hasMoreTokens()) {
			tmp = strToken.nextToken();
			if (!stopWordsVN.contains(tmp.toLowerCase()) && !stopWordsEng.contains(tmp.toLowerCase())) {
				if (tmp.equals("") || tmp.equals(" ")) {
					continue;
				}

				result += " " + tmp.toLowerCase();
			}
		}

		return result;
	}
}
