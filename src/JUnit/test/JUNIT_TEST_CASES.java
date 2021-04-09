package test;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

public class JUNIT_TEST_CASES {

	Chatbot cb;
	Chatbot.SpellChecker sc;

	/**
	 * <p>Team-2 Unit Testing of Chatbot<p>
	 * 
	 * @author Tyler Rogers
	 */
	@Before
	public void init() {
		try {
			cb = new Chatbot();
			sc = cb.new SpellChecker();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	/***************** TESTING CHATBOT UNITS *****************/

	/**
	 * <p>Team-2 Unit Testing of learn()<p>
	 * <p>
	 * check is when learn(param) gets call, param is actually added to the
	 * knowledge base.
	 * </p>
	 * 
	 * @author Tyler Rogers
	 */
	@Test
	public void testLearn() {
		try {
			cb.learn("hello world");
			Knowledge k = cb.getKnowledge();
			String s = k.getAnswerGroups().toString() + k.getAnswer().toString() + k.getQuestion().toString();
			boolean hasInput = s.contains("hello world");
			assertTrue(hasInput);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * <p>Team-2 Unit Testing of respondToAnswer()<p>
	 * <p>
	 * The bot should always respond to an answer with a question. Check that the
	 * response has a '?' in it.
	 * </p>
	 * 
	 * @author Tyler Rogers
	 */
	@Test
	public void testRespondToAnswer() {
		try {
			String resp = cb.respondTo("not a quesiton");
			boolean isEmpty = resp.isEmpty();
			boolean isQuesion = resp.contains("?");
			assertTrue(isQuesion && !isEmpty);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * <p>Team-2 Unit Testing of respondToQuestion()<p>
	 * <p>
	 * The bot should always respond to a question with an answer. Check that the
	 * response doesn't have a '?' in it.
	 * </p>
	 * 
	 * @author Tyler Rogers
	 */
	@Test
	public void testRespondToQuestion() {
		try {
			String resp = cb.respondTo("is a quesiton?");
			boolean isEmpty = resp.isEmpty();
			boolean isQuesion = resp.contains("?");
			assertTrue(!isQuesion && !isEmpty);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * <p>Team-2 Unit Testing of removePunctuation()<p>
	 * <p>
	 * only a->z, A->Z should be allowed into the bot. removePunctuation() gets
	 * called in everything coming into the bot. Check if it works as expected.
	 * </p>
	 * 
	 * @author Tyler Rogers
	 */
	@Test
	public void testRemovePunctuation() {
		try {
			String resp = cb.removePunctuation("____he7l@.lo wor$%ld");
			assertTrue(resp, resp.equals("hello world"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * <p>Team-2 Unit Testing of parseIntoWords()<p>
	 * <p>
	 * This method just calls the built in Java split() method. But it is vital the
	 * functioning of the bot for this method to work.
	 * </p>
	 * 
	 * @author Tyler Rogers
	 */
	@Test
	public void testParseIntoWords() {
		try {
			String[] resp = cb.parseIntoWords("hello world, i am here");
			boolean gotSplit = resp.length == 5;
			boolean firstWord = resp[0].equals("hello");
			boolean lastWord = resp[4].equals("here");
			assertTrue(gotSplit && firstWord && lastWord);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * <p>Team-2 Unit Testing of isQuestion()<p>
	 * <p>
	 * isQuesiton is a simple true/false method. But it is vital the functioning of
	 * the bot for this method to work.
	 * </p>
	 * 
	 * @author Tyler Rogers
	 */
	@Test
	public void testIsQuestion() {
		try {
			String question = "hello world?", answer = "hello world";
			boolean isAnswer = cb.isQuestion(answer);
			boolean isQuestion = cb.isQuestion(question);
			assertTrue(!isAnswer && isQuestion);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * <p>Team-2 Unit Testing of checkHashMap()<p>
	 * <p>
	 * It is normal for checkHashMap() to return an empty list. Make sure that if
	 * nothing is found in the hashmap, it actually returns an array list object.
	 * </p>
	 * 
	 * @author Tyler Rogers
	 */
	@Test
	public void testCheckHashMap() {
		try {
			assertTrue(cb.checkHashMap("hello world12345?") instanceof ArrayList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	/***************** TESTING SPELL CHECKER UNITS *****************/

	/**
	 * <p>Team-2 Unit Testing of getParsedKnowledge()<p>
	 * <p>
	 * Check that the spell checker has access to the knowledge, and can parse it.
	 * </p>
	 * 
	 * @author Tyler Rogers
	 */
	@Test
	public void testGetParsedKnowledge() {
		try {
			cb.learn("test123");
			String[] k = sc.getParsedKnowledge();
			boolean b = false;
			for (int i = 0; i < k.length; i++)
				if (k[i].equals("test")) {
					b = true;
					break;
				}
			assertTrue(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * <p>Team-2 Unit Testing of checkUserInput()<p>
	 * <p>
	 * checkUserInput() is the core of the spell checker system. All functionality
	 * of the spell checker converges in this method.
	 * 
	 * It returns the corrected version on the input, if it detected a spelling
	 * mistake.
	 * </p>
	 * 
	 * @author Tyler Rogers
	 */
	@Test
	public void testCheckUserInput() {
		try {
			cb.learn("test");
			String str = sc.checkUserInput("tast");
			assertTrue(str.equals("test"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

}
