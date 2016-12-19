package chess_app;

import java.sql.*;

import chess_app.MyConnection.Couleur;
import chess_app.MyConnection.Type;

public class InitMatch {

	private static Connection conn;

	public InitMatch(Connection conn){
		InitMatch.conn = conn;
	}
	
	public void InitialiserPartie(int idJoueurBlanc, int idJoueurNoir, int niveau, int partie) throws SQLException, PieceException {
		try {
			if (idJoueurBlanc == idJoueurNoir) {
				System.err.println("Les deux joueurs doivent être différents !");
				return;
			}
			
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM PARTICIPE WHERE numJoueur = ?");
			PreparedStatement pstmt2 = conn.prepareStatement("SELECT * FROM PARTIE WHERE NUMPARTIE = ? AND NUMNIVEAU = ?");
			int countBlanc = 0, countNoir = 0;
			
			pstmt.setInt(1, idJoueurBlanc); // On récupère toutes les parties précédentes jouées par idJoueurBlanc
			ResultSet res = pstmt.executeQuery(), res2;
		    while (res.next()) {
		    	pstmt2.setInt(1, res.getInt(3));
		    	pstmt2.setInt(2, res.getInt(4));
		    	res2 = pstmt2.executeQuery();
		    	res2.next(); // On récupère chaque partie en question
    			if (res2.getInt(3) == 0 || res2.getInt(3) != idJoueurBlanc) { // On vérifie qu'elle a bien été gagnée par idJoueurBlanc
    				System.err.println("Le joueur blanc n'est pas admis dans cette manche !");
    				return;
    			}
    			countBlanc++;
		    };
		    
		    pstmt.setInt(1, idJoueurNoir); // On fait la même chose pour idJoueurNoir
		    res = pstmt.executeQuery();
		    while (res.next()) {
		    	pstmt2.setInt(1, res.getInt(3));
		    	pstmt2.setInt(2, res.getInt(4));
		    	res2 = pstmt2.executeQuery();
		    	res2.next();
    			if (res2.getInt(3) == 0 || res2.getInt(3) != idJoueurBlanc) {
    				System.err.println("Le joueur noir n'est pas admis dans cette manche !");
    				return;
    			}
    			countBlanc++;
		    }
		    
		    if (countNoir != countBlanc) { // On vérifie que les deux joueurs ont gagné le même nombre de parties
		    	System.err.println("Les deux joueurs ne sont pas de même niveau");
				return;
		    }
		    if (countNoir + 1 != niveau) { // On vérifie que la partie qui va être créé est un niveau plus haut
		    	System.err.println("La partie n'est pas de niveau correct");
				return;
		    }
		    
		    int partiesNiveauCourant = 0;
		    pstmt = conn.prepareStatement("SELECT COUNT (*) FROM PARTIE WHERE NUMNIVEAU = ?"); // On récupère toutes les parties de niveau courant
		    pstmt.setInt(1, niveau);
		    ResultSet rs = pstmt.executeQuery();
		    if (rs.next()){
		    	partiesNiveauCourant = rs.getInt(1); // On les compte
		    }

		    if (partiesNiveauCourant + 1 != partie) { // On vérifie que la partie courante va être créé avec le numéro de partie suivant
		    	System.err.println("La partie n'est pas la dernière partie du niveau courant");
				return;
		    }
			
		    
		    /******************************* Fin des vérifications *******************************/
		    
            pstmt = conn.prepareStatement("INSERT INTO Partie VALUES(?,?,?)");
            pstmt.setInt(1, partie);
            pstmt.setInt(2, niveau);;
            pstmt.setNull(3, java.sql.Types.INTEGER);
            ResultSet rset = pstmt.executeQuery();    
            rset.close();
            pstmt.close();

            
		    // On va maintenant créer la partie
            pstmt = conn.prepareStatement("INSERT INTO participe VALUES(?,?,?,?)");
            
            pstmt.setInt(1, idJoueurBlanc);
            pstmt.setString(2, "BLANC");;
            pstmt.setInt(3, partie);
            pstmt.setInt(4, niveau);
	           
            rset = pstmt.executeQuery();    
            rset.close();
              
            pstmt.setInt(1, idJoueurNoir);
            pstmt.setString(2, "NOIR");;
            pstmt.setInt(3, partie);
            pstmt.setInt(4, niveau);
	    
            rset = pstmt.executeQuery();
            rset.close();
            pstmt.close();
            pstmt2.close();
	            
        } catch (SQLException e) {
            System.err.println("Failed to create game");
            e.printStackTrace(System.err);
        }
		
		PutPiece piece = new PutPiece(conn);
	
		piece.placerPiece(Type.TOUR    , Couleur.BLANC, 1, "a", niveau, partie);
		piece.placerPiece(Type.CAVALIER, Couleur.BLANC, 1, "b", niveau, partie);
		piece.placerPiece(Type.FOU     , Couleur.BLANC, 1, "c", niveau, partie);
		piece.placerPiece(Type.ROI     , Couleur.BLANC, 1, "d", niveau, partie);
		piece.placerPiece(Type.REINE   , Couleur.BLANC, 1, "e", niveau, partie);
		piece.placerPiece(Type.FOU     , Couleur.BLANC, 1, "f", niveau, partie);
		piece.placerPiece(Type.CAVALIER, Couleur.BLANC, 1, "g", niveau, partie);
		piece.placerPiece(Type.TOUR    , Couleur.BLANC, 1, "h", niveau, partie);

		
		piece.placerPiece(Type.PION, Couleur.BLANC, 2, "a",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.BLANC, 2, "b",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.BLANC, 2, "c",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.BLANC, 2, "d",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.BLANC, 2, "e",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.BLANC, 2, "f",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.BLANC, 2, "g",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.BLANC, 2, "h",  niveau, partie);
		
		
		piece.placerPiece(Type.TOUR    , Couleur.NOIR, 8, "a", niveau, partie);
		piece.placerPiece(Type.CAVALIER, Couleur.NOIR, 8, "b", niveau, partie);
		piece.placerPiece(Type.FOU     , Couleur.NOIR, 8, "c", niveau, partie);
		piece.placerPiece(Type.ROI     , Couleur.NOIR, 8, "d", niveau, partie);
		piece.placerPiece(Type.REINE   , Couleur.NOIR, 8, "e", niveau, partie);
		piece.placerPiece(Type.FOU     , Couleur.NOIR, 8, "f", niveau, partie);
		piece.placerPiece(Type.CAVALIER, Couleur.NOIR, 8, "g", niveau, partie);
		piece.placerPiece(Type.TOUR    , Couleur.NOIR, 8, "h", niveau, partie);

		
		piece.placerPiece(Type.PION, Couleur.NOIR, 7, "a",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.NOIR, 7, "b",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.NOIR, 7, "c",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.NOIR, 7, "d",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.NOIR, 7, "e",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.NOIR, 7, "f",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.NOIR, 7, "g",  niveau, partie);
		piece.placerPiece(Type.PION, Couleur.NOIR, 7, "h",  niveau, partie);
	}		
}
