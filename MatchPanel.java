package view;

import dao.EquipeDAO;
import dao.MatchDAO;
import dao.ClassementDAO;
import model.Match;
import model.Equipe;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MatchPanel extends JPanel {

    private MainFrame parent;
    private MatchDAO matchDAO;
    private EquipeDAO equipeDAO;
    private ClassementDAO classementDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbFiltre;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final String[] COLONNES = {"ID", "Journée", "Domicile", "Score", "Extérieur", "Date", "Phase", "Statut"};

    public MatchPanel(MainFrame parent) {
        this.parent = parent;
        this.matchDAO = new MatchDAO();
        this.equipeDAO = new EquipeDAO();
        this.classementDAO = new ClassementDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Calendrier des Matchs");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(label, BorderLayout.WEST);

        JPanel filtrePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filtrePanel.add(new JLabel("Filtrer : "));
        cmbFiltre = new JComboBox<>(new String[]{"Tous", "Planifié", "Terminé"});
        cmbFiltre.addActionListener(e -> charger());
        filtrePanel.add(cmbFiltre);
        topPanel.add(filtrePanel, BorderLayout.EAST);

        tableModel = new DefaultTableModel(COLONNES, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setMaxWidth(70);
        table.getColumnModel().getColumn(3).setMaxWidth(70);

        // Colorer les lignes selon statut
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    String statut = (String) tableModel.getValueAt(row, 7);
                    if ("Terminé".equals(statut)) setBackground(new Color(240, 255, 240));
                    else setBackground(new Color(255, 250, 220));
                }
                setHorizontalAlignment(col == 3 ? CENTER : LEFT);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnPlanifier  = creerBouton("📅 Planifier Match",  new Color(34, 139, 34));
        JButton btnResultat   = creerBouton("⚽ Saisir Résultat",  new Color(70, 130, 180));
        JButton btnSupprimer  = creerBouton("🗑️ Supprimer",        new Color(178, 34, 34));
        JButton btnRefresh    = creerBouton("🔄 Rafraîchir",       new Color(105, 105, 105));

        btnPlanifier.addActionListener(e -> {
            MatchFormDialog d = new MatchFormDialog(parent, matchDAO, equipeDAO);
            d.setVisible(true);
            charger();
        });

        btnResultat.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un match."); return; }
            String statut = (String) tableModel.getValueAt(row, 7);
            if ("Terminé".equals(statut)) {
                JOptionPane.showMessageDialog(this, "Ce match est déjà terminé.");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            Match m = matchDAO.getParId(id);
            ResultatDialog d = new ResultatDialog(parent, m, matchDAO, classementDAO);
            d.setVisible(true);
            charger();
            parent.setStatus("Résultat enregistré. Classement mis à jour.");
        });

        btnSupprimer.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un match."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Supprimer ce match ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                matchDAO.supprimer(id);
                classementDAO.recalculerTout();
                charger();
            }
        });

        btnRefresh.addActionListener(e -> charger());

        btnPanel.add(btnPlanifier);
        btnPanel.add(btnResultat);
        btnPanel.add(btnSupprimer);
        btnPanel.add(btnRefresh);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void charger() {
        tableModel.setRowCount(0);
        List<Match> matchs;
        String filtre = (String) cmbFiltre.getSelectedItem();
        if ("Tous".equals(filtre)) matchs = matchDAO.getTous();
        else matchs = matchDAO.getParStatut(filtre);

        for (Match m : matchs) {
            tableModel.addRow(new Object[]{
                m.getId(),
                "J" + m.getJournee(),
                m.getEquipeDomicileNom(),
                m.getScore(),
                m.getEquipeExterieurNom(),
                m.getDateMatch() != null ? sdf.format(m.getDateMatch()) : "-",
                m.getPhase(),
                m.getStatut()
            });
        }
        parent.setStatus(matchs.size() + " match(s) affiché(s).");
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
