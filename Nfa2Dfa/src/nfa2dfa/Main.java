package nfa2dfa;

import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<String> list = new ArrayList<>();
		executeFile("NFA.txt", list);
		new ConvertNfa2Dfa(list);
	}

	static void executeFile(String file, ArrayList<String> list) throws FileNotFoundException {
		Scanner reader = new Scanner(new File(file));
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			list.add(line);
		}
		reader.close();
	}
}