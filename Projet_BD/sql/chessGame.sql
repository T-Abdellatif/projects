CREATE TABLE Joueur (
	numJoueur INT CHECK(numJoueur > 0 AND numJoueur <= 32) PRIMARY KEY,
	nom CHAR(10) ,--NOT NULL,
	prenom CHAR(10),-- NOT NULL,
	adresse CHAR(20),
	naissance DATE
);

--CREATE TABLE Niveau (
--	numNiveau INT CHECK((numNiveau >= 1) AND (numNiveau <= 5)) PRIMARY KEY
--);

CREATE TABLE Couleur (
couleurJoueur CHAR(10) CHECK (couleurJoueur in ('BLANC','NOIR')) PRIMARY KEY
);

CREATE TABLE Partie (
	numPartie INT CHECK(numPartie >= 1 AND numPartie <= 16),
	numNiveau INT CHECK((numNiveau >= 1) AND (numNiveau <= 5)),
	vainqueur INT REFERENCES Joueur(numJoueur),
	CONSTRAINT IDpartie PRIMARY KEY (numPartie,numNiveau)
);

CREATE TABLE Participe (
	numJoueur INT REFERENCES Joueur(numJoueur),
	couleurJoueur CHAR(10) REFERENCES couleur(couleurJoueur),
	numPartie INT,
	numNiveau INT,
	FOREIGN KEY(numPartie,numNiveau) REFERENCES partie(numPartie,numNiveau),
	CONSTRAINT IDparticipe PRIMARY KEY (numJoueur,CouleurJoueur,numPartie,numNiveau)
);



CREATE TABLE Piece (
	numPiece INT CHECK((numPiece > 0) AND (numPiece <= 32)) ,
	estSortie INT CHECK((estSortie >= 0) AND (estSortie <= 1)),
	typePiece CHAR(10) NOT NULL CHECK (typePiece in ('ROI','REINE','FOU','CAVALIER','TOUR','PION')),	
	numPartie INT,
	numNiveau INT,	
	couleurJoueur CHAR(10) REFERENCES Couleur,
	ligne INT CHECK(ligne > 0 AND ligne <= 8),
	colonne CHAR(1) CHECK((colonne >= 'a') AND (colonne <= 'h')),
	FOREIGN KEY(numPartie,numNiveau) REFERENCES partie(numPartie,numNiveau), 
	CONSTRAINT IDpiece PRIMARY KEY (numPiece,numPartie,numNiveau)
);

CREATE TABLE Coup (
	numCoup INT,
	colonneDep CHAR(1) CHECK(colonneDep >= 'a' AND colonneDep <= 'h'),
	ligneDep INT CHECK(ligneDep > 0 AND ligneDep <= 8),
	colonneArr CHAR(1) CHECK(colonneArr >= 'a' AND colonneArr <= 'h'),
	ligneArr INT CHECK(ligneArr > 0 AND ligneArr <= 8),
	numPartie INT,
	numNiveau INT,
	FOREIGN KEY(numPartie,numNiveau) REFERENCES partie(numPartie,numNiveau), 
	CONSTRAINT IDcoup PRIMARY KEY (numCoup,numPartie,numNiveau)
);

