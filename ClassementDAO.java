package dao;

import model.Classement;
import model.Match;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassementDAO {

    private Connection conn;

    public ClassementDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // Récupère le classement trié (points DESC, diff buts DESC, buts pour DESC)
    public List<Classement> getClassement() {
        List<Classement> liste = new ArrayList<>();
        String sql = "SELECT c.*, e.nom as equipe_nom FROM classement c " +
                     "JOIN equipe e ON c.equipe_id = e.id " +
                     "ORDER BY c.points DESC, (c.buts_pour - c.buts_contre) DESC, c.buts_pour DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Classement cl = new Classement();
                cl.setId(rs.getInt("id"));
                cl.setEquipeId(rs.getInt("equipe_id"));
                cl.setEquipeNom(rs.getString("equipe_nom"));
                cl.setPoints(rs.getInt("points"));
                cl.setMatchsJoues(rs.getInt("matchs_joues"));
                cl.setVictoires(rs.getInt("victoires"));
                cl.setNuls(rs.getInt("nuls"));
                cl.setDefaites(rs.getInt("defaites"));
                cl.setButsPour(rs.getInt("buts_pour"));
                cl.setButsContre(rs.getInt("buts_contre"));
                liste.add(cl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Met à jour le classement après un match terminé
    public void mettreAJourApresMatch(Match match) {
        int domId  = match.getEquipeDomicileId();
        int extId  = match.getEquipeExterieurId();
        int butsDom = match.getButsDomicile();
        int butsExt = match.getButsExterieur();

        int ptsDom, ptsExt, vicDom, vicExt, nulDom, nulExt, defDom, defExt;

        if (butsDom > butsExt) {
            ptsDom = 3; ptsExt = 0;
            vicDom = 1; vicExt = 0;
            nulDom = 0; nulExt = 0;
            defDom = 0; defExt = 1;
        } else if (butsDom < butsExt) {
            ptsDom = 0; ptsExt = 3;
            vicDom = 0; vicExt = 1;
            nulDom = 0; nulExt = 0;
            defDom = 1; defExt = 0;
        } else {
            ptsDom = 1; ptsExt = 1;
            vicDom = 0; vicExt = 0;
            nulDom = 1; nulExt = 1;
            defDom = 0; defExt = 0;
        }

        mettreAJourEquipe(domId, ptsDom, vicDom, nulDom, defDom, butsDom, butsExt);
        mettreAJourEquipe(extId, ptsExt, vicExt, nulExt, defExt, butsExt, butsDom);
    }

    private void mettreAJourEquipe(int equipeId, int pts, int vic, int nul, int def, int bp, int bc) {
        String sql = "UPDATE classement SET " +
                     "points = points + ?, " +
                     "matchs_joues = matchs_joues + 1, " +
                     "victoires = victoires + ?, " +
                     "nuls = nuls + ?, " +
                     "defaites = defaites + ?, " +
                     "buts_pour = buts_pour + ?, " +
                     "buts_contre = buts_contre + ?, " +
                     "difference_buts = buts_pour - buts_contre " +
                     "WHERE equipe_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pts);
            ps.setInt(2, vic);
            ps.setInt(3, nul);
            ps.setInt(4, def);
            ps.setInt(5, bp);
            ps.setInt(6, bc);
            ps.setInt(7, equipeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Recalcule tout le classement depuis zéro (utile si on modifie un résultat)
    public void recalculerTout() {
        try {
            // Réinitialiser
            Statement st = conn.createStatement();
            st.executeUpdate("UPDATE classement SET points=0, matchs_joues=0, victoires=0, nuls=0, defaites=0, buts_pour=0, buts_contre=0, difference_buts=0");

            // Recalculer depuis les matchs terminés
            String sql = "SELECT * FROM match_foot WHERE statut='Terminé'";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Match m = new Match();
                m.setEquipeDomicileId(rs.getInt("equipe_domicile_id"));
                m.setEquipeExterieurId(rs.getInt("equipe_exterieur_id"));
                m.setButsDomicile(rs.getInt("buts_domicile"));
                m.setButsExterieur(rs.getInt("buts_exterieur"));
                mettreAJourApresMatch(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
