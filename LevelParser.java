import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LevelParser {

	public static int charClass;
	public static String inLine;
	public static char lexeme[] = new char[100];
	public static char nextChar;
	public static int lexLen;
	public static int token;
	public static int nextToken;
	
	public static final int LETTER = 0;
	public static final int DIGIT = 1;
	public static final int UNKNOWN = 99;
	public static final char EOF = '&';
	
	public static int INT_LIT = 10;
	public static int IDENT = 11;
	public static int EQUALS_OP = 20;
	public static int ADD_OP = 21;
	public static int SUB_OP = 22;
	public static int MULT_OP = 23;
	public static int DIV_OP = 24;
	public static int LEFT_PAREN = 25;
	public static int RIGHT_PAREN = 26;
	private static Scanner in;
	
	
	//**************************************
	//      Lexical Analyzer Code
	//**************************************
	
	public static void main(String[] args) throws IOException {
		
		//Define source file
		File inputFile = new File("input.txt");
		in = new Scanner(inputFile);

		//while source file has another line
		while(in.hasNext()) {
			inLine = in.nextLine();
			System.out.println(inLine);
			inLine = inLine + EOF;
			
			//if inline is null, file is empty/dne
			if(inLine == null) {
				System.out.println("Error, cannot read empty file");
			}
			else {
				//Get the first character of the line
				getChar();
				//call lex/expr function for each token
				do {
					lex();
					expr();
				}while (nextToken != EOF);
			}
		}	
	}
	
	//Add char to the lexeme
	public static void addChar() {
		
		if(lexLen <= 98) {
			lexeme[lexLen++] = nextChar;
			lexeme[lexLen] = 0;
		}
		
		else {
			//only shows up if an unknown symbol isn't mapped to a token
			System.out.println("Error, lexeme is too long");
		}
	}
	
	public static void getChar() throws IOException {
	
			//get the first character from the line
			nextChar = inLine.charAt(0);
			//take the first character and remove it from the original string
			inLine = inLine.substring(1);			
			
			if (nextChar != EOF) {
				
				//is character a letter?
				if(Character.isLetter(nextChar)) {
					
					charClass = LETTER;
				}
				
				//is character a digit?
				else if(Character.isDigit(nextChar)) {
					charClass = DIGIT;
				}
				
				//we don't know what the character is, set charclass to lookup
				else {
					charClass = UNKNOWN;
				}
					
			}
			//reached end of file
			else {
				charClass = EOF;
			}			
	}
	
	public static void getNonBlank() throws IOException {
		
		//if there is a space in the expression, ignore it. We don't need this character
		while (nextChar == ' ') {
			getChar();
		}
	}
	
	public static int lex() throws IOException {
		
		lexLen = 0;
		//get rid of spaces
		getNonBlank();
		
		switch (charClass) {
			
			case LETTER:
				
				//get all subsequent letters/digits and mark it as an identifier
				
				addChar();
				getChar();
				while (charClass == LETTER || charClass == DIGIT) {
					addChar();
					getChar();
				}
				nextToken = IDENT;
				break;
			
			case DIGIT:
				
				//get all subsequent digits and mark it as an integer
				
				addChar();
				getChar();
				while (charClass == DIGIT) {
					addChar();
					getChar();
				}
				nextToken = INT_LIT;
				break;
				
		case UNKNOWN:
			
			
				// we don't know the character, pass onto lookup for marking token
				lookup(nextChar);
				getChar();
				break;
				
		case EOF:
			
			//we reached the end of the expression! End the lex function
			
				nextToken = EOF;
				lexeme[0] = 'E';
				lexeme[1] = 'O';
				lexeme[2] = 'F';
				lexeme[3] = 0;
				break;
		}
		
		//print out our results
		System.out.printf("Next token is: " + nextTokenToString(nextToken) + ", next lexeme is: " + lexemeToString() + "\n");
		return nextToken;
	}
	
	private static int lookup(char nextChar2) {
		
		//check what character we have and mark it with a token
		switch(nextChar2) {

		case '(':
			
			nextToken = LEFT_PAREN;
			addChar();
			break;
			
		case ')':
			
			nextToken = RIGHT_PAREN;
			addChar();
			break;
			
		case '+':
			
			nextToken = ADD_OP;
			addChar();
			break;
			
		case '-':
			
			nextToken = SUB_OP;
			addChar();
			break;
			
		case '*':
			
			nextToken = MULT_OP;
			addChar();
			break;
			
		case '/':
			
			nextToken = DIV_OP;
			addChar();
			break;
			
		case '=':
			
			nextToken = EQUALS_OP;
			addChar();
			break;
			
		default:
			//end the program, feed the information
			nextToken = EOF;
			addChar();
			
		}
		
		return nextToken;
	}
	
	//goes through our lexeme array and prints out each individual part as called
	public static String lexemeToString() {
		
		String empty = "";
		for (int i = 0; i < lexeme.length; i++) {
			if (lexeme[i] == 0) {
				break;
			}
			empty += lexeme[i];
		}
		return empty;
	}
	
	
	//easiest way to change a character value to a string is to just pass the character value through a switch statement and return a string
	//as required
	public static String nextTokenToString(int nextToken) {
		
		String returnString = "";
		switch(nextToken) {
			
			case 10:
				returnString = "INT_LIT";				
				break;
			
			case 11:
				returnString = "IDENT";
				break;
				
			case 20:
				returnString = "EQUALS_OP";
				break;
				
			case 21:
				returnString = "ADD_OP";
				break;
				
			case 22:
				returnString = "SUB_OP";
				break;
				
			case 23:
				returnString = "MULT_OP";
				break;
				
			case 24:
				returnString = "DIV_OP";
				break;
				
			case 25:
				returnString = "LEFT_PAREN";
				break;
				
			case 26:
				returnString = "RIGHT_PAREN";
				break;
				
			case 38:
				returnString = "EOF";
				break;
		}
			
		
		return returnString;
	}
	
	//****************************************
	//            Parser Code
	//****************************************
	
	public static void expr() throws IOException{
		//Requirement asked for a string of *** around each expression
		//However, this lead to confusing output
		// >*** indicates an opened expression, similar to {
		// <*** indicates an closed expression, similar to }
		
		System.out.println(">*********************************************");
		System.out.printf("Enter <expr>\n");
		term();
		
		//Calles term function when a term is reached, per grammar rules
		while (nextToken == ADD_OP || nextToken == SUB_OP) {
			lex();
			term();
		}
		
		System.out.printf("Exit <expr>\n");
		System.out.println("<*********************************************");
	}
	
	public static void term() throws IOException {
		System.out.printf("Enter <term>\n");
		
		factor();
		//calls factor function when a factor is reached, per grammar rules
		while (nextToken == MULT_OP || nextToken == DIV_OP) {
			lex();
			factor();
		}
		System.out.printf("Exit <term>\n");
	}
	public static void factor() throws IOException {
		//Lowest form of the grammer given, can only be right parn or left paren or expression. 
		//if left paren, then there must be an expression, hence the call
		System.out.printf("Enter <factor>\n");
		
		if(nextToken == IDENT || nextToken == INT_LIT) {
			lex();
		}
		else {
			if(nextToken == LEFT_PAREN) {
				lex();
				expr();
				
				if (nextToken == RIGHT_PAREN) {
					lex();
				}
				else {
					//at this point, the token we end up with is not defined in our lookup. If we want more options, we can add them in lookup
					System.out.println("Token Unknown");
				}
			}
			else {
				//Same as above comment, except unknown token is outside parentheses
				System.out.println("Token Unknown");
			}
		}
		System.out.printf("Exit <factor>\n");
	}
		
}