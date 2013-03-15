package com.ngochoang.referenceselector;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class App {

	public static String db_host;
	public static String db_username;
	public static String db_password;
	public static String db_database;
	
	public static int number_of_attributes;
	public static int number_of_generated_records;
	public static int min_value_on_attribute;
	public static int max_value_on_attribute;
	public static int min_price;
	public static int max_price;
	public static int drop_rate;
	public static int number_of_returned_results;
	
	public static long random_seed;
	
	public static void Init()
	{
		Properties prop = new Properties();
    	try {
    		prop.load(new FileInputStream("config.properties"));
    		db_host = prop.getProperty("db_host");
    		db_username = prop.getProperty("db_username");
    		db_password = prop.getProperty("db_password");
    		db_database = prop.getProperty("db_database");

    		number_of_attributes = Integer.parseInt(prop.getProperty("number_of_attributes"));
    		number_of_generated_records = Integer.parseInt(prop.getProperty("number_of_generated_records"));
    		min_value_on_attribute = Integer.parseInt(prop.getProperty("min_value_on_attribute"));
    		max_value_on_attribute = Integer.parseInt(prop.getProperty("max_value_on_attribute"));
    		min_price = Integer.parseInt(prop.getProperty("min_price"));
    		max_price = Integer.parseInt(prop.getProperty("max_price"));
    		drop_rate = Integer.parseInt(prop.getProperty("drop_rate"));
    		number_of_returned_results = Integer.parseInt(prop.getProperty("number_of_returned_results"));

    		random_seed = Long.parseLong(prop.getProperty("random_seed"));
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
}
