package chess_app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import chess_app.MyConnection.Couleur;
import chess_app.MyConnection.Type;

public class MovePiece {

	private static Connection conn;

	public MovePiece(Connection conn) {
		MovePiece.conn = conn;
	}

	@SuppressWarnings("resource")
	public void Mouvement(int niveau, int partie, int idPiece, int Li, char Co) {

		// Validité coup

		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM PARTIE WHERE NUMPARTIE = ? AND NUMNIVEAU = ?");
			pstmt.setInt(1, partie);
			pstmt.setInt(2, niveau);
			ResultSet rs = pstmt.executeQuery(); // On vérifie que la partie existe et n'est pas terminée
			if (!rs.next()) {
				System.err.println("Partie inexistante !");
				return;
			}
			if (rs.getInt(3) != 0) {
				System.err.println("Partie déjà terminée !");
				return;
			}

			int nombreCoups = 0; // Coups déjà effectués
			pstmt = conn.prepareStatement("SELECT * FROM COUP WHERE NUMPARTIE = ? AND NUMNIVEAU = ?");
			pstmt.setInt(1, partie);
			pstmt.setInt(2, niveau);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				rs.last();
				nombreCoups = rs.getRow();
			}
			pstmt = conn.prepareStatement("SELECT * FROM PIECE WHERE NUMPIECE = ? AND NUMNIVEAU = ? AND NUMPARTIE = ?");
			pstmt.setInt(1, idPiece);
			pstmt.setInt(2, niveau);
			pstmt.setInt(3, partie);
			rs = pstmt.executeQuery(); // On vérifie que c'est au tour du bon joueur
			rs.next();
			if ((rs.getString(6) == "BLANC") ^ (nombreCoups % 2 != 0)) {
				System.err.println("Cette pièce appartient au joueur dont ce n'est pas le tour");
				return;
			}
			if (rs.getInt(2) == 1) {
				System.err.println("Cette pièce a été prise et n'est plus disponible");
				return;
			}

			String requete = "select estSortie, typePiece, couleurJoueur, ligne, colonne "
					+ "from Piece where ((numPiece = ?) " + "AND (numPartie = ?) And (numNiveau = ?))";

			pstmt = conn.prepareStatement(requete);

			pstmt.setInt(1, idPiece);
			pstmt.setInt(2, partie);
			pstmt.setInt(3, niveau);

			// Get line
			ResultSet rset = pstmt.executeQuery();

			int estSortie;
			Couleur couleur = null;
			int liDep = 0;
			String coDep = null;
			Type typePiece;

