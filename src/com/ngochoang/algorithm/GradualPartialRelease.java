package com.ngochoang.algorithm;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

import com.ngochoang.database.DatabaseLayer;
import com.ngochoang.referenceselector.UsefulFunctions;

public class GradualPartialRelease {

	DatabaseLayer db;
	
	public GradualPartialRelease(DatabaseLayer _db)
	{
		db = _db;
	}

	public void Process()
	{
		if (db.FetchReferenceResults())
		{
			try {
				while (db.ruleSet.next())
				{
					Vector<Vector<Integer>> attrs = UsefulFunctions.ConvertStringToVector(db.ruleSet.getString("rule_attributes"));
					for (int i=1;i <= db.ruleCount;i++)
					{
						
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
