package burp;

import burp.data.RceData;
import burp.utils.Payload;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class JPayloadTable extends AbstractTableModel {
    private int location = 0;
    public static String currentPayload = "";
    public static class PayloadTable extends JTable{
        public PayloadTable(JPayloadTable payloadTable){
            super(payloadTable);
        }

        @Override
        public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
            String select = Payload.payloads.get(rowIndex);
            currentPayload = select;
            super.changeSelection(rowIndex, columnIndex, toggle, extend);
        }
    }
    @Override
    public int getRowCount() {
        return Payload.payloads.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex)
        {
            case 0:
                return Payload.payloads.get(rowIndex);
            default:
                return "";
        }
    }
    @Override
    public String getColumnName(int column) {
        switch (column){
            case 0:
                return "payload";
            default:
                return "";
        }
    }
}
