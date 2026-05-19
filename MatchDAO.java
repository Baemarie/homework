package dao;

import model.Match;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {

    private Connection conn;

    public MatchDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean ajouter(Match match) {
        String sql = "INSERT INTO match_foot (equipe_domicile_id, equipe_exterieur_id, date_match, journee, phase, statut) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, match.getEquipeDomicileId());
            ps.setInt(2, match.getEquipeExterieurId());
            ps.setTimestamp(3, match.getDateMatch() != null ?
                new Timestamp(match.getDateMatch().getTime()) : null);
            ps.setInt(4, match.getJournee());
            ps.setString(5, match.getPhase());
            ps.setString(6, "Planifié");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saisirResultat(int matchId, int butsDomicile, int butsExterieur) {
        String sql = "UPDATE match_foot SET buts_domicile=?, buts_exterieur=?, statut='Terminé' WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, butsDomicile);
            ps.setInt(2, butsExterieur);
            ps.setInt(3, matchId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM match_foot WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Match> getTous() {
        List<Match> liste = new ArrayList<>();
        String sql = "SELECT m.*, e1.nom as dom_nom, e2.nom as ext_nom " +
                     "FROM match_foot m " +
                     "JOIN equipe e1 ON m.equipe_domicile_id = e1.id " +
                     "JOIN equipe e2 ON m.equipe_exterieur_id = e2.id " +
                     "ORDER BY m.date_match DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Match m = mapMatch(rs);
                liste.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public List<Match> getParJournee(int journee) {
        List<Match> liste = new ArrayList<>();
        String sql = "SELECT m.*, e1.nom as dom_nom, e2.nom as ext_nom " +
                     "FROM match_foot m " +
                     "JOIN equipe e1 ON m.equipe_domicile_id = e1.id " +
                     "JOIN equipe e2 ON m.equipe_exterieur_id = e2.id " +
                     "WHERE m.journee=? ORDER BY m.date_match";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, journee);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(mapMatch(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public List<Match> getParStatut(String statut) {
        List<Match> liste = new ArrayList<>();
        String sql = "SELECT m.*, e1.nom as dom_nom, e2.nom as ext_nom " +
                     "FROM match_foot m " +
                     "JOIN equipe e1 ON m.equipe_domicile_id = e1.id " +
                     "JOIN equipe e2 ON m.equipe_exterieur_id = e2.id " +
                     "WHERE m.statut=? ORDER BY m.date_match";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(mapMatch(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public Match getParId(int id) {
        String sql = "SELECT m.*, e1.nom as dom_nom, e2.nom as ext_nom " +
                     "FROM match_foot m " +
                     "JOIN equipe e1 ON m.equipe_domicile_id = e1.id " +
                     "JOIN equipe e2 ON m.equipe_exterieur_id = e2.id " +
                     "WHERE m.id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapMatch(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalButs() {
        String sql = "SELECT SUM(buts_domicile + buts_exterieur) FROM match_foot WHERE statut='Terminé'";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getNombreMatchsJoues() {
        String sql = "SELECT COUNT(*) FROM match_foot WHERE statut='Terminé'";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Match mapMatch(ResultSet rs) throws SQLException {
        Match m = new Match();
        m.setId(rs.getInt("id"));
        m.setEquipeDomicileId(rs.getInt("equipe_domicile_id"));
        m.setEquipeExterieurId(rs.getInt("equipe_exterieur_id"));
        m.setEquipeDomicileNom(rs.getString("dom_nom"));
        m.setEquipeExterieurNom(rs.getString("ext_nom"));
        m.setDateMatch(rs.getTimestamp("date_match"));
        m.setJournee(rs.getInt("journee"));
        m.setPhase(rs.getString("phase"));
        m.setButsDomicile(rs.getInt("buts_domicile"));
        m.setButsExterieur(rs.getInt("buts_exterieur"));
        m.setStatut(rs.getString("statut"));
        return m;
    }
}
