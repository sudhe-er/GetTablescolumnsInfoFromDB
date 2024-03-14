package awt;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBConnect;

public class DataDictionary extends WindowAdapter {
	Frame frame;
	DBConnect dbconnection;
	Connection connection;
	Choice tablesDropDown;
	Choice columnsDropDown;
	DatabaseMetaData metaData;
	TextArea textarea;

	// Constructor to initialize awt components
	public DataDictionary() {
		frame = new Frame("Data Dictionary");
		frame.setBackground(Color.BLACK);
		tablesDropDown = new Choice();
		columnsDropDown = new Choice();
		textarea = new TextArea();
		Label label = new Label(" Data Dictionary");
		label.setBackground(Color.PINK);
		Font boldFont = new Font(Font.DIALOG, Font.BOLD, 14);
		label.setFont(boldFont);
		label.setBounds(350, 50, 120, 30);
		textarea.setBounds(200, 200, 500, 300);
		tablesDropDown.setBounds(100, 100, 250, 75);
		columnsDropDown.setBounds(500, 100, 250, 50);
		tablesDropDown.add("Tables...");
		columnsDropDown.add("Retrieving Columns from the Database...");
		frame.add(label);
		frame.add(tablesDropDown);
		frame.add(columnsDropDown);
		frame.add(textarea);
		frame.setSize(1100, 1000);
		frame.setLayout(null);

	}

	public void interfaceFrame() {

		// fetching tables from the database
		try {
			connection = DBConnect.connect();
			metaData = connection.getMetaData();
			ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" });
			while (tables.next()) {
				tablesDropDown.add(tables.getString("TABLE_NAME"));
			}

			// add action to a selected table in tablesDropDown menu
			tablesDropDown.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {

					String selectedTable = tablesDropDown.getSelectedItem();
					if (selectedTable != null) {
						columnsDropDown.removeAll();
						textarea.setText("");
						PreparedStatement ColStatement;
						PreparedStatement ConstraintStatement;
						try {
							// SQL Query to get Column names from a selected Table
							String query = "SELECT COLUMN_NAME FROM information_schema.columns WHERE TABLE_NAME=?";
							ColStatement = connection.prepareStatement(query);
							ColStatement.setString(1, selectedTable);
							ResultSet columnsData = ColStatement.executeQuery();

							// SQL Query to get table constraints
							String constraints = "SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE FROM information_schema.table_constraints WHERE TABLE_NAME = ?";
							ConstraintStatement = connection.prepareStatement(constraints);
							ConstraintStatement.setString(1, selectedTable);
							ResultSet columnsConstraints = ConstraintStatement.executeQuery();

							// to load table-columns into the dropdown
							while (columnsData.next()) {
								String column = columnsData.getString("COLUMN_NAME");
								columnsDropDown.add(column);
							}

							// displaying constraints on the text area
							String formatted_heading = String.format("%35s  %35s", "CONSTRAINT NAME",
									"CONSTRAINT TYPE\n\n");
							textarea.append(formatted_heading);
							while (columnsConstraints.next()) {
								String cons_name = columnsConstraints.getString("CONSTRAINT_NAME");
								String cons_type = columnsConstraints.getString("CONSTRAINT_TYPE");
								String formmatting = String.format("%35s  %35s", cons_name, cons_type);
								textarea.append(formmatting + "\n");
							}

						} catch (SQLException e1) {
							e1.printStackTrace();
						}

					}
				}
			});

		} catch (SQLException e) {
			e.printStackTrace();
		}
		frame.addWindowListener(this);
		frame.setVisible(true);

	}
	// end of the method

	//
	public void windowClosing(WindowEvent e) {
		// Call dispose() method
		System.exit(0);

	}

}
