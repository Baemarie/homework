package model;

import java.util.Date;

public class Joueur {
    private int id;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String nationalite;
    private String poste;
    private int numeroMaillot;
    private int equipeId;
    private String equipeNom; // pour affichage

    public Joueur() {}

    public Joueur(int id, String nom, String prenom, Date dateNaissance,
                  String nationalite, String poste, int numeroMaillot, int equipeId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.nationalite = nationalite;
        this.poste = poste;
        this.numeroMaillot = numeroMaillot;
        this.equipeId = equipeId;
    }

    public Joueur(String nom, String prenom, Date dateNaissance,
                  String nationalite, String poste, int numeroMaillot, int equipeId) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.nationalite = nationalite;
        this.poste = poste;
        this.numeroMaillot = numeroMaillot;
        this.equipeId = equipeId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public int getNumeroMaillot() { return numeroMaillot; }
    public void setNumeroMaillot(int numeroMaillot) { this.numeroMaillot = numeroMaillot; }

    public int getEquipeId() { return equipeId; }
    public void setEquipeId(int equipeId) { this.equipeId = equipeId; }

    public String getEquipeNom() { return equipeNom; }
    public void setEquipeNom(String equipeNom) { this.equipeNom = equipeNom; }

    public String getNomComplet() { return prenom + " " + nom; }

    @Override
    public String toString() { return prenom + " " + nom; }
}
