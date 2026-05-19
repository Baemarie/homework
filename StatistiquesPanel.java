package view;

import dao.ClassementDAO;
import dao.EquipeDAO;
import dao.JoueurDAO;
import dao.MatchDAO;
import model.Classement;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class StatistiquesPanel extends JPanel {

    private MainFrame parent;
    private MatchDAO matchDAO;
    private JoueurDAO joueurDAO;
    private EquipeDAO equipeDAO;
    private ClassementDAO classementDAO;

    // Cartes stats globales
    private JLabel lblTotalMatchs, lblTotalButs, lblMoyenneButs, lblTotalEquipes, lblTotalJoueurs;

    // Table meilleurs buteurs
    private JTable tableBut;
    private DefaultTableModel modelBut;

    // Table meilleures attaques
    private JTable tableAttaque;
    private DefaultTableModel modelAttaque;

    public StatistiquesPanel(MainFrame parent) {
        this.parent = parent;
        this.matchDAO = new MatchDAO();
        this.joueurDAO = new JoueurDAO();
        this.equipeDAO = new EquipeDAO();
        this.classementDAO = new ClassementDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titre = new JLabel("📊 Statistiques du Championnat");
        titre.setFont(new Font("Arial", Font.BOLD, 18));
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // === Cartes stats globales ===
        JPanel cartesPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        lblTotalMatchs  = new JLabel("0", SwingConstants.CENTER);
        lblTotalButs    = new JLabel("0", SwingConstants.CENTER);
        lblMoyenneButs  = new JLabel("0.0", SwingConstants.CENTER);
        lblTotalEquipes = new JLabel("0", SwingConstants.CENTER);
        lblTotalJoueurs = new JLabel("0", SwingConstants.CENTER);

        cartesPanel.add(creerCarte("⚽ Matchs joués",   lblTotalMatchs,  new Color(70, 130, 180)));
        cartesPanel.add(creerCarte("🥅 Total buts",     lblTotalButs,    new Color(34, 139, 34)));
        cartesPanel.add(creerCarte("📈 Moy. buts/match",lblMoyenneButs,  new Color(218, 165, 32)));
        cartesPanel.add(creerCarte("🏟️ Équipes",        lblTotalEquipes, new Color(128, 0, 128)));
        cartesPanel.add(creerCarte("👤 Joueurs",        lblTotalJoueurs, new Color(178, 34, 34)));

        // === Meilleurs buteurs ===
        modelBut = new DefaultTableModel(new String[]{"#", "Joueur", "Équipe", "Buts"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableBut = creerTable(modelBut);

        JPanel panelBut = new JPanel(new BorderLayout(5, 5));
        JLabel lblBut = new JLabel("🏅 Meilleurs Buteurs (Top 10)");
        lblBut.setFont(new Font("Arial", Font.BOLD, 14));
        panelBut.add(lblBut, BorderLayout.NORTH);
        panelBut.add(new JScrollPane(tableBut), BorderLayout.CENTER);

        // === Meilleures attaques ===
        modelAttaque = new DefaultTableModel(new String[]{"#", "Équipe", "BP", "BC", "Diff", "Pts"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableAttaque = creerTable(modelAttaque);

        JPanel panelAttaque = new JPanel(new BorderLayout(5, 5));
        JLabel lblAtt = new JLabel("⚡ Classement Offensif / Défensif");
        lblAtt.setFont(new Font("Arial", Font.BOLD, 14));
        panelAttaque.add(lblAtt, BorderLayout.NORTH);
        panelAttaque.add(new JScrollPane(tableAttaque), BorderLayout.CENTER);

        // Deux tables côte à côte
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelBut, panelAttaque);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);

        JButton btnRefresh = new JButton("🔄 Rafraîchir");
        btnRefresh.setBackground(new Color(105, 105, 105));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> charger());

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southPanel.add(btnRefresh);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(cartesPanel, BorderLayout.NORTH);
        centerPanel.add(splitPane, BorderLayout.CENTER);

        add(titre, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    public void charger() {
        // Stats globales
        int nbMatchs = matchDAO.getNombreMatchsJoues();
        int totalButs = matchDAO.getTotalButs();
        double moy = nbMatchs > 0 ? (double) totalButs / nbMatchs : 0;
        int nbEquipes = equipeDAO.compter();
        int nbJoueurs = joueurDAO.compter();

        lblTotalMatchs.setText(String.valueOf(nbMatchs));
        lblTotalButs.setText(String.valueOf(totalButs));
        lblMoyenneButs.setText(String.format("%.2f", moy));
        lblTotalEquipes.setText(String.valueOf(nbEquipes));
        lblTotalJoueurs.setText(String.valueOf(nbJoueurs));

        // Meilleurs buteurs
        modelBut.setRowCount(0);
        List<Object[]> buteurs = joueurDAO.getMeilleursButeurs(10);
        int rang = 1;
        for (Object[] b : buteurs) {
            modelBut.addRow(new Object[]{rang++, b[0], b[1], b[2]});
        }
        if (buteurs.isEmpty()) modelBut.addRow(new Object[]{"", "Aucun buteur enregistré", "", ""});

        // Classement offensif
        modelAttaque.setRowCount(0);
        List<Classement> classement = classementDAO.getClassement();
        // Trier par buts pour (meilleure attaque)
        classement.sort((a, b) -> Integer.compare(b.getButsPour(), a.getButsPour()));
        int r = 1;
        for (Classement c : classement) {
            String diff = c.getDifferenceButs() >= 0 ? "+" + c.getDifferenceButs() : String.valueOf(c.getDifferenceButs());
            modelAttaque.addRow(new Object[]{r++, c.getEquipeNom(), c.getButsPour(), c.getButsContre(), diff, c.getPoints()});
        }

        parent.setStatus("Statistiques chargées.");
    }

    private JPanel creerCarte(String titre, JLabel valeur, Color couleur) {
        JPanel carte = new JPanel(new BorderLayout());
        carte.setBackground(couleur);
        carte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitre = new JLabel(titre, SwingConstants.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 11));
        lblTitre.setForeground(Color.WHITE);

        valeur.setFont(new Font("Arial", Font.BOLD, 28));
        valeur.setForeground(Color.WHITE);
        valeur.setOpaque(false);

        carte.add(lblTitre, BorderLayout.NORTH);
        carte.add(valeur, BorderLayout.CENTER);
        return carte;
    }

    private JTable creerTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(26);
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        t.setFont(new Font("Arial", Font.PLAIN, 12));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 248, 255));
                return this;
            }
        });
        return t;
    }
}
