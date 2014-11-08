package v1;

import java.util.ArrayList;
import java.util.Stack;

public class Parser 
{
	private String expression;
	private ArrayList<OperationGroup> processedExpression;
	private int idCounter;
	
	public Parser (String expression)
	{
		this.expression = expression;
		this.processedExpression = new ArrayList<OperationGroup> ();
		this.idCounter = -1;
	}
	
	public void parseExpression ()
	{
		// Stores the expression
		Stack<String> expStack = new Stack<String> ();
		
		// Stores the brackets only (check when to execute the expression inside the opening and closing brakets)
		Stack<Character> bracketStack = new Stack<Character> ();
		
		// Some temporary variable
		char check, before = 'z';
		char bracketCheck;
		String concat = "";
		//String result = "";
		String firstOperand, secondOperand, operator;
		
		// OperationGroup creation purpose
		OperationGroup op;
		
		// Index of character in the Expression string
		int i = 0;
		
		// As long as the bracketStack is not empty (meaning that not all operations inside the brackets have been touched, 
		// continue parsing
		// The condition i == 0 is only to skip the first iteration as the bracketStack is always empty initially (the 
		// opening bracket has not been pushed inside)
		while (!bracketStack.isEmpty() || i == 0)
		{
			check = this.expression.charAt(i);			
			//result = "";
			
			// If "(" -> push to stack
			if (check == '(')
			{
				bracketStack.push(check);
			}
			// If it is "|" and before this is a digit, then this symbol "|" must be the first one of the operator "||"
			else if (check == '|')
			{
				// The first symbol
				if (Character.isDigit(before))
				{
					// Then it means the number before is complete
					// Push the operator ID into the stack and reset the concat
					expStack.push(concat);
					concat = "";
				}
				
				// Add the "|" symbol. It doesn't matter whether it is the first or second
				// If it is the first one, concat is still empty
				// If it is the second one, concat's content is "|"
				concat += check;
				
				// If before is '|' also, it means the current symbol '|' is the second in the operator "||"
				// Therefore, concat now stores "||" and this is pushed to the stack and reset the concat
				if (before == '|')
				{
					expStack.push(concat);
					concat = "";
				}
			}
			// Actually, if check = '-', with the assumption that the expression is always correct, before '-' must be a digit
			else if (check == '-')
			{
				// This if statement is just to make sure the written above condition
				if (Character.isDigit(before))
				{
					// It means the number before is complete so it can be pushed to the stack
					expStack.push(concat);
					
					// Reset concat
					concat = "";
				}
				
				// With the assumption that the expression is always correct, concat should be empty currently (before the addition
				// of 'check')
				concat += check; // After this statement, concat should only consists of "-"
			}
			// If '>' is encountered, it must be after '-' 
			else if (check == '>')
			{
				// Just to make sure
				if (before == '-')
				{
					// Complete the operator "->"
					concat += check;
					
					// Push the operator
					expStack.push(concat);
					
					// Reset concat
					concat = "";
				}
			}
			// If check is a digit, just add to concat
			// The checking of whether the number is complete or not is done in other else if blocks
			else if (Character.isDigit(check))
			{
				concat += check;
			}
			// If check = ')', it is time to process the expression between the opening and closing brackets
			else if (check == ')')
			{
				// Push the token right before the closing bracket and after the operator (either || or ->)
				// Only push if the length of the concat is > 0 (error checking purpose)
				if (concat.length() > 0)
				{
					expStack.push(concat);
					concat = "";
				}
				
				bracketCheck = bracketStack.peek();
				if (bracketCheck == '(')
				{
					bracketStack.pop();
					
					/*
					// Inside here, can also determine which are the first operand, the operator, and the second operand
					// and store it in the object
					// Assume, first operator is always in the first site and second operator is always in the second site
					for (int j = 0; j < 3; j++)
					{
						result = expStack.pop() + result;
					}
					*/
										
					// Retrieve the first, second, and the operator -> to create a new OperationGroup object
					secondOperand = expStack.pop();
					operator = expStack.pop();
					firstOperand = expStack.pop();
					
					// Add a new OperationGroup to the ArrayList
					op = new OperationGroup(Integer.parseInt(firstOperand), Integer.parseInt(secondOperand), this.idCounter--, operator);
					this.processedExpression.add(op);
					
					// Push back the operation group which consists of 2 operands and 1 operator
					// Purpose: so that this group can be later grouped with other operand
					// Push back only the ID as we will get the object based on ID
					expStack.push(Integer.toString(op.getId()));
					
					// After this, should push the result which has been transformed into a group of operation with another class
					// so that it becomes like, for example:
					// Expression: (1 -> (2 || 3))
					// 1. (1 -> Group 1)
					// 2. Group 2
				}
			}
			
			before = check;
			i++;
		}
		
		/*
		 * This while part is not necessary assuming the expression is in the following format:
		 		- For each two operands, there is always one set of brackets bounding the 1 operation
				- Example: (((1->2)||(3->4))->(5->6))
		 * To make sure that all smaller group operation is grouped into a bigger one
		 * In case there are 3 operations that are not bounded by brackets
		 *  
		 
		
		String temp = null;
		while (!expStack.isEmpty())
		{
			if (temp != null) 
			{
				expStack.push(temp);
			}
			
			result = "";
			for (int j = 0; j < 3; j++)
			{
				result = expStack.pop() + result;
			}
			
			expStack.push(result);
			
			// Test whether there is only one more item left in the stack
			temp = expStack.pop();
		}
		
		*/
	}
	
	public String getExpression ()
	{
		return this.expression;
	}
	
	public ArrayList<OperationGroup> getParsedExpression ()
	{
		return this.processedExpression;
	}
}
