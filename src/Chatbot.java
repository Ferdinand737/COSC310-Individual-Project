// Core
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// GUI
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

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;



public class Chatbot {
	public GUI Interface;
	public Knowledge Knowledge;
	public WikiSearch WikiSearch = new WikiSearch();
	public SearchPlaces SearchPlaces = new SearchPlaces();
	/**
	 * <h3>Team-2 Implementation of the list of resent previous responses</h3>
	 * <p>
	 * This list is limited to a size of 5 by addToPreviousResponses().
	 * </p>
	 * <p>
	 * This is a linked list because it will constantly undergo add to index size,
	 * and remove from index 0 operations.
	 * </p>
	 */
	public LinkedList<String> previousResponses;

	/**
	 * <h3>Team-2 Implementation of Natural Language Processing</h3>
	 * <p>
	 * This component provides a 'places, people, and things' recognition service.
	 * </p>
	 * <p>
	 * This component provides stemming.
	 * </p>
	 * <p>
	 * <strong>WARNING: Need some packages to get this working.</strong> Download
	 * and Import the following packages.
	 * </p>
	 * <ul>
	 * <li>edu.stanford.nlp.ling.CoreAnnotations</li>
	 * <li>edu.stanford.nlp.ling.CoreLabel</li>
	 * <li>edu.stanford.nlp.pipeline.CoreDocument</li>
	 * <li>edu.stanford.nlp.pipeline.StanfordCoreNLP</li>
	 * </ul>
	 */
	public StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
	/**
	 * <h3>Team-2 Implementation of the Chatbot</h3>
	 * 
	 * @version 1.1
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @author Ferdinand Haaben, and Tyler Rogers
	 */
	public Chatbot() throws FileNotFoundException, ClassNotFoundException, IOException {
		previousResponses = new LinkedList<String>();
		readKnowledge();
		Interface = new GUI();
		Interface.draw();
	}

	/**
	 * <h3>Team-2 Implementation of how the Chatbot learns input</h3>
	 * <p>
	 * The bot has the ability to remember the conversation. This affects what the
	 * bot will output, improving conversation interactions.
	 * </p>
	 * 
	 * <p>
	 * This method adds the input to the knowledge base.
	 * </p>
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
	 * <h3>Team-2 Implementation of the Chatbot's main method</h3>
	 * <p>
	 * The bot will remember what the user said. When the bot does not have a
	 * question in the hashMap it will add the question to the hashMap, and then try
	 * to answer from its knowledge. Check if it just asked the user a question. If
	 * it did, and the user input is an answer, then add the input to the hashMap,
	 * using the previous response as the key. If the previous response was not a
	 * question, the bot performs a knowledge response, not a hashmap response.
	 * </p>
	 * 
	 * @param String
	 * @return String
	 * @author Ferdinand Haaben
	 * @author Refactored by Tyler Rogers
	 */
	public String respondTo(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
		learn(input);
		return addToPreviousResponses(isQuestion(input) ? getAnswer(input) : getQuestion(input));
	}

	/**
	 * <h3>Team-2 Implementation respondTo() helper</h3>
	 * <p>
	 * This method is passed a question. It checks the hashmap for possible
	 * responses. If it finds one, then it returns it, otherwise, it returns a
	 * response from the knowledge.
	 * </p>
	 * 
	 * @param String
	 * @return String
	 * @author Ferdinand Haaben
	 * @author Refactored by Tyler Rogers
	 */
	public String getAnswer(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
		ArrayList<String> possibleResponses = checkHashMap(input);
		return !possibleResponses.isEmpty() ? getRandomResponse(possibleResponses) : getKnowledgeResponse(input);
	}

	/**
	 * <h3>Team-2 Implementation respondTo() helper</h3>
	 * <p>
	 * This method is passed a question or answer. If the last response was a
	 * question, then the passed parameter is a added to the hashmap as a value, and
	 * the last response as its key. Then it returns a response from the knowledge.
	 * </p>
	 * 
	 * @param String
	 * @return String
	 * @author Ferdinand Haaben
	 * @author Refactored by Tyler Rogers
	 */
	public String getQuestion(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
		if (isQuestion(getLastResponse()))
			addAnswerToHashMap(getLastResponse(), input);
		return knowledgeResponse(input);
	}

