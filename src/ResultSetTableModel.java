
/**************************************************************************
	Name: Alyson Franco
	Course: CNT 4714 Summer 2024
	Assignment title: Project 2 – Helper Class
	Date: July 10, 2024
***************************************************************************/

// A TableModel that supplies ResultSet data to a JTable.
import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import com.mysql.cj.jdbc.MysqlDataSource;

public class ResultSetTableModel extends AbstractTableModel {
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	private int numberOfRows;
	private int rowsAffected;

	// keep track of database connection status
	private boolean connectedToDatabase = false;

	// constructor initializes resultSet and obtains its meta data object determines
	// number of rows
	public ResultSetTableModel(Connection connection/* , String query */) throws SQLException, ClassNotFoundException {
		// debug code
		System.out.println("Initializing ResultSetTableModel...");

		// establish connection to database
		this.connection = connection;

		// create Statement to query database
		this.statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		// update database connection status
		connectedToDatabase = true;

		// check db connection
		if (!connectedToDatabase) {
			throw new IllegalStateException("Not Connected to Database");
		}

	}

	// get class that represents column type
	@Override
	public Class getColumnClass(int column) throws IllegalStateException {
		// debug code
		System.out.println("Getting column class for column: " + column);
		// ensure database connection is available
		if (!connectedToDatabase) {
			throw new IllegalStateException("Not Connected to Database");
		}

		// determine Java class of column
		try {
			String className = metaData.getColumnClassName(column + 1);
			// return Class object that represents className
			return Class.forName(className);
		} catch (Exception exception) {
			System.err.println("Error getting column class: " + exception.getMessage());
			exception.printStackTrace();
		}

		// if problems occur above, assume type Object
		return Object.class;
	}

	// get number of columns in ResultSet
	@Override
	public int getColumnCount() throws IllegalStateException {
		// debug code
		System.out.println("Getting column count...");
		// ensure database connection is available
		if (!connectedToDatabase) {
			throw new IllegalStateException("Not Connected to Database");
		}

		// determine number of columns
		try {
			return metaData.getColumnCount();
		} catch (SQLException sqlException) {
			System.err.println("Error getting column count: " + sqlException.getMessage());
			sqlException.printStackTrace();
		}

		// if problems occur above, return 0 for number of columns
		return 0;
	}

	// get name of a particular column in ResultSet
	@Override
	public String getColumnName(int column) throws IllegalStateException {
		// debug code
		System.out.println("Getting column name for column: " + column);
		// ensure database connection is available
		if (!connectedToDatabase) {
			throw new IllegalStateException("Not Connected to Database");
		}

		// determine column name
		try {
			return metaData.getColumnName(column + 1);
		} catch (SQLException sqlException) {
			System.err.println("Error getting column name: " + sqlException.getMessage());
			sqlException.printStackTrace();
		}

		// if problems, return empty string for column name
		return "";
	}

	// return number of rows in ResultSet
	@Override
	public int getRowCount() throws IllegalStateException {
		System.out.println("Getting row count...");
		// ensure database connection is available
		if (!connectedToDatabase) {
			throw new IllegalStateException("Not Connected to Database");
		}

		return numberOfRows;
	}

	// obtain value in particular row and column
	@Override
	public Object getValueAt(int row, int column) throws IllegalStateException {
		// debug code
		System.out.println("Getting value at row: " + row + ", column: " + column);
		// ensure database connection is available
		if (!connectedToDatabase) {
			throw new IllegalStateException("Not Connected to Database");
		}

		// obtain a value at specified ResultSet row and column
		try {
			resultSet.next(); /* fixes a bug in MySQL/Java with date format */
			resultSet.absolute(row + 1);
			return resultSet.getObject(column + 1);
		} catch (SQLException sqlException) {
			System.err.println("Error getting value at [" + row + ", " + column + "]: " + sqlException.getMessage());
			sqlException.printStackTrace();
		}

		// if problems, return empty string object
		return "";
	}

	public static Connection connectToOperationsLog() throws SQLException {
		Properties properties = new Properties();
		FileInputStream filein = null;
		MysqlDataSource dataSource = null;
		try {
			// debug code
			System.out.println("Connecting to operations log...");
			// load properties file
			filein = new FileInputStream("project2app.properties");
			properties.load(filein);

			dataSource = new MysqlDataSource();
			dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
			dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
			dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));

