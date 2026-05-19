-- ============================================
-- BASE DE DONNÉES : championnat_db
-- Projet : Gestion Championnat de Football
-- ============================================

CREATE DATABASE IF NOT EXISTS championnat_db CHARACTER SET utf8 COLLATE utf8_general_ci;
USE championnat_db;

-- Table Equipe
CREATE TABLE IF NOT EXISTS equipe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    ville VARCHAR(100),
    entraineur VARCHAR(100),
    date_creation DATE
);

-- Table Joueur
CREATE TABLE IF NOT EXISTS joueur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE,
    nationalite VARCHAR(100),
    poste ENUM('Gardien','Défenseur','Milieu','Attaquant') NOT NULL,
    numero_maillot INT,
    equipe_id INT,
    FOREIGN KEY (equipe_id) REFERENCES equipe(id) ON DELETE SET NULL
);

-- Table Match
CREATE TABLE IF NOT EXISTS match_foot (
    id INT AUTO_INCREMENT PRIMARY KEY,
    equipe_domicile_id INT NOT NULL,
    equipe_exterieur_id INT NOT NULL,
    date_match DATETIME,
    journee INT,
    phase ENUM('Phase de groupes','Quart de finale','Demi-finale','Finale') DEFAULT 'Phase de groupes',
    buts_domicile INT DEFAULT 0,
    buts_exterieur INT DEFAULT 0,
    statut ENUM('Planifié','Terminé') DEFAULT 'Planifié',
    FOREIGN KEY (equipe_domicile_id) REFERENCES equipe(id),
    FOREIGN KEY (equipe_exterieur_id) REFERENCES equipe(id)
);

-- Table Classement
CREATE TABLE IF NOT EXISTS classement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    equipe_id INT NOT NULL UNIQUE,
    points INT DEFAULT 0,
    matchs_joues INT DEFAULT 0,
    victoires INT DEFAULT 0,
    nuls INT DEFAULT 0,
    defaites INT DEFAULT 0,
    buts_pour INT DEFAULT 0,
    buts_contre INT DEFAULT 0,
    difference_buts INT DEFAULT 0,
    FOREIGN KEY (equipe_id) REFERENCES equipe(id) ON DELETE CASCADE
);

-- Table Buteur (statistiques buts par joueur par match)
CREATE TABLE IF NOT EXISTS buteur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    joueur_id INT NOT NULL,
    match_id INT NOT NULL,
    nombre_buts INT DEFAULT 1,
    FOREIGN KEY (joueur_id) REFERENCES joueur(id),
    FOREIGN KEY (match_id) REFERENCES match_foot(id)
);

-- Données de test : équipes
INSERT INTO equipe (nom, ville, entraineur, date_creation) VALUES
('Lions FC', 'Douala', 'Jean Mbarga', '2000-01-01'),
('Eagles United', 'Yaoundé', 'Paul Ateba', '1998-05-15'),
('Panthers SC', 'Bafoussam', 'Marc Tchinda', '2005-03-10'),
('Tigers FC', 'Garoua', 'Ali Hassan', '2010-07-20'),
('Leopards AC', 'Limbe', 'Eric Nkeng', '1995-11-30'),
('Falcons FC', 'Ngaoundéré', 'David Biya', '2002-08-14');

-- Données de test : joueurs
INSERT INTO joueur (nom, prenom, date_naissance, nationalite, poste, numero_maillot, equipe_id) VALUES
('Kamga', 'Pierre', '1995-03-12', 'Camerounaise', 'Gardien', 1, 1),
('Nkomo', 'Alain', '1998-07-22', 'Camerounaise', 'Défenseur', 4, 1),
('Bello', 'Moussa', '1997-01-15', 'Camerounaise', 'Milieu', 8, 1),
('Tchamba', 'Eric', '1999-09-05', 'Camerounaise', 'Attaquant', 9, 1),
('Essama', 'Guy', '1996-11-30', 'Camerounaise', 'Gardien', 1, 2),
('Ottou', 'Jean', '2000-04-18', 'Camerounaise', 'Défenseur', 5, 2),
('Manga', 'Boris', '1998-06-25', 'Camerounaise', 'Milieu', 10, 2),
('Abena', 'Samuel', '1997-02-14', 'Camerounaise', 'Attaquant', 11, 2);

-- Données de test : classement initial
INSERT INTO classement (equipe_id) VALUES (1),(2),(3),(4),(5),(6);

-- Données de test : matchs
INSERT INTO match_foot (equipe_domicile_id, equipe_exterieur_id, date_match, journee, buts_domicile, buts_exterieur, statut) VALUES
(1, 2, '2026-01-10 15:00:00', 1, 2, 1, 'Terminé'),
(3, 4, '2026-01-10 17:00:00', 1, 0, 0, 'Terminé'),
(5, 6, '2026-01-11 15:00:00', 1, 3, 1, 'Terminé'),
(2, 3, '2026-01-17 15:00:00', 2, 1, 2, 'Terminé'),
(4, 5, '2026-01-17 17:00:00', 2, 2, 0, 'Terminé'),
(6, 1, '2026-01-18 15:00:00', 2, 0, 1, 'Terminé'),
(1, 3, '2026-01-24 15:00:00', 3, 0, 0, 'Planifié'),
(2, 4, '2026-01-24 17:00:00', 3, 0, 0, 'Planifié');
