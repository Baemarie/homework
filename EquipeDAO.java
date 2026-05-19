package dao;

import model.Equipe;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    private Connection conn;

    public EquipeDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // Ajouter une équipe
    public boolean ajouter(Equipe equipe) {
        String sql = "INSERT INTO equipe (nom, ville, entraineur, date_creation) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipe.getNom());
            ps.setString(2, equipe.getVille());
            ps.setString(3, equipe.getEntraineur());
            ps.setDate(4, equipe.getDateCreation() != null ?
                new java.sql.Date(equipe.getDateCreation().getTime()) : null);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                // Ajouter une entrée dans classement automatiquement
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    String sqlC = "INSERT INTO classement (equipe_id) VALUES (?)";
                    PreparedStatement psC = conn.prepareStatement(sqlC);
                    psC.setInt(1, newId);
                    psC.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Modifier une équipe
    public boolean modifier(Equipe equipe) {
        String sql = "UPDATE equipe SET nom=?, ville=?, entraineur=?, date_creation=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipe.getNom());
            ps.setString(2, equipe.getVille());
            ps.setString(3, equipe.getEntraineur());
            ps.setDate(4, equipe.getDateCreation() != null ?
                new java.sql.Date(equipe.getDateCreation().getTime()) : null);
            ps.setInt(5, equipe.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Supprimer une équipe
    public boolean supprimer(int id) {
        String sql = "DELETE FROM equipe WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Récupérer toutes les équipes
    public List<Equipe> getTous() {
        List<Equipe> liste = new ArrayList<>();
        String sql = "SELECT * FROM equipe ORDER BY nom";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Equipe e = new Equipe();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setVille(rs.getString("ville"));
                e.setEntraineur(rs.getString("entraineur"));
                e.setDateCreation(rs.getDate("date_creation"));
                liste.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Récupérer une équipe par ID
    public Equipe getParId(int id) {
        String sql = "SELECT * FROM equipe WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Equipe e = new Equipe();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setVille(rs.getString("ville"));
                e.setEntraineur(rs.getString("entraineur"));
                e.setDateCreation(rs.getDate("date_creation"));
                return e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Compter le nombre d'équipes
    public int compter() {
        String sql = "SELECT COUNT(*) FROM equipe";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
