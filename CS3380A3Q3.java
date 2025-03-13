import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Scanner;

public class CS3380A3Q3 {
	static Connection connection;

	public static void main(String[] args) throws Exception {

		// startup sequence
		MyDatabase db = new MyDatabase();
		runConsole(db);

		System.out.println("Exiting...");
	}

	public static void runConsole(MyDatabase db) {

		Scanner console = new Scanner(System.in);
		System.out.print("Welcome! Type h for help. ");
		System.out.print("db > ");
		String line = console.nextLine();
		String[] parts;
		String arg = "";

		while (line != null && !line.equals("q")) {
			parts = line.split("\\s+");
			if (line.indexOf(" ") > 0)
				arg = line.substring(line.indexOf(" ")).trim();

			if (parts[0].equals("h"))
				printHelp();
			else if (parts[0].equals("mp")) {
				db.getMostPublishers();
			}

			else if (parts[0].equals("s")) {
				if (parts.length >= 2)
					db.nameSearch(arg);
				else
					System.out.println("Require an argument for this command");
			}

			else if (parts[0].equals("l")) {
				try {
					if (parts.length >= 2)
						db.lookupByID(arg);
					else
						System.out.println("Require an argument for this command");
				} catch (Exception e) {
					System.out.println("id must be an integer");
				}
			}

			else if (parts[0].equals("sell")) {
				try {
					if (parts.length >= 2)
						db.lookupWhoSells(arg);
					else
						System.out.println("Require an argument for this command");
				} catch (Exception e) {
					System.out.println("id must be an integer");
				}
			}

			else if (parts[0].equals("notsell")) {
				try {
					if (parts.length >= 2)
						db.whoDoesNotSell(arg);
					else
						System.out.println("Require an argument for this command");
				} catch (Exception e) {
					System.out.println("id must be an integer");
				}
			}

			else if (parts[0].equals("mc")) {
				db.mostCities();
			}

			else if (parts[0].equals("notread")) {
				db.ownBooks();
			}

			else if (parts[0].equals("all")) {
				db.readAll();
			}

			else if (parts[0].equals("mr")) {
				db.mostReadPerCountry();
			}

			else
				System.out.println("Read the help with h, or find help somewhere else.");

			System.out.print("db > ");
			line = console.nextLine();
		}

		console.close();
	}

	private static void printHelp() {
		System.out.println("Library database");
		System.out.println("Commands:");
		System.out.println("h - Get help");
		System.out.println("s <name> - Search for a name");
		System.out.println("l <id> - Search for a user by id");
		System.out.println("sell <author id> - Search for a stores that sell books by this id");
		System.out.println("notread - Books not read by its own author");
		System.out.println("all - Authors that have read all their own books");
		System.out.println("notsell <author id>  - list of stores that do not sell this author");
		System.out.println("mp - Authors with the most publishers");
		System.out.println("mc - Authors with books in the most cities");
		System.out.println("mr - Most read book by country");
		System.out.println("");

		System.out.println("q - Exit the program");

		System.out.println("---- end help ----- ");
	}

}

class MyDatabase {
	private Connection connection;

	public MyDatabase() {
		try 
		{
			Class.forName("org.sqlite.JDBC");

			String url = "jdbc:sqlite:library.db";
			// create a connection to the database
			connection = DriverManager.getConnection(url);
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace(System.out);
		}
		catch (SQLException e) 
		{
			e.printStackTrace(System.out);
		}
	}

	//1
	public void nameSearch(String name) {
		final int TOKENNUM = 2;
		String tokens[] = name.split(" ", TOKENNUM);

		if(tokens.length == TOKENNUM)
		{
			tokens[0] = "%" + tokens[0].toLowerCase().trim() + "%";
			tokens[1] = "%" + tokens[1].toLowerCase().trim() + "%";

			try
			{
				String query = "SELECT DISTINCT first, last, id FROM people WHERE LOWER(first) LIKE ? AND LOWER(last) LIKE ?";
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, tokens[0]);
				preparedStatement.setString(2, tokens[1]);
				
				ResultSet resultSet = preparedStatement.executeQuery();
				System.out.println("-----------------------Results-----------------------");

				boolean hasResults = false;
				while (resultSet.next()) 
				{
					hasResults = true;
					System.out.println(
					"[First Name]: " + resultSet.getString("first") +
					"\t[Last Name]: " + resultSet.getString("last") +
					"\t[ID]: " + resultSet.getString("id")
					);
				}

				if (!hasResults) {
					System.out.println("No results found.");
				}

				resultSet.close();
				preparedStatement.close();
			}
			catch (SQLException e)
			{
				System.out.println("Oops! We were unable to find the name you are looking for!");
				e.printStackTrace(System.out);
			}
		}
		else
		{
			System.out.println("Please provide a last name for the person you are trying to find.");
		}
	}

	//2
	public void lookupByID(String id) 
	{
		if(id.length() > 0)
		{
			try
			{
				String query = "SELECT DISTINCT first, last, aid FROM people WHERE id LIKE ?";
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, id);
				
				ResultSet resultSet = preparedStatement.executeQuery();
				System.out.println("-----------------------Results-----------------------");

				boolean hasResults = false;
				while (resultSet.next()) 
				{
					String firstName = "[First Name]: " + resultSet.getString("first");
					String lastName = "\t[Last Name]: " + resultSet.getString("last");
					String aidPreped = "\t[AID]: ";
					String aidResult = resultSet.getString("aid");
					hasResults = true;

					if(aidResult != null)
					{
						System.out.println(firstName + lastName + aidPreped + aidResult);
					}
					else
					{
						System.out.println(firstName + lastName);
					}
				}

				if (!hasResults) {
					System.out.println("No results found.");
				}

				resultSet.close();
				preparedStatement.close();
			}
			catch (SQLException e)
			{
				System.out.println("Oops! We were unable to find the name you are looking for!");
				e.printStackTrace(System.out);
			}
		}
		else
		{
			System.out.println("Please enter a valid id number.");
		}
	}

	//3
	public void lookupWhoSells(String id)
	{
		if(id.length() > 0)
		{
			try
			{
				String query = "SELECT COUNT(*) FROM people NATURAL JOIN books NATURAL JOIN store NATURAL JOIN sells WHERE aid LIKE ?";
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, id);
				
				ResultSet resultSet = preparedStatement.executeQuery();
				System.out.println("-----------------------Results-----------------------");

				boolean hasResults = false;
				while (resultSet.next()) 
				{
					String booksOnSale = "[Books On Sale]: " + resultSet.getString("");
					hasResults = true;
					
					System.out.println(booksOnSale);
				}

				if (!hasResults) {
					System.out.println("No results found.");
				}

				resultSet.close();
				preparedStatement.close();
			}
			catch (SQLException e)
			{
				System.out.println("Oops! We were unable to find the name you are looking for!");
				e.printStackTrace(System.out);
			}
		}
		else
		{
			System.out.println("Please enter a valid id number.");
		}
	}

	//4
	public void ownBooks() {

	}

	//5
	public void readAll() {

	}

	//6
	public void whoDoesNotSell(String id) {

	}

	//7
	public void getMostPublishers() {

	}

	//8
	public void mostCities() {

	}

	//9
	public void mostReadPerCountry() {

	}

}
