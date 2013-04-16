package com.ngochoang.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import com.ngochoang.referenceselector.App;
import com.ngochoang.referenceselector.ReferenceSet;
import com.ngochoang.referenceselector.UsefulFunctions;

//this class contains all the methods to interact with database
public class DatabaseLayer {
	Connection connect = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet res = null;
	String table_prefix = "1";
	Random ran = new Random(App.random_seed);
	static int query_level = 0;
	public ResultSet ruleSet;
	public int ruleCount = 0;

	public boolean Connect() {
		table_prefix = App.db_prefix;// "ds" + counter + "_";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + App.db_host
					+ "/" + App.db_database + "?" + "user=" + App.db_username
					+ "&password=" + App.db_password);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void GenerateDatabase() {
		// int counter = 1;
		try {
			//
			// statement = connect.createStatement();
			// while (true) {
			// resultSet = statement
			// .executeQuery("SELECT COUNT(*) AS counter FROM information_schema.tables WHERE table_schema = '"
			// + App.db_database
			// + "' AND table_name = 'ds"
			// + counter + "_reference_data'");
			// resultSet.next();
			// if (resultSet.getInt("counter") == 0)
			// break;
			// else
			// counter++;
			// }
			// resultSet.close();
			// statement.close();

			statement = connect.createStatement();
			// table_prefix = App.db_prefix;//"ds" + counter + "_";
			String query = "CREATE TABLE IF NOT EXISTS " + table_prefix
					+ "reference_data (";
			query += " `attr_id` int(11) NOT NULL AUTO_INCREMENT,";
			for (int i = 0; i < App.number_of_attributes; i++) {
				query += " `attr_" + (i + 1) + "` int(11),";
			}
			query += " `price` int(11),";
			query += " PRIMARY KEY (`attr_id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1";
			statement.executeUpdate(query);
			statement.close();
			for (int i = 0; i < App.number_of_generated_records; i++) {
				query = "INSERT INTO " + table_prefix + "reference_data SET ";
				for (int j = 0; j < App.number_of_attributes; j++) {
					int attr_value = ran.nextInt(App.max_value_on_attribute
							- App.min_value_on_attribute)
							+ App.min_value_on_attribute;
					if (j == 0)
						query += " attr_" + (j + 1) + " = " + attr_value;
					else
						query += " ,attr_" + (j + 1) + " = " + attr_value;
				}
				int price = ran.nextInt(App.max_price - App.min_price)
						+ App.min_price;
				query += " ,price = " + price;
				statement = connect.createStatement();
				statement.executeUpdate(query);
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void GenenerateReference() {

	}

	public void InsertReference(Vector<Vector<String>> referenceAttributeSets,
			Vector<Vector<Vector<Integer>>> referenceValues) {
		try {
			int numAttr = 0;
			for (Vector<String> t : referenceAttributeSets) {
				numAttr += t.size();
			}

			String query = "CREATE TABLE IF NOT EXISTS " + table_prefix
					+ "reference_rules (";
			query += " `rule_id` int(11) NOT NULL AUTO_INCREMENT,";
			query += " `rule_attributes` varchar(500) ";
			for (int i = 0; i < App.max_attr_in_rule; i++) {
				query += ", `values_" + (i + 1) + "` varchar(500)";
			}
			query += ", PRIMARY KEY (`rule_id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1";
			statement = connect.createStatement();
			statement.executeUpdate(query);
			statement.close();

			query = "INSERT INTO " + table_prefix + "reference_rules SET ";
			query += " rule_attributes = '"
					+ UsefulFunctions
							.ConvertVectorToString2(referenceAttributeSets)
					+ "' ";
			for (int i = 0; i < numAttr; i++) {
				query += ", values_"
						+ (i + 1)
						+ " = '"
						+ UsefulFunctions.ConvertVectorToString(referenceValues
								.get(i)) + "'";
			}
			statement = connect.createStatement();
			statement.executeUpdate(query);
			statement.close();
		} catch (Exception Ex) {
			Ex.printStackTrace();
		}
	}

	public void GenerateSelectionQueries(ReferenceSet r) {
		try {
			String query = "CREATE TABLE IF NOT EXISTS " + table_prefix
					+ "reference_queries (";
			query += " `query_id` int(11) NOT NULL AUTO_INCREMENT,";
			query += " `query` varchar(5000) ";
			query += ", `query_attr` varchar(5000) ";
			query += ", `query_level` varchar(5000) ";
			query += ", PRIMARY KEY (`query_id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1";
			statement = connect.createStatement();
			statement.executeUpdate(query);
			statement.close();
			
			query = "DELETE FROM " + table_prefix
					+ "reference_queries";
			statement = connect.createStatement();
			statement.executeUpdate(query);
			statement.close();
			
			Vector<Vector<String>> rowQueries = new Vector<Vector<String>>();
			int curAtt = 0;
			for (Vector<String> t : r.referenceAttributeSets) {
				query = "";
				if (t.size() == 1)//no grouped attributes
				{
					Vector<String> rowQuery = new Vector<String>();
					for (Vector<Integer> values : r.referenceValueSets.get(curAtt))
					{
						if (values.size() == 1)
						{
							query = " attr_" + t.get(0) + " = '"+values.get(0)+"' ";
						}
						else
						{
							String orquery = "";
							for (Integer subt : values)
							{
								if (orquery.equals(""))
									orquery += " attr_" + t.get(0) + " = '"+subt+"' ";
								else
									orquery += " OR attr_" + t.get(0) + " = '"+subt+"' ";
							}
							
							query = " ("+orquery+") ";
						}
						rowQuery.add(query);
					}
					rowQueries.add(rowQuery);
					curAtt++;
				}
				else
				{
					Vector<Vector<Vector<Integer>>> values = new Vector<Vector<Vector<Integer>>>();
					int[] indexes = new int[t.size()];
					for (int i=0;i<indexes.length;i++)
						indexes[i] = 0;
					for (int i=curAtt;i<curAtt + t.size();i++)
						values.add(r.referenceValueSets.get(i));
					
					Vector<String> rowQuery = GenerateQueryGroup(t,values,indexes);
					rowQueries.add(rowQuery);
					curAtt += t.size();
				}
			}
			
			/*for (Vector<String> r1 : rowQueries)
			{
				boolean isFirst = true;
				for (String r2 : r1)
				{
					if (r2.equals(""))
						continue;
					if (isFirst)
						System.out.print(r2);
					else
						System.out.print(" -> " + r2);
					isFirst = false;
				}
				System.out.println();
			}*/
			int[] nindexes= new int[rowQueries.size()];
			for (int i=0;i<nindexes.length;i++)
				nindexes[i] = 0;
			
			while (true)
			{
				
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Vector<String> GenerateQueryGroup(Vector<String> t,
			Vector<Vector<Vector<Integer>>> values, int[] indexes) {
		Vector<String> rowQuery = new Vector<String>();
		Vector<Vector<int[]>> reservedIndexes = new Vector<Vector<int[]>>();
		Vector<int[]> reversedIndex = new Vector<int[]>();
		reversedIndex.add(indexes);
		reservedIndexes.add(reversedIndex);
		while (true)
		{
			boolean isFinished = true;
			int loopto = reversedIndex.size();
			Vector<int[]> prereversedIndex = reversedIndex;
			reversedIndex = new Vector<int[]>();
			for (int i=0;i<loopto;i++)
			{
				for (int j=0;j<prereversedIndex.get(i).length;j++)
				{
					if (prereversedIndex.get(i)[j] <= values.get(j).size() - 2)
					{
						int[] newV = new int[indexes.length];
						for (int h=0;h<prereversedIndex.get(i).length;h++)
							newV[h] = prereversedIndex.get(i)[h];
						newV[j]++;
						boolean isContain = false;
						for (int[] temp : reversedIndex)
							if (ArraystoString(temp).equals(ArraystoString(newV)))
								isContain = true;
						if (isContain == false)
						{
							reversedIndex.add(newV);
							isFinished = false;
						}
					}
				}
			}
			reservedIndexes.add(reversedIndex);
			if (isFinished == true)
				break;
		}
		for (Vector<int[]>  inds : reservedIndexes)
		{
			String rowQ = "";
			for (int[] ind : inds){
				String q = "";
				for (int i=0;i<t.size();i++)
				{
					if (values.get(i).get(ind[i]).size() == 1)
					{	
						if (q.equals(""))
							q += " attr_" + t.get(i) + " = '"+values.get(i).get(ind[i]).get(0)+"' ";
						else
							q += " AND attr_" + t.get(i) + " = '"+values.get(i).get(ind[i]).get(0)+"' ";
					}
					else
					{
						String orquery = "";
						for (Integer subt : values.get(i).get(ind[i]))
						{
							if (orquery.equals(""))
								orquery += " attr_" + t + " = '"+subt+"' ";
							else
								orquery += " OR attr_" + t + " = '"+subt+"' ";
						}
						if (q.equals(""))
							q += " ("+orquery+") ";
						else
							q += " AND ("+orquery+") ";
					}
				}
				if (rowQ.equals(""))
					rowQ += " ("+q+") ";
				else
					rowQ += " OR ("+q+")";
			}
			rowQuery.add(rowQ);
		}
		return rowQuery;
	}

	private String ArraystoString(int[] newV) {
		String t = "";
		for (int i : newV)
			if (t.equals(""))
				t += i;
			else
				t += "," + i;
		return t;
	}

	public Vector<Integer> RunAlgorithmOne() {
		Vector<Integer> res = new Vector<Integer>();
		try {
			String queryWhere = "";
			statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM "
					+ table_prefix + "reference_queries");
			while (resultSet.next()) {
				if (queryWhere.equals(""))
					queryWhere += "(" + resultSet.getString("query") + ")";
				else
					queryWhere += " OR (" + resultSet.getString("query") + ")";
			}
			statement.close();
			resultSet.close();

			String query = "SELECT attr_id FROM " + table_prefix
					+ "reference_data WHERE " + queryWhere;
			statement = connect.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				res.add(resultSet.getInt("attr_id"));
			}
			statement.close();
			resultSet.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return res;
	}

	public Vector<Integer> RunAlgorithmTwo(Integer max) {
		Vector<Integer> res = new Vector<Integer>();
		boolean isFinished = false;
		try {
			statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM "
					+ table_prefix + "reference_queries ORDER BY query_id");
			while (resultSet.next()) {
				String query = "SELECT attr_id FROM " + table_prefix
						+ "reference_data WHERE "
						+ resultSet.getString("query");
				Statement st2 = connect.createStatement();
				ResultSet rs2 = statement.executeQuery(query);
				while (rs2.next()) {
					if (res.contains(rs2.getInt("attr_id")) == false) {
						res.add(rs2.getInt("attr_id"));
						if (res.size() >= max)
							isFinished = true;
					}
					if (isFinished)
						break;
				}
				st2.close();
				rs2.close();
				if (isFinished)
					break;
			}
			statement.close();
			resultSet.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return res;
	}

	public Vector<Integer> RunAlgorithmBisection() {
		Vector<Integer> res = new Vector<Integer>();
		try {
			statement = connect.createStatement();
			ResultSet resultSet = statement
					.executeQuery("SELECT COUNT(*) AS counter FROM "
							+ table_prefix
							+ "reference_queries ORDER BY query_level");
			resultSet.next();
			int inittotal = resultSet.getInt("counter");
			int total = inittotal;
			resultSet.close();
			statement.close();
			int limit = total / 2;
			String query = "";
			while (limit <= inittotal) {
				query = "";
				statement = connect.createStatement();
				resultSet = statement.executeQuery("SELECT * FROM "
						+ table_prefix
						+ "reference_queries ORDER BY query_level");
				int lastPri = -1;
				int counter = 1;
				while (resultSet.next()) {
					int curPri = resultSet.getInt("query_level");
					if (counter > limit && lastPri != curPri)
						break;
					lastPri = curPri;
					if (query.equals(""))
						query += resultSet.getString("query");
					else
						query += " OR " + resultSet.getString("query");
					counter++;
				}
				resultSet.close();
				statement.close();

				// get products
				Statement statementPd = connect.createStatement();
				ResultSet resultSetPd = statementPd
						.executeQuery("SELECT COUNT(attr_id) AS counter FROM "
								+ table_prefix + "reference_data WHERE "
								+ query);
				resultSetPd.next();
				int resultCounter = resultSetPd.getInt("counter");
				resultSetPd.close();
				resultSetPd.close();

				if (resultCounter < App.number_of_returned_results) {
					if (limit == total)
						break;
					int newlimit = (int) Math.ceil((total + limit) / 2D);
					if (newlimit == limit) {
						break;
					}
					limit = newlimit;
				} else if (resultCounter > App.number_of_returned_results) {
					if (limit <= 1)
						break;
					if (limit == total)
						break;
					int newlimit = limit / 2;
					if (newlimit == limit) {
						break;
					}
					total = limit;
					limit = newlimit;
				}
			}
			if (!query.equals("")) {
				Statement statementPd = connect.createStatement();
				ResultSet resultSetPd = statementPd
						.executeQuery("SELECT attr_id FROM " + table_prefix
								+ "reference_data WHERE " + query);
				while (resultSetPd.next())
					res.add(resultSetPd.getInt("attr_id"));
				resultSetPd.close();
				resultSetPd.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return res;
	}

	public boolean FetchReferenceResults() {
		Statement statementPd;
		try {
			statementPd = connect.createStatement();
			ResultSet resultSetPd = statementPd
					.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'reference' AND table_name = '"
							+ table_prefix + "reference_rules+'");
			statementPd.close();
			resultSetPd.next();
			ruleCount = resultSetPd.getInt(0);
			statementPd = connect.createStatement();
			ruleSet = statementPd.executeQuery("SELECT * FROM " + table_prefix
					+ "reference_rules");
			statementPd.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void CreateSampleDB(int sample_data_size) {
		String where = "";
		Random ran = new Random();
		try {
			statement = connect.createStatement();
			int ranAttId = ran.nextInt(App.number_of_attributes) + 1;
			int ranAttVl = ran.nextInt(App.max_value_on_attribute
					- App.min_value_on_attribute)
					+ App.min_value_on_attribute;
			ResultSet resultSet = statement
					.executeQuery("SELECT COUNT(*) AS counter FROM "
							+ table_prefix + "reference_data WHERE attr_"
							+ ranAttId + " = '" + ranAttVl + "'");
			int counter = resultSet.getInt("counter");
			if (counter > sample_data_size)
			{}
			else
			{}
			statement.close();
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Vector<Integer> RunAlgorithmThree(int max) {
		Vector<Integer> res = new Vector<Integer>();
		boolean isFinished = false;
		String where = "";
		try {
			statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM "
					+ table_prefix + "reference_queries ORDER BY query_id");
			while (resultSet.next()) {
				if (where.equals(""))
					where = "("+resultSet.getString("query")+")";
				else
					where += " AND (" + resultSet.getString("query") + ")";
				String query = "SELECT attr_id FROM " + table_prefix
						+ "reference_data WHERE "
						+ where;
				Statement st2 = connect.createStatement();
				ResultSet rs2 = statement.executeQuery(query);
				while (rs2.next()) {
					if (res.contains(rs2.getInt("attr_id")) == false) {
						res.add(rs2.getInt("attr_id"));
						if (res.size() >= max)
							isFinished = true;
					}
					if (isFinished)
						break;
				}
				st2.close();
				rs2.close();
				if (isFinished)
					break;
			}
			statement.close();
			resultSet.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return res;
	}

	public void GenerateSelectionQueries() {
		try
		{
			Statement st = connect.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM "+table_prefix+"reference_rules");
			ReferenceSet r = new ReferenceSet();
			while (rs.next())
			{
				Vector<Vector<String>> attrs = new Vector<Vector<String>>();
				Vector<Vector<Vector<Integer>>> attrvals = new Vector<Vector<Vector<Integer>>>();
				String t = rs.getString("rule_attributes");
				int Attrs = 0;
				for (String i : t.split(";"))
				{
					Vector<String> t2 = new Vector<String>();
					for (String j : i.split(","))
					{
						t2.add(j);
						Attrs++;
					}
					attrs.add(t2);
				}
				
				for (int i=1;i<= Attrs;i++)
				{
					Vector<Vector<Integer>> tt = new Vector<Vector<Integer>>();
					t = rs.getString("values_" + i);
					for (String j : t.split(";"))
					{
						Vector<Integer> t3 = new Vector<Integer>();
						for (String k : j.split(","))
							t3.add(Integer.parseInt(k));
						tt.add(t3);
					}
					attrvals.add(tt);
				}
				r.referenceAttributeSets = attrs;
				r.referenceValueSets = attrvals;
				GenerateSelectionQueries(r);
			}
			rs.close();
			st.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
