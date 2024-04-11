import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.*;

public class A1_G6_t1 {
	private Integer minSupThreshold;
	private Integer total;
	private List<Set<String>> dataset;
	private HashMap<Double, List<Set<String>>> result;

	public A1_G6_t1(Double minSup, Integer total, List<Set<String>> dataset) {
		this.total = total;
		this.minSupThreshold = (int) Math.ceil(minSup * total);
		this.dataset = dataset;
		this.result = new HashMap<>();
	}

	public static void main(String[] args) throws IOException {
		double minSup = Double.parseDouble(args[1]);
		long startTime = System.currentTimeMillis();
		List<Set<String>> Groceries = new LinkedList<>();
		BufferedReader br = new BufferedReader(new FileReader(args[0]));

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
		A1_G6_t1 a = new A1_G6_t1(minSup, total, Groceries);
		a.run();
		a.result.entrySet().stream()
				.sorted(Map.Entry.<Double, List<Set<String>>>comparingByKey())
				.forEach(entry -> {
					Double support = entry.getKey();
					List<Set<String>> itemList = entry.getValue();
					itemList.forEach(item -> {
						System.out.println(item.toString() + " " + support);
					});
				});
		long endTime = System.currentTimeMillis();
		System.out.println("Apriori Processing Execution time: " + (endTime - startTime) / 1000.0);
	}

	public void run() {
		Set<Set<String>> LKminus1 = this.initialize();
		while (!LKminus1.isEmpty()) {
			Set<Set<String>> CK = combineSet(LKminus1);
			Set<Set<String>> CK2 = prune(CK, LKminus1);
			Set<Set<String>> LK = new HashSet<>();
			for (Set<String> itemset : CK2) {
				Integer sup = CalculateSup(itemset);
				if (sup >= minSupThreshold) {
					LK.add(itemset);
					Double t = (double) sup / total;
					List<Set<String>> itemList = result.getOrDefault(t, new ArrayList<>());
					itemList.add(itemset);
					result.put(t, itemList);
				}
			}
			LKminus1 = LK;
		}
	}

	public Set<Set<String>> initialize() {
		Map<String, Integer> ItemCountList = new HashMap<>();
		for (Set<String> transaction : this.dataset) {
			for (String item : transaction) {
				ItemCountList.put(item, ItemCountList.getOrDefault(item, 0) + 1);
			}
		}
		Set<Set<String>> init = new HashSet<>();
		ItemCountList.forEach((item, count) -> {
			if (count >= this.minSupThreshold) {
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

	public Set<Set<String>> combineSet(Set<Set<String>> data) {
		Set<Set<String>> newSet = new HashSet<>();
		for (Set<String> i : data) {
			for (Set<String> j : data) {
				Set<String> tmp = new HashSet<>(i);
				tmp.removeAll(j);
				if (tmp.size() == 1) {
					tmp.addAll(j);
					newSet.add(tmp);
				}
			}
		}
		return newSet;
	}

	public Set<Set<String>> prune(Set<Set<String>> CK, Set<Set<String>> LKminus1) {
		Set<Set<String>> result = new HashSet<>();
		for (Set<String> c : CK) {
			boolean isValid = true;
			for (String str : c) {
				Set<String> tmpp = new HashSet<>(c);
				tmpp.remove(str);
				if (!LKminus1.contains(tmpp)) {
					isValid = false;
					break;
				}
			}
			if (isValid)
				result.add(c);
		}
		return result;
	}

	public void RemoveAll_Include(Set<Set<String>> data, Set<String> target_data) {
		Set<Set<String>> tmp = new HashSet<>(data);
		for (Set<String> i : tmp) {
			if (i.containsAll(target_data))
				data.remove(i);
		}
		return;
	}

	public Integer CalculateSup(Set<String> target) {
		Integer cnt = 0;
		for (Set<String> i : this.dataset) {
			if (i.containsAll(target))
				cnt++;
		}
		return cnt;
	}
}