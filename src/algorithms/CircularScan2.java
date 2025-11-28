package algorithms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class CScan implements OperatingSystemAlgorithm {

    private JDialog inputDialog;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField headField;
    private JComboBox<String> dirBox;
    private JButton submitButton;

    @Override
    public String getInstructions() {
        return "<html>"
                + "<b>C-SCAN Disk Scheduling</b><br><br>"
                + "1. Enter the number of queue items (2–12).<br>"
                + "2. Fill in the request values (0–999).<br>"
                + "3. Enter the starting head position (0–999).<br>"
                + "4. Select the direction (Right or Left).<br>"
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

        // ---------------- TOP PANEL ----------------
        JPanel top = new JPanel(new FlowLayout());
        top.add(new JLabel("Number of Queue Items:"));
        JTextField countField = new JTextField(5);
        top.add(countField);

        JButton setBtn = new JButton("Set");
        top.add(setBtn);
        inputDialog.add(top, BorderLayout.NORTH);

        // ---------------- TABLE ----------------
        tableModel = new DefaultTableModel(new String[]{"Request #", "Value"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        inputDialog.add(scrollPane, BorderLayout.CENTER);

        // ---------------- BOTTOM PANEL ----------------
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(new JLabel("Head Position:"));
        headField = new JTextField(5);
        bottom.add(headField);

        bottom.add(new JLabel("Direction:"));
        dirBox = new JComboBox<>(new String[]{"Right", "Left"});
        bottom.add(dirBox);

        submitButton = new JButton("Run C-SCAN");
        bottom.add(submitButton);
        inputDialog.add(bottom, BorderLayout.SOUTH);

        // ---------------- ACTIONS ----------------
        setBtn.addActionListener((ActionEvent e) -> {
            try {
                int n = Integer.parseInt(countField.getText().trim());
                if (n < 2 || n > 12) {
                    JOptionPane.showMessageDialog(inputDialog,
                            "Queue items must be between 2 and 12",
                            "Invalid Input",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                tableModel.setRowCount(0);
                for (int i = 0; i < n; i++)
                    tableModel.addRow(new Object[]{"Request " + (i + 1), ""});

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(inputDialog,
                        "Enter a valid number of queue items!");
            }
        });

        submitButton.addActionListener((ActionEvent e) -> {
            try {
                if (table.isEditing()) table.getCellEditor().stopCellEditing();

                List<Integer> req = new ArrayList<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Object valObj = tableModel.getValueAt(i, 1);
                    if (valObj == null || valObj.toString().trim().isEmpty())
                        throw new NumberFormatException();

                    int val = Integer.parseInt(valObj.toString().trim());
                    if (val < 0 || val > 999)
                        throw new IllegalArgumentException("Request " + (i + 1) + " out of range (0–999).");
                    req.add(val);
                }

                int head = Integer.parseInt(headField.getText().trim());
                if (head < 0 || head > 999)
                    throw new IllegalArgumentException("Head position must be 0–999.");

                String direction = dirBox.getSelectedItem().toString();

                // Run the C-SCAN algorithm
                List<Integer> seq = CScanAlgorithm.run(req, head, direction);
                int totalSeek = CScanAlgorithm.computeSeekTime(seq);

                // Show result graph
                JFrame graphFrame = new JFrame("C-SCAN Graph");
                graphFrame.add(new CScanGraph(seq, totalSeek));
                graphFrame.pack();
                graphFrame.setLocationRelativeTo(null);
                graphFrame.setVisible(true);

                inputDialog.dispose();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(inputDialog,
                        ex.getMessage(),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(inputDialog,
                        "Please enter valid numbers in all fields!",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        inputDialog.setVisible(true);
    }
}

    //  C-SCAN ALGORITHM
    public static class CScanAlgorithm {

        public static List<Integer> run(List<Integer> req, int head, String direction) {

            int diskMax = 999;

            List<Integer> left = new ArrayList<>();
            List<Integer> right = new ArrayList<>();

            for (int r : req) {
                if (r < head)
                    left.add(r);
                else
                    right.add(r);
            }

            left.sort(Integer::compareTo);
            right.sort(Integer::compareTo);

            List<Integer> seq = new ArrayList<>();
            seq.add(head);

            boolean moveRight = direction.startsWith("Right");

            // go to 999
            if(moveRight){
                seq.addAll(right);
                if (seq.get(seq.size() - 1) != diskMax){
                    seq.add(diskMax);
                }
                seq.add(0);

                seq.addAll(left);
            //go to 0    
            }else{
                for (int i = left.size() - 1; i >= 0; i--)
                    seq.add(left.get(i));

                if (seq.get(seq.size() - 1) != 0)
                    seq.add(0);

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



    // GRAPH PANEL
    public static class CScanGraph extends JPanel {

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

            int leftX = 50;
            int rightX = getWidth() - 50;
            int yAxis = 80;

            // line
            g2.setColor(Color.BLACK);
            g2.drawLine(leftX, yAxis, rightX, yAxis);

            g2.setStroke(new BasicStroke(1));

            // ticks
            int[] ticks = {0, 50, 100, 150, 200, 250, 300, 350, 400, 450,500,550, 600, 650, 700, 750, 800, 850, 900, 950, 999};
            for (int t : ticks) {
                int x = map(t, 0, 999, leftX, rightX);
                g2.drawLine(x, yAxis - 5, x, yAxis + 5);
                g2.drawString(String.valueOf(t), x - 3, yAxis - 25);
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
                drawArrow(g2, x1, y1, x2, y2);
            }

            // Footer background box
            int footerY = getHeight() - 70;
            g2.setColor(new Color(230, 230, 230));
            g2.fillRect(0, footerY, getWidth(), 70);

            // Divider line
            g2.setColor(new Color(50, 50, 50));
            g2.drawLine(leftX, yAxis, rightX, yAxis);

            // Total seek text
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("Total Head Movement: " + totalSeek, 20, footerY + 40);
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






