package com.ngochoang.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Vector;

import com.ngochoang.referenceselector.App;
import com.ngochoang.referenceselector.ReferenceSet;

//this class contains all the methods to interact with database
public class DatabaseLayer {
	Connection connect = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet res = null;
	String table_prefix = "1";
	Random ran = new Random(App.random_seed);
	static int query_level = 0;

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
			query += " rule_attributes = '" + referenceAttributeSets.toString()
					+ "' ";
			for (int i = 0; i < numAttr; i++) {
				query += ", values_" + (i + 1) + " = '"
						+ referenceValues.get(i).toString() + "'";
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

			int numAttr = 0;
			for (Vector<String> t : r.referenceAttributeSets) {
				numAttr += t.size();
			}
			Vector<String> attrs = new Vector<String>();
			Vector<Vector<Integer>> values = new Vector<Vector<Integer>>();
			System.out.println("Attributes:");
			System.out.println(r.referenceAttributeSets.toString());
			for (Vector<String> ats : r.referenceAttributeSets) {
				attrs.addAll(ats);
			}
			System.out.println("Attribute Values:");
			for (Vector<Vector<Integer>> v1 : r.referenceValueSets) {
				Vector<Integer> v3 = new Vector<Integer>();
				System.out.println(v1.toString());
				for (Vector<Integer> v2 : v1) {
					v3.addAll(v2);
				}
				values.add(v3);
			}
			Vector<Integer> attrIndexes = new Vector<Integer>();
			for (int i = 0; i < numAttr; i++)
				attrIndexes.add(0);
			// Vector<String> subqueryset = new Vector<String>();

			while (true) {
				boolean isEqual = false;
				query = "";
				int curr_att = 0;
				for (String a : attrs) {
					if (query.equals(""))
						query += " attr_"
								+ a
								+ " = "
								+ values.get(curr_att).get(
										attrIndexes.get(curr_att)) + "";
					else
						query += " AND attr_"
								+ a
								+ " = "
								+ values.get(curr_att).get(
										attrIndexes.get(curr_att)) + "";
					curr_att++;
				}
				String qquery = "INSERT INTO " + table_prefix
						+ "reference_queries SET query='" + query
						+ "', query_attr='"
						+ r.referenceAttributeSets.toString()
						+ "', query_level='" + query_level + "'";
				statement = connect.createStatement();
				statement.executeUpdate(qquery);
				statement.close();

				for (int i = attrIndexes.size() - 1; i >= 0; i--) {
					if (attrIndexes.get(i) == values.get(i).size() - 1) {
						attrIndexes.set(i, 0);
					} else {
						int old = values.get(i).get(attrIndexes.get(i));
						int newV = values.get(i).get(attrIndexes.get(i) + 1);
						isEqual = false;
						for (Vector<Integer> v1 : r.referenceValueSets.get(i)) {
							if (v1.contains(old) && v1.contains(newV)) {
								isEqual = true;
								break;
							}
						}
						attrIndexes.set(i, attrIndexes.get(i) + 1);
						break;
					}
				}
				// System.out.println();

				// check if equal with previous query
				// if not equal
				if (!isEqual) {
					// queries.add(subqueryset);
					// subqueryset = new Vector<String>();
					// subqueryset.add(query);
					query_level++;
				} else {
					// subqueryset.add(query);
				}
				// check if finished
				boolean isFinished = true;
				for (int i = 0; i < numAttr; i++) {
					if (attrIndexes.get(i) < r.referenceValueSets.get(i).size() - 1) {
						isFinished = false;
						break;
					}
				}
				if (isFinished) {
					// if (subqueryset.size() > 0)
					// queries.add(subqueryset);
					break;
				}
			}

			/*
			 * for (int i = 0; i < numAttr; i++) { String query =
			 * "SELECT * FROM " + table_prefix + "reference_data WHERE 1=1"; int
			 * j = 0; for (Vector<String> attrs : r.referenceAttributeSets) {
			 * for (String attr : attrs) { Vector<Integer> values = new
			 * Vector<Integer>(); for (Vector<Integer> it :
			 * r.referenceValueSets.get(j)) { values.addAll(it); } if
			 * (values.size() <= i) query += " AND attr_" + attr + " = '" +
			 * values.get(values.size() - 1) + "'"; else query += " AND attr_" +
			 * attr + " = '" + values.get(i) + "'"; j++; } } queries.add(query);
			 * }
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
			int limit = total/2;
			String query = "";
			while (limit <= inittotal)
			{
				query = "";
				statement = connect.createStatement();
				resultSet = statement
						.executeQuery("SELECT * FROM "
								+ table_prefix
								+ "reference_queries ORDER BY query_level");
				int lastPri = -1;
				int counter = 1;
				while (resultSet.next())
				{
					int curPri = resultSet.getInt("query_level");
					if (counter > limit && lastPri != curPri)
							break;
					lastPri = curPri;
					if (query.equals(""))
						query += resultSet.getString("query");
					else
						query += " OR "+resultSet.getString("query");
					counter++;
				}
				resultSet.close();
				statement.close();

				
				//get products
				Statement statementPd = connect.createStatement();
				ResultSet resultSetPd = statementPd
						.executeQuery("SELECT COUNT(attr_id) AS counter FROM "
								+ table_prefix
								+ "reference_data WHERE " + query);
				resultSetPd.next();
				int resultCounter = resultSetPd.getInt("counter");
				resultSetPd.close();
				resultSetPd.close();
				
				if (resultCounter < App.number_of_returned_results)
				{
					if (limit == total)
						break;
					int newlimit = (int)Math.ceil((total + limit)/2D);
					if (newlimit == limit)
					{
						break;
					}
					limit = newlimit;
				}
				else if (resultCounter > App.number_of_returned_results)
				{
					if (limit <= 1)
						break;
					if (limit == total)
						break;
					int newlimit = limit/2;
					if (newlimit == limit)
					{
						break;
					}
					total = limit;
					limit = newlimit;
				}
			}
			if (!query.equals(""))
			{
				Statement statementPd = connect.createStatement();
				ResultSet resultSetPd = statementPd
						.executeQuery("SELECT attr_id FROM "
								+ table_prefix
								+ "reference_data WHERE " + query);
				while(resultSetPd.next())
					res.add(resultSetPd.getInt("attr_id"));
				resultSetPd.close();
				resultSetPd.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return res;
	}

}
