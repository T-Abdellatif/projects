package chess_app;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Date;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class App {
	
	public enum Choix {
		NONE, NEW_PLAYER, NEW_GAME, DISPLAY_GAME, PLAY_MOVE, SAVE, EXIT, EXIT_SAVE;  
	}
	
	public static void main(String [] args) throws SQLException, ParseException, PieceException {
		Boolean isSaved = false;
		
		MyConnection sqlConn = new MyConnection();
		sqlConn.openConnection("jdbc:oracle:thin:@ensioracle1.imag.fr:1521:ensioracle1", "jedray", "aaa753951");
		
		Savepoint save = MyConnection.getConn().setSavepoint();
		
		NewPlayer newPlayerInst = new NewPlayer(MyConnection.getConn()); 
		ShowTable showTableInst = new ShowTable(MyConnection.getConn());
		MovePiece movePieceInst = new MovePiece(MyConnection.getConn());
		InitMatch initMatchInst = new InitMatch(MyConnection.getConn());
		
		Scanner in = new Scanner(System.in);
		Choix userChoice = Choix.NONE;
		while (userChoice != Choix.EXIT && userChoice != Choix.EXIT_SAVE) {
			System.out.flush();
			System.err.flush();
			
			System.out.println("Application de tournoi d'échecs");
			System.out.println("===============================");
			System.out.println();
			System.out.println("1. Enregistrer un nouveau joueur");
			System.out.println("2. Créer une nouvelle partie");
			System.out.println("3. Afficher une partie en cours");
			System.out.println("4. Jouer un mouvement sur une partie en cours");
			System.out.println("5. Sauvegarder");
			System.out.println("6. Quitter");
			System.out.println("7. Sauvegarder et quitter");
			System.out.println();
			System.out.print("Votre choix : ");
			
			userChoice = Choix.values()[in.nextInt()];
			in.nextLine();
			
			System.out.println();
			System.out.println();
			String nom, prenom, addresse, birthDate = "";
			int idJoueurBlanc, idJoueurNoir, niveau, partie, idPiece, Li, Co;
			switch (userChoice) {
			case NEW_PLAYER:
				if (newPlayerInst.isFull()) {
					System.out.println("Impossible de rajouter de nouveaux joueurs, le tournoi est plein !");
					System.out.println();
				} else {
					System.out.print("Nom du joueur : ");
					nom = in.nextLine();
					System.out.print("Prénom du joueur : ");
					prenom = in.nextLine();
					System.out.print("Addresse du joueur : ");
					addresse = in.nextLine();
					System.out.print("Date de naissance du joueur (yyyyMMdd) : ");
			        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			        Date dateStr;
					try {
						dateStr = formatter.parse(in.nextLine());
						birthDate = formatter.format(dateStr);
					} catch (ParseException e) {
						System.out.println("Mauvais format de date, celle-ci sera ignorée.");
						birthDate = "";
					}
					System.out.println();
					newPlayerInst.NouveauJoueur(nom, prenom, addresse, birthDate);
				}
				break;
			case NEW_GAME:
				if (!newPlayerInst.isFull()) {
					System.out.println("Les parties ne peuvent pas commencer avant que tous les joueurs aient rejoint le tournoi.");
					System.out.println();
					break;
				}
				System.out.print("Id du joueur blanc (1 - 32) : ");
				idJoueurBlanc = App.clamp(in.nextInt(), 1, 32);
				in.nextLine();
				System.out.print("Id du joueur noir (1 - 32) : ");
				idJoueurNoir = App.clamp(in.nextInt(), 1, 32);
				in.nextLine();
				System.out.print("Niveau (1 - 5) : ");
				niveau = App.clamp(in.nextInt(), 1, 5);
				in.nextLine();
				System.out.print("Numéro de partie (1 - 16) : ");
				partie = App.clamp(in.nextInt(), 1, 16);
				in.nextLine();
				initMatchInst.InitialiserPartie(idJoueurBlanc, idJoueurNoir, niveau, partie);
				break;
			case DISPLAY_GAME:
				System.out.print("Niveau (1 - 5) : ");
				niveau = App.clamp(in.nextInt(), 1, 5);
				in.nextLine();
				System.out.print("Numéro de partie (1 - 16) : ");
				partie = App.clamp(in.nextInt(), 1, 16);
				in.nextLine();
				showTableInst.Afficher(niveau, partie);
				break;
			case PLAY_MOVE:
				System.out.print("Niveau (1 - 5) : ");
				niveau = App.clamp(in.nextInt(), 1, 5);
				in.nextLine();
				System.out.print("Numéro de partie (1 - 16) : ");
				partie = App.clamp(in.nextInt(), 1, 16);
				in.nextLine();
				System.out.print("Id de pièce (1 - 32) : ");
				idPiece = App.clamp(in.nextInt(), 1, 32);
				in.nextLine();
				System.out.print("Numéro ligne (1 - 8) : ");
				Li = App.clamp(in.nextInt(), 1, 8);
				in.nextLine();
				System.out.print("Numéro de colonne (a - h) : ");
				Co = App.clamp(in.nextLine().charAt(0), 'a', 'h');
				// Vérifications effectués dans MovePiece :
				// 	   Partie existe
				//     Tour du bon joueur
				//     Partie non terminée
				//     Mouvement valide,
				//     Pièce toujours disponible
				//     Cases au milieu vides
				//     Arrivée sur une case vide ou occupée par une piece non prise
				//     Update : Pièce éventuellement prise
				//     Update : Position
				//     Update : Gagnant éventuel
				movePieceInst.Mouvement(niveau, partie, idPiece, Li, ("abcdefgh").charAt(Co - 'a' + 1));
				break;
			case SAVE:
				System.out.print("Sauvegarde en cours... ");
				MyConnection.getConn().commit();
				save = MyConnection.getConn().setSavepoint();
				System.out.println("terminé");
				System.out.println();
				break;
			case EXIT_SAVE:
				isSaved = true;
				MyConnection.getConn().commit();
				break;
			default:
				break;
			}
		}
		
		if (!isSaved) {
			MyConnection.getConn().rollback(save);
		}
		
		in.close();
		
		sqlConn.closeConnection();
	}
	
	private static int clamp(int a, int min, int max) {
		return a < min ? min : (a > max ? max : a);
	}
}
