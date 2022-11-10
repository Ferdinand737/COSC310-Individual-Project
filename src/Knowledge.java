
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Knowledge implements Serializable {
	/**
	 * <h3>Team-2 Implementation of a hashmap</h3>
	 * <p>
	 * the hash map contains questions and possible answers. everything in the hash
	 * map has been entered by the user a some point.
	 * </p>
	 * 
	 * @author Ferdinand Haaben
	 */
	private HashMap<String, ArrayList<String>> answerGroups;
	private ArrayList<String> question;
	private ArrayList<String> answer;
	
	/**
	 * <h3>Team-2 Implementation of knowledge</h3>
	 * 
	 * <p>
	 * This class is the bot's knowledge, it stores all the responses that the bot
	 * knows. There is an ArrayList of questions and answers along with a HashMap
	 * that maps any question to a set of possible answers.
	 * </p>
	 * 
	 * @author Ferdinand Haaben
	 */
	public Knowledge() {
		setAnswerGroups(new HashMap<String, ArrayList<String>>());
		setQuestion(new ArrayList<String>());
		setAnswer(new ArrayList<String>());
	}

	public ArrayList<String> getQuestion() {
		return question;
	}

	public void setQuestion(ArrayList<String> question) {
		this.question = question;
	}

	public ArrayList<String> getAnswer() {
		return answer;
	}

	public void setAnswer(ArrayList<String> answer) {
		this.answer = answer;
	}

	public HashMap<String, ArrayList<String>> getAnswerGroups() {
		return answerGroups;
	}

	public void setAnswerGroups(HashMap<String, ArrayList<String>> answerGroups) {
		this.answerGroups = answerGroups;
	}
}