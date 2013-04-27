package com.victor.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Vector;

import com.victor.preferenceselector.App;
import com.victor.preferenceselector.UsefulFunctions;
import com.victor.preferenceselector.preferenceSet;

//this class contains all the methods to interact with database
public class DatabaseLayer {
	public boolean overflow = false;
	Connection connect = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet res = null;
	String table_prefix = "1";
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
			// + counter + "_preference_data'");
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
					+ "preference_data (";
			query += " `attr_id` int(11) NOT NULL AUTO_INCREMENT,";
			for (int i = 0; i < App.number_of_attributes; i++) {
				query += " `attr_" + (i + 1) + "` int(11),";
			}
			query += " `price` int(11),";
			query += " PRIMARY KEY (`attr_id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1";
			statement.executeUpdate(query);
			statement.close();

			statement = connect.createStatement();
			// table_prefix = App.db_prefix;//"ds" + counter + "_";
			query = "CREATE TABLE IF NOT EXISTS " + table_prefix
					+ "preference_data_sample (";
			query += " `attr_id` int(11) NOT NULL AUTO_INCREMENT,";
			for (int i = 0; i < App.number_of_attributes; i++) {
				query += " `attr_" + (i + 1) + "` int(11),";
			}
			query += " `price` int(11),";
			query += " PRIMARY KEY (`attr_id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1";
			statement.executeUpdate(query);
			statement.close();

