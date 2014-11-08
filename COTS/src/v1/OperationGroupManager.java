package v1;

import java.util.ArrayList;

public class OperationGroupManager 
{
	public static OperationGroup getOperationGroupById (ArrayList<OperationGroup> operationGroups, int id)
	{
		for (OperationGroup og: operationGroups)
		{
			if (og.getId() == id)
			{
				return og;
			}
		}
		
		return null;
	}
}