	/**
	 * <h3>Team-2 Implementation respondTo() helper</h3>
	 * <p>
	 * This method is passed a question. That question gets added to the hashmap,
	 * then it returns a response from the knowledge.
	 * </p>
	 * 
	 * @param String
	 * @return String
	 * @author Ferdinand Haaben
	 * @author Refactored by Tyler Rogers
	 */
	public String getKnowledgeResponse(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
		addQuestionToHashMap(input);
		return knowledgeResponse(input);
	}
	/**
	 * <h3>Updated knowledgeResponse method for individual project</h3>
	 * <p>
	 * This method uses the stanford NLP library, wikipedia API, and google places API to return a relevant string.
	 * The method will wikipedia search and google places search any noun,verb or adjective and the adjacent words.
	 * For example in the following string "word prevWord NOUN nextWord word". The method will search(prev NOUN next).
	 * 
	 * </p>
	 * 
	 * @param String
	 * @return String
	 * @author Ferdinand Haaben
	 */
	public String knowledgeResponse(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
		
		ArrayList<String> posTags = new ArrayList<String>(Arrays.asList("NNP","NN","NNS","NP","NPS","JJ","VV","VVD","VVG","VVP","VVZ"));
		
		String response = "";
		
		CoreDocument inNLP = new CoreDocument(input);
		stanfordCoreNLP.annotate(inNLP);
		List<CoreLabel> inputWordList = inNLP.tokens();
		
		for (int i = 0; i < inputWordList.size(); i++) {
			
			String pos = inputWordList.get(i).getString(CoreAnnotations.PartOfSpeechAnnotation.class);
			
			if (!posTags.contains(pos)) {
				continue;
			}
			
			String prevWord = "";
			
			String currWord = inputWordList.get(i).originalText();
			
			String nextWord = "";
			
			if(i > 0) {
				prevWord = inputWordList.get(i - 1).originalText();
			}
			
			if(i < inputWordList.size() - 1) {
				nextWord = inputWordList.get(i + 1).originalText();
			}
			
			prevWord = prevWord != "" ? prevWord + " " : "";
			
			nextWord = nextWord != "" ? " " + nextWord : "";
			
			String wiki = prevWord + currWord + nextWord;
			
			String places = prevWord + currWord + nextWord;
			
			response = WikiSearch.searchWikipedia(wiki) != null ?  WikiSearch.searchWikipedia(wiki) : "";
			
			response += SearchPlaces.search(places) != null ?  " " + SearchPlaces.search(places) : "";
			
			if(!response.equals("")) {
				break;
			}
			
		}
		if(response.equals("")) {
			return wordMatching(input);
		}
		
		return response;
	}
	/**
	 * <h3>Team-2 Implementation of how the Chatbot returns output</h3>
	 * <p>
	 * In this method the bot compares the input string to it's Knowledge base. If
	 * the input is a question, the bot searches through its answers and vice versa.
	 * the bot chooses a response based on the number of matching words. The bot
	 * will respond with a string containing the maximum number of matching words
	 * with the input string. Also, this method makes sure that the bot does not
	 * repeatedly say the same thing, by comparing what it just said, to what is it
	 * about to say.
	 * </p>
	 * 
	 * @param String
	 * @return String
	 * @author Ferdinand Haaben
	 */
	public String wordMatching(String input) throws FileNotFoundException, ClassNotFoundException, IOException {
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
	 * <h3>Team-2 Implementation of how the Chatbot understands input.</h3>
	 * 
	 * <p>
	 * This method uses stanfordNLP to:
	 * </p>
	 * <ol>
	 * <li>Simplify the input string into a List of simple root words "I am trying
	 * to start running" becomes [I, be, try, to, start, run].</li>
	 * <li>Iterate through the simple root words.</li>
	 * <li>Check the type of word and ignore any that are not nouns, verbs, or
	 * adjectives.</li>
	 * <li>For each simple word, iterate through the knowledge words.</li>
	 * <li>Use lemma() method to simplify current knowledge word and compare it to
	 * the simple input word.</li>
	 * <li>If a match is found, increment matchingWords.</li>
	 * </ol>
	 * 
	 * @param String, String
	 * @return int
	 * @author Ferdinand Haaben
	 */
	public int compare(String input, String Knowledge) {

		int matchingWords = 0;

		CoreDocument inNLP = new CoreDocument(input);
		CoreDocument knowledgeNLP = new CoreDocument(Knowledge);
		stanfordCoreNLP.annotate(inNLP);
		stanfordCoreNLP.annotate(knowledgeNLP);
		List<CoreLabel> inputWordList = inNLP.tokens();
		List<CoreLabel> knowledgeWordList = knowledgeNLP.tokens();

		String simpleInput = "";

		for (CoreLabel inputWord : inputWordList) {
			simpleInput = simpleInput + inputWord.lemma() + " ";
		}
		inNLP = new CoreDocument(simpleInput);
		stanfordCoreNLP.annotate(inNLP);
		inputWordList = inNLP.tokens();

		for (CoreLabel inputWord : inputWordList) {

			String pos = inputWord.getString(CoreAnnotations.PartOfSpeechAnnotation.class);

			// we only care about matching nouns, verbs and adjectives
			if (!(pos.equals("JJ") || pos.equals("NN") || pos.equals("NP") || pos.equals("VV"))) {
				continue;
			}
			for (CoreLabel knowledgeWord : knowledgeWordList) {

				if (inputWord.lemma().equals(knowledgeWord.lemma())) {
	
					matchingWords++;
				}
			}
		}
		return matchingWords;
	}

	
	/**
	 * <h3>Team-2 Implementation of checking if an input is a question</h3>
	 * <p>
	 * This method returns true is the passed string contains a '?', otherwise, it
	 * returns false.
	 * </p>
	 * 
	 * @param String
	 * @return boolean
	 * @author Ferdinand Haaben
	 */
	public boolean isQuestion(String input) {
		if (input == null)
			return false;
		return input.contains("?");
	}

	/**
	 * <h3>Team-2 Implementation of getting a question or answer</h3>
	 * <p>
	 * This is one of the possible ways the bot has to respond. If the user just
	 * gave an answer, this method returns a list of questions. If the user just
	 * asked a question, this method returns a list of answers. All returned values
	 * come from the knowledge.
	 * </p>
	 * 
	 * @param boolean
	 * @return ArrayList of the type String
	 * @author Ferdinand Haaben
	 */
	public ArrayList<String> getResponse(boolean isQuestion) {
		if (isQuestion)
			return Knowledge.getQuestion();
		else
			return Knowledge.getAnswer();
	}

	/**
	 * <h3>Team-2 Implementation getter for the former response</h3>
	 * <p>
	 * Returns the former response in the conversation.
	 * </p>
	 * 
	 * @param none
	 * @return String
	 * @author Ferdinand Haaben
	 */
	public String getLastResponse() {
		if (getPreviousResponses().isEmpty())
			return "";
		return getPreviousResponses().getLast();
	}

	/**
	 * <h3>Team-2 Implementation of selecting a random response</h3>
	 * <p>
	 * Returns a random possible response from a passed list of them.
	 * </p>
	 * 
	 * @param ArrayList
	 * @return String
	 * @author Ferdinand Haaben
	 */
	public String getRandomResponse(ArrayList<String> input) {
		return input.get((int) (Math.random() * input.size()));
	}

	/**
	 * <h3>Team-2 Implementation of getter for PreviousResponses</h3>
	 * <p>
	 * Getter for all PreviousResponses.
	 * </p>
	 * 
	 * @param none
	 * @return LinkedList of the type String
	 * @author Ferdinand Haaben
	 */
	public LinkedList<String> getPreviousResponses() {
		return previousResponses;
	}

	/**
	 * <h3>Team-2 Implementation of adding a response to list of
	 * PreviousResponses</h3>
	 * 
	 * <p>
	 * When this method is passed a response string, it adds the string to the head
	 * of linked list of previous responses.
	 * </p>
	 * 
	 * <p>
	 * <strong>WARNING: This method maintains the size of the previousResponses
	 * linked list. It limits the size it 5, meaning the bot only has a </strong>
	 * </p>
	 */
	public String addToPreviousResponses(String response) {
		previousResponses.add(response);
		if (previousResponses.size() > 5)
			previousResponses.remove();
		return response;
	}

	/**
	 * <h3>Team-2 Implementation adding an answer to hashmap</h3>
	 * <p>
	 * This method is called when the user enters an answer. Adds it to the hashmap
	 * if the question (first param key) is in the hashmap, and the input (last
	 * param value) is not in the hashmap.
	 * </p>
	 * 
	 * @param String key
	 * @param String value
	 * @return none
	 * @author Ferdinand Haaben
	 */
	public void addAnswerToHashMap(String question, String input) {
		if (Knowledge.getAnswerGroups().keySet().contains(question))
			if (!(checkHashMap(question).contains(input)))
				checkHashMap(question).add(input);
	}

	/**
	 * <h3>Team-2 Implementation adding a question to hashmap</h3>
	 * <p>
	 * This method is called when the user enters a question. adds it to the
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
	 * This method is called when the user enters a question. Checks if an answer
	 * exists in the hashmap.
	 * </p>
	 * <p>
	 * The hash map contains questions, and possible answers. Everything in the hash
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
	 * This is a setter function for the bot's knowledge.
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
	 * This is a getter function for the bot's knowledge.
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
	 * This is an I/O function which writes the current knowledge to the disk.
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
	 * This is an I/O function which reads the knowledge file from the disk. Runs
	 * once at init time.
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
	 * This method dumps all the bot's knowledge into the console for debugging.
	 * Called when the user enters '###'.
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
	 * This method eventually just calls split(), but performs a null check first.
	 * </p>
	 * 
	 * @param String
	 * @return String[]
	 * @author Ferdinand Haaben
	 * @author Tyler Rogers
	 */
	public String[] parseIntoWords(String s) {
		if (s == null || s.equals(""))
			return new String[0];
		return s.split(" ");
	}

	/**
	 * <h3>Team-2 Implementation of input cleaning</h3>
	 * <p>
	 * only allows a -> z, A -> Z, and spaces.
	 * </p>
	 * <p>
	 * <strong>WARNING:</strong> this will remove questions marks, so the bot won't
	 * understand input as a question. this is the desired behavior for this method.
	 * </p>
	 * <p>
	 * Initially written in C lang, then adapted to work in Java
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
		 * Draws the window and layout. provides spell checking, error catching, and
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
		 * Creates the window with which the user interacts with the bot. This is the
		 * only point of entry between the bot and user.
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
			SpellChecker = new SpellChecker();
			UserInArea.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent userPressedEnter) {
					String input = UserInArea.getText().toLowerCase();

					if (input.equals("stop"))
						System.exit(0);

					if (input.equals("###")) {
						displayKnowledge();
						BotOutArea.setText("Chatbot says: Displayed knowledge in console");
					} else if (!input.isEmpty()) {
						try {
							//input = SpellChecker.checkUserInput(input);
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
		 * <h3>Team-2 Implementation of how the UI checks for spelling mistakes</h3>
		 * <p>
		 * SpellChecking is a member of the GUI. It is the UI's job to provide a spell
		 * checking service, not the bot's. The bot is the user's friend, not a spelling
		 * checking slave.
		 * </p>
		 * <h3>Spell Checking uses Levenshtein Distance Algorithm</h3>
		 * <p>
		 * Checks the strength of the relationship between two strings.
		 * </p>
		 * <p>
		 * Levenshtein Distance Algorithm finds the 'distance' between two Strings. The
		 * distance represents the similarity between the two strings. The greater the
		 * distance value, the less alike the strings are, and vice verca. This
		 * algorithm is used to check the spelling of words fed to the bot.
		 * </p>
		 * <h3>Misspelled vs. New "Unknown" words</h3>
		 * <p>
		 * Need to determine if a word is spelled wrong, or if it is a new word. This is
		 * done by doing a sanity check. The sanity check is done after it finds the
		 * best matching replacement word from the knowledge base. If the words are
		 * within 3 extra or fewer characters in length, and have a very strong
		 * relationship found by Levenshtein, then the word was just spelled wrong and
		 * it gets corrected. Otherwise, it was a new word, so just pipe it into the
		 * bot's knowledge. The Levenshtein distance is set to <= 2, which means that
		 * there can only be max 2 differences in length or, characters in same indexes
		 * between the strings.
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
				correctedInput += currentWord + " ";
			}
			
			if(tackQuestionMarkOnEndOfInput) {
				correctedInput = correctedInput.substring(0,correctedInput.length() - 1) + "?";
			}else {
				correctedInput = correctedInput.substring(0,correctedInput.length() - 1);
			}
			
			return correctedInput;// replace punctuation
		}

		public boolean isSpellingMistake(String word) {
			for (int i = 0; i < KnownWords.length; i++)
				if (word.equals(KnownWords[i]))
					return false;
			return true;
		}

		/**
		 * <h3>Team-2 Spelling Mistake Decision Making helper</h3>
		 * <p>
		 * This method just passes it's param to findClosestMatchingSupportedWord(param)
		 * </p>
		 * 
		 * @param String
		 * @return String
		 * @author Tyler Rogers
		 */
		public String correctSpelling(String word) {
			return findClosestMatchingSupportedWord(word);
		}

		/**
		 * <h3>Team-2 Spelling mistake decision making</h3>
		 * <p>
		 * The method will only run if the SpellChecker has flagged a word for a
		 * potential spelling mistake. Compares the word with known words, and tests
		 * their similarities. If they are similar, replace the word with know word,
		 * otherwise, learn the new word.
		 * </p>
		 * <p>
		 * The logic is this, the bot only know words about food, hobbies, and sports.
		 * So if the user feeds the bot with a word that it has not encountered before,
		 * it needs to decide if the new word is just spelled wrong, or is a new, valid
		 * word.
		 * </p>
		 * <p>
		 * Go through all know words and compare the new word to each of them. Compare
		 * the best matching word to new word. This is the sanity check.
		 * </p>
		 * <h3>Sanity Check</h3>
		 * <ul>
		 * <li>matchStrength <= 2 is how similar the words are in terms of matching
		 * characters, and length.</li>
		 * <li>bestMatchingWordSoFar.length() > 0 don't match empty strings</li>
		 * <li>Math.abs(word.length() - bestMatchingWordSoFar.length()) < 3 is strictly
		 * comparing the length of the two strings</li>
		 * </ul>
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
			// Sanity Check
			if (matchStrength <= 2 && bestMatchingWordSoFar.length() > 0
					&& Math.abs(word.length() - bestMatchingWordSoFar.length()) < 3) {
				System.out.println("Spelling mistake detected: replaced " + word + " with " + bestMatchingWordSoFar);
				return bestMatchingWordSoFar;
			}
			return word;
		}

		/**
		 * <h3>Team-2 Implementation of knowledge parsing only for use by the spell
		 * checker</h3>
		 * <p>
		 * Returns every word the bot knows. It does this by getting all the strings
		 * from the knowledge file, concats them into one big string, removing anything
		 * that is not a word, and splitting the words into indexes in an array.
		 * <p>
		 * It is that string array that is returned.
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
			 * <strong>This code is not ours, we do not take credit for it, we are just
			 * using it.</strong> It was taken from,
			 * jdk.internal.org.jline.utils.Levenshtein
			 * </p>
			 * <p>
			 * Could not import jdk.internal.org.jline.utils.Levenshtein, but could view it
			 * in Declaration panel in Eclipse, so copied and pasted into program.
			 * </p>
			 * <p>
			 * The smaller that table[source.length() - 1][target.length() - 1] is, the more
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
				Map<Character, Integer> sourceIndexByCharacter = new HashMap<Character, Integer>();
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