			for (int i = 0; i < App.number_of_generated_records; i++) {
				query = "INSERT INTO " + table_prefix + "preference_data SET ";
				for (int j = 0; j < App.number_of_attributes; j++) {
					int attr_value = UsefulFunctions.RandomInRange(App.min_value_on_attribute,App.max_value_on_attribute);
					if (j == 0)
						query += " attr_" + (j + 1) + " = " + attr_value;
					else
						query += " ,attr_" + (j + 1) + " = " + attr_value;
				}
				int price = UsefulFunctions.RandomInRange(App.min_price,App.max_price);
				query += " ,price = " + price;
				statement = connect.createStatement();
				statement.executeUpdate(query);
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void GeneneratePreference() {

	}

	public void InsertPreference(Vector<Vector<String>> preferenceAttributeSets,
			Vector<Vector<Vector<Integer>>> preferenceValues) {
		try {
			int numAttr = 0;
			for (Vector<String> t : preferenceAttributeSets) {
				numAttr += t.size();
			}

			String query = "CREATE TABLE IF NOT EXISTS " + table_prefix
					+ "preference_rules (";
			query += " `rule_id` int(11) NOT NULL AUTO_INCREMENT,";
			query += " `rule_attributes` varchar(500) ";
			for (int i = 0; i < App.max_attr_in_rule; i++) {
				query += ", `values_" + (i + 1) + "` varchar(500)";
			}
			query += ", PRIMARY KEY (`rule_id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1";
			statement = connect.createStatement();
			statement.executeUpdate(query);
			statement.close();

			query = "INSERT INTO " + table_prefix + "preference_rules SET ";
			query += " rule_attributes = '"
					+ UsefulFunctions
							.ConvertVectorToString2(preferenceAttributeSets)
					+ "' ";
			for (int i = 0; i < numAttr; i++) {
				query += ", values_"
						+ (i + 1)
						+ " = '"
						+ UsefulFunctions.ConvertVectorToString(preferenceValues
								.get(i)) + "'";
			}
			statement = connect.createStatement();
			statement.executeUpdate(query);
			statement.close();
		} catch (Exception Ex) {
			Ex.printStackTrace();
		}
	}

	public void GenerateSelectionQueries(preferenceSet r) {
		try {
			String query = "CREATE TABLE IF NOT EXISTS " + table_prefix
					+ "preference_queries (";
			query += " `query_id` int(11) NOT NULL AUTO_INCREMENT,";
			query += " `query` varchar(5000) ";
			query += ", `query_attr` varchar(5000) ";
			query += ", `query_level` varchar(5000) ";
			query += ", PRIMARY KEY (`query_id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1";
			statement = connect.createStatement();
			statement.executeUpdate(query);
			statement.close();

			query = "DELETE FROM " + table_prefix + "preference_queries";
			statement = connect.createStatement();
			statement.executeUpdate(query);
			statement.close();

			Vector<Vector<String>> rowQueries = new Vector<Vector<String>>();
			int curAtt = 0;
			for (Vector<String> t : r.preferenceAttributeSets) {
				query = "";
				if (t.size() == 1)// no grouped attributes
				{
					Vector<String> rowQuery = new Vector<String>();
					for (Vector<Integer> values : r.preferenceValueSets
							.get(curAtt)) {
						if (values.size() == 1) {
							query = " attr_" + t.get(0) + " = '"
									+ values.get(0) + "' ";
						} else {
							String orquery = "";
							for (Integer subt : values) {
								if (orquery.equals(""))
									orquery += " attr_" + t.get(0) + " = '"
											+ subt + "' ";
								else
									orquery += " OR attr_" + t.get(0) + " = '"
											+ subt + "' ";
							}

							query = " (" + orquery + ") ";
						}
						rowQuery.add(query);
					}
					rowQueries.add(rowQuery);
					curAtt++;
				} else {
					Vector<Vector<Vector<Integer>>> values = new Vector<Vector<Vector<Integer>>>();
					int[] indexes = new int[t.size()];
					for (int i = 0; i < indexes.length; i++)
						indexes[i] = 0;
					for (int i = curAtt; i < curAtt + t.size(); i++)
						values.add(r.preferenceValueSets.get(i));

					Vector<String> rowQuery = GenerateQueryGroup(t, values,
							indexes);
					rowQueries.add(rowQuery);
					curAtt += t.size();
				}
			}

			for (Vector<String> r1 : rowQueries) {
				boolean isFirst = true;
				for (String r2 : r1) {
					if (r2.equals(""))
						continue;
					if (isFirst)
						System.out.print(r2);
					else
						System.out.print(" -> " + r2);
					isFirst = false;
				}
				System.out.println();
			}
			int[] nindexes = new int[rowQueries.size()];
			for (int i = 0; i < nindexes.length; i++)
				nindexes[i] = 0;

			
			//we decomposed group of attributes and values into AND and OR, here we create list of queries from them
			int level = 0;
			while (true) {
				boolean isFinished = false;
				String insertingQuery = "";
				//in a rule set, called each line is a rule line, 
				//we loop in each line and generate the query by priority
				for (int h = 0; h < rowQueries.size(); h++) {
					if (nindexes[0] >= rowQueries.get(0).size())
					{
						isFinished = true;
						break;
					}
					if (insertingQuery.equals(""))
						insertingQuery += rowQueries.get(h).get(nindexes[h]);
					else
						insertingQuery += " AND ("
								+ rowQueries.get(h).get(nindexes[h]) + ")";

					//in the last line
					if (h == rowQueries.size() - 1) {

						boolean cannextstep = false;
						//create next indexes for next query
						for (int u = nindexes.length - 1; u >= 0; u--) {
							if (cannextstep == true)
								break;
							//if current line index is the last index value
							if (nindexes[u] >= rowQueries.get(u).size() - 1) {
								//put it back to 0 and +1 for the above line
								nindexes[u] = 0;
								if (u == 0) {//the last one, so we finish
									isFinished = true;
									break;
								}
								else//+1 for the above line
								{
									if (nindexes[u-1] >= rowQueries.get(u-1).size() - 1)//if exceed above line, we move the the next above
										continue;
									nindexes[u - 1] = nindexes[u - 1] + 1;
									cannextstep = true;
								}
							} else {
								if (cannextstep == true)
									break;
								nindexes[u] = nindexes[u] + 1;
								break;
							}
						}
					}
				}
				System.out.println(insertingQuery);
				query = "INSERT INTO " + table_prefix
						+ "preference_queries SET query='"
						+ insertingQuery.replace("'", "''") + "', query_attr='"
						+ r.preferenceAttributeSets.toString()
						+ "', query_level='" + level + "'";
				level++;
				statement = connect.createStatement();
				statement.executeUpdate(query);
				statement.close();
				if (isFinished)
					break;
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
		while (true) {
			boolean isFinished = true;
			int loopto = reversedIndex.size();
			Vector<int[]> prereversedIndex = reversedIndex;
			reversedIndex = new Vector<int[]>();
			for (int i = 0; i < loopto; i++) {
				for (int j = 0; j < prereversedIndex.get(i).length; j++) {
					if (prereversedIndex.get(i)[j] <= values.get(j).size() - 2) {
						int[] newV = new int[indexes.length];
						for (int h = 0; h < prereversedIndex.get(i).length; h++)
							newV[h] = prereversedIndex.get(i)[h];
						newV[j]++;
						boolean isContain = false;
						for (int[] temp : reversedIndex)
							if (ArraystoString(temp).equals(
									ArraystoString(newV)))
								isContain = true;
						if (isContain == false) {
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
		for (Vector<int[]> inds : reservedIndexes) {
			String rowQ = "";
			for (int[] ind : inds) {
				String q = "";
				for (int i = 0; i < t.size(); i++) {
					if (values.get(i).get(ind[i]).size() == 1) {
						if (q.equals(""))
							q += " attr_" + t.get(i) + " = '"
									+ values.get(i).get(ind[i]).get(0) + "' ";
						else
							q += " AND attr_" + t.get(i) + " = '"
									+ values.get(i).get(ind[i]).get(0) + "' ";
					} else {
						String orquery = "";
						for (Integer subt : values.get(i).get(ind[i])) {
							if (orquery.equals(""))
								orquery += " attr_" + t.get(i) + " = '" + subt + "' ";
							else
								orquery += " OR attr_" + t.get(i) + " = '" + subt
										+ "' ";
						}
						if (q.equals(""))
							q += " (" + orquery + ") ";
						else
							q += " AND (" + orquery + ") ";
					}
				}
				if (rowQ.equals(""))
					rowQ += " (" + q + ") ";
				else
					rowQ += " OR (" + q + ")";
			}
			if (rowQ.equals("") == false)
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

	public Vector<Integer> RunAlgorithmOne(int max, int drop) {
		overflow = false;//default we set it false. we will check and set it to true if nb of results >= nb of expected results
		Vector<Integer> res = new Vector<Integer>();
		try {
			String queryWhere = "";
			statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM "
					+ table_prefix + "preference_queries");
			//combining all query into one
			while (resultSet.next()) {
				if (queryWhere.equals(""))
					queryWhere += "(" + resultSet.getString("query") + ")";
				else
					queryWhere += " OR (" + resultSet.getString("query") + ")";
			}
			statement.close();
			resultSet.close();

			//run and get result
			String query = "SELECT attr_id FROM " + table_prefix
					+ "preference_data WHERE " + queryWhere + " ORDER BY price ASC";
			statement = connect.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				res.add(resultSet.getInt("attr_id"));
			}
			statement.close();
			resultSet.close();
			
			/*//overflow check
			if (res.size() >= App.number_of_returned_results)
				overflow = true;
			else
				overflow = false;
			
			//do the drop
			Vector<Integer> res2 = new Vector<Integer>();
			int newsize = res.size() - res.size() * App.drop_rate/100;
			if (newsize < App.number_of_returned_results)
				newsize = App.number_of_returned_results;
			for (int i2=0;i2<newsize;i2++)
			{
				res2.add(res.get(i2));
			}
				
			if (res.size() <= App.number_of_returned_results)
				return res;
			else
			{
				//selecting randomly expected results
				res = new Vector<Integer>();
				Collections.shuffle(res2);
				
				for (int i2=0;i2<App.number_of_returned_results;i2++)
					res.add(res2.get(i2));
				return res;
			}*/
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DoPeepsDrop(res,max,drop);
	}
	
	Vector<Integer> DoPeepsDrop(Vector<Integer> resultSet, int max, int drop)
	{
		Vector<Integer> res = new Vector<Integer>();
		int newsize = resultSet.size() - resultSet.size() * drop/100;
		for (int i2=0;i2<newsize;i2++)
		{
			res.add(res.get(i2));
		}
		if (res.size() <= max)
			return res;
		else
		{
			Vector<Integer> res2 = new Vector<Integer>();
			Collections.shuffle(res);
			for (int i2=0;i2<max;i2++)
				res2.add(res.get(i2));
			return res2;
		}
	}

	public Vector<Integer> RunAlgorithmTwo(int max,int drop) {
		Vector<Integer> res = new Vector<Integer>();
		boolean isFinished = false;
		try {
			statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM "
					+ table_prefix + "preference_queries ORDER BY query_id");
			while (resultSet.next()) {
				String query = "SELECT attr_id FROM " + table_prefix
						+ "preference_data WHERE "
						+ resultSet.getString("query");
				Statement st2 = connect.createStatement();
				ResultSet rs2 = st2.executeQuery(query);
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
		return DoPeepsDrop(res, max, drop);
	}

	public Vector<Integer> RunAlgorithmBisection(int max, int drop) {
		Vector<Integer> res = new Vector<Integer>();
		try {
			statement = connect.createStatement();
			ResultSet resultSet = statement
					.executeQuery("SELECT COUNT(*) AS counter FROM "
							+ table_prefix
							+ "preference_queries ORDER BY query_level");
			resultSet.next();
			int inittotal = resultSet.getInt("counter");
			int total = inittotal;
			resultSet.close();
			statement.close();
			int limit = total / 2;
			String query = "";
			int from = 0;
			while (limit <= inittotal) {
				query = "";
				statement = connect.createStatement();
				resultSet = statement.executeQuery("SELECT * FROM "
						+ table_prefix
						+ "preference_queries ORDER BY query_level " + "LIMIT "
						+ from + "," + limit);
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
								+ table_prefix + "preference_data WHERE "
								+ query);
				resultSetPd.next();
				int resultCounter = res.size() + resultSetPd.getInt("counter");
				resultSetPd.close();
				resultSetPd.close();

				if (resultCounter < max) {
					from = from + limit;
					if (limit == total)
						break;
					int newlimit = (int) Math.ceil(limit / 2D);
					if (newlimit == limit) {
						break;
					}
					limit = newlimit;
				} else if (resultCounter > max) {
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
								+ "preference_data WHERE " + query);
				while (resultSetPd.next())
					res.add(resultSetPd.getInt("attr_id"));
				resultSetPd.close();
				resultSetPd.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DoPeepsDrop(res, max, drop);
	}

	public boolean FetchPreferenceResults() {
		Statement statementPd;
		try {
			statementPd = connect.createStatement();
			ResultSet resultSetPd = statementPd
					.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'preference' AND table_name = '"
							+ table_prefix + "preference_rules+'");
			statementPd.close();
			resultSetPd.next();
			ruleCount = resultSetPd.getInt(0);
			statementPd = connect.createStatement();
			ruleSet = statementPd.executeQuery("SELECT * FROM " + table_prefix
					+ "preference_rules");
			statementPd.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void CreateSampleDB(int sample_data_size, int sample_data_size_step) {
		try {
			String sq = "";

			do {
				String where = "";
				boolean lastCheck = true;
				while (true) {
					statement = connect.createStatement();
					int ranAttId = UsefulFunctions.RandomInRange(1,App.number_of_attributes);
					int ranAttVl = UsefulFunctions.RandomInRange(App.min_value_on_attribute,App.max_value_on_attribute);
					if (where.equals(""))
						where += " attr_" + ranAttId + " = '" + ranAttVl
						+ "'";
					else
						where += " AND attr_" + ranAttId + " = '" + ranAttVl
								+ "'";
					ResultSet resultSet = statement
							.executeQuery("SELECT COUNT(*) AS counter FROM "
									+ table_prefix
									+ "preference_data WHERE " + where);
					resultSet.next();
					int counter = resultSet.getInt("counter");
					statement.close();
					resultSet.close();
					if (counter == 0) {
						break;
					} else if (counter < sample_data_size_step) {
						if (lastCheck == false) {
							if (sq.equals(""))
								sq += "("+where+")";
							else
								sq += " OR (" + where +")";
							break;
						}
						lastCheck = false;
					} else if (counter > sample_data_size_step) {
						lastCheck = false;
					}
				}

				statement = connect.createStatement();
				if (sq.equals("") == false)
				{
					ResultSet resultSet = statement
							.executeQuery("SELECT COUNT(*) AS counter FROM "
									+ table_prefix + "preference_data WHERE "
									+ sq);
					resultSet.next();
					int counter = resultSet.getInt("counter");
					statement.close();
					resultSet.close();
					if (counter > sample_data_size) {
						break;
					}
				}
			} while (true);

			statement = connect.createStatement();
			statement.executeUpdate("INSERT INTO " + table_prefix
					+ "preference_data_sample SELECT * FROM "
					+ table_prefix + "preference_data WHERE " + sq);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Vector<Integer> RunAlgorithmThree(int max, int drop) {
		Vector<Integer> res = new Vector<Integer>();
		boolean isFinished = false;
		String where = "";
		try {
			statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM "
					+ table_prefix + "preference_queries ORDER BY query_id");
			while (resultSet.next()) {
				if (where.equals(""))
					where = "(" + resultSet.getString("query") + ")";
				else
					where += " AND (" + resultSet.getString("query") + ")";
				String query = "SELECT attr_id FROM " + table_prefix
						+ "preference_data WHERE " + where;
				Statement st2 = connect.createStatement();
				ResultSet rs2 = st2.executeQuery(query);
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
		return DoPeepsDrop(res, max, drop);
	}

	public void GenerateSelectionQueries() {
		try {
			Statement st = connect.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM " + table_prefix
					+ "preference_rules");
			preferenceSet r = new preferenceSet();
			while (rs.next()) {
				Vector<Vector<String>> attrs = new Vector<Vector<String>>();
				Vector<Vector<Vector<Integer>>> attrvals = new Vector<Vector<Vector<Integer>>>();
				String t = rs.getString("rule_attributes");
				int Attrs = 0;
				for (String i : t.split(";")) {
					Vector<String> t2 = new Vector<String>();
					for (String j : i.split(",")) {
						t2.add(j);
						Attrs++;
					}
					attrs.add(t2);
				}

				for (int i = 1; i <= Attrs; i++) {
					Vector<Vector<Integer>> tt = new Vector<Vector<Integer>>();
					t = rs.getString("values_" + i);
					for (String j : t.split(";")) {
						Vector<Integer> t3 = new Vector<Integer>();
						for (String k : j.split(","))
							t3.add(Integer.parseInt(k));
						tt.add(t3);
					}
					attrvals.add(tt);
				}
				r.preferenceAttributeSets = attrs;
				r.preferenceValueSets = attrvals;
				GenerateSelectionQueries(r);
			}
			rs.close();
			st.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
