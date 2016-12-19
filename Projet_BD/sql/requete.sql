--InitaliserPartie(idJoueurBlanc, idJoueurNoir, Niveau, Partie) : 

INSERT INTO partie VALUES(Partie,Niveau,NULL);
INSERT INTO participe VALUES(idJoueurBlanc,'Blanc',Partie,Niveau);
INSERT INTO participe VALUES(idJoueurNoir,'Noir',Partie,Niveau);

INSERT INTO Joueur Values(idJoueurBlanc,NULL,NULL,NULL,NULL);
INSERT INTO Joueur Values(idJoueurBlanc,NULL,NULL,NULL,NULL);

INSERT INTO Piece values(1,0,'ROI',Partie,Niveau,'NOIR',8,'e'); 
INSERT INTO Piece values(2,0,'REINE',Partie,Niveau,'NOIR',8,'d'); 
INSERT INTO Piece values(3,0,'FOU',Partie,Niveau,'NOIR',8,'c'); 
INSERT INTO Piece values(4,0,'FOU',Partie,Niveau,'NOIR',8,'f'); 
INSERT INTO Piece values(5,0,'CAVALIER',Partie,Niveau,'NOIR',8,'b'); 
INSERT INTO Piece values(6,0,'CAVALIER',Partie,Niveau,'NOIR',8,'g'); 
INSERT INTO Piece values(7,0,'TOUR',Partie,Niveau,'NOIR',8,'a'); 
INSERT INTO Piece values(8,0,'TOUR',Partie,Niveau,'NOIR',8,'h'); 
INSERT INTO Piece values(9,0,'PION',Partie,Niveau,'NOIR',7,'a'); 
INSERT INTO Piece values(10,0,'PION',Partie,Niveau,'NOIR',7,'b'); 
INSERT INTO Piece values(11,0,'PION',Partie,Niveau,'NOIR',7,'c'); 
INSERT INTO Piece values(12,0,'PION',Partie,Niveau,'NOIR',7,'d'); 
INSERT INTO Piece values(13,0,'PION',Partie,Niveau,'NOIR',7,'e'); 
INSERT INTO Piece values(14,0,'PION',Partie,Niveau,'NOIR',7,'f'); 
INSERT INTO Piece values(15,0,'PION',Partie,Niveau,'NOIR',7,'g'); 
INSERT INTO Piece values(16,0,'PION',Partie,Niveau,'NOIR',7,'h'); 

INSERT INTO Piece values(17,0,'Roi',Partie,Niveau,'BLANC',1,'e'); 
INSERT INTO Piece values(18,0,'Reine',Partie,Niveau,'BLANC',1,'d'); 
INSERT INTO Piece values(19,0,'FOU',Partie,Niveau,'BLANC',1,'c'); 
INSERT INTO Piece values(20,0,'FOU',Partie,Niveau,'BLANC',1,'f'); 
INSERT INTO Piece values(21,0,'CAVALIER',Partie,Niveau,'BLANC',1,'b'); 
INSERT INTO Piece values(22,0,'CAVALIER',Partie,Niveau,'BLANC',1,'g'); 
INSERT INTO Piece values(23,0,'TOUR',Partie,Niveau,'BLANC',1,'a'); 
INSERT INTO Piece values(24,0,'TOUR',Partie,Niveau,'BLANC',1,'h'); 
INSERT INTO Piece values(25,0,'PION',Partie,Niveau,'BLANC',2,'a'); 
INSERT INTO Piece values(26,0,'PION',Partie,Niveau,'BLANC',2,'b'); 
INSERT INTO Piece values(27,0,'PION',Partie,Niveau,'BLANC',2,'c'); 
INSERT INTO Piece values(28,0,'PION',Partie,Niveau,'BLANC',2,'d'); 
INSERT INTO Piece values(29,0,'PION',Partie,Niveau,'BLANC',2,'e'); 
INSERT INTO Piece values(30,0,'PION',Partie,Niveau,'BLANC',2,'f'); 
INSERT INTO Piece values(31,0,'PION',Partie,Niveau,'BLANC',2,'g'); 
INSERT INTO Piece values(32,0,'PION',Partie,Niveau,'BLANC',2,'h'); 

--*************************************-------------***********************************************
--PlacerPièce(type,couleur,Li,Co,Niveau,Partie): // Attention à idPiece

INSERT INTO Piece values(idPiece,0,type,Partie,Niveau,couleur,Li,Co);

--*************************************-------------***********************************************
--Afficher(Niveau, Partie)

select * from Piece
where ((estSortie = 0) AND (numPartie = Partie) And (numNiveau = Niveau)); 

--*************************************-------------***********************************************
--Mouvement(Niveau, Partie, IdPièce, Li, Co)

var ligneDep INT;
var colonneDep CHAR(1);

begin
select (select ligne from Piece 
where ((numPiece = idPiece) AND (numPartie = Partie) And (numNiveau = Niveau)));
into: ligneDep from dual;
end;
/

begin
select (select colonne from Piece 
where ((numPiece = idPiece) AND (numPartie = Partie) And (numNiveau = Niveau)));
into: colonneDep from dual;
end;
/

update Piece set ligne = Li , colonne = Co 
where ((numPiece = idPiece) AND (numPartie = Partie) And (numNiveau = Niveau));

INSERT INTO Coup values(numCoup,:colonneDep,:ligneDep,Co,Li,Partie,Niveau); 

--*************************************-------------***********************************************
--verificationPiece(typePiece,couleurPiece)
SELECT COUNT(*) FROM Piece
where (typePiece = typePiece) AND (couleurJoueur = couleurPiece);


