package view;

import dao.ClassementDAO;
import model.Classement;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ClassementPanel extends JPanel {

    private MainFrame parent;
    private ClassementDAO classementDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    private final String[] COLONNES = {"#", "Équipe", "Pts", "MJ", "V", "N", "D", "BP", "BC", "Diff"};

    public ClassementPanel(MainFrame parent) {
        this.parent = parent;
        this.classementDAO = new ClassementDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Tableau de Classement");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(label, BorderLayout.WEST);

        JButton btnRecalc = new JButton("🔄 Recalculer tout");
        btnRecalc.setBackground(new Color(70, 130, 180));
        btnRecalc.setForeground(Color.WHITE);
        btnRecalc.setFont(new Font("Arial", Font.BOLD, 12));
        btnRecalc.setFocusPainted(false);
        btnRecalc.addActionListener(e -> {
            classementDAO.recalculerTout();
            charger();
            parent.setStatus("Classement recalculé depuis tous les matchs.");
        });

        JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel2.add(btnRecalc);
        topPanel.add(btnPanel2, BorderLayout.EAST);

        tableModel = new DefaultTableModel(COLONNES, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        // Colonnes numériques centrées
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < COLONNES.length; i++) {
            if (i != 1) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Largeurs
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        for (int i = 2; i < COLONNES.length; i++) table.getColumnModel().getColumn(i).setMaxWidth(55);

        // Colorer top 3
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    if (row == 0) setBackground(new Color(255, 215, 0, 80));      // or : 1er
                    else if (row == 1) setBackground(new Color(192, 192, 192, 80)); // argent : 2ème
                    else if (row == 2) setBackground(new Color(205, 127, 50, 80));  // bronze : 3ème
                    else setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 248, 255));
                }
                if (col != 1) setHorizontalAlignment(CENTER);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        // Légende
        JPanel legendePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendePanel.add(legendeLabel("🥇 1er", new Color(255, 215, 0, 150)));
        legendePanel.add(legendeLabel("🥈 2ème", new Color(192, 192, 192, 150)));
        legendePanel.add(legendeLabel("🥉 3ème", new Color(205, 127, 50, 150)));
        legendePanel.add(new JLabel("   MJ=matchs joués  V=victoires  N=nuls  D=défaites  BP=buts pour  BC=buts contre  Diff=différence"));

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(legendePanel, BorderLayout.SOUTH);
    }

    public void charger() {
        tableModel.setRowCount(0);
        List<Classement> classement = classementDAO.getClassement();
        int rang = 1;
        for (Classement c : classement) {
            tableModel.addRow(new Object[]{
                rang++,
                c.getEquipeNom(),
                c.getPoints(),
                c.getMatchsJoues(),
                c.getVictoires(),
                c.getNuls(),
                c.getDefaites(),
                c.getButsPour(),
                c.getButsContre(),
                c.getDifferenceButs() > 0 ? "+" + c.getDifferenceButs() : String.valueOf(c.getDifferenceButs())
            });
        }
        parent.setStatus("Classement mis à jour — " + classement.size() + " équipe(s).");
    }

    private JLabel legendeLabel(String texte, Color bg) {
        JLabel lbl = new JLabel("  " + texte + "  ");
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return lbl;
    }
}
