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
	ResultSet resultSet = null;
	String table_prefix = "1";
	Random ran = new Random(App.random_seed);
	static int query_level = 0;

	public boolean Connect() {
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
		int counter = 1;
		try {
			statement = connect.createStatement();
			while (true) {
				resultSet = statement
						.executeQuery("SELECT COUNT(*) AS counter FROM information_schema.tables WHERE table_schema = '"
								+ App.db_database
								+ "' AND table_name = 'ds"
								+ counter + "_reference_data'");
				resultSet.next();
				if (resultSet.getInt("counter") == 0)
					break;
				else
					counter++;
			}
			resultSet.close();
			statement.close();

			statement = connect.createStatement();
			table_prefix = "ds" + counter + "_";
			String query = "CREATE TABLE " + table_prefix + "reference_data (";
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
			//Vector<String> subqueryset = new Vector<String>();
			
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
						+ "', query_attr='"+r.referenceAttributeSets.toString()+"', query_level='" + query_level + "'";
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
				//System.out.println();

				// check if equal with previous query
				// if not equal
				if (!isEqual) {
					//queries.add(subqueryset);
					//subqueryset = new Vector<String>();
					//subqueryset.add(query);
					query_level++;
				} else {
					//subqueryset.add(query);
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
					//if (subqueryset.size() > 0)
						//queries.add(subqueryset);
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
}
