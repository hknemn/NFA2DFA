package nfa2dfa;

import java.util.*;

public class NfaFormalDefination {

	private ArrayList<String> list;
	private String[] states;
	private String[] alphabets;
	private HashMap<String, HashMap<String, String>> transitionFunction = new HashMap<>();
	private String startState;
	private String[] acceptStates;

	public NfaFormalDefination(ArrayList<String> list) {
		this.list = list;
		this.states = getStates();
		this.alphabets = getAlphabets();
		this.startState = getStartState();
		this.transitionFunction = getTransitionFunction();
		this.acceptStates = getAcceptStates();
	}

	public String[] getStates() {
		String statesLine = list.get(0).replace(" ", "");
		statesLine = statesLine.substring(3, statesLine.length() - 1);
		states = statesLine.split(",", -1);

		return states;
	}

	public String[] getAlphabets() {
		String alphabetsLine = list.get(1).replace(" ", "");
		alphabetsLine = alphabetsLine.substring(3, alphabetsLine.length() - 1);
		alphabets = alphabetsLine.split(",", -1);

		return alphabets;
	}

	public HashMap<String, HashMap<String, String>> getTransitionFunction() {
		int rowCount = states.length;
		int columnCount = alphabets.length + 1;
		for (int i = 0; i < rowCount; i++) {
			transitionFunction.put(states[i], new HashMap<String, String>());
			String[] transitionValues = list.get(i + 3).replace(" ", "").replace("	", " ").split(" ", -1);
			for (int j = 0; j < columnCount; j++) {
				if (columnCount - 1 == j) {
					transitionFunction.get(states[i]).put("ε", transitionValues[j + 1]);
				} else {
					transitionFunction.get(states[i]).put(alphabets[j], transitionValues[j + 1]);
				}
			}
		}
		return transitionFunction;
	}

	public String getStartState() {
		startState = list.get(list.size() - 2).replace(" ", "");
		startState = startState.split("=", -1)[1];

		return startState;
	}

	public String[] getAcceptStates() {
		String acceptStatesLine = list.get(list.size() - 1).replace(" ", "");
		acceptStatesLine = acceptStatesLine.substring(3, acceptStatesLine.length() - 1);
		acceptStates = acceptStatesLine.split(",", -1);

		return acceptStates;
	}
}
