package test.federicorifugiato.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Random;

import com.mysql.cj.jdbc.MysqlDataSource;

public class DB {
    private static Connection conn;
    
    private DB() {};
    
    public static Connection startConnection(String dbName)  {
        if (conn == null) {
            MysqlDataSource dataSource = new MysqlDataSource();
            try {
                dataSource.setServerName("localhost");
                dataSource.setPortNumber(3306);
                dataSource.setUser("root");
                dataSource.setPassword("admin");
                
                if (dbName == null || dbName == "") {
                	System.out.println("nessun db selezionato");
                    conn = dataSource.getConnection();
                } else {
                    conn = dataSource.getConnection();
    		        DB.createDatabase(conn, dbName);
    		        DB.useDatabase(conn, dbName);
    		        DB.createTables(conn);
    		        DB.insertData(conn);
    		        DB.insertPrestiti(conn);
                }
                
				if (conn.isValid(100)) {
				    System.out.println("Connessione valida!");
				}
			} catch (SQLException e) {
			    System.out.println("Connessione non valida! " + e.getMessage());
			}
        }
        return conn;
    }
    
    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connessione chiusa con successo");
            } catch (SQLException e) {
                System.out.println("Errore durante la chiusura della connessione: " + e.getMessage());
            }
        }
    }

    public static void createDatabase(Connection connection, String dbName) {
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + dbName;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createDatabaseSQL);
            System.out.println("Database creato con successo");
        }  catch (SQLException e) {
            System.out.println("Errore durante la creazione del db: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Errore durante la creazione del db: " + e.getMessage());
        }
    }

    public static void useDatabase(Connection connection, String dbName) {
        String useDatabaseSQL = "USE corso_java";
        try (Statement statement = connection.createStatement()) {
        	statement.executeUpdate(useDatabaseSQL);
            System.out.println("Database selezionato con successo");
        } catch (SQLException e) {
            System.out.println("Errore durante la selezione del db: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Errore durante la creazione del db: " + e.getMessage());
        }
    }
    
    public static void createTables(Connection connection) {

        String createTableUtenteSQL = "CREATE TABLE IF NOT EXISTS U (" +
                "id INT PRIMARY KEY," +
                "Cognome VARCHAR(255)," +
                "Nome VARCHAR(255)" +
                ")";

        String createTableLibroSQL = "CREATE TABLE IF NOT EXISTS L (" +
                "id INT PRIMARY KEY," +
                "Titolo VARCHAR(255)," +
                "Autore VARCHAR(255)" +
                ")";

        String createTablePrestitoSQL = "CREATE TABLE IF NOT EXISTS P (" +
                "id INT PRIMARY KEY," +
                "inizio DATE," +
                "fine DATE," + //SPECIFICO CHE LA DATA DI FINE PRESTITO è DA INTENDERSI COME SCADENZA E NON COME DATA DI RICONSEGNA
                "id_U INT," +
                "id_L INT," +
                "FOREIGN KEY (id_U) REFERENCES U(id)," +
                "FOREIGN KEY (id_L) REFERENCES L(id)" +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableUtenteSQL);
            statement.executeUpdate(createTableLibroSQL);
            statement.executeUpdate(createTablePrestitoSQL);
            System.out.println("Tabelle create con successo");
        } catch (SQLException e) {
            System.out.println("Errore durante la creazione delle tabelle: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Errore durante la creazione del db: " + e.getMessage());
        }
    }
    
    public static void insertData(Connection connection)  {

        String insertUtentiSQL = "INSERT INTO U (id, Cognome, Nome) VALUES (?, ?, ?)";
        String insertLibriSQL = "INSERT INTO L (id, Titolo, Autore) VALUES (?, ?, ?)";

        String[] nomi = {"Mario", "Andrea", "Massimo", "Sara", "Marco", "Marzia"};
        String[] cognomi = {"Rossi", "Verdi", "Bianchi", "Vallieri", "Graviglia", "Esposito"};

        try (PreparedStatement pstmtUtenti = connection.prepareStatement(insertUtentiSQL)) {
            for (int i = 0; i < nomi.length; i++) {
                pstmtUtenti.setInt(1, i + 1);
                pstmtUtenti.setString(2, cognomi[i]);
                pstmtUtenti.setString(3, nomi[i]);
                pstmtUtenti.executeUpdate();
            }
        }catch (SQLException e) {
            System.out.println("Errore durante l'inserimento degli utenti: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Errore durante la creazione del db: " + e.getMessage());
        }

        try (PreparedStatement pstmtLibri = connection.prepareStatement(insertLibriSQL)) {
            for (int i = 0; i < 10; i++) {
            	pstmtLibri.setInt(1, i+1);
            	pstmtLibri.setString(2, "Libro " + (i+1));
            	pstmtLibri.setString(3, "Autore " + (i+1));
            	pstmtLibri.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento dei libri: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Errore durante la creazione del db: " + e.getMessage());
        }
    }
    
    public static void insertPrestiti(Connection connection) {
        String insertPrestitiSQL = "INSERT INTO P (id, inizio, fine, id_U, id_L) VALUES (?, ?, ?, ?, ?)";
        
        Random rand = new Random();
        
        try (PreparedStatement pstmtPrestiti = connection.prepareStatement(insertPrestitiSQL)) {
            for (int i = 1; i <= 20; i++) {
                
                pstmtPrestiti.setInt(1, i);
                
                int randomMonth = rand.nextInt(12) + 1;
                int randomDay = rand.nextInt(28) + 1; 
                LocalDate randomDate = LocalDate.of(2024, randomMonth, randomDay);
                pstmtPrestiti.setDate(2, Date.valueOf(randomDate));

                randomMonth = rand.nextInt(12) + 1;
                randomDay = rand.nextInt(28) + 1; 
                randomDate = LocalDate.of(2024, randomMonth, randomDay);
                pstmtPrestiti.setDate(3, Date.valueOf(randomDate));
                
                pstmtPrestiti.setInt(4, rand.nextInt(1,6));
                pstmtPrestiti.setInt(5, rand.nextInt(1,10));
                pstmtPrestiti.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento dei prestiti: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Errore durante la creazione del db: " + e.getMessage());
        }
    }
    
    public static void querySelectSDD(Connection connection, String query, String nome) {
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("Titolo: " + rs.getString(1) +
                                   ", Inizio: " + rs.getDate(2) +
                                   ", Fine: " + rs.getDate(3));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query: " + e.getMessage());
        }
    }

    public static void querySelectSSI(Connection connection, String query) {

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Campo 1: " + rs.getString(1) +
                                   ", Campo 2: " + rs.getString(2) +
                                   ", Campo 3: " + rs.getInt(3));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query: " + e.getMessage());
        }
    }    
    
    public static void querySelectSSS(Connection connection, String query) {

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Campo 1: " + rs.getString(1) +
                                   ", Campo 2: " + rs.getString(2) +
                                   ", Campo 3: " + rs.getString(3));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query: " + e.getMessage());
        }
    }

    public static void querySelectSDD(Connection connection, String query, int userId, LocalDate data1, LocalDate data2) {

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(data1));
            pstmt.setDate(3, Date.valueOf(data2));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("Campo 1: " + rs.getString(1) +
                                   ", Campo 2: " + rs.getDate(2) +
                                   ", Campo 3: " + rs.getDate(3));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query: " + e.getMessage());
        }
    }

    public static void querySelectSI(Connection connection, String query) {

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Campo 1: " + rs.getString("Titolo") +
                                   ", Campo 2: " + rs.getInt("NumPrestiti"));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query: " + e.getMessage());
        }
    }

    public static void querySelectCustom(Connection connection, String query) {

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Cognome: " + rs.getString("Cognome") +
                                   ", Nome: " + rs.getString("Nome") +
                                   ", Titolo: " + rs.getString("Titolo") +
                                   ", Inizio: " + rs.getDate("inizio") +
                                   ", Fine: " + rs.getDate("fine") +
                                   ", Durata: " + rs.getInt("Durata") + " giorni");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query: " + e.getMessage());
        }
    }
}