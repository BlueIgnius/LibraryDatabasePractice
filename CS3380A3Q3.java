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

class MyDatabase
{
	private Connection connection;

	public MyDatabase()
	{
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
	public void nameSearch(String name)
	{
		final int TOKENNUM = 2;
		String tokens[] = name.split(" ", TOKENNUM);

		if(tokens.length == TOKENNUM)
		{
			tokens[0] = "%" + tokens[0].toLowerCase().trim() + "%";
			tokens[1] = "%" + tokens[1].toLowerCase().trim() + "%";

			try
			{
				String query = "SELECT DISTINCT first, last, id " + 
								"FROM people WHERE LOWER(first) " +
								"LIKE ? AND LOWER(last) LIKE ?";
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

				if (!hasResults)
				{
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
				String aid = id.trim();
				String query = "SELECT DISTINCT first, last, aid FROM people WHERE id = ?";
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, aid);
				
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

				if (!hasResults)
				{
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
				String aid = id.trim();

				String query = "SELECT DISTINCT store.name, COUNT(books.pid) AS numbooks " +
							   "FROM store " +
							   "JOIN sells ON store.id = sells.sid " +
							   "JOIN books ON sells.pid = books.pid " +
							   "WHERE books.aid = ? " +
							   "GROUP BY store.name";
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, aid);
				
				ResultSet resultSet = preparedStatement.executeQuery();
				System.out.println("-----------------------Results-----------------------");

				boolean hasResults = false;

				while(resultSet.next())
				{
					String storeName = resultSet.getString("name");
					String numBooks = resultSet.getString("numbooks");

					if(storeName != null)
					{
						hasResults = true;
						System.out.println(String.format("[Store Name]: %-20s [Books On Sale]: %s", storeName, numBooks));
					}
				}

				if (!hasResults)
				{
					System.out.println("No results found.");
				}

				resultSet.close();
				preparedStatement.close();
			}
			catch(SQLException e)
			{
				System.out.println("Oops! We were unable to find the name you are looking for");
				e.printStackTrace(System.out);
			}
		}
		else
		{
			System.out.println("Please enter a valid id number.");
		}
	}

	//4
	public void ownBooks()
	{
		try
		{
			String query = "SELECT DISTINCT people.first, people.last, books.title " +
							"FROM people NATURAL JOIN books WHERE people.aid = books.aid " +
							"EXCEPT SELECT people.first, people.last, books.title " +
							"FROM people NATURAL JOIN books NATURAL JOIN read " +
							"WHERE people.aid = books.aid";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			System.out.println("-----------------------Results-----------------------");

			boolean hasResults = false;
			
			while (resultSet.next()) 
			{
				String firstName = "[First Name]: " + resultSet.getString("first");
				String lastName = "\t[Last Name]: " + resultSet.getString("last");
				String bookTitle = "\t[Book Title]: " + resultSet.getString("title");

				hasResults = true;

				System.out.println(firstName + lastName + bookTitle);
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

	//5
	public void readAll()
	{
		try
		{
			String query = "SELECT DISTINCT people.first, people.last, books.title " +
							"FROM people NATURAL JOIN books NATURAL JOIN read " +
							"WHERE people.aid = books.aid ";

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			System.out.println("-----------------------Results-----------------------");

			boolean hasResults = false;
			
			while (resultSet.next()) 
			{
				String firstName = "[First Name]: " + resultSet.getString("first");
				String lastName = "\t[Last Name]: " + resultSet.getString("last");
				String bookTitle = "\t[Book Title]: " + resultSet.getString("title");

				hasResults = true;

				System.out.println(firstName + lastName + bookTitle);
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

	//6
	public void whoDoesNotSell(String id)
	{
		if(id.length() > 0)
		{
			try
			{
				String aid = id;

				String query = "SELECT DISTINCT store.name FROM store " + 
							   "EXCEPT SELECT store.name " +
							   "FROM store " +
							   "JOIN sells ON store.id = sells.sid " +
							   "JOIN books ON sells.pid = books.pid " +
							   "WHERE books.aid = ? " +
							   "GROUP BY store.name";
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, aid);
				
				ResultSet resultSet = preparedStatement.executeQuery();
				System.out.println("-----------------------Results-----------------------");

				boolean hasResults = false;

				while(resultSet.next())
				{
					String storeName = resultSet.getString("name");

					if(storeName != null)
					{
						hasResults = true;
						System.out.println("[Store Name]: " + storeName);
					}
				}

				if (!hasResults)
				{
					System.out.println("No results found.");
				}

				resultSet.close();
				preparedStatement.close();
			}
			catch(SQLException e)
			{
				System.out.println("Oops! We were unable to find the name you are looking for");
				e.printStackTrace(System.out);
			}
		}
		else
		{
			System.out.println("Please enter a valid id number.");
		}
	}

	//7
	public void getMostPublishers()
	{
		try
		{
			String query = "SELECT DISTINCT people.first, people.last, count(books.pid) AS pidNum " +
							"FROM people JOIN books ON people.aid = books.aid " +
							"GROUP BY people.first " +
							"ORDER BY count(books.pid) DESC " +
							"LIMIT 5";

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			System.out.println("-----------------------Results-----------------------");

			boolean hasResults = false;
			
			while (resultSet.next()) 
			{
				String firstName = "[First Name]: " + resultSet.getString("first");
				String lastName = "\t[Last Name]: " + resultSet.getString("last");
				String pidTotal = "\t[Number of Publishers]: " + resultSet.getString("pidNum");

				hasResults = true;

				System.out.println(firstName + lastName + pidTotal);
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

	//8
	public void mostCities()
	{
		try
		{
			String query = "SELECT DISTINCT people.first, people.last, count(DISTINCT store.cid) AS cidNum " +
							"FROM people JOIN books ON people.aid = books.aid " +
							"JOIN sells ON books.pid = sells.pid " +
							"JOIN store ON sells.sid = store.id " +
							"GROUP BY people.first " +
							"ORDER BY count(DISTINCT store.cid) DESC " +
							"LIMIT 5";

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			System.out.println("-----------------------Results-----------------------");

			boolean hasResults = false;
			
			while (resultSet.next()) 
			{
				String firstName = "[First Name]: " + resultSet.getString("first");
				String lastName = "\t[Last Name]: " + resultSet.getString("last");
				String pidTotal = "\t[Number of Cities]: " + resultSet.getString("cidNum");

				hasResults = true;

				System.out.println(firstName + lastName + pidTotal);
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

	//9
	public void mostReadPerCountry()
	{

	}

}
