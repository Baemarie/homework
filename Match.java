package model;

import java.util.Date;

public class Match {
    private int id;
    private int equipeDomicileId;
    private int equipeExterieurId;
    private String equipeDomicileNom;
    private String equipeExterieurNom;
    private Date dateMatch;
    private int journee;
    private String phase;
    private int butsDomicile;
    private int butsExterieur;
    private String statut;

    public Match() {}

    public Match(int id, int equipeDomicileId, int equipeExterieurId,
                 Date dateMatch, int journee, String phase,
                 int butsDomicile, int butsExterieur, String statut) {
        this.id = id;
        this.equipeDomicileId = equipeDomicileId;
        this.equipeExterieurId = equipeExterieurId;
        this.dateMatch = dateMatch;
        this.journee = journee;
        this.phase = phase;
        this.butsDomicile = butsDomicile;
        this.butsExterieur = butsExterieur;
        this.statut = statut;
    }

    public Match(int equipeDomicileId, int equipeExterieurId,
                 Date dateMatch, int journee, String phase) {
        this.equipeDomicileId = equipeDomicileId;
        this.equipeExterieurId = equipeExterieurId;
        this.dateMatch = dateMatch;
        this.journee = journee;
        this.phase = phase;
        this.statut = "Planifié";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEquipeDomicileId() { return equipeDomicileId; }
    public void setEquipeDomicileId(int equipeDomicileId) { this.equipeDomicileId = equipeDomicileId; }

    public int getEquipeExterieurId() { return equipeExterieurId; }
    public void setEquipeExterieurId(int equipeExterieurId) { this.equipeExterieurId = equipeExterieurId; }

    public String getEquipeDomicileNom() { return equipeDomicileNom; }
    public void setEquipeDomicileNom(String nom) { this.equipeDomicileNom = nom; }

    public String getEquipeExterieurNom() { return equipeExterieurNom; }
    public void setEquipeExterieurNom(String nom) { this.equipeExterieurNom = nom; }

    public Date getDateMatch() { return dateMatch; }
    public void setDateMatch(Date dateMatch) { this.dateMatch = dateMatch; }

    public int getJournee() { return journee; }
    public void setJournee(int journee) { this.journee = journee; }

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }

    public int getButsDomicile() { return butsDomicile; }
    public void setButsDomicile(int butsDomicile) { this.butsDomicile = butsDomicile; }

    public int getButsExterieur() { return butsExterieur; }
    public void setButsExterieur(int butsExterieur) { this.butsExterieur = butsExterieur; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getScore() {
        if (statut.equals("Terminé")) return butsDomicile + " - " + butsExterieur;
        return "vs";
    }

    @Override
    public String toString() {
        return equipeDomicileNom + " " + getScore() + " " + equipeExterieurNom;
    }
}