			// establishes the connection to the database
			return dataSource.getConnection();
		} catch (SQLException sqlException) {
			System.err.println("Error connecting to operations log: " + sqlException.getMessage());
			sqlException.printStackTrace();
			System.exit(1);
			return null;
		} catch (IOException e) {
			System.err.println("Error loading properties file: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	// set new database query string
	public void setQuery(String query) throws SQLException, IllegalStateException, ClassNotFoundException {
		// debug code
		System.out.println("Setting query: " + query);

		// ensure database connection is available
		if (!connectedToDatabase) {
			throw new IllegalStateException("Not Connected to Database");
		}

		// Close the previous ResultSet if it exists
		if (resultSet != null) {
			resultSet.close();
		}

		// specify query and execute it
		resultSet = statement.executeQuery(query);
		// obtain meta data for ResultSet
		metaData = resultSet.getMetaData();

		// determine number of rows in ResultSet
		resultSet.last(); // move to last row
		numberOfRows = resultSet.getRow(); // get row number

		// get a connection as a project2app user to the operationslog db
		Connection operationslogDBconnection = connectToOperationsLog();

		if (operationslogDBconnection != null) {
			//
			try (PreparedStatement selectStatement = operationslogDBconnection
					.prepareStatement("select * from operationscount where login_username = ?;");
					PreparedStatement insertStatement = operationslogDBconnection.prepareStatement(
							"insert into operationscount (login_username, num_queries, num_updates) values (?, ?, ?);");
					PreparedStatement updateStatement = operationslogDBconnection.prepareStatement(
							"update operationscount set num_queries = num_queries + 1 where login_username = ?;")) {

				// debug code
				System.out.println("Logging query execution...");

				// Get the metaData for the connection object
				DatabaseMetaData operationsLogMetaData = connection.getMetaData();
				// Extract the username from the connection object
				String userName = operationsLogMetaData.getUserName();

				// find row that belong to user
				selectStatement.setString(1, userName);
				ResultSet resSet = selectStatement.executeQuery();

				if (!resSet.next()) {
					// setting num_queries to 1 and updates to 0 if there is no row for the user
					insertStatement.setString(1, userName);
					insertStatement.setInt(2, 1);
					insertStatement.setInt(3, 0);
					// debug code
					System.out.println("Inserting new user: " + userName + " with num_queries=1 and num_updates=0");
					// execute prepared statement
					insertStatement.executeUpdate();
				} else {
					// increment num_queries by 1 if there is a row for the user
					updateStatement.setString(1, userName);
					// debug code
					System.out.println("Updating query count for user: " + userName);
					// execute prepared statement
					updateStatement.executeUpdate();
				}

			} catch (SQLException sqlException) {
				System.err.println("Error logging query execution: " + sqlException.getMessage());
				sqlException.printStackTrace();
				System.exit(1);
			} finally {
				// Close connection to operationslog DB
				try {
					operationslogDBconnection.close();
				} catch (SQLException sqlException) {
					System.err.println("Error closing operations log connection: " + sqlException.getMessage());
					sqlException.printStackTrace();
				}
			}
		}

		// notify JTable that model has changed
		fireTableStructureChanged();
	}

	// set new database update-query string
	public int setUpdate(String query) throws SQLException, IllegalStateException {

		// ensure database connection is available
		if (!connectedToDatabase) {
			throw new IllegalStateException("Not Connected to Database");
		}

		// Close the previous ResultSet if it exists
		if (resultSet != null) {
			resultSet.close();
		}

		// specify query and execute it
		rowsAffected = statement.executeUpdate(query);
		// debug code
		System.out.println("Setting update query: " + query);

		// get a connection as a project2app user to the operationslog db
		Connection operationslogDBconnection = connectToOperationsLog();

		if (operationslogDBconnection != null) {
			try (PreparedStatement selectStatement = operationslogDBconnection
					.prepareStatement("select * from operationscount where login_username = ?;");
					PreparedStatement insertStatement = operationslogDBconnection.prepareStatement(
							"insert into operationscount (login_username, num_queries, num_updates) values (?, ?, ?);");
					PreparedStatement updateStatement = operationslogDBconnection.prepareStatement(
							"update operationscount set num_updates = num_updates + 1 where login_username = ?;")) {

				// debug code
				System.out.println("Logging update execution...");

				// Get the metaData for the connection object
				DatabaseMetaData operationsLogMetaData = connection.getMetaData();
				// Extract the username from the connection object
				String userName = operationsLogMetaData.getUserName();

				// find row that belong to user
				selectStatement.setString(1, userName);
				ResultSet resSet = selectStatement.executeQuery();

				if (!resSet.next()) {
					// setting num_updates to 1 and queries to 0 if there is no row for the user
					insertStatement.setString(1, userName);
					insertStatement.setInt(2, 0);
					insertStatement.setInt(3, 1);
					// debug code
					System.out.println("Inserting new user: " + userName + " with num_queries=0 and num_updates=1");
					// execute prepared statement
					insertStatement.executeUpdate();
				} else {
					// increment num_updates by 1 if there is a row for the user
					updateStatement.setString(1, userName);
					// debug code
					System.out.println("Updating update count for user: " + userName);
					// execute prepared statement
					updateStatement.executeUpdate();
				}

			} catch (SQLException sqlException) {
				System.err.println("Error logging update execution: " + sqlException.getMessage());
				sqlException.printStackTrace();
				System.exit(1);
			} finally {
				// Close connection to operationslog DB
				try {
					operationslogDBconnection.close();
				} catch (SQLException sqlException) {
					System.err.println("Error closing operations log connection: " + sqlException.getMessage());
					sqlException.printStackTrace();
				}
			}
		}

		return rowsAffected;
	}

	// method to store rows affected during updates
	public int getRowsAffected() {
		return rowsAffected;
	}

	// close Statement and Connection
	public void disconnectFromDatabase() {
		// debug code
		System.out.println("Disconnecting from database...");
		if (!connectedToDatabase) {
			return;
			// close Statement and Connection
		} else {
			// attempt to close connection
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
				// debug code
				System.out.println("Database connection closed.");
				System.out.println("ResultSetTableModelClosed connection closed.");
			} catch (SQLException sqlException) {
				System.err.println("Error disconnecting from database: " + sqlException.getMessage());
				sqlException.printStackTrace();
			} finally {
				// update database connection status
				connectedToDatabase = false;
			}
		}
	}

} // end class ResultSetTableModel
