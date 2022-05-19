package burp;

import burp.utils.JSONUtils;
import burp.utils.Payload;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigPane {
    private BurpExtender burpExtender;

    public ConfigPane(BurpExtender burpExtender){
        this.burpExtender = burpExtender;
    }
    public void setPane(Box pane){
        setModel(pane);
        setPayloadList(pane);
    }
    public void setModel(Box pane){
        JLabel label = new JLabel("扫描模式");
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setSize(200, 200);
        PrintWriter stdout = new PrintWriter(burpExtender.getCallbacks().getStdout(), true);

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton proxy = new JRadioButton("模式扫描(请求体为JSON格式请求)", true);
        proxy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                burpExtender.isScanner = true;
                burpExtender.ismatch = false;

            }
        });
        JRadioButton global = new JRadioButton("全局扫描(所有代理请求)");
        global.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                burpExtender.isScanner = true;
                burpExtender.ismatch = true;
            }
        });
        JRadioButton noScanner = new JRadioButton("不扫描(只接受转发)");
        noScanner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                burpExtender.isScanner = false;
            }
        });
        buttonGroup.add(proxy);
        buttonGroup.add(global);
        buttonGroup.add(noScanner);
        pane.add(label);

        Box horizontalBox = Box.createHorizontalBox();
        JPanel jPanel = new JPanel();
        jPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        horizontalBox.add(jPanel);
        jPanel.add(proxy);
        jPanel.add(global);
        jPanel.add(noScanner);
        pane.add(horizontalBox);
    }
    public void setPayloadList(Box pane){
//        JLabel label = new JLabel("Payload");
//        pane.add(label);
        JPayloadTable jPayloadTable = new JPayloadTable();
        JPayloadTable.PayloadTable payloadTable = new JPayloadTable.PayloadTable(jPayloadTable);
        payloadTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    OutputStream stdout = burpExtender.getCallbacks().getStdout();
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    // 封装文本内容
                    Transferable trans = new StringSelection(jPayloadTable.currentPayload);
                    // 把文本内容设置到系统剪贴板
                    clipboard.setContents(trans, null);
                    PrintWriter printWriter = new PrintWriter(stdout, true);
                    printWriter.println(jPayloadTable.currentPayload);
                }
            }
        });
//        列表集合
        Box horizontalBox = Box.createHorizontalBox();
        JScrollPane jScrollPane = new JScrollPane(payloadTable);
        jScrollPane.setSize(50, 50);
        horizontalBox.add(jScrollPane);

        Box verticalBox = Box.createVerticalBox();
        Button addButton = new Button("添加");
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                int option = jFileChooser.showOpenDialog(pane);
                if(option == JFileChooser.APPROVE_OPTION){
                    File file = jFileChooser.getSelectedFile();
                    try {
                        analyFile(file, jPayloadTable);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        Button deleteButton = new Button("删除");
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                OutputStream stdout = burpExtender.getCallbacks().getStdout();
                PrintWriter printWriter = new PrintWriter(stdout, true);
                Payload.payloads.remove(jPayloadTable.currentPayload);
//                DefaultTableModel model = (DefaultTableModel)payloadTable.getModel();
//                for(String elem : Payload.payloads){
//                    model.addRow(new Object[]{elem});
//                }
//                payloadTable.setModel(model);
                jPayloadTable.fireTableRowsInserted(Payload.payloads.size(),Payload.payloads.size());
            }
        });
        verticalBox.add(addButton);
        verticalBox.add(deleteButton);
        horizontalBox.add(verticalBox);
        pane.add(horizontalBox);
    }

    public static void analyFile(File filename, JPayloadTable table) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filename);
        int available = fileInputStream.available();
        byte[] bytes = new byte[available];
        fileInputStream.read(bytes);
        String newPayload = new String(bytes);
        String[] split = newPayload.split("\r\n\r\n");
        for(String elem: split){
            boolean isJson = JSONUtils.isJSONString(elem);
//            if(isJson){
                Payload.payloads.add(elem);
                table.fireTableRowsInserted(Payload.payloads.size(), Payload.payloads.size());
//            }else{
                BurpExtender.stdout.println("插入失败!");
//            }
        }

    }
}
