package model;

import java.util.Date;

public class Equipe {
    private int id;
    private String nom;
    private String ville;
    private String entraineur;
    private Date dateCreation;

    public Equipe() {}

    public Equipe(int id, String nom, String ville, String entraineur, Date dateCreation) {
        this.id = id;
        this.nom = nom;
        this.ville = ville;
        this.entraineur = entraineur;
        this.dateCreation = dateCreation;
    }

    public Equipe(String nom, String ville, String entraineur, Date dateCreation) {
        this.nom = nom;
        this.ville = ville;
        this.entraineur = entraineur;
        this.dateCreation = dateCreation;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getEntraineur() { return entraineur; }
    public void setEntraineur(String entraineur) { this.entraineur = entraineur; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    @Override
    public String toString() { return nom; }
}
