package algorithms; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder; // For padding

public class MainGui {
    private JFrame frame;
    private JLabel footerLabel;

    // --- NEW: Panels for CardLayout ---
    private JPanel cardContainer; // The main panel that holds the "cards"
    private CardLayout cardLayout; // The layout manager that swaps panels
    
    // --- Card 1: Home Screen ---
    private JPanel homePanel;
    private JButton btnPreemptivePriority;
    private JButton btnRoundRobin;
    private JButton btnCircularScan;
    
    // --- Card 2: Results Screen ---
    private JPanel resultsPanel;
    private JTextArea outputArea;
    private JScrollPane scrollPane;
    private JButton btnBack; // Button to return to home screen

    // Algorithm objects
    private OperatingSystemAlgorithm priorityAlg;
    private OperatingSystemAlgorithm rrAlg;
    private OperatingSystemAlgorithm cscanAlg;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainGui window = new MainGui();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public MainGui() {
        // Create algorithm objects
        priorityAlg = new PreemptivePriority();
        rrAlg = new RoundRobin();
        cscanAlg = new CircularScan();
        
        // Build the GUI
        initialize();
    }
    
    private void initialize() {
        
        // --- 1. SET UP THE MAIN WINDOW (JFrame) ---
        frame = new JFrame();
        frame.setTitle("OS Algorithm Showcase");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setLayout(new BorderLayout()); // Main layout
        frame.setLocationRelativeTo(null); 

        // --- 2. SET UP THE CARD CONTAINER ---
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        
        // --- 3. CREATE Card 1: Home Panel ---
        homePanel = new JPanel();
        // Use BoxLayout for a vertical stack
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        // Add padding around the whole panel
        homePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add "Welcome" title
        JLabel titleLabel = new JLabel("Welcome to the Operating System Algorithm Simulator");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center it

        // Add description labels
        JLabel descLabel1 = new JLabel("This app aims to simulate and calculate the values of Preemptive Priority, Round Robin,");
        descLabel1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel2 = new JLabel("and C Scan to demonstrate how it works.");
        descLabel2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel3 = new JLabel("You will simply provide the values and the application will simulate it for you!");
        descLabel3.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel4 = new JLabel("To get started, please select an Operating System Algorithm below.");
        descLabel4.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create buttons
        Dimension buttonSize = new Dimension(350, 40); // Set a fixed size
        
        btnPreemptivePriority = new JButton("Preemptive Priority");
        btnPreemptivePriority.setAlignmentX(Component.CENTER_ALIGNMENT); // Center it
        btnPreemptivePriority.setMaximumSize(buttonSize); // Apply fixed size
        
        btnRoundRobin = new JButton("Round Robin");
        btnRoundRobin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRoundRobin.setMaximumSize(buttonSize);

        btnCircularScan = new JButton("Circular Scan");
        btnCircularScan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCircularScan.setMaximumSize(buttonSize);

        // Add components to homePanel, with spacing
        homePanel.add(Box.createVerticalGlue()); // Push content to vertical center
        homePanel.add(titleLabel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 25))); // 25px space
        homePanel.add(descLabel1);
        homePanel.add(descLabel2);
        homePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        homePanel.add(descLabel3);
        homePanel.add(Box.createRigidArea(new Dimension(0, 25)));
        homePanel.add(descLabel4);
        homePanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        homePanel.add(btnPreemptivePriority);
        homePanel.add(Box.createRigidArea(new Dimension(0, 10))); // 10px space
        homePanel.add(btnRoundRobin);
        homePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        homePanel.add(btnCircularScan);
        homePanel.add(Box.createVerticalGlue()); // Push content to vertical center

        // --- 4. CREATE Card 2: Results Panel ---
        resultsPanel = new JPanel(new BorderLayout(10, 10)); // Use BorderLayout
        resultsPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        outputArea.setEditable(false); 
        scrollPane = new JScrollPane(outputArea);
        resultsPanel.add(scrollPane, BorderLayout.CENTER); // Text area in the middle
        
        btnBack = new JButton("Back to Home");
        resultsPanel.add(btnBack, BorderLayout.SOUTH); // "Back" button at the bottom
        
        // --- 5. ADD CARDS to CardContainer ---
        cardContainer.add(homePanel, "HOME");    // Add home panel with name "HOME"
        cardContainer.add(resultsPanel, "RESULTS"); // Add results panel with name "RESULTS"
        
        // --- 6. SET UP THE FOOTER ---
        footerLabel = new JLabel("Laraze, Santos, Teodoro | 3ITC", SwingConstants.CENTER);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // --- 7. ADD Main Panels to Frame ---
        frame.add(cardContainer, BorderLayout.CENTER); // Card swapper in the middle
        frame.add(footerLabel, BorderLayout.SOUTH);   // Footer at the bottom
        
        // --- 8. HOOK UP ALL BUTTONS ---
        
        // Home Panel Buttons
        btnPreemptivePriority.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(priorityAlg);
            }
        });
        
        btnRoundRobin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(rrAlg);
            }
        });
        
        btnCircularScan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(cscanAlg);
            }
        });
        
        // Results Panel "Back" Button
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Show the "HOME" card
                cardLayout.show(cardContainer, "HOME");
            }
        });
    }

    // --- MODIFIED runAlgorithm Method ---
    private void runAlgorithm(OperatingSystemAlgorithm alg) {
        String instructions = alg.getInstructions();
        int choice = JOptionPane.showConfirmDialog(
            frame,                       
            instructions,                
            "Algorithm Confirmation",    
            JOptionPane.YES_NO_OPTION,   
            JOptionPane.QUESTION_MESSAGE 
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // 1. Run the algorithm (it will fill outputArea)
            alg.run(outputArea); 
            
            // 2. SWAP to the "RESULTS" card
            cardLayout.show(cardContainer, "RESULTS");
        } else {
            // User clicked NO, so we just stay on the home screen.
            // No need to do anything.
        }
    }
}