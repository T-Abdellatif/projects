package chess_app;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class NewPlayer {

	private static Connection conn;

	public NewPlayer(Connection conn){
		NewPlayer.conn = conn;
	}
	
	public void NouveauJoueur(String nom, String prenom, String addresse, String birthDate) throws ParseException {
		try {
			int idJoueur = getPlayerCount() + 1;
			java.sql.Date sqlDate;
			if (birthDate == "") {
				sqlDate = new java.sql.Date(0);
			} else {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		        java.util.Date parsed = format.parse(birthDate);
		        sqlDate = new java.sql.Date(parsed.getTime());
			}
			
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM JOUEUR WHERE NUMJOUEUR = ?");
			pstmt.setInt(1, idJoueur);
		    if (pstmt.executeQuery().next()) {
		    	System.err.println("Erreur: Cet identifiant de joueur existe déjà (base de données mal formée).");
		    	return;
		    }
		    
            pstmt = conn.prepareStatement("INSERT INTO JOUEUR VALUES(?,?,?,?,?)");
            
            pstmt.setInt(1, idJoueur);
            pstmt.setString(2, truncate(nom, 10));
            pstmt.setString(3, truncate(prenom, 10));
            pstmt.setString(4, truncate(addresse, 20));
            pstmt.setDate(5, sqlDate);
            
            ResultSet rset = pstmt.executeQuery();   
            rset.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("Failed to create player");
            e.printStackTrace(System.err);
        }
	}	
	
	public Boolean isFull() throws SQLException {
		return getPlayerCount() == 32;
	}
	
	private String truncate(String str, int len) {
		return str.length() > len ? str.substring(0, len) : str;
	}
	
	public int getPlayerCount() throws SQLException {
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    ResultSet rs = stmt.executeQuery("SELECT * FROM JOUEUR");
	    if (rs.next()) {
	        rs.last();
	        return rs.getRow();
	    } else {
	        return 0;
	    }
	}
}
