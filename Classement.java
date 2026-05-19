package model;

public class Classement {
    private int id;
    private int equipeId;
    private String equipeNom;
    private int points;
    private int matchsJoues;
    private int victoires;
    private int nuls;
    private int defaites;
    private int butsPour;
    private int butsContre;
    private int differenceButs;

    public Classement() {}

    public Classement(int equipeId, String equipeNom, int points, int matchsJoues,
                      int victoires, int nuls, int defaites, int butsPour, int butsContre) {
        this.equipeId = equipeId;
        this.equipeNom = equipeNom;
        this.points = points;
        this.matchsJoues = matchsJoues;
        this.victoires = victoires;
        this.nuls = nuls;
        this.defaites = defaites;
        this.butsPour = butsPour;
        this.butsContre = butsContre;
        this.differenceButs = butsPour - butsContre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEquipeId() { return equipeId; }
    public void setEquipeId(int equipeId) { this.equipeId = equipeId; }

    public String getEquipeNom() { return equipeNom; }
    public void setEquipeNom(String equipeNom) { this.equipeNom = equipeNom; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getMatchsJoues() { return matchsJoues; }
    public void setMatchsJoues(int matchsJoues) { this.matchsJoues = matchsJoues; }

    public int getVictoires() { return victoires; }
    public void setVictoires(int victoires) { this.victoires = victoires; }

    public int getNuls() { return nuls; }
    public void setNuls(int nuls) { this.nuls = nuls; }

    public int getDefaites() { return defaites; }
    public void setDefaites(int defaites) { this.defaites = defaites; }

    public int getButsPour() { return butsPour; }
    public void setButsPour(int butsPour) { this.butsPour = butsPour; }

    public int getButsContre() { return butsContre; }
    public void setButsContre(int butsContre) { this.butsContre = butsContre; }

    public int getDifferenceButs() { return butsPour - butsContre; }
    public void setDifferenceButs(int differenceButs) { this.differenceButs = differenceButs; }
}
