package test.federicorifugiato.main;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Random;

import test.federicorifugiato.database.DB;


public class Main {
    public static void main(String[] args) {

    	String dbName = "corso_java";
    	
    	// Operazioni preliminari atte a preparare connessione e db
    	Connection connection = DB.startConnection(dbName);

        String query1 = "SELECT L.Titolo, P.inizio, P.fine " +
                       "FROM P " +
                       "JOIN U ON P.id_U = U.id " +
                       "JOIN L ON P.id_L = L.id " +
                       "WHERE U.Cognome = ? " +
                       "ORDER BY P.inizio";
        String cognome = "Vallieri";
        
        System.out.println("Query 1");
        DB.querySelectSDD(connection, query1, cognome);
        
        String query2 = "SELECT U.Cognome, U.Nome, COUNT(P.id) AS NumLibri " +
                "FROM P " +
                "JOIN U ON P.id_U = U.id " +
                "GROUP BY U.id " +
                "ORDER BY NumLibri DESC " +
                "LIMIT 3";

        System.out.println("Query 2");
        DB.querySelectSSI(connection, query2);
        
        String query3 = "SELECT U.Cognome, U.Nome, L.Titolo " +
                "FROM P " +
                "JOIN U ON P.id_U = U.id " +
                "JOIN L ON P.id_L = L.id " +
                "WHERE P.fine > CURDATE()";

        System.out.println("Query 3");
        DB.querySelectSSS(connection, query3);

        String query4 = "SELECT L.Titolo, P.inizio, P.fine " +
                "FROM P " +
                "JOIN L ON P.id_L = L.id " +
                "WHERE P.id_U = ? AND P.inizio BETWEEN ? AND ? " +
                "ORDER BY P.inizio";
        
        int userId = 1;

        System.out.println("Query 4");
        
        Random rand = new Random();
        
        int randomMonth = rand.nextInt(12) + 1;
        int randomDay = rand.nextInt(28) + 1; 
        LocalDate data2 = LocalDate.of(2024, randomMonth, randomDay);
        
        randomMonth = rand.nextInt(randomMonth-1) + 1;
        randomDay = rand.nextInt(randomDay-1) + 1; 
        LocalDate data1 = LocalDate.of(2024, randomMonth, randomDay);

        System.out.println("data inizio ricerca: "+ data1);
        System.out.println("data fine ricerca: "+ data2);
        DB.querySelectSDD(connection, query4, userId, data1, data2);
        
        String query5 = "SELECT L.Titolo, COUNT(P.id) AS NumPrestiti " +
                "FROM P " +
                "JOIN L ON P.id_L = L.id " +
                "GROUP BY L.id " +
                "ORDER BY NumPrestiti DESC";
        
        System.out.println("Query 5");
        DB.querySelectSI(connection, query5);
        
        String query6 = "SELECT U.Cognome, U.Nome, L.Titolo, P.inizio, P.fine, DATEDIFF(P.fine, P.inizio) AS Durata " +
                "FROM P " +
                "JOIN U ON P.id_U = U.id " +
                "JOIN L ON P.id_L = L.id " +
                "WHERE DATEDIFF(P.fine, P.inizio) > 15";

        System.out.println("Query 6");
        DB.querySelectCustom(connection, query6);
        DB.closeConnection();
    }
}