package algorithms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class CircularScan2 implements OperatingSystemAlgorithm {

    private JDialog inputDialog;
    private JTable requestTable;
    private DefaultTableModel tableModel;

    @Override
    public String getInstructions() {
        return "<html>"
                + "<b>C-SCAN Disk Scheduling (Preemptive Priority GUI Style)</b><br><br>"
                + "1. Enter the number of requests (2–12).<br>"
                + "2. Fill in request values (0–999).<br>"
                + "3. Enter starting head position (0–999).<br>"
                + "4. Select direction (Right or Left).<br>"
                + "5. Click 'Run C-SCAN' to see the result and graph.<br><br>"
                + "<b>Do you want to continue?</b>"
                + "</html>";
    }

    @Override
    public void run() {
        createInputDialog();
    }

    //Input
    private void createInputDialog() {
        inputDialog = new JDialog();
        inputDialog.setTitle("C-SCAN Input");
        inputDialog.setModal(true);
        inputDialog.setSize(550, 400);
        inputDialog.setLocationRelativeTo(null);
        inputDialog.setLayout(new BorderLayout());

        // requests
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Number of Requests:"));
        JTextField requestCountField = new JTextField(5);
        topPanel.add(requestCountField);

        JButton setRequestCountBtn = new JButton("Set");
        topPanel.add(setRequestCountBtn);
        inputDialog.add(topPanel, BorderLayout.NORTH);

        // requests table
        tableModel = new DefaultTableModel(new String[]{"Request #", "Value"}, 0);
        requestTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(requestTable);
        inputDialog.add(tableScroll, BorderLayout.CENTER);

        //Head position and direction
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(new JLabel("Head Position:"));
        JTextField headField = new JTextField(5);
        bottomPanel.add(headField);

        bottomPanel.add(new JLabel("Direction:"));
        JComboBox<String> directionBox = new JComboBox<>(new String[]{"Right", "Left"});
        bottomPanel.add(directionBox);

        JButton runButton = new JButton("Run C-SCAN");
        bottomPanel.add(runButton);
        inputDialog.add(bottomPanel, BorderLayout.SOUTH);

        //Buttons
        setRequestCountBtn.addActionListener(e -> populateTable(requestCountField));
        runButton.addActionListener(e -> processInput(headField, directionBox));

        inputDialog.setVisible(true);
    }

    private void populateTable(JTextField requestCountField) {
        try {
            int count = Integer.parseInt(requestCountField.getText().trim());
            if (count < 2 || count > 12) {
                JOptionPane.showMessageDialog(inputDialog,
                        "Number of requests must be between 2 and 12",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            tableModel.setRowCount(0);
            for (int i = 0; i < count; i++) {
                tableModel.addRow(new Object[]{"Request " + (i + 1), ""});
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(inputDialog,
                    "Enter a valid number of requests!");
        }
    }

    private void processInput(JTextField headField, JComboBox<String> directionBox) {
        try {
            if (requestTable.isEditing()) requestTable.getCellEditor().stopCellEditing();

            List<Integer> requests = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int value = Integer.parseInt(tableModel.getValueAt(i, 1).toString().trim());
                if (value < 0 || value > 999)
                    throw new IllegalArgumentException("Request " + (i + 1) + " must be 0–999.");
                requests.add(value);
            }

            int headPosition = Integer.parseInt(headField.getText().trim());
            if (headPosition < 0 || headPosition > 999)
                throw new IllegalArgumentException("Head position must be 0–999.");

            String direction = directionBox.getSelectedItem().toString();

            // Run c-scan
            List<Integer> sequence = CScanAlgorithm.run(requests, headPosition, direction);
            int totalSeek = CScanAlgorithm.computeSeekTime(sequence);

            // Show graph
            showGraph(sequence, totalSeek);

            inputDialog.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(inputDialog,
                    ex.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // C-scan Algo
    public static class CScanAlgorithm {

        public static List<Integer> run(List<Integer> requests, int head, String direction) {
            int diskMax = 999;
            List<Integer> left = new ArrayList<>();
            List<Integer> right = new ArrayList<>();

            for (int r : requests) {
                if (r < head) left.add(r);
                else right.add(r);
            }

            left.sort(Integer::compareTo);
            right.sort(Integer::compareTo);

            List<Integer> sequence = new ArrayList<>();
            sequence.add(head);

            if (direction.startsWith("Right")) {
                sequence.addAll(right);
                if (!right.isEmpty() && sequence.get(sequence.size() - 1) != diskMax) sequence.add(diskMax);
                sequence.add(0);
                sequence.addAll(left);
            } else {
                for (int i = left.size() - 1; i >= 0; i--) sequence.add(left.get(i));
                if (!left.isEmpty() && sequence.get(sequence.size() - 1) != 0) sequence.add(0);
                sequence.add(diskMax);
                for (int i = right.size() - 1; i >= 0; i--) sequence.add(right.get(i));
            }

            return sequence;
        }

        public static int computeSeekTime(List<Integer> sequence) {
            int total = 0;
            for (int i = 0; i < sequence.size() - 1; i++) {
                total += Math.abs(sequence.get(i + 1) - sequence.get(i));
            }
            return total;
        }
    }

    //Graph part
    private void showGraph(List<Integer> sequence, int totalSeek) {
        JFrame frame = new JFrame("C-SCAN Graph");
        frame.setLayout(new BorderLayout());

        // Graph panel
        CScanGraph graphPanel = new CScanGraph(sequence, totalSeek);
        frame.add(graphPanel, BorderLayout.CENTER);

        // Footer panel
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(230, 230, 230));
        footer.setPreferredSize(new Dimension(graphPanel.getWidth(), 50));

        JLabel totalLabel = new JLabel("Total Head Movement: " + totalSeek);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setHorizontalAlignment(SwingConstants.LEFT);
        footer.add(totalLabel, BorderLayout.WEST);

        JButton backButton = new JButton("Back to Home");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.addActionListener(e -> frame.dispose());
        footer.add(backButton, BorderLayout.EAST);

        frame.add(footer, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static class CScanGraph extends JPanel {

        private final List<Integer> sequence;
        private final int totalSeek;

        public CScanGraph(List<Integer> sequence, int totalSeek) {
            this.sequence = sequence;
            this.totalSeek = totalSeek;
            setPreferredSize(new Dimension(1000, 700));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int leftMargin = 50, rightMargin = getWidth() - 50, baseY = 80;

            // Horizontal line
            g2.setColor(Color.BLACK);
            g2.drawLine(leftMargin, baseY, rightMargin, baseY);

            // Ticks and labels
            for (int t = 0; t <= 999; t += 50) {
                int x = map(t, 0, 999, leftMargin, rightMargin);
                g2.drawLine(x, baseY - 5, x, baseY + 5);
                g2.drawString(String.valueOf(t), x - 5, baseY - 25);
            }

            // Seek lines with arrows
            g2.setColor(Color.RED.darker());
            g2.setStroke(new BasicStroke(2));
            for (int i = 0; i < sequence.size() - 1; i++) {
                int x1 = map(sequence.get(i), 0, 999, leftMargin, rightMargin);
                int x2 = map(sequence.get(i + 1), 0, 999, leftMargin, rightMargin);
                int y1 = baseY + 40 + i * 20;
                int y2 = baseY + 40 + (i + 1) * 20;
                g2.drawLine(x1, y1, x2, y2);
                drawArrow(g2, x1, y1, x2, y2);
            }
        }

        private int map(int value, int minIn, int maxIn, int minOut, int maxOut) {
            return (value - minIn) * (maxOut - minOut) / (maxIn - minIn) + minOut;
        }

        private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int len = 10;

            int ax1 = (int) (x2 - len * Math.cos(angle - Math.PI / 6));
            int ay1 = (int) (y2 - len * Math.sin(angle - Math.PI / 6));
            g2.drawLine(x2, y2, ax1, ay1);

            int ax2 = (int) (x2 - len * Math.cos(angle + Math.PI / 6));
            int ay2 = (int) (y2 - len * Math.sin(angle + Math.PI / 6));
            g2.drawLine(x2, y2, ax2, ay2);
        }
    }
}




    



    






