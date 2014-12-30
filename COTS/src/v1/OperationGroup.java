package v1;

import java.util.ArrayList;

public class OperationGroup 
{
	/**
	 * Assumption: the first operand will always be drawn in the first dimension (EAST) and the second operand
	 * 			   in the second dimension (NORTH)
	 */
	
	/**
	 * The integer stored here is the ID of a single operand (if it is a positive integer)
	 * or an OperationGroup object (if it is a negative integer)
	 * 
	 * Positive: SINGLE operand
	 * Negative: Another OperationGroup object (composed of many other operands)
	 */
	private int firstOperand;
	private int secondOperand;
	
	/**
	 * The ID of this OperationGroup
	 * To quickly fetch an object from all OperationGroup objects
	 */
	private int id;
	
	/**
	 * The operation type
	 */
	private String operator;
	
	/**
	 * Whether the current processed OperationGroup is a concurrent operation -> based on the operator
	 */
	private boolean concurrency;
	
	/**
	 *	Whether the current OperationGroup is CALLED from a concurrent OperationGroup object
	 * 	If we know that the current OperationGroup is from a CONCURRENT OperationGroup, we can
	 *	draw the other site part together with this site 
	 */
	private boolean prevConcurrent;
	
	public OperationGroup (int firstOperand, int secondOperand, int id, String operator)
	{
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
		this.id = id;
		this.operator = operator;
	}
	
	/**
	 * Checking how many operations there are inside an OperationGroup
	 * 
	 * @param operationGroups	to retrieve the correct OperationGroup object based on the ID
	 * @param n					counter -> needed as an initialize value in a recursive file (no pointer like in C)
	 * @return					the number of operations inside this OperationGroup
	 */
	public int getDepth ()
	{
		int firstDepth = -1, secondDepth = -1;
		OperationGroup og;
		
		if (this.firstOperand > 0 && this.secondOperand > 0)
		{
			return 2;
		}
		else
		{
			if (this.firstOperand < 0)
			{
				og = OperationGroupManager.getOperationGroupById(this.firstOperand);
				firstDepth = 1 + og.getDepth();
			}
			
			if (this.secondOperand < 0)
			{
				og = OperationGroupManager.getOperationGroupById(this.secondOperand);
				secondDepth = 1 + og.getDepth();
			}
			
			// As there are two SITE, get the most number of operations
			return (firstDepth > secondDepth)? firstDepth : secondDepth;
		}
	}
	
	public ArrayList<Integer> getOtherSiteID (ArrayList<Integer> a) 
	{
		if (this.firstOperand > 0)
		{
			a.add(this.firstOperand);
		}
		
		if (this.secondOperand > 0)
		{
			a.add(this.secondOperand);
		}
		
		if (this.firstOperand > 0 && this.secondOperand > 0)
		{
			return a;
		}
		else if (this.firstOperand < 0)
		{
			return OperationGroupManager.getOperationGroupById(this.firstOperand).getOtherSiteID(a);
		}
		else if (this.secondOperand < 0)
		{
			return OperationGroupManager.getOperationGroupById(this.secondOperand).getOtherSiteID(a);
		}
		
		return a;
	}
	
	public int getFirstOperand ()
	{
		return this.firstOperand;
	}
	
	public int getSecondOperand ()
	{
		return this.secondOperand;
	}
	
	public int getId ()
	{
		return this.id;
	}
	
	public String getOperator ()
	{
		return this.operator;
	}
	
	public boolean getConcurrency () 
	{
		return this.concurrency;
	}
	
	public void setConcurrency (boolean concurrency)
	{
		this.concurrency = concurrency;
	}
	
	public boolean getPrevConcurrency ()
	{
		return this.prevConcurrent;
	}
	
	public void setPrevConcurrency (boolean prevConcurrent)
	{
		this.prevConcurrent = prevConcurrent;
	}
}