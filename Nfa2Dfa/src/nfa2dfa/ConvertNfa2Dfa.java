package nfa2dfa;

import java.util.*;

public class ConvertNfa2Dfa {

	private NfaFormalDefination nfaFormalDefination;
	private HashMap<String, HashMap<String, String>> transitionFunction = new HashMap<>();
	private HashMap<String, HashMap<String, String>> transitionFunctionFinal = new HashMap<>();
	private List<List<String>> subsetsList;
	private String[] alphabets;
	private List<String> transitionValueList = new ArrayList<>();
	private List<String> transitionList = new ArrayList<>();
	private String dfaStates;
	private String[][] transitionArray;

	public ConvertNfa2Dfa(ArrayList<String> list) {
		this.nfaFormalDefination = new NfaFormalDefination(list);
		this.transitionFunction = nfaFormalDefination.getTransitionFunction();
		this.alphabets = nfaFormalDefination.getAlphabets();
		run();
	}

	void run() {
		createTransitionFunctionList();
		System.out.println("The formal definition of DFA is as follows\n");
		printDfaStates();
		printDfaAlphabets();
		printDfaTransitionFunction();
		printDfaStartState();
		printDfaAcceptStates();
		userInput();
	}

	void printDfaStates() {
		removeUnaffectingStates();
		System.out.println("Q = " + dfaStates.replace("[", "{").replace("]", ""));
	}

	void printDfaAlphabets() {
		System.out.println("Σ = " + Arrays.toString(alphabets).replace("[", "{").replace("]", "}"));
	}

	void printDfaTransitionFunction() {
		createTransitionArray();
		for (int i = 0; i < subsetsList.size() + 1; i++) {
			for (int j = 0; j < alphabets.length + 1; j++) {
				System.out.print(transitionArray[i][j] + "	");
			}
			System.out.println();
		}
	}

	void printDfaStartState() {
		System.out.println("q0 = {" + getStartState() + "}");
	}

	void printDfaAcceptStates() {
		System.out.println("F = "
				+ getAcceptStates().toString().replace("[", "{").replace("]", "}").replace(", ", ",").replace(",", ", ")
				+ "\n");
	}

	void userInput() {
		Scanner input = new Scanner(System.in);
		String continuation = "Y";
		while (continuation.equalsIgnoreCase("Y")) {
			System.out.println("Please enter the string you want to query..");
			String str = input.nextLine();
			String query = getStartState().replace(" ", "");
			for (int i = 0; i < str.length(); i++) {
				boolean flag = false;
				String value = str.substring(i, i + 1);
				query = query + value;
				for (String transition : transitionList) {
					if (transition.startsWith(query)) {
						flag = true;
						String transitionValue = transition.substring(query.length(), transition.length());
						query = transitionValue;
					}
					if (flag) {
						break;
					}
				}
				if (str.length() - 1 == i) {
					boolean flag2 = false;
					for (int j = 0; j < getAcceptStates().size(); j++) {
						if (getAcceptStates().toArray()[j].toString().equals("[" + query + "]")) {
							flag2 = true;
							System.out.println("Accept!");
						}
					}
					if (!flag2) {
						System.out.println("Reject!");
					}
				}
			}
			System.out.println("continue? (Y/N): ");
			continuation = input.nextLine();
		}
		input.close();
		System.out.println("thanks for coming :)");
	}

	void createTransitionFunctionList() {
		addEpsilonState2TransitionFunction();
		deleteRepetitiveValuesInTransitionFunction();
		startStateTransitionFunction();
		subsetsList = getAllSubsets();
		int listSize = (int) Math.pow(2, nfaFormalDefination.getStates().length);
		for (int i = 0; i < listSize; i++) {
			String firstKey = subsetsList.get(i).toString().replace("[", "").replace("]", "").replace(" ", "");
			for (int j = 0; j < alphabets.length; j++) {
				String term = "";
				if (i == 0) {
					term = term.concat("∅" + alphabets[j] + "∅");
					transitionList.add(term);
					transitionValueList.add("∅");
				} else if (!firstKey.contains(",")) {
					String value = getTransitionValuesArray(firstKey)[j];
					if (!value.equals("∅")) {
						term = term.concat(firstKey + alphabets[j] + value);
						transitionList.add(term);
						transitionValueList.add(value);
					} else {
						term = term.concat(firstKey + alphabets[j] + "∅");
						transitionList.add(term);
					}
				} else {
					List<String> uniqueValueList = new ArrayList<>();
					term = term.concat(firstKey + alphabets[j]);
					String tempValue = "";
					String value = "";
					for (String parseKey : firstKey.split(",", -1)) {
						value = getTransitionValuesArray(parseKey)[j];
						if (!value.equals("∅")) {
							for (String parseValue : value.split(",", -1)) {
								if (!uniqueValueList.contains(parseValue)) {
									uniqueValueList.add(parseValue);
									tempValue = tempValue.concat(parseValue + "-");
								}
							}
						}
					}
					if (!tempValue.equals("")) {
						transitionList.add(term.concat(getSortedString(tempValue)));
						transitionValueList.add(getSortedString(tempValue));
					}
				}
			}
		}
	}

