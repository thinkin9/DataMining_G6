import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Aprioricopy {
	private Integer minSupThreshold; // minimum support threshold (Integer value)
	private Integer total; // total number of transactions
	private List<Set<String>> dataset; // storing database
	private HashMap<Double, List<Set<String>>> result; // storing result

	public Aprioricopy(Double minSup, Integer total, List<Set<String>> dataset) { // initialize class
		this.total = total;
		this.minSupThreshold = (int) Math.ceil(minSup * total); // calculate minimum support threshold based on double
																// value and total number of transactions
		this.dataset = dataset;
		this.result = new HashMap<>();
	}

	public static void main(String[] args) throws IOException {
		double minSup = Double.parseDouble(args[1]); // minimum support threshold (double value)
		// long startTime = System.currentTimeMillis(); // to check execution time
		List<Set<String>> Groceries = new LinkedList<>();
		BufferedReader br = new BufferedReader(new FileReader(args[0]));

		// reading data from csv file and store to Groceries
		String line;
		int total = 0;
		while ((line = br.readLine()) != null) {
			String[] itemList = line.split(",");
			Set<String> items = new HashSet<>();
			for (String item : itemList) {
				items.add(item.trim());
			}
			Groceries.add(items);
			total += 1;
		}
		br.close();
		Aprioricopy a = new Aprioricopy(minSup, total, Groceries); // making a new class
		a.run(); // execute Apriori algorithm
		a.result.entrySet().stream() // Sort with support value and print the result
				.sorted(Map.Entry.<Double, List<Set<String>>>comparingByKey())
				.forEach(entry -> {
					Double support = entry.getKey();
					List<Set<String>> itemList = entry.getValue();
					itemList.forEach(item -> {
						System.out.println(item + " " + support);
					});
				});
		// long endTime = System.currentTimeMillis();
		// System.out.println("Apriori Processing Execution time: " + (endTime -
		// startTime) / 1000.0); //print execution time
	}

	public void run() { // Apriori algorithm
		Set<Set<String>> LKminus1 = this.initialize();
		while (!LKminus1.isEmpty()) {
			Set<Set<String>> CK = combineSet(LKminus1); // candidate itemsets
			Set<Set<String>> CK2 = prune(CK, LKminus1); // pruned candidate itemsets
			Set<Set<String>> LK = new HashSet<>();
			for (Set<String> itemset : CK2) {
				Integer sup = CalculateSup(itemset);
				if (sup >= minSupThreshold) { // check support meets threshold for each candidate
					LK.add(itemset);
					Double t = (double) sup / total;
					List<Set<String>> itemList = result.getOrDefault(t, new ArrayList<>());
					itemList.add(itemset);
					this.result.put(t, itemList);
				}
			}
			LKminus1 = LK;
		}
	}

	// Making L1: generate itemsets with single item
	public Set<Set<String>> initialize() {
		Map<String, Integer> ItemCountList = new HashMap<>();
		for (Set<String> transaction : this.dataset) { // count each item's occurrence
			for (String item : transaction) {
				ItemCountList.put(item, ItemCountList.getOrDefault(item, 0) + 1);
			}
		}
		Set<Set<String>> init = new HashSet<>();
		ItemCountList.forEach((item, count) -> {
			if (count >= this.minSupThreshold) { // check support meets the threshold
				Set<String> tmp = new HashSet<>();
				tmp.add(item);
				double sup = count;
				sup /= this.total;
				List<Set<String>> itemList = result.getOrDefault(sup, new ArrayList<>());
				itemList.add(tmp);
				this.result.put(sup, itemList);
				init.add(tmp);
			}
		});
		return init;
	}

	// Self-Join procedure, returns candidate set
	public Set<Set<String>> combineSet(Set<Set<String>> data) {
		Set<Set<String>> newSet = new HashSet<>();
		for (Set<String> i : data) {
			for (Set<String> j : data) { // pick two itemsets
				Set<String> tmp = new HashSet<>(i);
				tmp.removeAll(j);
				if (tmp.size() == 1) { // if the size of set i - j is 1, then size of union of set i and j is k.
					tmp.addAll(j);
					newSet.add(tmp);
				}
			}
		}
		return newSet;
	}

	// prune procedure, returns pruned candidate set
	public Set<Set<String>> prune(Set<Set<String>> CK, Set<Set<String>> LKminus1) {
		Set<Set<String>> result = new HashSet<>();
		for (Set<String> c : CK) {
			boolean isValid = true;
			for (String str : c) { // make subset with k-1 items
				Set<String> tmpp = new HashSet<>(c);
				tmpp.remove(str); // by removing each single item in itemset
				if (!LKminus1.contains(tmpp)) { // if any subset of the itemset is not in L_k-1, prune the itemset
					isValid = false;
					break;
				}
			}
			if (isValid)
				result.add(c);
		}
		return result;
	}

	// calculates Integer support value and returns it
	public Integer CalculateSup(Set<String> target) {
		Integer cnt = 0;
		for (Set<String> i : this.dataset) {
			if (i.containsAll(target))
				cnt++;
		}
		return cnt;
	}
}