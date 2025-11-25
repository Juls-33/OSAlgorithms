package algorithms;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CircularScan {

    // ------------------------- GUI PANEL -------------------------
    public static class CScanGraph  extends JPanel {

        private final List<Integer> seq;

        public CScanGraph (List<Integer> seq) {
            this.seq = seq;
            setPreferredSize(new Dimension(900, 350));
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

            // Main horizontal axis
            g2.setColor(Color.BLACK);
            g2.drawLine(leftX, yAxis, rightX, yAxis);

            // Dashed vertical lines at 0 and 199
            float[] dashPattern = {6, 6};
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));

            g2.drawLine(leftX, yAxis - 20, leftX, getHeight() - 20);
            g2.drawLine(rightX, yAxis - 20, rightX, getHeight() - 20);

            g2.setStroke(new BasicStroke(1));

            // Tick marks
            int[] ticks = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 199};
            for (int t : ticks) {
                int x = map(t, 0, 199, leftX, rightX);
                g2.drawLine(x, yAxis - 5, x, yAxis + 5);
                g2.drawString(String.valueOf(t), x - 10, yAxis - 10);
            }

            // Plot seek sequence
            g2.setColor(Color.RED.darker());
            g2.setStroke(new BasicStroke(2));

            for (int i = 0; i < seq.size() - 1; i++) {
                int x1 = map(seq.get(i), 0, 199, leftX, rightX);
                int x2 = map(seq.get(i + 1), 0, 199, leftX, rightX);

                int y1 = yAxis + 40 + i * 20;
                int y2 = yAxis + 40 + (i + 1) * 20;

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

            int x = (int) (x2 - len * Math.cos(angle - Math.PI / 6));
            int y = (int) (y2 - len * Math.sin(angle - Math.PI / 6));
            g2.drawLine(x2, y2, x, y);

            x = (int) (x2 - len * Math.cos(angle + Math.PI / 6));
            y = (int) (y2 - len * Math.sin(angle + Math.PI / 6));
            g2.drawLine(x2, y2, x, y);
        }
    }


    // ------------------------- MAIN PROGRAM -------------------------
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        System.out.print("How many queue items? ");
        int n = Integer.parseInt(scan.nextLine());

        int[] req = new int[n];

        System.out.println("Enter each queue item:");
        for (int i = 0; i < n; i++) {
            System.out.print("Request " + (i + 1) + ": ");
            req[i] = Integer.parseInt(scan.nextLine());
        }

        System.out.print("Enter head position: ");
        int head = Integer.parseInt(scan.nextLine());

        int diskMax = 199;

        // Separate left and right
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

        int totalSeek = 0;

        // 1. Move right
        for (int r : right) {
            totalSeek += Math.abs(head - r);
            head = r;
            seq.add(head);
        }

        // 2. Go to 199
        if (head != diskMax) {
            totalSeek += Math.abs(diskMax - head);
            head = diskMax;
            seq.add(head);
        }

        // 3. Wrap 199 → 0
        totalSeek += diskMax;
        head = 0;
        seq.add(0);

        // 4. Move left
        for (int r : left) {
            totalSeek += Math.abs(head - r);
            head = r;
            seq.add(head);
        }

        System.out.println("\nTotal Seek Time = " + totalSeek);

        // GUI Window
        JFrame f = new JFrame("C-SCAN Graph");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new CScanGraph (seq));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}