	void addEpsilonState2TransitionFunction() {
		int statesCount = nfaFormalDefination.getStates().length;
		for (int i = 0; i < statesCount; i++) {
			if (!getTransitionValuesArray(nfaFormalDefination.getStates()[i])[alphabets.length].equals("∅")) {
				String epsilonValue = getTransitionValuesArray(nfaFormalDefination.getStates()[i])[alphabets.length];
				for (String parseValue : epsilonValue.split(",", -1)) {
					for (int k = 0; k < alphabets.length + 1; k++) {
						String tempValue = getTransitionValuesArray(parseValue)[k];
						if (!tempValue.equals("∅")) {
							for (String parseValue2 : tempValue.split(",", -1)) {
								if (parseValue2.equals(nfaFormalDefination.getStates()[i]) && (k != alphabets.length)) {
									transitionFunction.get(parseValue).replace(alphabets[k], "{" + tempValue + "}",
											"{" + tempValue + "," + parseValue + "}");
								} else if (k == alphabets.length) {
									transitionFunction.get(nfaFormalDefination.getStates()[i]).replace("ε",
											"{" + epsilonValue + "}", "{" + tempValue + "," + parseValue + "}");
								}
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < statesCount; i++) {
			for (int j = 0; j < alphabets.length; j++) {
				if (!getTransitionValuesArray(nfaFormalDefination.getStates()[i])[j].equals("∅")) {
					String value = getTransitionValuesArray(nfaFormalDefination.getStates()[i])[j];
					for (String parseValue : value.split(",", -1)) {
						if (!getTransitionValuesArray(parseValue)[alphabets.length].equals("∅")) {
							String value2 = getTransitionValuesArray(parseValue)[alphabets.length];
							transitionFunction.get(nfaFormalDefination.getStates()[i]).replace(alphabets[j],
									"{" + value + "}", "{" + value + "," + value2 + "}");
						}
					}
				}
			}
		}
	}

	void deleteRepetitiveValuesInTransitionFunction() {
		int statesCount = nfaFormalDefination.getStates().length;
		for (int i = 0; i < statesCount; i++) {
			for (int k = 0; k < alphabets.length + 1; k++) {
				List<String> tempList = new ArrayList<>();
				String result = "";
				String tempValue = getTransitionValuesArray(nfaFormalDefination.getStates()[i])[k];
				if (!tempValue.equals("∅")) {
					for (String parseValue : tempValue.split(",", -1)) {
						if (!tempList.contains(parseValue)) {
							tempList.add(parseValue);
							result = result.concat(parseValue + "-");
						}
					}
					result = getSortedString(result);
					if (k != alphabets.length) {
						transitionFunction.get(nfaFormalDefination.getStates()[i]).replace(alphabets[k],
								"{" + tempValue + "}", "{" + result + "}");
					} else {
						transitionFunction.get(nfaFormalDefination.getStates()[i]).replace("ε", "{" + tempValue + "}",
								"{" + result + "}");
					}
				}
			}
		}
	}

	void startStateTransitionFunction() {
		transitionFunctionFinal = copyMap();
		String startState = nfaFormalDefination.getStartState();
		if (!getTransitionValuesArray(startState)[alphabets.length].equals("∅")) {
			String tempValue = getTransitionValuesArray(startState)[alphabets.length];
			for (String parseValue : tempValue.split(",", -1)) {
				for (int i = 0; i < alphabets.length + 1; i++) {
					String epsilonTransitionValue = transitionFunctionFinal.get(parseValue).values().toString()
							.replace("[", "").replace("]", "").split(", ", -1)[i];
					if (!epsilonTransitionValue.equals("∅")) {
						String oldValue = transitionFunctionFinal.get(startState).values().toString().replace("[", "")
								.replace("]", "").split(", ", -1)[i];
						String newValue = (oldValue.equals("∅")) ? epsilonTransitionValue
								: getUnionStates(oldValue, epsilonTransitionValue);
						if (i != alphabets.length) {
							transitionFunctionFinal.get(startState).replace(alphabets[i], oldValue, newValue);
						} else {
							transitionFunctionFinal.get(startState).replace("ε", oldValue, newValue);
						}
					}

				}
			}
		}
	}

	void removeUnaffectingStates() {
		subsetsList = getAllSubsets();
		int j = 0;
		int size = subsetsList.size();
		for (int i = 1; i < size; i++) {
			if (!getAllSubsets().get(i).toString().replace("[", "").replace("]", "").equals(getStartState())
					&& !transitionValueList.contains(
							getAllSubsets().get(i).toString().replace("[", "").replace("]", "").replace(" ", ""))) {
				subsetsList.remove(i - j);
				j++;
			}
		}
		String[] subsetsArray = subsetsList.toString().replace("[", "{").replace("]", "}").split(", ", -1);
		subsetsArray[0] = "∅";
		dfaStates = Arrays.toString(subsetsArray);
	}

	void createTransitionArray() {
		transitionArray = new String[subsetsList.size() + 1][alphabets.length + 1];
		transitionArray[0][0] = "δ";
		for (int i = 2; i < subsetsList.size() + 1; i++) {
			transitionArray[i][0] = subsetsList.get(i - 1).toString().replace("[", "{").replace("]", "}");
		}
		for (int j = 1; j < alphabets.length + 1; j++) {
			transitionArray[0][j] = alphabets[j - 1];
		}
		for (int k = 0; k < alphabets.length + 1; k++) {
			transitionArray[1][k] = "∅";
		}
		for (int x = 2; x < subsetsList.size() + 1; x++) {
			for (int y = 1; y < alphabets.length + 1; y++) {
				for (String value : transitionList) {
					if (value.startsWith(transitionArray[x][0].replace("{", "").replace("}", "").replace(" ", "")
							+ transitionArray[0][y])) {
						int index = transitionArray[x][0].replace("{", "").replace("}", "").replace(" ", "")
								.concat(transitionArray[0][y]).length();
						if (!value.substring(index, value.length()).equals("∅")) {
							transitionArray[x][y] = "{" + value.substring(index, value.length()).replace(",", ", ")
									+ "}";
						} else {
							transitionArray[x][y] = value.substring(index, value.length());
						}

					}
				}
			}
		}
	}

	String getStartState() {
		List<String> uniqueValueList = new ArrayList<>();
		String startState = nfaFormalDefination.getStartState();
		if (!getTransitionValuesArray(startState)[alphabets.length].equals("∅")) {
			String epsilonValue = getTransitionValuesArray(startState)[alphabets.length];
			for (String parseValue : epsilonValue.split(",", -1)) {
				if (!uniqueValueList.contains(parseValue) && !parseValue.equals(startState)) {
					uniqueValueList.add(parseValue);
					startState = startState.concat(", " + parseValue);
				}
			}
		}
		String[] tempArray = startState.split(", ", -1);
		Arrays.sort(tempArray);
		String result = "";
		for (int i = 0; i < tempArray.length; i++) {
			result = result.concat(tempArray[i]);
			if (tempArray.length != 1 && i < tempArray.length - 1) {
				result = result.concat(", ");
			}
		}
		return result;
	}

	List<List<String>> getAcceptStates() {
		List<List<String>> acceptStatesList = new ArrayList<>();
		int size = subsetsList.size();
		for (int i = 1; i < size; i++) {
			List<String> value = new ArrayList<>();
			String states = subsetsList.get(i).toString().replace("[", "").replace("]", "").replace(" ", "");
			for (String parseStates : states.split(",", -1)) {
				if (nfaFormalDefination.getStartState().equals(parseStates)) {
					value.add(states);
					break;
				}
			}
			if (!value.isEmpty()) {
				acceptStatesList.add(value);
			}
		}
		return acceptStatesList;
	}

	String[] getTransitionValuesArray(String key) {
		String[] transitionValuesArray = transitionFunction.get(key).values().toString().replace("[", "")
				.replace("]", "").replace("{", "").replace("}", "").split(", ", -1);
		return transitionValuesArray;
	}

	List<List<String>> getAllSubsets() {
		List<List<String>> subsetsList = new ArrayList<>();
		int setSize = nfaFormalDefination.getStates().length;
		int powSetSize = (int) Math.pow(2, setSize);
		for (int i = 0; i < powSetSize; i++) {
			List<String> values = new ArrayList<>();
			for (int j = 0; j < setSize; j++) {
				if ((i & (1 << j)) != 0) {
					values.add(nfaFormalDefination.getStates()[j]);
				}
			}
			subsetsList.add(values);
		}
		return subsetsList;
	}

	String getSortedString(String term) {
		String[] tempArray = term.split("-", -1);
		Arrays.sort(tempArray);
		String result = "";
		for (int i = 0; i < tempArray.length; i++) {
			result = result.concat(tempArray[i]);
			if (i != 0 && i != tempArray.length - 1) {
				result = result.concat(",");
			}
		}
		return result;
	}

	String getUnionStates(String term1, String term2) {
		List<String> uniqueValueList = new ArrayList<>();
		String result = "";
		term1 = term1.replace("{", "").replace("}", "");
		term2 = term2.replace("{", "").replace("}", "");
		for (String parseValue1 : term1.split(",", -1)) {
			if (!uniqueValueList.contains(parseValue1) && !parseValue1.equals("∅")) {
				uniqueValueList.add(parseValue1);
				result = result.concat(parseValue1 + "-");
			}
		}
		for (String parseValue2 : term2.split(",", -1)) {
			if (!uniqueValueList.contains(parseValue2)) {
				uniqueValueList.add(parseValue2);
				result = result.concat(parseValue2 + "-");
			}
		}
		return "{" + getSortedString(result) + "}";
	}

	HashMap<String, HashMap<String, String>> copyMap() {
		for (Map.Entry<String, HashMap<String, String>> entry : transitionFunction.entrySet()) {
			transitionFunctionFinal.put(entry.getKey(), new HashMap<String, String>(entry.getValue()));
		}
		return transitionFunctionFinal;
	}
}
