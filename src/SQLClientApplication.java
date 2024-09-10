
/**************************************************************************
	Name: Alyson Franco
	Course: CNT 4714 Summer 2024
	Assignment title: Project 2 – A Two-tier Client-Server Application
	Date: July 10, 2024
***************************************************************************/

import java.sql.*;
import java.util.Properties;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.FileInputStream;
import java.io.IOException;

public class SQLClientApplication extends JPanel {
	/*************
	 * Variables
	 *************/

	// define buttons
	private JButton ConnectButton;
	private JButton DisconnectButton;
	private JButton ClearCommand;
	private JButton ExecuteButton;
	private JButton ClearResultWindow;

	// define JLabel
	private JLabel ConnectionDetailsLabel;
	private JLabel DBPropertiesLabel;
	private JLabel UserPropertiesLabel;
	private JLabel UsernameLabel;
	private JLabel PasswordLabel;
	private JLabel SQLCommandLabel;
	private JLabel DBConnectionStatusLabel;
	private JLabel ExecutionResultLabel;

	// define JTextAreas
	private JTextArea textCommand;

	// define JComboBoxes
	private JComboBox<String> dbPropertiesBox;
	private JComboBox<String> userPropertiesBox;

	// define JTextField
	private JTextField UsernameField;

	// define JPasswordField
	private JPasswordField PasswordField;

	// define connection object and JTable
	private Connection connect;
	private JTable resultTable;
	private ResultSetTableModel tableModel;

