package burp;

import burp.data.RceData;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.List;

public class Table extends JTable {
    private BurpExtender extender;
    public Table(BurpExtender extender,TableModel tableModel){
        super(tableModel);
        this.extender = extender;
    }

    @Override
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        List<RceData> rs = extender.result;
        RceData rceData = rs.get(rowIndex);
        byte[] request = rceData.getRequestResponse().getRequest();
        extender.requestViewer.setMessage(request, true);
        extender.responseViewer.setMessage(rceData.getRequestResponse().getResponse(), true);
        extender.currentlyDisplayedItem = rceData.getRequestResponse();
        super.changeSelection(rowIndex, columnIndex, toggle, extend);
    }
}