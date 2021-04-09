package test;

//Core
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
//GUI
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Chatbot {
	public GUI Interface;
	public Knowledge Knowledge;
	public LinkedList<String> previousResponses;

	/**
	 * Team-2 Implementation of the Chatbot.
	 * 
	 * @version 1.0
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @author Ferdinand Haaben, and Tyler Rogers
	 */
	public Chatbot() throws FileNotFoundException, ClassNotFoundException, IOException {
		previousResponses = new LinkedList<String>();
		readKnowledge();
		// Interface = new GUI();
		// Interface.draw();
	}

	/**
	 * Team-2 Implementation of how the Chatbot learns input. this affects what the
	 * bot will output, improving conversation interactions.
	 * 
	 * @param String input
	 * @return none
	 * @author Ferdinand Haaben
	 */
	public void learn(String input) throws FileNotFoundException, IOException, ClassNotFoundException {
		if (!(getResponse(isQuestion(input)).contains(input))) {
			getResponse(isQuestion(input)).add(input);
			saveKnowledge();
		}
	}

	/**
	 * Team-2 Implementation of the Chatbot 'main' method. the bot will remember
	 * what the user said. when the bot does not have a question in the hashMap it
	 * will add the question to the hashMap and then try to answer from its
	 * Knowledge. * check if it just asked the user a question. If it did, and the
	 * user input is an answer. then add the input to the hashMap, using the
	 * previous response as the key. the previous response was not a question, the
	 * bot performs a Knowledge response.
	 * 
	 * @param String
	 * @return String
	 * @author Ferdinand Haaben
	 * @author Refactored by Tyler Rogers
	 */
	public String respondTo(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
		if (input == null)
			return "invalid";

		input = input.toLowerCase();

		if (input.equals("stop"))
			System.exit(0);
		if (input.equals("###")) {
			displayKnowledge();
			return "displayed current Knowledge in console";
		}

		learn(input);
		return addToPreviousResponses(isQuestion(input) ? getAnswer(input) : getQuestion(input));
	}

	public String getAnswer(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
		ArrayList<String> possibleResponses = checkHashMap(input);
		return !possibleResponses.isEmpty() ? getRandomResponse(possibleResponses) : getKnowledgeResponse(input);
	}

	public String getQuestion(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
		if (isQuestion(getLastResponse()))
			addAnswerToHashMap(getLastResponse(), input);
		return knowledgeResponse(input);
	}

	public String getKnowledgeResponse(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
		addQuestionToHashMap(input);
		return knowledgeResponse(input);
	}

	/**
	 * Team-2 Implementation of how the Chatbot returns output. in this method the
	 * bot compares the input string to its Knowledge base. if the input is a
	 * question the bot searches through its answers and vice versa. the bot chooses
	 * a response based on the number of matching words. the bot will respond with a
	 * string containing the maximum number of matching words with the input string.
	 * also makes sure that the bot does not repeatedly say the same thing.
	 * 
	 * @param String
	 * @return String
	 * @author Ferdinand Haaben
	 */
	public String knowledgeResponse(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
		String response = "";
		int topMatchingWords = 0;
		boolean isQuestion = isQuestion(input);
		for (int i = 0; i < getResponse(!isQuestion).size(); i++) {
			int matchingWords = compare(input, getResponse(!isQuestion).get(i));
			// this line makes sure that the bot does not repeatedly say the same thing.
			if ((matchingWords > topMatchingWords)
					&& !(getPreviousResponses().contains(getResponse(!isQuestion).get(i)))) {
				response = getResponse(!isQuestion).get(i);
				topMatchingWords = matchingWords;
			}
		}
		if (topMatchingWords == 0) {
			response = getRandomResponse(getResponse(!isQuestion));
		}
		return response;
	}

	/**
	 * Team-2 Implementation of how the Chatbot understands input.
	 * 
	 * @param String, String
	 * @return int
	 * @author Ferdinand Haaben
	 */
	public int compare(String input, String Knowledge) {
		if (isQuestion(input))
			input = input.substring(0, input.length() - 1);// remove "?" to make comparing words easier
		if (isQuestion(Knowledge))
			Knowledge = Knowledge.substring(0, Knowledge.length() - 1);
		int matchingWords = 0;

		String[] parsedKnowledge = parseIntoWords(Knowledge);
		String[] parsedInput = parseIntoWords(input);

		for (String in : parsedInput) {
			// ignore some words to improve matching
			if (in.equals("i") || in.equals("like") || in.equals("you") || in.equals("are") || in.equals("of")
					|| in.equals("the") || in.equals("to"))
				continue;

			for (String Kn : parsedKnowledge)
				// matches words even if they have been modified by "ing" or "s"
				if (in.equals(Kn) || in.equals(Kn + "s") || in.equals(Kn + "ing") || Kn.equals(in + "s")
						|| Kn.equals(in + "ing"))
					matchingWords++;
		}
		return matchingWords;
	}

	// helpers

	public boolean isQuestion(String input) {
		if (input == null)
			return false;
		return input.contains("?");
	}

	public ArrayList<String> getResponse(boolean isQuestion) {
		if (isQuestion)
			return Knowledge.getQuestion();
		else
			return Knowledge.getAnswer();
	}

	public String getLastResponse() {
		if (getPreviousResponses().isEmpty())
			return "";
		return getPreviousResponses().getLast();
	}

	public String getRandomResponse(ArrayList<String> input) {
		return input.get((int) (Math.random() * input.size()));
	}

	public LinkedList<String> getPreviousResponses() {
		return previousResponses;
	}

	public String addToPreviousResponses(String response) {
		previousResponses.add(response);
		if (previousResponses.size() > 5)
			previousResponses.remove();
		return response;
	}

	/**
	 * <h3>Team-2 Implementation adding to hashmap</h3>
	 * <p>
	 * this function is called when the user enters an answer. adds it to the
	 * hashmap.
	 * </p>
	 * 
	 * @param String, String
	 * @return none
	 * @author Ferdinand Haaben
	 */
	public void addAnswerToHashMap(String question, String input) {
		if (Knowledge.getAnswerGroups().keySet().contains(question))
			if (!(checkHashMap(question).contains(input)))
				checkHashMap(question).add(input);
	}

	/**
	 * <h3>Team-2 Implementation adding to hashmap</h3>
	 * <p>
	 * this function is called when the user enters a question. adds it to the
	 * hashmap.
	 * </p>
	 * 
	 * @param String
	 * @return none
	 * @author Ferdinand Haaben
	 */
	public void addQuestionToHashMap(String input) {
		Knowledge.getAnswerGroups().put(input, new ArrayList<String>());
	}

	/**
	 * <h3>Team-2 Implementation of hashmap keying</h3>
	 * <p>
	 * this function is called when the user enters a question. check if an answer
	 * exists in the hashmap.
	 * </p>
	 * <p>
	 * The hash map contains questions and possible answers. everything in the hash
	 * map has been entered by the user a some point.
	 * </p>
	 * 
	 * @param String
	 * @return ArrayList of the type String
	 * @author Ferdinand Haaben
	 */
	public ArrayList<String> checkHashMap(String input) {
		if (Knowledge.getAnswerGroups().get(input) == null)
			return new ArrayList<String>();
		return Knowledge.getAnswerGroups().get(input);
	}

	/**
	 * <h3>Team-2 Implementation knowledge setter</h3>
	 * <p>
	 * this is a setter function for the bot's knowledge.
	 * </p>
	 * 
	 * @param none
	 * @return none
	 * @author Ferdinand Haaben
	 */
	public void setKnowledge(Knowledge Knowledge) {
		this.Knowledge = Knowledge;
	}

	/**
	 * <h3>Team-2 Implementation knowledge getter</h3>
	 * <p>
	 * this is a getter function for the bot's knowledge.
	 * </p>
	 * 
	 * @param none
	 * @return Knowledge
	 * @author Ferdinand Haaben
	 */
	public Knowledge getKnowledge() {
		return Knowledge;
	}

	/**
	 * <h3>Team-2 Implementation of writing to the knowledge file</h3>
	 * <p>
	 * this is an I/O function which writes the current knowledge to the disk.
	 * </p>
	 * 
	 * @param none
	 * @return none
	 * @author Ferdinand Haaben
	 */
	public void saveKnowledge() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Knowledge.dat"));
		out.writeObject(getKnowledge());
		out.close();
	}

	/**
	 * <h3>Team-2 Implementation of reading the knowledge file</h3>
	 * <p>
	 * this is an I/O function which reads the knowledge file into the bot at init.
	 * </p>
	 * 
	 * @param none
	 * @return none
	 * @author Ferdinand Haaben
	 */
	public void readKnowledge() throws FileNotFoundException, IOException, ClassNotFoundException {
		if (new File("Knowledge.dat").exists()) {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("Knowledge.dat"));
			setKnowledge((Knowledge) in.readObject());
			in.close();
		} else {// use defaults
			setKnowledge(new Knowledge());
			learn("yes");
			learn("how are you?");
		}
	}

	/**
	 * <h3>Team-2 Implementation of printing the knowledge file</h3>
	 * <p>
	 * this function dumps all the bot's knowledge into the console for debugging.
	 * </p>
	 * 
	 * @param none
	 * @return none
	 * @author Ferdinand Haaben
	 */
	public void displayKnowledge() {
		System.out.println(Knowledge.getQuestion().toString());
		System.out.println(Knowledge.getAnswer().toString());
		System.out.println(Knowledge.getAnswerGroups().toString());
	}

	/**
	 * <h3>Team-2 Implementation of split()</h3>
	 * <p>
	 * this function calls eventually just calls split(), but performs a null check
	 * first.
	 * </p>
	 * 
	 * @param String
	 * @return String[]
	 * @author Tyler Rogers
	 */
	public String[] parseIntoWords(String s) {
		if (s == null || s.equals(""))
			return new String[0];
		return s.split(" ");
	}

	/**
	 * <h3>Team-2 Implementation of input validation</h3>
	 * <p>
	 * only allows a -> z, A -> Z, and spaces.
	 * </p>
	 * <p>
	 * <strong>WARNING:</strong> this will remove questions marks, so the bot won't
	 * understand input as a question. this is the desired behavior for this method.
	 * </p>
	 * 
	 * @param String
	 * @return String
	 * @author Tyler Rogers
	 */
	public String removePunctuation(String str) {
		String temp = "";
		char[] arr = str.toLowerCase().toCharArray();
		for (char elem : arr)
			if (((int) elem >= 97 && (int) elem <= 122) | (int) elem == 32)
				temp += elem;
		return temp;
	}

	public class GUI {
		public int inputCount;
		public JFrame Window;
		public JPanel Panel;
		public JPanel PanelHistory;
		public JTextField UserInArea;
		public JLabel BotOutArea;
		public SpellChecker SpellChecker;

		/**
		 * <h3>Team-2 Implementation of the GUI used to interact with the bot</h3>
		 * <p>
		 * draws the window and layout. provides spell checking, error catching, and
		 * recent conversation history.
		 * </p>
		 * 
		 * @author Tyler Rogers
		 */
		public GUI() {
			Window = new JFrame();
			Panel = new JPanel();
			PanelHistory = new JPanel();
			UserInArea = new JTextField(35);
			BotOutArea = new JLabel("Chatbot says: ");
		}

		/**
		 * <h3>Team-2 Implementation of the GUI</h3>
		 * <p>
		 * creates the window with which the user interacts with the bot. this is the
		 * only point of entry between input and output.
		 * </p>
		 * 
		 * @param none
		 * @return none
		 * @author Tyler Rogers
		 */
		public void draw() {
			inputCount = 0;
			Panel.setBorder(BorderFactory.createEmptyBorder());
			Panel.setLayout(new GridLayout(10, 1));
			PanelHistory.setBorder(BorderFactory.createTitledBorder("Recent History"));
			PanelHistory.setLayout(new GridLayout(30, 1));
			UserInArea.setName("type here");
			// SpellChecker = new SpellChecker();
			UserInArea.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent userPressedEnter) {
					String input = UserInArea.getText();
					if (!input.isEmpty())
						try {
							// input = SpellChecker.checkUserInput(input);
							String botOut = respondTo(input);
							BotOutArea.setText("Chatbot says: " + botOut);
							UserInArea.setText("");
							PanelHistory.add(new JLabel(input));
							PanelHistory.add(new JLabel(botOut));
							if (inputCount < 13)
								inputCount++;
							else {// stop text overflow in history panel
									// this is dirty, comments are necessary here.
									// I don't see a better way of doing it with current GUI architecture
								PanelHistory.remove(0);// remove the oldest thing the user said from history
								PanelHistory.remove(0);// remove the oldest thing the bot said from history
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			});
			Panel.add(UserInArea, BorderLayout.BEFORE_FIRST_LINE);
			Panel.add(BotOutArea, BorderLayout.AFTER_LAST_LINE);
			Window.add(Panel, BorderLayout.WEST);
			Window.add(PanelHistory, BorderLayout.EAST);
			Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Window.setTitle("Chatbot GUI");
			Window.setPreferredSize(new Dimension(700, 500));
			Window.pack();
			Window.setVisible(true);
		}
	}

	public class SpellChecker {
		public Levenshtein MatchStrengthTester;
		public String[] KnownWords;

		/**
		 * <h3>Team-2 Implementation of how the Chatbot checks for spelling
		 * mistakes</h3>
		 * <p>
		 * checks the strength of the relationship between two strings.
		 * </p>
		 * <h3>Spell Checking uses Levenshtein Distance Algorithm</h3>
		 * <p>
		 * Levenshtein Distance Algorithm find the distance between two Strings. The
		 * distance represents the similarity between the two strings. The greater the
		 * distance value, the less alike the strings are, and vice verca. This
		 * algorithm is used to check spelling of words fed to the Chatbot.
		 * </p>
		 * <h3>Misspelled vs. New "Unknown" words</h3>
		 * <p>
		 * need to determine if a word is spelled wrong, or if it is a new word. this is
		 * done with the code below. if the words are within 3 extra or fewer characters
		 * in length, and have a very strong relationship, then the word was spelled
		 * wrong and it gets corrected. Otherwise, it was a new word, so just pipe it
		 * into the bot. The strength is set to <= 2, which means that there can only be
		 * max 2 differences in length or, characters in same indexes between the
		 * strings.
		 * </p>
		 * 
		 * @author Tyler Rogers
		 */
		public SpellChecker() {
			MatchStrengthTester = new Levenshtein();
			KnownWords = getParsedKnowledge();
		}

		public String checkUserInput(String input) {
			boolean tackQuestionMarkOnEndOfInput = false;
			if (isQuestion(input))
				tackQuestionMarkOnEndOfInput = true;
			input = removePunctuation(input);// the process below can't handle any punctuation
			String[] words = parseIntoWords(input);
			String currentWord, correctedInput = "";
			for (int i = 0; i < words.length; i++) {
				currentWord = words[i];
				if (isSpellingMistake(currentWord))
					currentWord = correctSpelling(currentWord);
				correctedInput += currentWord;
			}
			return tackQuestionMarkOnEndOfInput ? correctedInput + '?' : correctedInput;// replace punctuation
		}

		public boolean isSpellingMistake(String word) {
			for (int i = 0; i < KnownWords.length; i++)
				if (word.equals(KnownWords[i]))
					return false;
			return true;
		}

		/**
		 * <h3>Team-2 Spelling Mistake Decision Making</h3>
		 * 
		 * 
		 * @param String
		 * @return String
		 * @author Tyler Rogers
		 */
		public String correctSpelling(String word) {
			return findClosestMatchingSupportedWord(word);
		}

		/**
		 * <h3>Team-2 Spelling Mistake Decision Making</h3>
		 * <p>
		 * the function will only run if the SpellChecker is flagged a word for a
		 * potential spelling mistake. compares the word with known words, and tests
		 * there similarities. if they are similar, replace the word with know word,
		 * otherwise, learn the new word.
		 * </p>
		 * <p>
		 * the logic is this, the bot only words about food, hobbies, and sports. so if
		 * the user feeds the bot with a word that it has not encountered before, if
		 * needs to decide if the new word is just spelled wrong, or is a new valid
		 * word.
		 * </p>
		 * <p>
		 * go through all know words and compare the new word to each of them. compare
		 * the best matching word to new word. this is done by this code. <
		 * </p>
		 * <p>
		 * matchStrength <= 2 is how similar the words are in terms of matching
		 * characters, and length.
		 * </p>
		 * <p>
		 * bestMatchingWordSoFar.length() > 0 don't match empty strings
		 * </p>
		 * <p>
		 * Math.abs(word.length() - bestMatchingWordSoFar.length()) < 3 is strictly
		 * comparing the length of the two strings
		 * </p>
		 * 
		 * @param String
		 * @return String CorrectedWord
		 * @author Tyler Rogers
		 */
		public String findClosestMatchingSupportedWord(String word) {
			int minMatchStrengthSoFar = 100;
			String bestMatchingWordSoFar = "";
			int matchStrength = 0;
			int currentMatchStrength = 0;

			for (int i = 0; i < KnownWords.length; i++) {
				currentMatchStrength = MatchStrengthTester.distance(word.toLowerCase(), KnownWords[i].toLowerCase());
				if (currentMatchStrength < minMatchStrengthSoFar) {
					minMatchStrengthSoFar = currentMatchStrength;
					bestMatchingWordSoFar = KnownWords[i];
				}
			}
			if (matchStrength <= 2 && bestMatchingWordSoFar.length() > 0
					&& Math.abs(word.length() - bestMatchingWordSoFar.length()) < 3) {
				System.out.println("Spelling mistake detected: replaced " + word + " with " + bestMatchingWordSoFar);
				return bestMatchingWordSoFar;
			}
			return word;
		}

		/**
		 * <h3>Team-2 Implementation of knowledge parsing for only use by the spell
		 * checker</h3>
		 * <p>
		 * returns every word the bot knows. it does this by getting all the stings from
		 * the Knowledge file, concat into one big string, removing anything that is not
		 * a word, and splitting the words into indexes in an array. it is this string
		 * array that is returned.
		 * </p>
		 * 
		 * @param none
		 * @return String[]
		 * @author Tyler Rogers
		 */
		public String[] getParsedKnowledge() {
			return parseIntoWords(removePunctuation(Knowledge.getQuestion().toString()
					+ Knowledge.getAnswer().toString() + Knowledge.getAnswerGroups().toString()));
		}

		public class Levenshtein {
			/**
			 * <h3>Levenshtein distance algorithm</h3> <br>
			 * <p>
			 * <strong>this code is not ours, we do not take credit for it, we are just
			 * using it.</strong> it was taken from,
			 * jdk.internal.org.jline.utils.Levenshtein
			 * </p>
			 * <p>
			 * could not import jdk.internal.org.jline.utils.Levenshtein, but could view it
			 * in Declaration panel in Eclipse, so copied and pasted in program.
			 * </p>
			 * <p>
			 * the smaller that table[source.length() - 1][target.length() - 1] is, the more
			 * related the two strings are to each other.
			 * </p>
			 * 
			 * @param String string1, String string2
			 * @return Integer representing strength of relationship
			 * @author jdk.internal.org.jline.utils.Levenshtein
			 */
			public int distance(CharSequence lhs, CharSequence rhs) {
				return distance(lhs, rhs, 1, 1, 1, 1);
			}

			public int distance(CharSequence source, CharSequence target, int deleteCost, int insertCost,
					int replaceCost, int swapCost) {
				/*
				 * Required to facilitate the premise to the algorithm that two swaps of the
				 * same character are never required for optimality.
				 */
				if (2 * swapCost < insertCost + deleteCost) {
					throw new IllegalArgumentException("Unsupported cost assignment");
				}
				if (source.length() == 0) {
					return target.length() * insertCost;
				}
				if (target.length() == 0) {
					return source.length() * deleteCost;
				}
				int[][] table = new int[source.length()][target.length()];
				Map<Character, Integer> sourceIndexByCharacter = new HashMap<>();
				if (source.charAt(0) != target.charAt(0)) {
					table[0][0] = Math.min(replaceCost, deleteCost + insertCost);
				}
				sourceIndexByCharacter.put(source.charAt(0), 0);
				for (int i = 1; i < source.length(); i++) {
					int deleteDistance = table[i - 1][0] + deleteCost;
					int insertDistance = (i + 1) * deleteCost + insertCost;
					int matchDistance = i * deleteCost + (source.charAt(i) == target.charAt(0) ? 0 : replaceCost);
					table[i][0] = Math.min(Math.min(deleteDistance, insertDistance), matchDistance);
				}
				for (int j = 1; j < target.length(); j++) {
					int deleteDistance = (j + 1) * insertCost + deleteCost;
					int insertDistance = table[0][j - 1] + insertCost;
					int matchDistance = j * insertCost + (source.charAt(0) == target.charAt(j) ? 0 : replaceCost);
					table[0][j] = Math.min(Math.min(deleteDistance, insertDistance), matchDistance);
				}
				for (int i = 1; i < source.length(); i++) {
					int maxSourceLetterMatchIndex = source.charAt(i) == target.charAt(0) ? 0 : -1;
					for (int j = 1; j < target.length(); j++) {
						Integer candidateSwapIndex = sourceIndexByCharacter.get(target.charAt(j));
						int jSwap = maxSourceLetterMatchIndex;
						int deleteDistance = table[i - 1][j] + deleteCost;
						int insertDistance = table[i][j - 1] + insertCost;
						int matchDistance = table[i - 1][j - 1];
						if (source.charAt(i) != target.charAt(j)) {
							matchDistance += replaceCost;
						} else {
							maxSourceLetterMatchIndex = j;
						}
						int swapDistance;
						if (candidateSwapIndex != null && jSwap != -1) {
							int iSwap = candidateSwapIndex;
							int preSwapCost;
							if (iSwap == 0 && jSwap == 0) {
								preSwapCost = 0;
							} else {
								preSwapCost = table[Math.max(0, iSwap - 1)][Math.max(0, jSwap - 1)];
							}
							swapDistance = preSwapCost + (i - iSwap - 1) * deleteCost + (j - jSwap - 1) * insertCost
									+ swapCost;
						} else {
							swapDistance = Integer.MAX_VALUE;
						}
						table[i][j] = Math.min(Math.min(Math.min(deleteDistance, insertDistance), matchDistance),
								swapDistance);
					}
					sourceIndexByCharacter.put(source.charAt(i), i);
				}
				return table[source.length() - 1][target.length() - 1];
			}

		}
	}
}