			if (rset.next()) {
				estSortie = rset.getInt(1);
				System.out.println(rset.getString(2));
				if(rset.getString(2) == "PION")
					typePiece = Type.PION;
				else if (rset.getString(2) == "ROI")
					typePiece = Type.ROI	;
				else if (rset.getString(2) == "REINE")
					typePiece = Type.REINE	;
				else if (rset.getString(2) == "TOUR")
					typePiece = Type.TOUR	;
				else if (rset.getString(2) == "FOU")
					typePiece = Type.FOU	;
				else 
					typePiece = Type.CAVALIER	;
				
				if(rset.getString(3) == "BLANC")
					couleur = Couleur.BLANC;
				else 
					couleur = Couleur.NOIR	;

				liDep = rset.getInt(4);
				coDep = rset.getString(5);

				if (coDep == null || liDep == 0) {
					System.err.println("failed to insert");
					return;
				}
				if (coDep.charAt(0) == Co && liDep == Li) {
					System.err.print("coup  position invalide");
					return;
				}
				if (estSortie == 1) {
					System.err.print("coup sortie invalide");
					return;
				}

				int l = Math.abs(liDep - Li);
				int c = Math.abs(coDep.charAt(0) - Co);
				switch (typePiece) {
				case TOUR:
					if (l * c != 0){
						System.err.print("coup tour invalide");
						return;
					}else if (l == 0) {
						rset.close();
						pstmt.close();

						requete = "select colonne " + "from Piece where ((numPiece != ?) "
								+ "AND (numPartie = ?) AND (numNiveau = ?) " + "AND (estSortie = 0) AND (ligne = ?))";

						pstmt = conn.prepareStatement(requete);
						
						pstmt.setInt(1, idPiece);
						pstmt.setInt(2, partie);
						pstmt.setInt(3, niveau);
						pstmt.setInt(4, Li);

						rset = pstmt.executeQuery();

						while (rset.next()) {
							if (isBetween(rset.getString(1).charAt(0), Co, coDep.charAt(0))) {
								System.err.print("coup tour invalide");
							}
						}
					} else if (c == 0) {
						rset.close();
						pstmt.close();

						requete = "select ligne " + "from Piece where ((numPiece != ?) "
								+ "AND (numPartie = ?) AND (numNiveau = ?) " + "AND (estSortie = 0) AND (colonne = ?))";
						pstmt = conn.prepareStatement(requete);
						pstmt.setInt(1, idPiece);
						pstmt.setInt(2, partie);
						pstmt.setInt(3, niveau);
						pstmt.setString(4, "" + Co);

						rset = pstmt.executeQuery();

						while (rset.next()) {
							if (isBetween(rset.getInt(1), Li, liDep)) {
								System.err.print("coup tour invalide");
								return;
							}
						}
					}
					break;
				case FOU:
					if (l != c){
						System.err.print("coup fou invalide");
						return;
					} else {
						rset.close();
						pstmt.close();
	
						requete = "select idPice, ligne, colonne " + "from Piece where ((numPiece != ?) "
								+ "AND (numPartie = ?) AND (numNiveau = ?) "
								+ "AND (estSortie = 0) AND (colonne > ?) AND (colonne < ?) " 
								+ "AND (ligne > ?) AND (ligne < ?))";
						pstmt = conn.prepareStatement(requete);
						pstmt.setInt(1, idPiece);
						pstmt.setInt(2, partie);
						pstmt.setInt(3, niveau);
						if (Co < coDep.charAt(0)) {
							pstmt.setString(4, "" + Co);
							pstmt.setString(5, coDep);
						} else {
							pstmt.setString(4, coDep);
							pstmt.setString(5, "" + Co);
						}

						if (Li < liDep) {
							pstmt.setInt(6, Li);
							pstmt.setInt(7, liDep);
						} else {
							pstmt.setInt(6, liDep);
							pstmt.setInt(7, Li);
						}
						rset = pstmt.executeQuery();
						while (rset.next()) {
							System.err.print("coup fou invalide");
							return;
						}
					}
					break;
				case REINE:
					if (l != c && l * c != 0){
						System.err.print("coup reine invalide");
						return;
					}else if (l == 0) {
						rset.close();
						pstmt.close();

						requete = "select colonne " + "from Piece where ((numPiece != ?) "
								+ "AND (numPartie = ?) AND (numNiveau = ?) " + "AND (estSortie = 0) AND (ligne = ?))";
						pstmt = conn.prepareStatement(requete);
						pstmt.setInt(1, idPiece);
						pstmt.setInt(2, partie);
						pstmt.setInt(3, niveau);
						pstmt.setInt(4, Li);

						rset = pstmt.executeQuery();

						while (rset.next()) {
							if (isBetween(rset.getString(1).charAt(0), Co, coDep.charAt(0))) {
								System.err.print("coup reine invalide");
								return;
							}
						}
					} else if (c == 0) {
						rset.close();
						pstmt.close();

						requete = "select ligne " + "from Piece where ((numPiece != ?) "
								+ "AND (numPartie = ?) AND (numNiveau = ?) " + "AND (estSortie = 0) AND (colonne = ?))";
						pstmt = conn.prepareStatement(requete);
						pstmt.setInt(1, idPiece);
						pstmt.setInt(2, partie);
						pstmt.setInt(3, niveau);
						pstmt.setString(4, "" + Co);

						rset = pstmt.executeQuery();

						while (rset.next()) {
							if (isBetween(rset.getInt(1), Li, liDep)) {
								System.err.print("coup reine invalide");
								return ; 
							}
						}
					} else {
						rset.close();
						pstmt.close();

						requete = "select idPice, ligne, colonne " + "from Piece where ((numPiece != ?) "
								+ "AND (numPartie = ?) AND (numNiveau = ?) "
								+ "AND (estSortie = 0) AND (colonne > ?) AND (colonne < ?) " 
								+ "AND (ligne > ?) AND (ligne < ?))";
						pstmt = conn.prepareStatement(requete);
						pstmt.setInt(1, idPiece);
						pstmt.setInt(2, partie);
						pstmt.setInt(3, niveau);
						if (Co < coDep.charAt(0)) {
							pstmt.setString(4, "" + Co);
							pstmt.setString(5, coDep);
						} else {
							pstmt.setString(4, coDep);
							pstmt.setString(5, "" + Co);
						}

						if (Li < liDep) {
							pstmt.setInt(6, Li);
							pstmt.setInt(7, liDep);
						} else {
							pstmt.setInt(6, liDep);
							pstmt.setInt(7, Li);
						}
						rset = pstmt.executeQuery();
						while (rset.next()) {
							System.err.print("coup reine invalide");
							return;
						}
					}
					break;
				case ROI:
					if (l * c <= 1){
						System.err.print("coup invalide");
						return;
						}
					break;
				case CAVALIER:
					if (!((l == 2 && c == 1) || (l == 1 && c == 2))){
						System.err.print("coup invalide");
						return;
					}
					break;
				case PION:
					if ((couleur.ordinal() == 1) ^ (Li - liDep <= 0)){
						System.err.print("coup xor invalide");
						return;
					}
					break;
				}
				
			}
			rset.close();
			pstmt.close();
			//-------- [TEST] : position destination  			
			requete = "select idPice, couleur, typePiece " + "from Piece where ((numPiece != ?) "
					+ "AND (numPartie = ?) AND (numNiveau = ?) "
					+ "AND (estSortie = 0) AND (colonne = ?) AND (ligne = ?) )";
			
