package chess_app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowTable {
	private static Connection conn;
	
	public ShowTable(Connection conn) {
		ShowTable.conn = conn;
	}

	public void Afficher(int niveau, int partie){
		try {
			
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM PARTIE WHERE NUMPARTIE = ? AND NUMNIVEAU = ?");
			pstmt.setInt(1, partie);
			pstmt.setInt(2, niveau);
			ResultSet rs = pstmt.executeQuery(); // On vérifie que la partie existe
			if (!rs.next()) {
				System.err.println("Partie inexistante !");
				return;
			}
			
			// Creation de la requete
			pstmt = conn.prepareStatement("SELECT * FROM Piece WHERE ((estSortie = 0) AND (numPartie = ?) And (numNiveau = ?)) ORDER BY estSortie");
        
            pstmt.setInt(1, partie);
            pstmt.setInt(2, niveau);

			ResultSet rset = pstmt.executeQuery();
			System.out.print("numPiece\t");
			System.out.print("estSortie\t");
			System.out.print("typePiece\t");
			System.out.print("numPartie\t");
			System.out.print("numNiveau\t");
			System.out.print("couleurJoueur\t\t");
			System.out.print("ligne\t\t");
			System.out.println("colonne ");
		
			while (rset.next()) {
	            System.out.print(rset.getInt(1)+"\t\t");
	            System.out.print(rset.getInt(2)+"\t\t");
	            System.out.print(rset.getString(3)+"\t");
	            System.out.print(rset.getInt(4)+"\t\t");
	            System.out.print(rset.getInt(5)+"\t\t");
	            System.out.print(rset.getString(6)+"\t\t");
	            System.out.print(rset.getInt(7)+"\t\t");
	            System.out.println(rset.getString(8));
			}
			rset.close();        
			pstmt.close();
	    } catch (SQLException e) {
	        System.err.println("failed to insert");
	        e.printStackTrace(System.err);
	    }
	}

	
}
