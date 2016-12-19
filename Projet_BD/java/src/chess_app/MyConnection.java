package chess_app;

import java.sql.*;

public class MyConnection {

	public enum Type {
		ROI, REINE, FOU, CAVALIER, TOUR, PION;
	}

	public enum Couleur {
		NOIR, BLANC;
	}

	private static Connection conn;

	public MyConnection() {
		super();
	}

	public void openConnection(String url, String user, String password) {
		try {
			// Enregistrement du driver Oracle
			System.out.print("Loading Oracle driver... ");
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			System.out.println("loaded");

			// Etablissement de la connection
			System.out.print("Connecting to the database... ");
			conn = (Connection) DriverManager.getConnection(url, user, password);
			System.out.println("connected");

			// Desactivation de l'autocommit
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

		} catch (SQLException e) {
			System.err.println("failed while trying to open connection");
			e.printStackTrace(System.err);
		}
	}

	public void closeConnection() {
		try {
			// Fermeture de la connection
			System.out.print("Closing database connection... ");
			conn.close();
			System.out.println("closed");
		} catch (SQLException e) {
			System.err.println("failed while trying to close connection");
			e.printStackTrace(System.err);
		}
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		MyConnection.conn = conn;
	}

}

/*
 * -- Initialisation de toutes les parties du tournoi INSERT INTO Partie
 * values(1,5,NULL); INSERT INTO Partie values(2,5,NULL); INSERT INTO Partie
 * values(3,5,NULL); INSERT INTO Partie values(4,5,NULL); INSERT INTO Partie
 * values(5,5,NULL); INSERT INTO Partie values(6,5,NULL); INSERT INTO Partie
 * values(7,5,NULL); INSERT INTO Partie values(8,5,NULL); INSERT INTO Partie
 * values(9,5,NULL); INSERT INTO Partie values(10,5,NULL); INSERT INTO Partie
 * values(11,5,NULL); INSERT INTO Partie values(12,5,NULL); INSERT INTO Partie
 * values(13,5,NULL); INSERT INTO Partie values(14,5,NULL); INSERT INTO Partie
 * values(15,5,NULL); INSERT INTO Partie values(16,5,NULL);
 * 
 * INSERT INTO Partie values(1,4,NULL); INSERT INTO Partie values(2,4,NULL);
 * INSERT INTO Partie values(3,4,NULL); INSERT INTO Partie values(4,4,NULL);
 * INSERT INTO Partie values(5,4,NULL); INSERT INTO Partie values(6,4,NULL);
 * INSERT INTO Partie values(7,4,NULL); INSERT INTO Partie values(8,4,NULL);
 * 
 * INSERT INTO Partie values(1,3,NULL); INSERT INTO Partie values(2,3,NULL);
 * INSERT INTO Partie values(3,3,NULL); INSERT INTO Partie values(4,3,NULL);
 * 
 * INSERT INTO Partie values(3,2,NULL); INSERT INTO Partie values(4,2,NULL);
 * 
 * INSERT INTO Partie values(3,1,NULL);
 * 
 * 
 * 
 * 
 * -- Initialisation des pieces -- joueur NOIR INSERT INTO Piece
 * values(1,0,'Roi',1,5,'NOIR',8,'e'); INSERT INTO Piece
 * values(2,0,'Reine',NULL,NULL,'NOIR',8,'d'); INSERT INTO Piece
 * values(3,0,'Fou',NULL,NULL,'NOIR',8,'c'); INSERT INTO Piece
 * values(4,0,'Fou',NULL,NULL,'NOIR',8,'f'); INSERT INTO Piece
 * values(5,0,'Cavalier',NULL,NULL,'NOIR',8,'b'); INSERT INTO Piece
 * values(6,0,'Cavalier',NULL,NULL,'NOIR',8,'g'); INSERT INTO Piece
 * values(7,0,'Tour',NULL,NULL,'NOIR',8,'a'); INSERT INTO Piece
 * values(8,0,'Tour',NULL,NULL,'NOIR',8,'h'); INSERT INTO Piece
 * values(9,0,'Pion',NULL,NULL,'NOIR',7,'a'); INSERT INTO Piece
 * values(10,0,'Pion',NULL,NULL,'NOIR',7,'b'); INSERT INTO Piece
 * values(11,0,'Pion',NULL,NULL,'NOIR',7,'c'); INSERT INTO Piece
 * values(12,0,'Pion',NULL,NULL,'NOIR',7,'d'); INSERT INTO Piece
 * values(13,0,'Pion',NULL,NULL,'NOIR',7,'e'); INSERT INTO Piece
 * values(14,0,'Pion',NULL,NULL,'NOIR',7,'f'); INSERT INTO Piece
 * values(15,0,'Pion',NULL,NULL,'NOIR',7,'g'); INSERT INTO Piece
 * values(16,0,'Pion',NULL,NULL,'NOIR',7,'h');
 * 
 * -- joueur BLANC
 * 
 * INSERT INTO Piece values(17,0,'Roi',NULL,NULL,'BLANC',1,'e'); INSERT INTO
 * Piece values(18,0,'Reine',NULL,NULL,'BLANC',1,'d'); INSERT INTO Piece
 * values(19,0,'Fou',NULL,NULL,'BLANC',1,'c'); INSERT INTO Piece
 * values(20,0,'Fou',NULL,NULL,'BLANC',1,'f'); INSERT INTO Piece
 * values(21,0,'Cavalier',NULL,NULL,'BLANC',1,'b'); INSERT INTO Piece
 * values(22,0,'Cavalier',NULL,NULL,'BLANC',1,'g'); INSERT INTO Piece
 * values(23,0,'Tour',NULL,NULL,'BLANC',1,'a'); INSERT INTO Piece
 * values(24,0,'Tour',NULL,NULL,'BLANC',1,'h'); INSERT INTO Piece
 * values(25,0,'Pion',NULL,NULL,'BLANC',2,'a'); INSERT INTO Piece
 * values(26,0,'Pion',NULL,NULL,'BLANC',2,'b'); INSERT INTO Piece
 * values(27,0,'Pion',NULL,NULL,'BLANC',2,'c'); INSERT INTO Piece
 * values(28,0,'Pion',NULL,NULL,'BLANC',2,'d'); INSERT INTO Piece
 * values(29,0,'Pion',NULL,NULL,'BLANC',2,'e'); INSERT INTO Piece
 * values(30,0,'Pion',NULL,NULL,'BLANC',2,'f'); INSERT INTO Piece
 * values(31,0,'Pion',NULL,NULL,'BLANC',2,'g'); INSERT INTO Piece
 * values(32,0,'Pion',NULL,NULL,'BLANC',2,'h');
 */