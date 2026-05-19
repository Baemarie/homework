package dao;

import model.Joueur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JoueurDAO {

    private Connection conn;

    public JoueurDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean ajouter(Joueur joueur) {
        String sql = "INSERT INTO joueur (nom, prenom, date_naissance, nationalite, poste, numero_maillot, equipe_id) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, joueur.getNom());
            ps.setString(2, joueur.getPrenom());
            ps.setDate(3, joueur.getDateNaissance() != null ?
                new java.sql.Date(joueur.getDateNaissance().getTime()) : null);
            ps.setString(4, joueur.getNationalite());
            ps.setString(5, joueur.getPoste());
            ps.setInt(6, joueur.getNumeroMaillot());
            ps.setInt(7, joueur.getEquipeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean modifier(Joueur joueur) {
        String sql = "UPDATE joueur SET nom=?, prenom=?, date_naissance=?, nationalite=?, poste=?, numero_maillot=?, equipe_id=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, joueur.getNom());
            ps.setString(2, joueur.getPrenom());
            ps.setDate(3, joueur.getDateNaissance() != null ?
                new java.sql.Date(joueur.getDateNaissance().getTime()) : null);
            ps.setString(4, joueur.getNationalite());
            ps.setString(5, joueur.getPoste());
            ps.setInt(6, joueur.getNumeroMaillot());
            ps.setInt(7, joueur.getEquipeId());
            ps.setInt(8, joueur.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM joueur WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Joueur> getTous() {
        List<Joueur> liste = new ArrayList<>();
        String sql = "SELECT j.*, e.nom as equipe_nom FROM joueur j LEFT JOIN equipe e ON j.equipe_id = e.id ORDER BY j.nom";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Joueur j = new Joueur();
                j.setId(rs.getInt("id"));
                j.setNom(rs.getString("nom"));
                j.setPrenom(rs.getString("prenom"));
                j.setDateNaissance(rs.getDate("date_naissance"));
                j.setNationalite(rs.getString("nationalite"));
                j.setPoste(rs.getString("poste"));
                j.setNumeroMaillot(rs.getInt("numero_maillot"));
                j.setEquipeId(rs.getInt("equipe_id"));
                j.setEquipeNom(rs.getString("equipe_nom"));
                liste.add(j);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public List<Joueur> getParEquipe(int equipeId) {
        List<Joueur> liste = new ArrayList<>();
        String sql = "SELECT * FROM joueur WHERE equipe_id=? ORDER BY numero_maillot";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Joueur j = new Joueur();
                j.setId(rs.getInt("id"));
                j.setNom(rs.getString("nom"));
                j.setPrenom(rs.getString("prenom"));
                j.setDateNaissance(rs.getDate("date_naissance"));
                j.setNationalite(rs.getString("nationalite"));
                j.setPoste(rs.getString("poste"));
                j.setNumeroMaillot(rs.getInt("numero_maillot"));
                j.setEquipeId(rs.getInt("equipe_id"));
                liste.add(j);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Meilleurs buteurs (via table buteur)
    public List<Object[]> getMeilleursButeurs(int limit) {
        List<Object[]> liste = new ArrayList<>();
        String sql = "SELECT j.prenom, j.nom, e.nom as equipe, SUM(b.nombre_buts) as total_buts " +
                     "FROM buteur b " +
                     "JOIN joueur j ON b.joueur_id = j.id " +
                     "JOIN equipe e ON j.equipe_id = e.id " +
                     "GROUP BY j.id ORDER BY total_buts DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("prenom") + " " + rs.getString("nom"),
                    rs.getString("equipe"),
                    rs.getInt("total_buts")
                };
                liste.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public int compter() {
        String sql = "SELECT COUNT(*) FROM joueur";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
