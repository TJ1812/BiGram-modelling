import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 * @author Tej
 *
 */
public class BigramProbabilities {
	/**
	 * HashMaps to store unigram and bigram counts.
	 */
	private Map<String, Integer> uniMap, biMap;

	/**
	 * Reads from file.
	 */
	private Scanner fileReader;

	/**
	 * Initializes the scanner to read from file and set things up for
	 * tokenization.
	 * 
	 * @throws FileNotFoundException
	 */
	public BigramProbabilities(File f) throws FileNotFoundException {
		fileReader = new Scanner(f);
		uniMap = new HashMap<>();
		biMap = new LinkedHashMap<>();
		tokenize();
	}

	/**
	 * Tokenizes the input file by specified delimiter and updates hashmaps.
	 */
	private void tokenize() {
		fileReader.useDelimiter("\\s+");
		String prev = null;
		String cur = null;
		if (fileReader.hasNext()) {
			cur = fileReader.next();
			uniMap.put(cur, 1);
		}
		while (fileReader.hasNext()) {
			prev = cur;
			cur = fileReader.next();
			uniMap.put(cur, uniMap.getOrDefault(cur, 0) + 1);
			biMap.put(prev.concat(" ".concat(cur)), biMap.getOrDefault(prev.concat(" ".concat(cur)), 0) + 1);
		}
	}

	/**
	 * Computes bigram probabilities without smoothing and stores result in
	 * file.
	 * 
	 * @throws IOException
	 */
	public void getBigramProbabilitiesNoSmoothing() throws IOException {
		FileWriter f = new FileWriter("bgp_no_smoothing.txt");
		PrintWriter fileWriter = new PrintWriter(f);

		for (Map.Entry<String, Integer> e : biMap.entrySet()) {
			fileWriter.println(e.getKey() + ", " + e.getValue() + ", "
					+ (((double) e.getValue()) / uniMap.get(e.getKey().split("\\s+")[0])));
		}
		fileWriter.close();
		System.out.println("No-Smoothing probabilities calculation finished, please refer the file created");
	}

	/**
	 * Computes bigram probabilities using add-one smoothing and stores result
	 * in file.
	 * 
	 * @throws IOException
	 */
	public void getBigramProbabilitiesAddOneSmoothing() throws IOException {
		FileWriter f = new FileWriter("bgp_add_one_smoothing.txt");
		PrintWriter fileWriter = new PrintWriter(f);

		for (Map.Entry<String, Integer> e : biMap.entrySet()) {
			fileWriter.println(e.getKey() + ", " + e.getValue() + ", "
					+ ((double) e.getValue() + 1) / (uniMap.size() + uniMap.get(e.getKey().split("\\s+")[0])));
		}
		fileWriter.close();
		System.out.println("Add-One probabilities calculation finished, please refer the file created");
	}

	/**
	 * Computes bigram probabilities using Good-Turing smoothing and stores
	 * result in file.
	 * 
	 * @throws IOException
	 */
	public void getBigramProbabilitiesGTSmoothing() throws IOException {
		FileWriter f = new FileWriter("bgp_gt_smoothing.txt");
		PrintWriter fileWriter = new PrintWriter(f);

		int N = biMap.size(); // total words in corpus

		Map<Integer, Integer> count = new HashMap<>(); // bigram bins
		for (Integer value : biMap.values()) {
			count.put(value, count.getOrDefault(value, 0) + 1);
		}

		for (Map.Entry<String, Integer> e : biMap.entrySet()) {
			int c = e.getValue();
			fileWriter.println(e.getKey() + ", " + c + ", "
					+ (((double) (c + 1) * count.getOrDefault(c + 1, 0)) / count.get(c)) / N);
		}

		fileWriter.close();
		System.out.println("Good-Touring probabilities calculation finished, please refer the file created");
	}

	public static void main(String[] cd) throws IOException {
        if(cd.length != 1) {
            System.out.println("Improper Arguements");
            System.exit(1);
        }
		BigramProbabilities bp = new BigramProbabilities(
				new File(cd[0]));
		bp.getBigramProbabilitiesNoSmoothing(); // No smoothing
		bp.getBigramProbabilitiesAddOneSmoothing(); // Add one smoothing
		bp.getBigramProbabilitiesGTSmoothing(); // Good Turing Smoothing
	}
}
