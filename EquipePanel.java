package view;

import dao.EquipeDAO;
import model.Equipe;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class EquipePanel extends JPanel {

    private MainFrame parent;
    private EquipeDAO equipeDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtRecherche;

    private final String[] COLONNES = {"ID", "Nom", "Ville", "Entraîneur", "Date Création"};

    public EquipePanel(MainFrame parent) {
        this.parent = parent;
        this.equipeDAO = new EquipeDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panneau du haut : titre + recherche
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        JLabel label = new JLabel("Liste des Équipes");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(label, BorderLayout.WEST);

        JPanel recherchePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        recherchePanel.add(new JLabel("Recherche : "));
        txtRecherche = new JTextField(15);
        txtRecherche.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filtrer(txtRecherche.getText()); }
        });
        recherchePanel.add(txtRecherche);
        topPanel.add(recherchePanel, BorderLayout.EAST);

        // Table
        tableModel = new DefaultTableModel(COLONNES, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // Alterner couleur des lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 248, 255));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnAjouter   = creerBouton("➕ Ajouter",   new Color(34, 139, 34));
        JButton btnModifier  = creerBouton("✏️ Modifier",  new Color(70, 130, 180));
        JButton btnSupprimer = creerBouton("🗑️ Supprimer", new Color(178, 34, 34));
        JButton btnRefresh   = creerBouton("🔄 Rafraîchir", new Color(105, 105, 105));

        btnAjouter.addActionListener(e -> {
            EquipeFormDialog dialog = new EquipeFormDialog(parent, null, equipeDAO);
            dialog.setVisible(true);
            charger();
            parent.setStatus("Équipe ajoutée avec succès.");
        });

        btnModifier.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une équipe."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            Equipe eq = equipeDAO.getParId(id);
            EquipeFormDialog dialog = new EquipeFormDialog(parent, eq, equipeDAO);
            dialog.setVisible(true);
            charger();
            parent.setStatus("Équipe modifiée.");
        });

        btnSupprimer.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une équipe."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            String nom = (String) tableModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer l'équipe \"" + nom + "\" ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                equipeDAO.supprimer(id);
                charger();
                parent.setStatus("Équipe supprimée.");
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
        List<Equipe> equipes = equipeDAO.getTous();
        for (Equipe e : equipes) {
            tableModel.addRow(new Object[]{
                e.getId(), e.getNom(), e.getVille(),
                e.getEntraineur(), e.getDateCreation()
            });
        }
        parent.setStatus(equipes.size() + " équipe(s) chargée(s).");
    }

    private void filtrer(String texte) {
        tableModel.setRowCount(0);
        List<Equipe> equipes = equipeDAO.getTous();
        for (Equipe e : equipes) {
            if (e.getNom().toLowerCase().contains(texte.toLowerCase()) ||
                e.getVille().toLowerCase().contains(texte.toLowerCase())) {
                tableModel.addRow(new Object[]{
                    e.getId(), e.getNom(), e.getVille(),
                    e.getEntraineur(), e.getDateCreation()
                });
            }
        }
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