	// constructor method
	public SQLClientApplication() {

		/***********************
		 * Construct GUI Instance
		 ***********************/

		// Initialize the drop down menus
		String[] dbPropertiesItems = { "bikedb.properties", "project2.properties", "operationslog.properties" };
		String[] userPropertiesItems = { "root.properties", "client1.properties", "client2.properties",
				"theaccountant.properties" };

		/*****************
		 * Define Buttons
		 ****************/

		// Connect button styling
		ConnectButton = new JButton("Connect to Database");
		styleButton(ConnectButton, Color.BLUE, Color.WHITE);

		// Disconnect button styling
		DisconnectButton = new JButton("Disconnect from Database");
		styleButton(DisconnectButton, Color.RED, Color.WHITE);

		// Clear command button styling
		ClearCommand = new JButton("Clear SQL Command");
		styleButton(ClearCommand, Color.YELLOW, Color.BLACK);

		// Execute button styling
		ExecuteButton = new JButton("Execute SQL Command");
		styleButton(ExecuteButton, Color.GREEN, Color.BLACK);

		// Clear result button styling
		ClearResultWindow = new JButton("Clear Result Window");
		styleButton(ClearResultWindow, Color.YELLOW, Color.BLACK);

		/*****************
		 * Define Labels
		 ****************/

		// Connections Details Label & styling
		ConnectionDetailsLabel = new JLabel();
		ConnectionDetailsLabel.setText("Connections Details");
		styleLabel(ConnectionDetailsLabel, Color.BLUE);

		// DB URL Properties Label & styling
		DBPropertiesLabel = new JLabel();
		DBPropertiesLabel.setText("DB URL Properties");
		styleLabel(DBPropertiesLabel, Color.BLACK);

		// User Properties Label & styling
		UserPropertiesLabel = new JLabel();
		UserPropertiesLabel.setText("User Properties");
		styleLabel(UserPropertiesLabel, Color.BLACK);

		// Username Label & styling
		UsernameLabel = new JLabel();
		UsernameLabel.setText("Username");
		styleLabel(UsernameLabel, Color.BLACK);

		// Password Label & styling
		PasswordLabel = new JLabel();
		PasswordLabel.setText("Password");
		styleLabel(PasswordLabel, Color.BLACK);

		// SQL Commands Label & styling
		SQLCommandLabel = new JLabel();
		SQLCommandLabel.setText("Enter an SQL Command");
		styleLabel(SQLCommandLabel, Color.BLUE);

		// DB Connection Label & styling
		DBConnectionStatusLabel = new JLabel();
		DBConnectionStatusLabel.setText(" NO CONNECTION ESTABLISHED");
		styeConnectionStatus(DBConnectionStatusLabel, Color.RED);

		// Execution Results Label & styling
		ExecutionResultLabel = new JLabel();
		ExecutionResultLabel.setText("SQL Execution Result Window");
		styleLabel(ExecutionResultLabel, Color.BLUE);

		/***************
		 * Entry Areas
		 ***************/

		// Connections Details area
		dbPropertiesBox = new JComboBox<>(dbPropertiesItems);
		dbPropertiesBox.setBackground(Color.WHITE);

		userPropertiesBox = new JComboBox<>(userPropertiesItems);
		userPropertiesBox.setBackground(Color.WHITE);

		UsernameField = new JTextField("", 10);
		UsernameField.setBackground(Color.WHITE);

		PasswordField = new JPasswordField("", 10);
		PasswordField.setBackground(Color.WHITE);

		// SQL Command Text Area
		textCommand = new JTextArea(5, 5);
		textCommand.setLineWrap(true);
		textCommand.setWrapStyleWord(true);
		JScrollPane scrollQuery = new JScrollPane(textCommand);

		// Execution Result table
		resultTable = new JTable();
		resultTable.setEnabled(false);
		resultTable.setGridColor(Color.BLACK);
		resultTable.setBackground(Color.WHITE);
		JScrollPane scrollResults = new JScrollPane(resultTable);
		scrollResults.getViewport().setBackground(Color.WHITE);

		/*****************
		 * Window Layout
		 *****************/

		setLayout(null);

		// Connection Details Section
		ConnectionDetailsLabel.setBounds(20, 20, 150, 25);

		DBPropertiesLabel.setBounds(20, 50, 200, 25);
		dbPropertiesBox.setBounds(220, 50, 200, 25);

		UserPropertiesLabel.setBounds(20, 90, 200, 25);
		userPropertiesBox.setBounds(220, 90, 200, 25);

		UsernameLabel.setBounds(20, 130, 200, 25);
		UsernameField.setBounds(220, 130, 200, 25);

		PasswordLabel.setBounds(20, 170, 200, 25);
		PasswordField.setBounds(220, 170, 200, 25);

		ConnectButton.setBounds(20, 210, 200, 25);
		DisconnectButton.setBounds(240, 210, 200, 25);

		// SQL Command Section
		SQLCommandLabel.setBounds(500, 20, 170, 25);
		scrollQuery.setBounds(500, 50, 400, 150);
		ClearCommand.setBounds(500, 210, 200, 25);
		ExecuteButton.setBounds(720, 210, 180, 25);

		// Disconnect Status
		DBConnectionStatusLabel.setBounds(20, 250, 880, 25);

		// Execution Result Window Section
		ExecutionResultLabel.setBounds(20, 280, 420, 25);
		scrollResults.setBounds(20, 320, 880, 300);
		ClearResultWindow.setBounds(20, 630, 200, 25);

		// Add components to the panel
		add(ConnectionDetailsLabel);

		add(DBPropertiesLabel);
		add(dbPropertiesBox);

		add(UserPropertiesLabel);
		add(userPropertiesBox);

		add(UsernameLabel);
		add(UsernameField);

		add(PasswordLabel);
		add(PasswordField);

		add(SQLCommandLabel);
		add(scrollQuery);

		add(ExecutionResultLabel);
		add(scrollResults);

		add(ConnectButton);

		add(DisconnectButton);

		add(DBConnectionStatusLabel);

		add(ClearResultWindow);

		add(ClearCommand);

		add(ExecuteButton);

		// Register listeners
		registerListeners();
	}

