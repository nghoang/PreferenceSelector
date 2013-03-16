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

//this class contains all the methods to interact with database
public class DatabaseLayer {
	Connection connect = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	String table_prefix = "1";
	Random ran = new Random(App.random_seed);
	
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

	public void InsertReference(int numAttr, Vector<Vector<String>> referenceAttributeSets,
			Vector<Vector<Integer>> referenceValues) {
		try {
			String query = "CREATE TABLE IF NOT EXISTS " + table_prefix + "reference_rules (";
			query += " `rule_id` int(11) NOT NULL AUTO_INCREMENT,";
			query += " `rule_attributes` varchar(500) ";
			for (int i = 0; i < numAttr; i++) {
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
	
	public Vector<String> GenerateSelectionQueries(int numAttr, Vector<Vector<String>> referenceAttributeSets,
			Vector<Vector<Integer>> referenceValues)
	{
		Vector<String> queries = new Vector<String>();
		for (int i=0;i<numAttr;i++)
		{
			String query = "SELECT * FROM " + table_prefix + "reference_data WHERE ";
			for (Vector<String> attrs : referenceAttributeSets)
			{
				for (String attr : attrs)
				{
					query += " attr_" + attr + " = ''";
				}
			}
		}
		return queries;
	}
}
