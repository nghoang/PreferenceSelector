package com.victor.preferenceselector;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class App {

	public static String db_host;
	public static String db_username;
	public static String db_password;
	public static String db_database;
	public static String db_prefix;
	public static String step;
	public static String algorithm;
	public static int sample_data_size_step;
	public static int sample_data_size;
	
	public static int number_of_attributes;
	public static int number_of_generated_records;
	public static int min_value_on_attribute;
	public static int max_value_on_attribute;
	public static int min_price;
	public static int max_price;
	public static int drop_rate;
	public static int number_of_returned_results;
	
	public static long random_seed;

	public static int number_of_rules;
	public static int min_attr_in_rule;
	public static int max_attr_in_rule;
	public static int min_customer_value_on_attribute;
	public static int max_customer_value_on_attribute;
	public static int min_values_in_rule;
	public static int max_values_in_rule;
	public static int attribute_grouping_rate;
		
	public static void Init()
	{
		Properties prop = new Properties();
    	try {
    		prop.load(new FileInputStream("config.properties"));
    		db_host = prop.getProperty("db_host");
    		db_username = prop.getProperty("db_username");
    		db_password = prop.getProperty("db_password");
    		db_database = prop.getProperty("db_database");
    		db_prefix = prop.getProperty("db_prefix");
    		step = prop.getProperty("step");
    		algorithm = prop.getProperty("algorithm");

    		sample_data_size_step = Integer.parseInt(prop.getProperty("sample_data_size_step"));
    		sample_data_size = Integer.parseInt(prop.getProperty("sample_data_size"));

    		number_of_attributes = Integer.parseInt(prop.getProperty("number_of_attributes"));
    		number_of_generated_records = Integer.parseInt(prop.getProperty("number_of_generated_records"));
    		min_value_on_attribute = Integer.parseInt(prop.getProperty("min_value_on_attribute"));
    		max_value_on_attribute = Integer.parseInt(prop.getProperty("max_value_on_attribute"));
    		min_price = Integer.parseInt(prop.getProperty("min_price"));
    		max_price = Integer.parseInt(prop.getProperty("max_price"));
    		drop_rate = Integer.parseInt(prop.getProperty("drop_rate"));
    		number_of_returned_results = Integer.parseInt(prop.getProperty("number_of_returned_results"));

    		random_seed = Long.parseLong(prop.getProperty("random_seed"));

    		number_of_rules = Integer.parseInt(prop.getProperty("number_of_rules"));
    		min_attr_in_rule = Integer.parseInt(prop.getProperty("min_attr_in_rule"));
    		max_attr_in_rule = Integer.parseInt(prop.getProperty("max_attr_in_rule"));
    		min_customer_value_on_attribute = Integer.parseInt(prop.getProperty("min_customer_value_on_attribute"));
    		max_customer_value_on_attribute = Integer.parseInt(prop.getProperty("max_customer_value_on_attribute"));
    		attribute_grouping_rate = Integer.parseInt(prop.getProperty("attribute_grouping_rate"));
    		min_values_in_rule = Integer.parseInt(prop.getProperty("min_values_in_rule"));
    		max_values_in_rule = Integer.parseInt(prop.getProperty("max_values_in_rule"));
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
}
