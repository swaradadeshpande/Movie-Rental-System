import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MovieRentalGUI extends JFrame {

    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    // --- FINAL COLOR PALETTE ---
    private final Color SIDEBAR_BG      = new Color(33, 37, 41);    // Dark Gray/Black
    private final Color ACCENT_BLUE     = new Color(13, 110, 253);   // Match the Search Button Blue
    private final Color BUTTON_TEXT     = Color.WHITE;               
    private final Color HEADER_BG       = new Color(52, 58, 64);     
    
    // Row Colors
    private final Color ROW_AVAILABLE   = new Color(240, 252, 240);  
    private final Color ROW_RENTED      = new Color(255, 242, 242);  
    private final Color TEXT_AVAILABLE  = new Color(20, 100, 20);    
    private final Color TEXT_RENTED     = new Color(160, 0, 0);      

    public MovieRentalGUI() {
        // Ensure backend data is loaded
        MovieRentalSystem.initDataFiles();
        MovieRentalSystem.loadData();

        setTitle("Movie Rental System v4.3");
        setSize(1150, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Sidebar
        add(createSidebar(), BorderLayout.WEST);

        // 2. Main Content
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        mainContent.add(createDashboardHeader(), BorderLayout.NORTH);
        mainContent.add(createTableSection(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
        refreshTable(""); 
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        JLabel logo = new JLabel("<html><body style='text-align:center; padding: 25px 0;'>"
                + "<p style='color:white; font-family:sans-serif; font-size:22px; font-weight:bold;'>🎬 MOVIE</p>"
                + "<p style='color:#0dcaf0; font-family:sans-serif; font-size:12px; font-weight:bold;'>MANAGEMENT</p>"
                + "</body></html>");
        sidebar.add(logo);

        // Sidebar Buttons now use the ACCENT_BLUE
        sidebar.add(createMenuButton("Rent Movie", e -> handleRent()));
        sidebar.add(createMenuButton("Return Movie", e -> handleReturn()));
        sidebar.add(createMenuButton("History Logs", e -> handleHistory()));
        sidebar.add(createMenuButton("Refresh Data", e -> refreshTable("")));
        
        return sidebar;
    }

    private JButton createMenuButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 48));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(ACCENT_BLUE); 
        btn.setForeground(BUTTON_TEXT); 
        btn.setFocusPainted(false);
        btn.setOpaque(true); 
        btn.setBorderPainted(false);
        btn.addActionListener(action);
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(ACCENT_BLUE.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(ACCENT_BLUE); }
        });
        return btn;
    }

    private JPanel createDashboardHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Available Movies ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(SIDEBAR_BG);

        JPanel searchBox = new JPanel(new BorderLayout(10, 0));
        searchBox.setOpaque(false);
        
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1), new EmptyBorder(5, 12, 5, 12)));
        
        JButton sBtn = new JButton("Search");
        sBtn.setPreferredSize(new Dimension(100, 40));
        sBtn.setBackground(ACCENT_BLUE); // Now all buttons are color-synced
        sBtn.setForeground(Color.WHITE);
        sBtn.setOpaque(true);
        sBtn.setBorderPainted(false);
        sBtn.addActionListener(e -> refreshTable(searchField.getText()));

        searchBox.add(searchField, BorderLayout.CENTER);
        searchBox.add(sBtn, BorderLayout.EAST);

        header.add(title, BorderLayout.WEST);
        header.add(searchBox, BorderLayout.EAST);
        return header;
    }

    private JScrollPane createTableSection() {
        String[] cols = {"ID", "Movie Title", "Genre", "Director", "Year", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        movieTable = new JTable(tableModel);
        movieTable.setRowHeight(48);
        movieTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        movieTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean f, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, isSelected, f, row, col);
                Object statusObj = t.getModel().getValueAt(row, 5);
                String status = (statusObj != null) ? statusObj.toString() : "Available";

                if (isSelected) {
                    c.setBackground(ACCENT_BLUE);
                    c.setForeground(Color.WHITE);
                } else {
                    if (status.contains("Available")) {
                        c.setBackground(ROW_AVAILABLE);
                        c.setForeground(TEXT_AVAILABLE);
                    } else {
                        c.setBackground(ROW_RENTED);
                        c.setForeground(TEXT_RENTED);
                    }
                }
                
                if (col == 5) setHorizontalAlignment(SwingConstants.CENTER);
                else setHorizontalAlignment(SwingConstants.LEFT);
                
                return c;
            }
        });

        // Column Headers visibility fix
        JTableHeader header = movieTable.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 50));

        JScrollPane scroll = new JScrollPane(movieTable);
        scroll.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        return scroll;
    }

    // --- CONNECTED BACKEND METHODS ---

    private void refreshTable(String query) {
        tableModel.setRowCount(0);
        for (Movie m : MovieRentalSystem.movies.values()) {
            if (m.getTitle().toLowerCase().contains(query.toLowerCase())) {
                tableModel.addRow(new Object[]{
                    m.getId(), m.getTitle(), m.getGenre(), m.getDirector(), m.getYear(),
                    m.isAvailable() ? "● Available" : "○ Rented"
                });
            }
        }
    }

    private void handleRent() {
        int row = movieTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a movie first!"); return; }

        String id = tableModel.getValueAt(row, 0).toString();
        Movie m = MovieRentalSystem.movies.get(id);

        if (!m.isAvailable()) { JOptionPane.showMessageDialog(this, "Movie already rented."); return; }

        String customer = JOptionPane.showInputDialog(this, "Enter Customer Name:");
        if (customer != null && !customer.trim().isEmpty()) {
            m.setAvailable(false);
            MovieRentalSystem.rentals.add(new Rental("T"+(System.currentTimeMillis()%1000), customer, id, m.getTitle(), MovieRentalSystem.today()));
            MovieRentalSystem.saveData();
            refreshTable("");
            JOptionPane.showMessageDialog(this, "Rented successfully!");
        }
    }

    private void handleReturn() {
        String name = JOptionPane.showInputDialog(this, "Enter Customer Name:");
        if (name == null || name.trim().isEmpty()) return;

        // Filter for ONLY rented items for this specific user
        List<Rental> activeRentals = MovieRentalSystem.rentals.stream()
                .filter(r -> r.getUser().equalsIgnoreCase(name) && r.getStatus().equals("RENTED"))
                .collect(Collectors.toList());

        if (activeRentals.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active rentals found for: " + name);
            return;
        }

        // --- RESTORED SELECTION MENU ---
        String[] options = activeRentals.stream()
                .map(r -> r.getTxnId() + ": " + r.getMovieTitle())
                .toArray(String[]::new);

        String selection = (String) JOptionPane.showInputDialog(
                this, 
                "Select which movie " + name + " is returning:", 
                "Return Movie Selection", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                options, 
                options[0]
        );

        if (selection != null) {
            String selectedTxnId = selection.split(":")[0];
            Rental rentalToReturn = activeRentals.stream()
                    .filter(r -> r.getTxnId().equals(selectedTxnId))
                    .findFirst().get();

            rentalToReturn.setStatus("RETURNED");
            rentalToReturn.setReturnDate(MovieRentalSystem.today());
            MovieRentalSystem.movies.get(rentalToReturn.getMovieId()).setAvailable(true);
            
            MovieRentalSystem.saveData();
            refreshTable("");
            JOptionPane.showMessageDialog(this, "Movie returned successfully!");
        }
    }

    private void handleHistory() {
        String[] columns = {"TxnID", "User", "Movie", "Date", "Status"};
        DefaultTableModel histModel = new DefaultTableModel(columns, 0);
        for (Rental r : MovieRentalSystem.rentals) {
            histModel.addRow(new Object[]{r.getTxnId(), r.getUser(), r.getMovieTitle(), r.getRentDate(), r.getStatus()});
        }
        JTable histTable = new JTable(histModel);
        JOptionPane.showMessageDialog(this, new JScrollPane(histTable), "Full History Logs", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MovieRentalGUI().setVisible(true));
    }
}
