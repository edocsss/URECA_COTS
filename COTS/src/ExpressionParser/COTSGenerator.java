package ExpressionParser;

import java.util.Scanner;

/**
 * The main application class of COTS Diagram Automatic Generator.
 * 
 * @author Edwin Candinegara
 * @version 1.0
 *
 */

public class COTSGenerator 
{
	public static void main (String[] args)
	{
		String s;
		Scanner scan = new Scanner (System.in);
		
		System.out.println("Please note that current version of COTS Diagram Generator has a format for a given expression:");
		System.out.println("1. For each 2 operands, it has to be bounded by opening and closing parenthesis");
		System.out.println("2. Do not put any whitespace in between");
		System.out.println("Example: ((1->2)||3)");
		System.out.println("");
		
		System.out.print("Enter an expression: ");
		s = scan.next();
		
		Parser parser = new Parser(s);
		parser.parseExpression();
		
		DiagramGenerator diagramGenerator = new DiagramGenerator();
		diagramGenerator.constructLines(parser.getParsedExpression());
		
		diagramGenerator.printOutLine();
		diagramGenerator.generateDiagram();
		
		scan.close();
	}
}