	private void styleButton(JButton button, Color bg, Color fg) {
		button.setFont(new Font("Arial", Font.BOLD, 12));
		button.setBackground(bg);
		button.setForeground(fg);
		button.setBorderPainted(false);
		button.setOpaque(true);
	}

	private void styleLabel(JLabel label, Color fg) {
		label.setFont(new Font("Arial", Font.BOLD, 14));
		label.setForeground(fg);
	}

	private void styeConnectionStatus(JLabel label, Color fg) {
		label.setFont(new Font("Arial", Font.PLAIN, 14));
		label.setBackground(Color.BLACK);
		label.setOpaque(true);
		label.setForeground(fg);

	}

	/***************************************************************
	 * Register Action Listeners and Event Handlers for Each Button
	 ***************************************************************/
	private void registerListeners() {

		/***************************
		 * Connect Button
		 ***************************/
		ConnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// debug code
				System.out.println("Connect button pressed.");
				boolean userCredentialsOK = false;
				Properties userProperties = new Properties();
				MysqlDataSource dataSource = null;

				// get user input from combo box
				String dbPropertiesFile = dbPropertiesBox.getSelectedItem().toString();
				String userPropertiesFile = userPropertiesBox.getSelectedItem().toString();
				String userInput = UsernameField.getText();
				String passwordInput = String.valueOf(PasswordField.getPassword());
				// debug code
				System.out.println("Loading properties files: " + dbPropertiesFile + " and " + userPropertiesFile);

				// read the properties files
				try (FileInputStream dbFileIn = new FileInputStream(dbPropertiesFile);
						FileInputStream userFileIn = new FileInputStream(userPropertiesFile)) {
					// debug code
					System.out.println("Properties files loaded successfully.");

					// load properties from file for db details
					userProperties.load(dbFileIn);
					// load MysqlDataSource object
					dataSource = new MysqlDataSource();
					dataSource.setURL(userProperties.getProperty("MYSQL_DB_URL"));
					// debug code
					System.out.println(
							"Database properties loaded and URL set: " + userProperties.getProperty("MYSQL_DB_URL"));

					// load properties from file for user details
					userProperties.load(userFileIn);
					// match username and password with properties file values
					String correctUsername = userProperties.getProperty("MYSQL_DB_USERNAME");
					String correctPassword = userProperties.getProperty("MYSQL_DB_PASSWORD");

					dataSource.setUser(correctUsername);
					dataSource.setPassword(correctPassword);
					System.out.println("User properties loaded and set in data source.");

					// check user credentials
					userCredentialsOK = correctUsername.equals(userInput) && correctPassword.equals(passwordInput);
					// debug code
					System.out.println("User credentials check: " + (userCredentialsOK ? "passed" : "failed"));

					if (userCredentialsOK) {
						// connect to database
						connect = dataSource.getConnection();
						// debug code
						System.out.println("Successfully connected to the database.");

						// return connection info
						DBConnectionStatusLabel.setText(" Connected to " + userProperties.getProperty("MYSQL_DB_URL"));
						styeConnectionStatus(DBConnectionStatusLabel, Color.YELLOW);
					} else {
						// indicate no connection
						DBConnectionStatusLabel.setText(
								" NO CONNECTION ESTABLISHED - User Credetials DO NOT Match Properties File!!!");
						styeConnectionStatus(DBConnectionStatusLabel, Color.RED);
						// debug code
						System.out.println("User credentials do not match.");
					}

				} catch (

				IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Properties File error",
							JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		/***************************
		 * Disconnect from BD Button
		 ****************************/
		DisconnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// debug code
				System.out.println("Disconnect button pressed.");
				// Clear the results displayed in the window
				textCommand.setText("");
				// Clear the input command area
				UsernameField.setText("");
				PasswordField.setText("");
				// clear results window
				resultTable.setModel(new DefaultTableModel());

				// check for db connnection
				try {
					if (connect == null || connect.isClosed()) {
						// don't do anything
						System.out.println("No connection to disconnect.");
						JOptionPane.showMessageDialog(null, "No Connection to disconnect.", "Connection error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				} catch (SQLException e) {
					// exit method is error occurs
					System.out.println("Error checking connection status.");
					e.printStackTrace();
					return;
				}

				// Indicate connection was terminated and no connection currently exists
				try {
					// close ResultSet and Statement objects
					if (tableModel != null) {
						tableModel.disconnectFromDatabase();
					}

					connect.close();
					// debug code
					System.out.println("Database connection closed.");
					DBConnectionStatusLabel.setText(" NO CONNECTION ESTABLISHED");
					styeConnectionStatus(DBConnectionStatusLabel, Color.RED);
				} catch (SQLException e) {
					// debug code
					System.out.println("Unable to disconnect from database");
					e.printStackTrace();
				}
				// statement.close();

			}
		});

		/*************************
		 * Clear Command Button
		 *************************/
		ClearCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// debug code
				System.out.println("Clear Command button pressed.");
				// Clears the text displayed in the query/command window
				textCommand.setText("");
				// debug code
				System.out.println("Command Text Area Cleared");
			}
		});

		/***************************
		 * Execute Button
		 ***************************/
		ExecuteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// debug code
				System.out.println("Execute Command button pressed.");

				try {
					// check for connection to database to handle user permissions
					if (connect == null || connect.isClosed()) {
						JOptionPane.showMessageDialog(null, "Please connect to the database.",
								"No Connection Established", JOptionPane.ERROR_MESSAGE);
						return;
					}
					// Get SQL command from textCommand JTextArea
					String sqlCommand = textCommand.getText().trim();

					// If select statement is used, use executeQuery()
					if (sqlCommand.toLowerCase().startsWith("select")) {
						// initialize tableModel
						tableModel = new ResultSetTableModel(connect);
						// call setQuery in ResultSetTableModel class
						tableModel.setQuery(sqlCommand);

						// create TableModel for results
						resultTable.setModel(tableModel);

					} else { // All other command types will use executeUpdate()

						// initialize tableModel
						tableModel = new ResultSetTableModel(connect);

						// call setUpdate() in ResultSetTableModel class
						tableModel.setUpdate(sqlCommand);

						// Clears the results displayed in the window
						resultTable.setModel(new DefaultTableModel());

						// display after successful update
						int rowsAffected = tableModel.getRowsAffected();
						JOptionPane.showMessageDialog(null, "Succesful Update..." + rowsAffected + " rows affected",
								"Succesful Update", JOptionPane.INFORMATION_MESSAGE);
					}
					// catch db error
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(null, "MySQL driver not found", "Driver not found",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		/******************************
		 * Clear Result Window Button
		 *******************************/
		ClearResultWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// debug code
				System.out.println("Clear Result Window button pressed.");
				// Clears the results displayed in the window
				resultTable.setModel(new DefaultTableModel());
				// debug code
				System.out.println("Result window cleared.");
			}
		});

	}

	/***********
	 * MAIN
	 ***********/
	public static void main(String[] args) {
		// debug code
		System.out.println("SQLClientApplication started.");
		// new JFrame
		JFrame frame = new JFrame("SQL Client Application (AF - CNT 4714 - Summer 2024 - Project 2)");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// create Prog3_GUI
		SQLClientApplication app = new SQLClientApplication();
		frame.getContentPane().add(new SQLClientApplication());
		frame.pack();
		frame.setSize(920, 700);
		frame.setLocationRelativeTo(null);
		// show GUI
		frame.setVisible(true);

		// disconnect from database and exit when window has closed
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent event) {
				// close ResultSetTableModel
				if (app.tableModel != null) {
					app.tableModel.disconnectFromDatabase();
				}
				// close connection
				if (app.connect != null) {
					try {
						app.connect.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(0);
				// debug code
				System.out.println("SQLClientApplication ended.");
			}
		}); // end of window action listener
	}
}