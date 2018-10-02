import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * A class to validate a bracket sequence according to a set of rules
 */
public class BracketsValidation extends Thread {
	
	/**
	 * Element to be stored in the bracketStack.
	 * 
	 * value: The value of the element that is pushed
	 * next: value of the first element pushed on top of this element
	 */
	static class StackElement {
		static final Character NOT_SET = Character.MIN_VALUE;
		
		Character value = NOT_SET;
		Character next = NOT_SET;
		
		StackElement(Character value, Character next){
			this.value = value;
			this.next = next;
		}
	}
	
	String input;
	Integer index;
	Stack<StackElement> bracketStack = new Stack<StackElement>();
	
	static HashSet<Character> validOpenBrackets = new HashSet<Character>();
	static HashMap<Character, Character> closedToOpenBrackets = new HashMap<Character, Character>();
	static HashMap<Character, HashSet<Character>> validNextBrackets = new HashMap<Character, HashSet<Character>>();
	
	// Initialize bracket rules
	static { 
		closedToOpenBrackets.put(')', '(');
		closedToOpenBrackets.put('}', '{');
		closedToOpenBrackets.put(']', '[');
		
		validOpenBrackets.add('{');
		validOpenBrackets.add('[');
		validOpenBrackets.add('(');
		
		validNextBrackets.put('{', new HashSet<>(Arrays.asList('[')));
		validNextBrackets.put('[', new HashSet<>(Arrays.asList('[','{','(')));
		validNextBrackets.put('(', new HashSet<>(Arrays.asList('{')));
		validNextBrackets.put(StackElement.NOT_SET, new HashSet<>(Arrays.asList('[','{','(')));
	}
	
	public BracketsValidation(Integer index, String input){
		this.input=input;
		this.index=index;
	}
	
	private boolean _validate() {
		if(input == null || input.length() == 0){
			return false;
		}
		
		for(Character currentBracket: input.toCharArray()){
			if(!_isValidBracket(currentBracket)){
				return false;
			} 
			
			if (_isValidOpenBracket(currentBracket)){
				if(processOpenBracket(currentBracket)){
					continue;
				}
				
				return false;
			}
			
			if(_isValidClosedBracket(currentBracket)){
				if(processClosedBracket(currentBracket)){
					continue;
				} 

				return false;
			} 
		}
		
		return !bracketStack.isEmpty() && bracketStack.peek().value == StackElement.NOT_SET;
	}
	
	private boolean processClosedBracket(Character closedBracket){
		if (bracketStack.isEmpty()){
			return false;
		}
		
		return bracketStack.pop().value == closedToOpenBrackets.get(closedBracket);
	}

	private boolean processOpenBracket(Character openBracket){
		if(bracketStack.isEmpty()){
			bracketStack.push(new StackElement(StackElement.NOT_SET, openBracket));
			bracketStack.push(new StackElement(openBracket, StackElement.NOT_SET));
			return true;
		}
		
		StackElement lastOpenBracket= bracketStack.peek();
		
		if(validNextBrackets.get(lastOpenBracket.value).contains(openBracket)){
			if(lastOpenBracket.next == StackElement.NOT_SET){
				lastOpenBracket.next = openBracket;
				bracketStack.push(new StackElement(openBracket, StackElement.NOT_SET));
				return true;
			}
			
			if(lastOpenBracket.next == openBracket){
				bracketStack.push(new StackElement(openBracket, StackElement.NOT_SET));
				return true;
			}
		}
	
		return false;
	}
	
	private boolean _isValidBracket(Character bracket){
		return _isValidOpenBracket(bracket) || _isValidClosedBracket(bracket);
	}
	
	private boolean _isValidOpenBracket(Character bracket){
		return validOpenBrackets.contains(bracket);
	}
	
	private boolean _isValidClosedBracket(Character bracket){
		return closedToOpenBrackets.containsKey(bracket);
	}
	
	public void run(){
		System.out.println(index + " : " +  input  + " : " + _validate());
	}
	
	// Entry to main thread
	public static void main(String args[]) {
		String inputFilePath = "./BracketStrings.txt";
		
		List<BracketsValidation> validators = new ArrayList<BracketsValidation>();
		
		// Populate validators for all input strings with a unique index
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader(inputFilePath));
			Integer stringIndex=0;
			String inputString;
			while((inputString = inputStream.readLine())!=null){
				validators.add(new BracketsValidation(++stringIndex, inputString));
			}
			inputStream.close();
		} catch(IOException ex ) {
			ex.printStackTrace();
		}
		
		// Start all the threads in validators
		for(BracketsValidation validator : validators) {
			validator.start();
		}
	}
}
