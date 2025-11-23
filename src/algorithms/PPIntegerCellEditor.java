package algorithms;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PPIntegerCellEditor extends DefaultCellEditor {
    JFormattedTextField ftf;

    public PPIntegerCellEditor(NumberFormatter formatter) {
        super(new JFormattedTextField(formatter));
        ftf = (JFormattedTextField) getComponent();
        ftf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { 
                SwingUtilities.invokeLater(() -> ftf.selectAll()); 
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
        JFormattedTextField f = (JFormattedTextField) super.getTableCellEditorComponent(t, v, s, r, c);
        f.setValue(v);
        return f;
    }

    @Override
    public boolean stopCellEditing() {
        try { 
            ftf.commitEdit(); 
        } catch (Exception e) { 
            return false; 
        }
        return super.stopCellEditing();
    }
}