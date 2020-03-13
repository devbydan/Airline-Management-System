/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	/*
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 * Author   -> Dr. Mariam Salloum
	 * Modifier -> Dan Murphy
	 * Method   -> DBProject (String dbname, String dbport, String user,
	 *                        String passwd) throws SQLException
	 * Purpose  -> Method which creates a new instance of the DB Project and
	 *             serves as an initializing intermediary between the
	 *             server/localhost and the database.
	 * -----------------------------------------------------------------------
	 * @param hostname PSQL server hostname
	 * @param database Name of the database
	 * @param username the user name used to login to the database
	 * @param password the user login password
	 * @throws java.sql.SQLException when failed to make a connection.
	 * -----------------------------------------------------------------------
	 * Receives -> dbname, dbport, user, passwd
	 * Returns  -> NONE
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 */
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");

			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy
   * Method   -> void executeUpdate (String sql) throws SQLException
   * Purpose  -> Method to execute an update SQL statement.
   *             Update SQL instruction includes the following:
   *             CREATE, INSERT, UPDATE, DELETE, DROP
   * -----------------------------------------------------------------------
   * @param sql the input SQL string
   * @throws java.sql.SQLException when update failed
   * -----------------------------------------------------------------------
   * Receives -> [String] sql
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
	public void executeUpdate (String sql) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy
   * Method   -> int executeQueryAndPrintResult (String query)
   *                                            throws SQLException
   * Purpose  -> Method to execute an input query SQL instruction (i.e. SELECT).
   *             This method issues the query to the DBMS and outputs the
   *             results to standard out.
   * -----------------------------------------------------------------------
   * @param query the input query string
   * @return the number of rows returned
   * @throws java.sql.SQLException when failed to execute the query
   * -----------------------------------------------------------------------
   * Receives -> [String] sql
   * Returns  -> [int] rowCount
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}

	/*
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 * Author   -> Dr. Mariam Salloum
	 * Modifier -> Dan Murphy
	 * Method   -> List<List<String>> executeQueryAndReturnResult(String query)
	 *                                                       throws SQLException
	 * Purpose  -> Method to execute an input query SQL instruction
	 *             (i.e. SELECT).
	 *             This method issues the query to the DBMS and returns the
	 *             results as a list of records.
	 *             Each record is a list of attribute values.
	 * -----------------------------------------------------------------------
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 * -----------------------------------------------------------------------
	 * Receives -> [String] query
	 * Returns  -> List<List<String>> result
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 * obtains the metadata object for the returned result set.  The metadata
		 * contains row and column info.
		*/
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		/* ------------------------------------------------- */

		/* Iterates through the result set and saves the data
		 * returned by the query.
		 */
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>();
		while (rs.next()){
			List<String> record = new ArrayList<String>();
			for (int i=1; i<=numCol; ++i)
				record.add(rs.getString (i));
			result.add(record);
		}//end while
		stmt.close ();
		return result;
	}//end executeQueryAndReturnResult

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy
   * Method   -> int executeQuery (String query) throws SQLException
   * Purpose  -> Method to execute an input query SQL instruction
   *             (i.e. SELECT).
   *             This method issues the query to the DBMS and returns the
   *             number of results.
   * -----------------------------------------------------------------------
   * @param query the input query string
   * @return the number of rows returned
   * @throws java.sql.SQLException when failed to execute the query
   * -----------------------------------------------------------------------
   * Receives -> [String] query
   * Returns  -> [int] rowCount
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}

	/*
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 * Author   -> Dr. Mariam Salloum
	 * Modifier -> Dan Murphy
	 * Method   -> int getCurrSeqVal(String sequence) throws SQLException
	 * Purpose  -> Method to fetch the last value from the sequence.
	 *             This method issues the query to the DBMS and returns the
	 *             current value of sequence used for the autogenerated keys.
	 * -----------------------------------------------------------------------
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 * -----------------------------------------------------------------------
	 * Receives -> [String] query
	 * Returns  -> [int] 1 || [int] -1
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 */
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();

		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy
   * Method   -> void cleanup()
   * Purpose  -> Method to close the physical connection if it is open.
   * -----------------------------------------------------------------------
   * Receives -> NONE
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 *
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if

		DBproject esql = null;

		try{
			System.out.println("(1)");

			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}

			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];

			esql = new DBproject (dbname, dbport, user, "");

			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Plane");
				System.out.println("2. Add Pilot");
				System.out.println("3. Add Flight");
				System.out.println("4. Add Technician");
				System.out.println("5. Book Flight");
				System.out.println("6. List number of available seats for a given flight.");
				System.out.println("7. List total number of repairs per plane in descending order");
				System.out.println("8. List total number of repairs per year in ascending order");
				System.out.println("9. Find total number of passengers with a given status");
				System.out.println("10. < EXIT");

				switch (readChoice()){
					case 1: AddPlane(esql); break;
					case 2: AddPilot(esql); break;
					case 3: AddFlight(esql); break;
					case 4: AddTechnician(esql); break;
					case 5: BookFlight(esql); break;
					case 6: ListNumberOfAvailableSeats(esql); break;
					case 7: ListsTotalNumberOfRepairsPerPlane(esql); break;
					case 8: ListTotalNumberOfRepairsPerYear(esql); break;
					case 9: FindPassengersCountWithStatus(esql); break;
					case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if
			}catch(Exception e){
				// ignored.
			}
		}
	}

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy
   * Method   -> int readChoice()
   * Purpose  -> Method to read the users choice from the terminal menu.
	 *             Returns only if a correct value is given.
   * -----------------------------------------------------------------------
   * Receives -> NONE
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
	public static int readChoice() {
		int input;
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy, Jose Estrada
   * Method   -> void AddPlane(DBproject esql)
   * Purpose  -> Method to add a plane to the DB by reading the users choice
	 *             from the terminal menu.
	 *             Returns only if a correct value is given.
   * -----------------------------------------------------------------------
   * Receives -> DBproject esql
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */

	 /* /// OPTION 1 /// OPTION 1 /// OPTION 1 /// OPTION 1 /// OPTION 1 /// */
	public static void AddPlane(DBproject esql) {

		/* Grab Plane ID from user --- */
		int plane_id;
		while(true) {
			try {
				System.out.println("\tEnter plane ID: ");
				plane_id = Integer.parseInt(in.readLine());
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}
		/* ---------------------------------------------------------- */

		/* Grab Plane make from user --- */
		String plane_make;
		while(true) {
			try {
				System.out.println("\tEnter plane make: ");
				plane_make = in.readLine();
				if(plane_make.length() <= 0 || plane_make.length() > 32) {
					throw new RuntimeException("Length cannot exceed 32 and cannot be empty");
				}
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}
		/* ---------------------------------------------------------- */

		/* Grab Plane model from user --- */
		String plane_model;
		while(true) {
			try {
				System.out.println("\tEnter plane model: ");
				plane_model = in.readLine();
				if(plane_model.length() <= 0 || plane_model.length() > 64) {
					throw new RuntimeException("cannot exceed 64 and cannot be empty");
				}
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}
		/* ---------------------------------------------------------- */

		/* Grab Plane age from user --- */
		int plane_age;
		while(true) {
			try {
				System.out.println("\tEnter plane age: ");
				plane_age = Integer.parseInt(in.readLine());
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}
		/* ---------------------------------------------------------- */

		/* Grab Plane seat number from user --- */
		int plane_seats;
		while(true) {
			try {
				System.out.println("\tEnter plane seat number: ");
				plane_seats = Integer.parseInt(in.readLine());
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}

		 /* Try the following query
		  * If valid query, call the method to execute and print the query results
		  * Else, exception handle is caught
		  */
			try {
				String query = "INSERT INTO Plane (id, make, model, age, seats) VALUES (" + plane_id + ", \'" + plane_make + "\', \'" + plane_model + "\', " + plane_age + ", " + plane_seats + ");";
				esql.executeUpdate(query);
			}catch(Exception e) {
				System.err.println(e.getMessage());
			}
	}/* End of AddPlane method ----------------------------------------------- */

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy, Jose Estrada
   * Method   -> void AddPilot(DBproject esql)
   * Purpose  -> Method to add a pilot to the DB by reading the users choice
	 *             from the terminal menu.
	 *             Returns only if a correct value is given.
   * -----------------------------------------------------------------------
   * Receives -> DBproject esql
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */

	 /* /// OPTION 2 /// OPTION 2 /// OPTION 2 /// OPTION 2 /// OPTION 2 /// */
	public static void AddPilot(DBproject esql) {

		/* Grab Pilot ID from user --- */
		int pilot_id;
		while(true) {
			try {
				System.out.println("\tEnter pilot id: ");
				 pilot_id = Integer.parseInt(in.readLine());
				 break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab Pilot full name from user --- */
		String pilot_full_name;
		while (true) {
			try {
				System.out.println("\tEnter pilot's full name: ");
				pilot_full_name = in.readLine();
				if(pilot_full_name.length() > 128) {
					throw new RuntimeException("cannot exceed 128");
				}
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab Pilot nationality from user --- */
		String pilot_nationality;
		while (true) {
			try {
				System.out.println("\tEnter pilot nationality: ");
				pilot_nationality = in.readLine();
				if(pilot_nationality.length() > 24) {
					throw new RuntimeException("cannot exceed 24");
				}
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Try the following query
		 * If valid query, call the method to execute and print the query results
		 * Else, exception handle is caught
		 */
		try {
			String query = "INSERT INTO Pilot (id, fullname, nationality) VALUES (" + pilot_id + ", \'" + pilot_full_name + "\', \'" + pilot_nationality + "\');";

			esql.executeUpdate(query);
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	} /* End of AddPilot method ---------------------------------------------- */

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy, Jose Estrada
   * Method   -> void AddFlight(DBproject esql)
   * Purpose  -> Method to add a flight to the DB by reading the users
	 *             choice from the terminal menu.
   * -----------------------------------------------------------------------
   * Receives -> DBproject esql
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */

	 /* /// OPTION 3 /// OPTION 3 /// OPTION 3 /// OPTION 3 /// OPTION 3 /// */
	public static void AddFlight(DBproject esql) {

		/* Grab Flight number from user --- */
		int flight_number;
		while (true) {
			try {
				System.out.println("\tEnter flight number: ");
				flight_number = Integer.parseInt(in.readLine());
				if (flight_number <= 0){
					throw new RuntimeException("Flight number cannot be 0 or negative");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab Flight cost from user --- */
		int flight_cost;
		while (true) {
			try {
				System.out.println("\tEnter flight cost: ");
				flight_cost = Integer.parseInt(in.readLine());
				if (flight_number <= 0){
					throw new RuntimeException("Flight cost cannot be 0 or negative");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab Flight number of seats sold from user --- */
		int flight_num_sold;
		while (true) {
			try {
				System.out.println("\tEnter number of seats sold: ");
				flight_num_sold = Integer.parseInt(in.readLine());
				if (flight_number <= 0){
					throw new RuntimeException("Flight number sold cannot be 0 or negative");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab Flight number of stops from user --- */
		int flight_num_stops;
		while (true) {
			try {
				System.out.println("\tEnter number of stops: ");
				flight_num_stops = Integer.parseInt(in.readLine());
				if (flight_number <= 0){
					throw new RuntimeException("Flight number cannot be 0 or negative");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab Flight departure date from user --- */
		LocalDate departureDate;
		String flight_depart_date;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		while (true) {
			System.out.print("Input the flight departure time as (YYYY-MM-DD hh:mm): ");
			try {
				flight_depart_date = in.readLine();
				departureDate = LocalDate.parse(flight_depart_date, formatter);
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab Flight arrival date from user --- */
		String flight_arrival_date;
		while (true) {
			System.out.print("Input the flight arrival time as (YYYY-MM-DD hh:mm): ");
			try {
				flight_arrival_date = in.readLine();
				LocalDate arrivalDate = LocalDate.parse(flight_arrival_date, formatter);
				if(!arrivalDate.isAfter(departureDate)) {
					throw new RuntimeException("Arrival date cannot be before departure date.");
				}
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab Flight arrival airport from user --- */
		String flight_arrival_airport;
		while (true) {
			try {
				System.out.println("\tEnter arrival airport: ");
				flight_arrival_airport = in.readLine();
				if(flight_arrival_airport.length() <= 0 || flight_arrival_airport.length() > 5) {
					throw new RuntimeException("cannot be greater than 5 and cannot be null");
				}
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

			/* Flight departure airport from user --- */
			String flight_departure_airport;
			while (true) {
				try {
					System.out.println("\tEnter departure airport: ");
					flight_departure_airport = in.readLine();
					if(flight_departure_airport.length() <= 0 || flight_departure_airport.length() > 5) {
						throw new RuntimeException("cannot be greater than 5 and cannot be null");
					}
					break;
				} catch (Exception e) {
					System.out.println(e);
					continue;
				}
			} /* ------------------------------------------------------------------- */

			/* Try the following query
			 * If valid query, call the method to execute and print the query results
			 * Else, exception handle is caught
			 */
				try {
					String query = "INSERT INTO Flight (fnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_airport, departure_airport) VALUES (" + flight_number + ", " + flight_cost + ", " + flight_num_sold + ", " + flight_num_stops + ", \'" + flight_depart_date + "\', \'" + flight_arrival_date + "\', \'" + flight_arrival_airport + "\', \'" + flight_departure_airport + "\');";

					esql.executeUpdate(query);
				}catch (Exception e) {
					System.err.println (e.getMessage());
				}
	}/* End of AddFlight method ---------------------------------------------- */

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy, Jose Estrada
   * Method   -> void AddTechnician(DBproject esql)
   * Purpose  -> Method to add a technician to the DB by reading the users
	 *             choice from the terminal menu.
   * -----------------------------------------------------------------------
   * Receives -> DBproject esql
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */

	 /* /// OPTION 4 /// OPTION 4 /// OPTION 4 /// OPTION 4 /// OPTION 4 /// */
	public static void AddTechnician(DBproject esql) {

		/* Grab Technician ID from user --- */
		int technician_id;
		while (true) {
			try {
				System.out.println("\tEnter technician ID: ");
				technician_id = Integer.parseInt(in.readLine());
				if(technician_id < 0) {
					throw new RuntimeException("ERROR -> Cannot be NULL");
				}
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab Technician full name from user --- */
		String technician_full_name;
		while (true) {
			try {
				System.out.println("\tEnter Technician's full name: ");
				technician_full_name = in.readLine();
				if(technician_full_name.length() > 128) {
					throw new RuntimeException("ERROR -> Cannot exceed 128");
				}
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Try the following query
		 * If valid query, call the method to execute and print the query results
		 * Else, exception handle is caught
		 */
		try {
			String query = "INSERT INTO Technician (id, full_name) VALUES(" + technician_id + ", \'" + technician_full_name + "\');";

			esql.executeUpdate(query);
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}/* End of AddTechnician method ------------------------------------------ */

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy, Jose Estrada
   * Method   -> void BookFlight(DBproject esql)
   * Purpose  -> Method to book a flight in the DB given a customer and a
	 *             flight.
   * -----------------------------------------------------------------------
   * Receives -> DBproject esql
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */

	 /* /// OPTION 5 /// OPTION 5 /// OPTION 5 /// OPTION 5 /// OPTION 5 /// */
	public static void BookFlight(DBproject esql) {

		/* Grab Customer ID from user --- */
		int customer_id;
		while (true) {
			System.out.println("\tEnter Customer ID: ");
			try {
				customer_id = Integer.parseInt(in.readLine());
				if(customer_id <= 0){
					throw new RuntimeException("ERROR -> Customer ID cannot be NULL.");
				}
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab Customer ID from user --- */
		int flight_number;
		while (true) {
			System.out.println("\tEnter Flight number: ");
			try {
				flight_number = Integer.parseInt(in.readLine());
				if(flight_number <= 0){
					throw new RuntimeException("ERROR -> Flight number cannot be NULL.");
				}
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Try the following query
		 * If valid query, call the method to execute and print the query results
		 * Else, exception handle is caught
		 */
		try {
			String query = "SELECT R.status " +
										 "FROM Reservation R " +
										 "WHERE R.cid = " + customer_id + " AND R.fid = " + flight_number;

			if(esql.executeQueryAndPrintResult(query) == 0) {
				while(true){
					System.out.println("No reservation found. Would you like to book one? (Y/N)\n");

					String input_reservation;
					String reservation_status;
					try{
						input_reservation = in.readLine();
						if( input_reservation.equals("y") || input_reservation.equals("Y") ){
							int new_reservation_num;
							while(true){
								System.out.println("Enter your new reservation number: ");
								try{
									new_reservation_num = Integer.parseInt(in.readLine());
									if(new_reservation_num <= 0){
										throw new RuntimeException("Reservation number cannot be 0 or negative!\n\n");
									}
									break;
							}catch (Exception e) {
								System.out.println("Your input is invalid!"); continue;
							}/* End of catch --- */
						}/* End of while(true) --- */
						//String reservation_status;
						try{
							query = "SELECT Total_Seats - Seats_Sold as \"Seats Available\"\nFROM(\nSELECT P.seats as Total_Seats\nFROM Plane P, FlightInfo FI\nWHERE FI.flight_id = " + flight_number + " AND FI.plane_id = P.id\n)total,\n(\nSELECT F.num_sold as Seats_Sold\nFROM Flight F\nWHERE F.fnum = " + flight_number + "\n)sold;";
							if(esql.executeQueryAndPrintResult(query) != 0){
								reservation_status = "R"; /* RESERVED --- */
							}else{
							reservation_status = "W"; /* ELSE WAITLISTED IF NOT RESERVED --- */
						}
							//break;
						}catch(Exception e) {
							System.err.println(e.getMessage()); continue;
						} /* End of catch --- */
						try{
							query = "INSERT INTO Reservation (rnum, cid, fid, status) VALUES (" + new_reservation_num + ", " + customer_id + ", " + flight_number + ", \'" + reservation_status + "\');";
							esql.executeUpdate(query);
						}catch(Exception e) {
							System.err.println(e.getMessage());
						}/* End of catch --- */
					}/* End of if "y" || "Y" ------------ */
					else if(!input_reservation.equals("n") || !input_reservation.equals("N")){
						throw new RuntimeException("ERROR -> Invalid Input. (Y/N) only! *** \n\n");
					}
					break;
				}catch(Exception e) {
						System.err.println(e.getMessage()); continue;
					}/* End of catch --- */
			}/* End of while(true) ------------------------------------- */
		} else {
			String update_reservation;
			while(true){
				try{
					System.out.println("Would you like to update your current reservation? (Y/N)");
					update_reservation = in.readLine();
					String reservation_status;
					if( update_reservation.equals("y") || update_reservation.equals("Y") ){
						reservation_status = "C";

						try{
							query = "UPDATE Reservation SET status = \'" + reservation_status + "\' WHERE cid = " + customer_id + " AND fid = " + flight_number + ";";
							esql.executeUpdate(query);
						}catch (Exception e) {
							System.out.println(e); continue;
						}/* End of catch --- */
					}/* End of if "Y" or "y" statement --- */
					else if( !update_reservation.equals("n") || !update_reservation.equals("N") ){
						throw new RuntimeException("ERROR -> Invalid Input! (Y/N) only!\n\n");
					}/* End of else if --- */
					break;
				} catch (Exception e) {
					System.out.println(e); continue;
				}/* End of catch statement --- */
			}/* End of while(true) --- */
		}/* End of parent else statement -------- */
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}/* End of catch statement --- */

	}/* End of BookFlight method --- */

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy, Jose Estrada
   * Method   -> void ListNumberOfAvailableSeats(DBproject esql)
   * Purpose  -> Method to find the number of available seats, given a
   *             flight number and a date.
   * -----------------------------------------------------------------------
   * Receives -> DBproject esql
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */

	 /* /// OPTION 6 /// OPTION 6 /// OPTION 6 /// OPTION 6 /// OPTION 6 /// */
	public static void ListNumberOfAvailableSeats(DBproject esql) {

		/* Grab Flight number from user --- */
		int flight_number;
		while(true) {
			try {
				System.out.println("\tEnter flight number: ");
				flight_number = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Grab date from user --- */
		String date;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		while(true) {
			System.out.println("Enter departure date as (yyyy-mm-dd hh:mm)");
			try {
				date = in.readLine();
				LocalDate departure_date = LocalDate.parse(date, formatter);
				break;
			}catch (Exception e) {
				System.out.println("Invalid input. Please enter again.");
				continue;
			}
		} /* ------------------------------------------------------------------- */

		/* Try the following query
		 * If valid query, call the method to execute and print the query results
		 * Else, exception handle is caught
		 */
		try {
			String query = "SELECT Total_Seats - Seats_Sold as \"Seats Available\"\nFROM(\nSELECT P.seats as Total_Seats\nFROM Plane P, FlightInfo FI\nWHERE FI.flight_id = " + flight_number + " AND FI.plane_id = P.id\n)total,\n(\nSELECT F.num_sold as Seats_Sold\nFROM Flight F\nWHERE F.fnum = " + flight_number + " AND F.actual_departure_date = \'" + date + "\'\n)sold;";

			if(esql.executeQueryAndPrintResult(query) == 0) {
				System.out.println("Flight or Departure Time does not exist");
			}
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}/* End of AddTechnician method ------------------------------------------ */

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy, Jose Estrada
   * Method   -> void ListsTotalNumberOfRepairsPerPlane(DBproject esql)
   * Purpose  -> Method to list the total number of repairs per plane in
   *             descending order.
   * -----------------------------------------------------------------------
   * Receives -> DBproject esql
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */

	 /* /// OPTION 7 /// OPTION 7 /// OPTION 7 /// OPTION 7 /// OPTION 7 /// */
	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {

		/* Try the following query
		 * If valid query, call the method to execute and print the query results
		 * Else, exception handle is caught
		 */
		try {
			String query = "SELECT R.plane_id, COUNT(*) AS total_num_repairs " +
							 "FROM Repairs R " +
							 "GROUP BY R.plane_id " +
							 "ORDER BY total_num_repairs DESC";

		  System.out.println("\n\n --- EXECUTING QUERY --- \n\n");
			esql.executeQueryAndPrintResult(query);
			System.out.println("\n\n --- END OF QUERY RESULTS --- \n\n");
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}/* End of ListsTotalNumberOfRepairsPerPlane method ---------------------- */

	/*
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 * Author   -> Dr. Mariam Salloum
	 * Modifier -> Dan Murphy, Jose Estrada
	 * Method   -> void ListsTotalNumberOfRepairsPerYear(DBproject esql)
	 * Purpose  -> Method to count repairs per year and list them in
	 *             ascending order.
	 * -----------------------------------------------------------------------
	 * Receives -> DBproject esql
	 * Returns  -> NONE
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 */

	 /* /// OPTION 8 /// OPTION 8 /// OPTION 8 /// OPTION 8 /// OPTION 8 /// */
	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {

		/* Try the following query
		 * If valid query, call the method to execute and print the query results
		 * Else, exception handle is caught
		 */
		try {
			String query = "SELECT EXTRACT(year FROM R.repair_date) AS yyyy, COUNT(*) AS total_num_repairs " +
							 "FROM Repairs R " +
							 "GROUP BY yyyy " +
							 "ORDER BY total_num_repairs ASC";

		  System.out.println("\n\n --- EXECUTING QUERY --- \n\n");
			esql.executeQueryAndPrintResult(query);
			System.out.println("\n\n --- END OF QUERY RESULTS --- \n\n");
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}/* End of ListTotalNumberOfRepairsPerYear method ------------------------ */

	/*
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 * Author   -> Dr. Mariam Salloum
	 * Modifier -> Dan Murphy, Jose Estrada
	 * Method   -> void ListsTotalNumberOfRepairsPerPlane(DBproject esql)
	 * Purpose  -> Method to count repairs per year and list them in
	 *             ascending order.
	 * -----------------------------------------------------------------------
	 * Receives -> DBproject esql
	 * Returns  -> NONE
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 */

	 /* /// OPTION 9 /// OPTION 9 /// OPTION 9 /// OPTION 9 /// OPTION 9 /// */
	public static void FindPassengersCountWithStatus(DBproject esql) {
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.

		/* Grab Passenger status from user --- */
		String passenger_status;
		//catch user input to determine if status is valid
		while(true) {
			try {
				System.out.println("\tEnter passenger status: ");
				passenger_status = in.readLine();
				/* IF status is valid (W, C, R), proceed to query --- */
				if(!passenger_status.equals("W") && !passenger_status.equals("C")
																				 && !passenger_status.equals("R")) {
				throw new RuntimeException("Input must either be W, C, or R");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}

		/* Try the following query
		 * If valid query, call the method to execute and print the query results
		 * Else, exception handle is caught
		 */
		try {
			String query = "SELECT COUNT(*) AS total_num_passengers " +
							 "FROM Reservation R " +
							 "WHERE R.status =  \'" + passenger_status + "\';";

			esql.executeQueryAndPrintResult(query);
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}/* End of FindPassengersCountWithStatus method -------------------------- */

}/* End of DBproject !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
