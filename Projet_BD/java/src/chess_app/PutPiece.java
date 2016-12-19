package chess_app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import chess_app.MyConnection.Couleur;
import chess_app.MyConnection.Type;

public class PutPiece {

	private static Connection conn;
	private int numeroPiece = 0;

	public PutPiece(Connection conn) {
		PutPiece.conn = conn;
	}
	
	public void placerPiece(Type type, Couleur couleur, int ligne,
				  String colonne, int niveau, int partie) throws SQLException, PieceException {
		// Insertion d'une pièce
		try {
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Piece values(?,?,?,?,?,?,?,?)");
			if (numeroPiece > 32) {
				throw new PieceException("Vous avez dépassé le nombre de pièces possible !");
			}
			numeroPiece++;
			pstmt.setInt(1, numeroPiece);
			pstmt.setInt(2, 0);
			pstmt.setString(3, type.toString());;
			pstmt.setInt(4, partie);
			pstmt.setInt(5, niveau);
			pstmt.setString(6, couleur.toString());
			pstmt.setInt(7, ligne);
			pstmt.setString(8, colonne);

			ResultSet rset = pstmt.executeQuery();
			rset.close();
			pstmt.close();
	        
	        // Verifier si la piece peut etre ajoutée
	        pstmt = conn.prepareStatement("SELECT COUNT(*) FROM Piece " + "where ((typePiece = ?) AND (couleurJoueur = ?) AND (numPartie = ?) AND (numNiveau = ?))");
	        pstmt.setString(1, type.toString());
	        pstmt.setString(2, couleur.toString());
	        pstmt.setInt(3, partie);
	        pstmt.setInt(4,	niveau);
	        rset = pstmt.executeQuery();

			int nbPieceExistantes = 0;
			System.out.println(nbPieceExistantes);

			if (rset.next()) {
				nbPieceExistantes = rset.getInt(1);
				System.out.println(nbPieceExistantes);
				switch (type) {
				case ROI:
					if (nbPieceExistantes > 1)
						throw new PieceException("Le nombre de rois dépasse 1");
					break;
				case REINE:
					if (nbPieceExistantes > 1)
						throw new PieceException("Le nombre de reines dépasse 1");
					break;
				case FOU:
					if (nbPieceExistantes > 2)
						throw new PieceException("Le nombre de fous dépasse 2");
					break;
				case CAVALIER:
					if (nbPieceExistantes > 2)
						throw new PieceException("Le nombre de cavaliers dépasse 2");
					break;
				case TOUR:
					if (nbPieceExistantes > 2)
						throw new PieceException("Le nombre de tours dépasse 2");
					break;
				default:
					if (nbPieceExistantes > 8)
						throw new PieceException("Le nombre de pions dépasse 8");
					break;
				}
				rset.close();
			}

			pstmt.close();
		}
		catch(SQLException e) {
			System.err.println("Failed to insert Piece (SQL)");
			e.printStackTrace(System.err);
		}
		catch(PieceException e) {
			System.err.println("Failed to insert Piece (Piece)");
		}
	}
}

class PieceException extends Exception {
	private static final long serialVersionUID = 1L;
	String msg;

	public PieceException(String m) {
		msg = m;
		System.out.println(msg);
}}
