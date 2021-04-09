package test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Knowledge implements Serializable {
	/*
	 * This class is the bot's knowledge, it stores all the responses that the bot
	 * knows. There is an ArrayList of questions and answers along with a HashMap
	 * that maps any question to a set of possible answers.
	 */

	/**
	 * 
	 */
	private ArrayList<String> question;
	private ArrayList<String> answer;

	private HashMap<String, ArrayList<String>> answerGroups;

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