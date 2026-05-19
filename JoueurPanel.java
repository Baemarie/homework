package view;

import dao.EquipeDAO;
import dao.JoueurDAO;
import model.Equipe;
import model.Joueur;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class JoueurPanel extends JPanel {

    private MainFrame parent;
    private JoueurDAO joueurDAO;
    private EquipeDAO equipeDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbFiltreEquipe;

    private final String[] COLONNES = {"ID", "Prénom", "Nom", "Poste", "N° Maillot", "Nationalité", "Équipe"};

    public JoueurPanel(MainFrame parent) {
        this.parent = parent;
        this.joueurDAO = new JoueurDAO();
        this.equipeDAO = new EquipeDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Liste des Joueurs");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(label, BorderLayout.WEST);

        JPanel filtrePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filtrePanel.add(new JLabel("Filtrer par équipe : "));
        cmbFiltreEquipe = new JComboBox<>();
        cmbFiltreEquipe.addItem("Toutes les équipes");
        equipeDAO.getTous().forEach(e -> cmbFiltreEquipe.addItem(e.getNom()));
        cmbFiltreEquipe.addActionListener(e -> charger());
        filtrePanel.add(cmbFiltreEquipe);
        topPanel.add(filtrePanel, BorderLayout.EAST);

        tableModel = new DefaultTableModel(COLONNES, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 248, 255));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnAjouter   = creerBouton("➕ Ajouter",   new Color(34, 139, 34));
        JButton btnModifier  = creerBouton("✏️ Modifier",  new Color(70, 130, 180));
        JButton btnSupprimer = creerBouton("🗑️ Supprimer", new Color(178, 34, 34));
        JButton btnRefresh   = creerBouton("🔄 Rafraîchir", new Color(105, 105, 105));

        btnAjouter.addActionListener(e -> {
            JoueurFormDialog d = new JoueurFormDialog(parent, null, joueurDAO, equipeDAO);
            d.setVisible(true);
            charger();
        });

        btnModifier.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un joueur."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            // Récupérer le joueur complet
            List<Joueur> tous = joueurDAO.getTous();
            Joueur j = tous.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
            JoueurFormDialog d = new JoueurFormDialog(parent, j, joueurDAO, equipeDAO);
            d.setVisible(true);
            charger();
        });

        btnSupprimer.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un joueur."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            String nom = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer le joueur \"" + nom + "\" ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                joueurDAO.supprimer(id);
                charger();
            }
        });

        btnRefresh.addActionListener(e -> charger());

        btnPanel.add(btnAjouter);
        btnPanel.add(btnModifier);
        btnPanel.add(btnSupprimer);
        btnPanel.add(btnRefresh);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void charger() {
        tableModel.setRowCount(0);
        List<Joueur> joueurs;
        String selection = (String) cmbFiltreEquipe.getSelectedItem();
        if (selection == null || selection.equals("Toutes les équipes")) {
            joueurs = joueurDAO.getTous();
        } else {
            List<Equipe> equipes = equipeDAO.getTous();
            int equipeId = equipes.stream()
                .filter(e -> e.getNom().equals(selection))
                .mapToInt(Equipe::getId).findFirst().orElse(-1);
            joueurs = joueurDAO.getParEquipe(equipeId);
        }
        for (Joueur j : joueurs) {
            tableModel.addRow(new Object[]{
                j.getId(), j.getPrenom(), j.getNom(),
                j.getPoste(), j.getNumeroMaillot(),
                j.getNationalite(), j.getEquipeNom()
            });
        }
        parent.setStatus(joueurs.size() + " joueur(s) affiché(s).");
    }

    private JButton creerBouton(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
