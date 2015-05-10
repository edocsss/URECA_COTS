package v1;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;

@SuppressWarnings("serial")
public class COTSLine extends Line2D.Double {
	private HashSet<Integer> prevContext = null;
	private int curContext;
	private boolean dashed = false;
	
	public COTSLine(Point2D.Double startPoint, Point2D.Double endPoint, int c) 
	{
		super(startPoint, endPoint);
		this.curContext = c;
		this.prevContext = new HashSet<Integer> ();
	}

	public void setDashed (boolean b)
	{
		this.dashed = b;
	}
	
	public boolean getDashed () 
	{
		return this.dashed;
	}
	
	public void addPrevContext (int c) 
	{
		this.prevContext.add(c);
	}
	
	public HashSet<Integer> getPrevContext () 
	{
		return this.prevContext;
	}
	
	public void resetPrevContext () 
	{
		this.prevContext.clear();
	}
	
	public void setCurContext (int c)
	{
		this.curContext = c;
	}
	
	public int getCurContext ()
	{
		return this.curContext;
	}
 }
