package algorithms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class CircularScan2 implements OperatingSystemAlgorithm {

    private JDialog inputDialog;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton submitButton;

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
        inputDialog = new JDialog();
        inputDialog.setTitle("C-SCAN Input");
        inputDialog.setModal(true);
        inputDialog.setSize(550, 400);
        inputDialog.setLocationRelativeTo(null);
        inputDialog.setLayout(new BorderLayout());

        // Top Panel 
        JPanel top = new JPanel(new FlowLayout());
        top.add(new JLabel("Number of Requests:"));
        JTextField countField = new JTextField(5);
        top.add(countField);
        JButton setBtn = new JButton("Set");
        top.add(setBtn);
        inputDialog.add(top, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Location #", "Value"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        inputDialog.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(new JLabel("Head Position:"));
        JTextField headField = new JTextField(5);
        bottom.add(headField);

        bottom.add(new JLabel("Direction:"));
        JComboBox<String> dirBox = new JComboBox<>(new String[]{"Right", "Left"});
        bottom.add(dirBox);

        submitButton = new JButton("Run C-SCAN");
        bottom.add(submitButton);
        inputDialog.add(bottom, BorderLayout.SOUTH);

        // Actions
        setBtn.addActionListener((ActionEvent e) -> {
            try {
                int n = Integer.parseInt(countField.getText().trim());
                if (n < 2 || n > 12) {
                    JOptionPane.showMessageDialog(inputDialog,
                            "Number of requests must be between 2 and 12",
                            "Invalid Input",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                tableModel.setRowCount(0);
                for (int i = 0; i < n; i++) {
                    tableModel.addRow(new Object[]{"Request " + (i + 1), ""});
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(inputDialog,
                        "Enter a valid number of requests!");
            }
        });

        submitButton.addActionListener((ActionEvent e) -> {
            try {
                if (table.isEditing()) table.getCellEditor().stopCellEditing();

                List<Integer> requests = new ArrayList<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    int val = Integer.parseInt(tableModel.getValueAt(i, 1).toString().trim());
                    if (val < 0 || val > 999)
                        throw new IllegalArgumentException("Location " + (i + 1) + " out of range (0–999).");
                    requests.add(val);
                }

                int head = Integer.parseInt(headField.getText().trim());
                if (head < 0 || head > 999)
                    throw new IllegalArgumentException("Head position must be 0–999.");

                String direction = dirBox.getSelectedItem().toString();

                //Run C-Scan 
                List<Integer> seq = CScanAlgorithm.run(requests, head, direction);
                int totalSeek = CScanAlgorithm.computeSeekTime(seq);

                //Show Graph 
                JFrame graphFrame = new JFrame("C-SCAN Graph");
                graphFrame.add(new CScanGraph(seq, totalSeek));
                graphFrame.pack();
                graphFrame.setLocationRelativeTo(null);
                graphFrame.setVisible(true);

                inputDialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(inputDialog,
                        ex.getMessage(),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        inputDialog.setVisible(true);
    }

    //C-Scan Algorithm 
    static class CScanAlgorithm {
        public static List<Integer> run(List<Integer> req, int head, String direction) {
            int diskMax = 999;

            List<Integer> left = new ArrayList<>();
            List<Integer> right = new ArrayList<>();

            for (int r : req) {
                if (r < head) left.add(r);
                else right.add(r);
            }
            left.sort(Integer::compareTo);
            right.sort(Integer::compareTo);

            List<Integer> seq = new ArrayList<>();
            seq.add(head);

            if (direction.startsWith("Right")) {
                seq.addAll(right);
                if (seq.get(seq.size() - 1) != diskMax) seq.add(diskMax);
                seq.add(0);
                seq.addAll(left);
            } else {
                for (int i = left.size() - 1; i >= 0; i--) seq.add(left.get(i));
                if (seq.get(seq.size() - 1) != 0) seq.add(0);
                seq.add(diskMax);
                seq.addAll(right);
            }
            return seq;
        }

        public static int computeSeekTime(List<Integer> seq) {
            int sum = 0;
            for (int i = 0; i < seq.size() - 1; i++) {
                sum += Math.abs(seq.get(i + 1) - seq.get(i));
            }
            return sum;
        }
    }

    //Graph Panel 
    static class CScanGraph extends JPanel {
        private final List<Integer> seq;
        private final int totalSeek;

        public CScanGraph(List<Integer> seq, int totalSeek) {
            this.seq = seq;
            this.totalSeek = totalSeek;
            setPreferredSize(new Dimension(1000, 700));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int leftX = 50, rightX = getWidth() - 50, yAxis = 80;

            // horizontal line
            g2.setColor(Color.BLACK);
            g2.drawLine(leftX, yAxis, rightX, yAxis);

            // ticks
            for (int t = 0; t <= 999; t += 50) {
                int x = map(t, 0, 999, leftX, rightX);
                g2.drawLine(x, yAxis - 5, x, yAxis + 5);
                g2.drawString(String.valueOf(t), x - 5, yAxis - 25);
            }

            // seek lines
            g2.setColor(Color.RED.darker());
            g2.setStroke(new BasicStroke(2));
            for (int i = 0; i < seq.size() - 1; i++) {
                int x1 = map(seq.get(i), 0, 999, leftX, rightX);
                int x2 = map(seq.get(i + 1), 0, 999, leftX, rightX);
                int y1 = yAxis + 40 + i * 20;
                int y2 = yAxis + 40 + (i + 1) * 20;
                g2.drawLine(x1, y1, x2, y2);
            }

            // total seek
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("Total Head Movement: " + totalSeek, 20, getHeight() - 40);
        }

        private int map(int value, int minIn, int maxIn, int minOut, int maxOut) {
            return (value - minIn) * (maxOut - minOut) / (maxIn - minIn) + minOut;
        }
    }
}







