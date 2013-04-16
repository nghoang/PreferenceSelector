package com.ngochoang.referenceselector;

import java.util.Vector;

public class UsefulFunctions {
	public static String ConvertVectorToString(Vector<Vector<Integer>> v)
	{
		String res = "";
		for (Vector<Integer> v1 : v)
		{
			String t = "";
			for (Integer v2 : v1)
			{
				if (t.equals(""))
					t += v2;
				else
					t += "," + v2;
			}
			if (!t.equals(""))
			{
				if (res.equals(""))
					res += t;
				else
					res += ";" + t;
			}
		}
		return res;
	}
	
	public static String ConvertVectorToString2(Vector<Vector<String>> v)
	{
		String res = "";
		for (Vector<String> v1 : v)
		{
			String t = "";
			for (String v2 : v1)
			{
				if (t.equals(""))
					t += v2;
				else
					t += "," + v2;
			}
			if (!t.equals(""))
			{
				if (res.equals(""))
					res += t;
				else
					res += ";" + t;
			}
		}
		return res;
	}
	
	public static Vector<Vector<Integer>> ConvertStringToVector(String vs)
	{
		Vector<Vector<Integer>> res = new Vector<Vector<Integer>>();
		for (String v1 : vs.split(";"))
		{
			Vector<Integer> t = new Vector<Integer>();
			for (String v2 : v1.split(","))
			{
				t.add(Integer.parseInt(v1));
			}
			if (t.size() > 0)
				res.add(t);
		}
		return res;
	}
}