			pstmt = conn.prepareStatement(requete);
			pstmt.setInt(1, idPiece);
			pstmt.setInt(2, partie);
			pstmt.setInt(3, niveau);
			pstmt.setString(4, "" + Co);
			pstmt.setInt(5, Li);
			
			rset = pstmt.executeQuery();
			
			if(rset.next()){
				// ---------[TEST] : couleur pièce dans position destination
				if (Couleur.valueOf(rset.getString(2)) == couleur){
					System.err.println("coup invalide : position destination ocuppée par une pièce de même couleur");
					return;
				// ---------[TEST] : coup gagnant checkmate
				} else if (Type.valueOf(rset.getString(3)) == Type.ROI){
					rset.close();
					pstmt.close();
					
					requete = "SELECT numjoueur "
							+ "FROM participe"
							+ "WHERE ((couleur == ?) AND (numPartie = ?) AND (numNiveau = ?))";
					pstmt = conn.prepareStatement(requete);
					pstmt.setString(1, couleur.toString());
					pstmt.setInt(2, partie);
					pstmt.setInt(3, niveau);
					rset = pstmt.executeQuery();
					int numVainqueur ;
					if (rset.next()){
						numVainqueur = rset.getInt(1);
						rset.close();
						pstmt.close();
						
						requete = "UPDATE partie "
								+ "SET vainqueur = ? "
								+ "WHERE ((numPartie = ?) AND (numNiveau = ?))";
						pstmt = conn.prepareStatement(requete);
						pstmt.setInt(1, numVainqueur);
						pstmt.setInt(2, partie);
						pstmt.setInt(3, niveau);
						rset = pstmt.executeQuery();
					} else {
						System.err.println("Initialisation vainqueur echoué");
						return;
					}
						
				// ---------[TEST] : pièce éliminée	
				} else {			
					int numPiece = rset.getInt(1); 
					
					rset.close();
					pstmt.close();
					
					requete = "UPDATE piece "
							+ "SET estSortie = 1 "
							+ "WHERE ((numPiece = ? ) AND (numPartie = ?) AND (numNiveau = ?))";
					pstmt = conn.prepareStatement(requete);
					pstmt.setInt(1, numPiece);
					pstmt.setInt(2, partie);
					pstmt.setInt(3, niveau);
					rset = pstmt.executeQuery();	
				}
			} 
			
			rset.close();
			pstmt.close();
			
			// ---- modifier position -------------
			
			rset.close();
			pstmt.close();
			
			requete = "UPDATE piece "
					+ "SET ligne = ?, colonne = ? "
					+ "WHERE ((numPiece = ? ) AND (numPartie = ?) AND (numNiveau = ?))";
			pstmt = conn.prepareStatement(requete);
			pstmt.setInt(1, Li);
			pstmt.setString(2, ""+Co);
			pstmt.setInt(3, idPiece);
			pstmt.setInt(4, partie);
			pstmt.setInt(5, niveau);
			rset = pstmt.executeQuery();
			

		} catch (SQLException e) {
			System.err.println("failed to insert move");
			e.printStackTrace(System.err);
		}

	}

	private Boolean isBetween(int a, int bound1, int bound2) {
		return (bound1 > bound2) ^ (a > bound1 && a < bound2);
	}

}
