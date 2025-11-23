package algorithms;

import javax.swing.*;
import java.awt.*;

public class PPGanttChartPanel extends JPanel {
    // CHANGED: Array []
    private PPGanttChart[] ganttChart;
    private int totalTime;

    public PPGanttChartPanel(PPGanttChart[] ganttChart) {
        this.ganttChart = ganttChart;
        
        // LOGIC to calculate totalTime using Array
        this.totalTime = 0;
        // We loop to find the last non-null item
        for(int i = 0; i < ganttChart.length; i++) {
            if (ganttChart[i] != null) {
                // This will update until the very last valid block
                this.totalTime = ganttChart[i].endTime;
            } else {
                break; // Stop when we hit nulls
            }
        }
        
        this.setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth() - 60;
        int h = 50;
        int x = 30;
        int y = getHeight() / 2 - 25; 

        // CHANGED: Loop through Array
        for (int i = 0; i < ganttChart.length; i++) {
            PPGanttChart r = ganttChart[i];
            
            // STUDENT CHECK: If the slot is empty, stop drawing
            if (r == null) break;

            int duration = r.endTime - r.startTime;
            if (duration <= 0) continue;

            int boxX = x + (int) ((double) r.startTime / totalTime * w);
            int boxW = (int) ((double) duration / totalTime * w);
            
            if (r.pid == -1) g2.setColor(Color.LIGHT_GRAY);
            else g2.setColor(Color.getHSBColor((r.pid * 0.618f) % 1, 0.6f, 0.9f)); 
            
            g2.fillRect(boxX, y, boxW, h);
            g2.setColor(Color.BLACK);
            g2.drawRect(boxX, y, boxW, h);
            
            String lbl = (r.pid == -1) ? "Idle" : "P" + r.pid;
            if (boxW > 20) {
                g2.setColor(Color.BLACK);
                g2.drawString(lbl, boxX + boxW / 2 - 5, y + 30);
            }
            g2.drawString(String.valueOf(r.startTime), boxX, y + h + 15);
        }
        g2.drawString(String.valueOf(totalTime), x + w, y + h + 15);
    }
}