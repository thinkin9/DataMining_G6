import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Aprioricopy {
	private Double minSup;
	private Integer minSupThreshold;
	private Integer total;
	private List<Set<String>> dataset;
	private TreeMap<Double, Set<String>> result;

	public Aprioricopy(Double minSup, Integer total, List<Set<String>> dataset) {
		this.minSup = minSup;
		this.total = total;
		this.minSupThreshold = (int) Math.ceil(minSup * total);
		this.dataset = dataset;
		this.result = new TreeMap<>();
	}

	public static void main(String[] args) throws IOException {
		double minSup = Double.parseDouble(args[1]);

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
		Aprioricopy a = new Aprioricopy(minSup, total, Groceries);
		a.run();
		a.result.forEach((count, item) -> {
			System.out.println(item + " " + count);
		});
	}

	public void run() {
		Set<Set<String>> sett = this.initialize();
		Integer cnt = 1;
		Integer max = 10;
		while (cnt < max && !sett.isEmpty()) {
			Set<Set<String>> newSet = combineSet(sett);
			sett = newSet;
			for (Set<String> item : sett) {
				this.result.put((double) CalculateSup(item) / this.total, item);
			}
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
				this.result.put((double) count / this.total, tmp);
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
					if (!newSet.contains(tmp)) {
						Boolean isValid = true;
						for (String str : tmp) {
							Set<String> tmpp = new HashSet<>(tmp);
							tmpp.remove(str);
							if (!data.contains(tmpp)) {
								isValid = false;
								break;
							}
						}
						if (isValid) {
							if (CalculateSup(tmp) >= minSupThreshold) {
								newSet.add(tmp);
							}
						}
					}

				}
			}
			if(CalculateSup(i) < minSup){
                RemoveAll_Include(newSet, i);
            }
		}
		return newSet;
	}

	public void RemoveAll_Include(Set<Set<String>> data, Set<String> target_data) {
        Set<Set<String>> tmp = new HashSet<>(data);
        for(Set<String> i: tmp) {
            if(i.containsAll(target_data)) data.remove(i);
        }
        return;
    }
	/*
	public Set<Set<String>> combineSet(Set<Set<String>> data) {
        Set<Set<String>> set = new HashSet<>();
        for(Set<String> i: data) {
            Set<String> tmp = new HashSet<>(i);
            for(Set<String> j: data) {
                Set<String> next_tmp = new HashSet<>(j);
                next_tmp.removeAll(tmp);
                if(next_tmp.size() == 1) {
                    Set<String> input = new HashSet<>(tmp);
                    input.addAll(next_tmp);
                    set.add(input);
                }
            }
            if(CalculateSup(i) < minSup){
                RemoveAll_Include(set, i);
            }
        }
        return set;   
    }

	public void RemoveAll_Include(Set<Set<String>> data, Set<String> target_data) {
        Set<Set<String>> tmp = new HashSet<>(data);
        for(Set<String> i: tmp) {
            if(i.containsAll(target_data)) data.remove(i);
        }
        return;
    }
	*/

	public double CalculateSup(Set<String> target) {
		Integer cnt = 0;
		for (Set<String> i : this.dataset) {
			if (i.containsAll(target))
				cnt++;
		}
		return cnt;
	}
}