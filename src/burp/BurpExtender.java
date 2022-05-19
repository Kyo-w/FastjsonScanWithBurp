package burp;

import burp.data.RceData;
import burp.utils.JSONUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

public class BurpExtender extends AbstractTableModel implements IBurpExtender, ITab,
IHttpListener, IMessageEditorController, IContextMenuFactory{

    private IBurpExtenderCallbacks callbacks;

    private JTabbedPane globalPane;

    private JScrollPane jScrollPane;

    public static PrintWriter stdout;

    private Table table;
    public IMessageEditor requestViewer;
    public IMessageEditor responseViewer;
    public IHttpRequestResponse currentlyDisplayedItem;

    public static boolean ismatch = false;
    public static boolean isScanner = true;


    //    ɨ��Ľ��
    public List<RceData> result = new ArrayList<RceData>();

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        callbacks.setExtensionName("FastJson����ƽ̨");
        this.callbacks = callbacks;
        this.stdout = new PrintWriter(callbacks.getStdout(), true);

//      ��ർ�����Ҳ๦�ܽ���
        JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        this.globalPane = jTabbedPane;

//      ɨ�����
        Table table = new Table(this, BurpExtender.this);
        this.table = table;
//      ɨ����棺����б�
        jScrollPane = new JScrollPane(table);
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jSplitPane.setLeftComponent(jScrollPane);

//      ɨ����棺ĳ���б��Ӧ���������Ӧ
        JTabbedPane tabs = new JTabbedPane();
        requestViewer = callbacks.createMessageEditor(BurpExtender.this, true);
        responseViewer = callbacks.createMessageEditor(BurpExtender.this, false);
        tabs.addTab("Requests", requestViewer.getComponent());
        tabs.addTab("Response", responseViewer.getComponent());
        jSplitPane.setRightComponent(tabs);
        jTabbedPane.addTab("ɨ��", jSplitPane);


//
        JPanel jPanel = new JPanel();
        Box verticalBox = Box.createVerticalBox();
        jPanel.add(verticalBox);
        new ConfigPane(BurpExtender.this).setPane(verticalBox);
        jTabbedPane.addTab("����", jPanel);
//        jTabbedPane.addTab("Exploit������", new JScrollPane());
        jTabbedPane.addTab("Mysql Inject", new JSplitPane());

        callbacks.registerContextMenuFactory(this);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                callbacks.addSuiteTab(BurpExtender.this);
                callbacks.registerHttpListener(BurpExtender.this);
            }
        });
    }

    public IBurpExtenderCallbacks getCallbacks() {
        return callbacks;
    }

    /*###############################
            ITabʵ��
    *################################
    * */
    @Override
    public String getTabCaption() {
        return "fastJson����ƽ̨";
    }

    @Override
    public Component getUiComponent() {
        return globalPane;
    }


    /*#############################
     *      AbstractTableModelʵ��
     *#############################
     */
    @Override
    public int getRowCount() {
        return result.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column){
            case 0:
                return "Url";
            case 1:
                return "Payload";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RceData rceData = result.get(rowIndex);
        switch (columnIndex){
            case 0:
                return rceData.getUrl();
            case 1:
                return rceData.getPayload();
            default:
                return "";
        }
    }
    /*#########################
    *       ʵ��http��������
    * #########################
    * */
    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
//        ������
        if(!isScanner){
            return;
        }
        if(toolFlag ==IBurpExtenderCallbacks.TOOL_EXTENDER){
            return;
        }
        IExtensionHelpers helpers = callbacks.getHelpers();
        List<String> newHeaders = new ArrayList<>();

        boolean isJson = false;
        IRequestInfo iRequestInfo = helpers.analyzeRequest(messageInfo.getHttpService(), messageInfo.getRequest());
//        �ж��Ƿ�ΪPOST�����������Ƿ�JSON��ʽ
        if(iRequestInfo.getMethod().equalsIgnoreCase("post") || ismatch){
            isJson = JSONUtils.isJSONString(messageInfo.getRequest());
        }
        isJson = true;
        if(isJson){
            List<String> headers = iRequestInfo.getHeaders();
            for(String header: headers){
                if (header.startsWith("Content-Type")) {
                    newHeaders.add("Content-Type: application/json");
                } else {
                    newHeaders.add(header);
                }
            }
            new Thread(new Exploit(this, messageInfo.getHttpService(), newHeaders, iRequestInfo.getUrl().toString())).start();
        }
    }

    @Override
    public IHttpService getHttpService() {
        return null;
    }

    @Override
    public byte[] getRequest() {
        return currentlyDisplayedItem.getRequest();
    }

    @Override
    public byte[] getResponse() {
        return currentlyDisplayedItem.getResponse();
    }

    /*####################################
     *      ��ӵ��˵�
     *####################################
     */
    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        List<JMenuItem> menus = new ArrayList<>(1);
        IHttpRequestResponse[] responses = invocation.getSelectedMessages();
        IRequestInfo iRequestInfo = callbacks.getHelpers().analyzeRequest(responses[0]);
        JMenuItem menuItem = new JMenuItem("���͵�fastjsonƽ̨");
        menus.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> newHeaders = new ArrayList<>();
                List<String> headers = iRequestInfo.getHeaders();
                for(String header: headers){
                    if (header.startsWith("Content-Type")) {
                        newHeaders.add("Content-Type: application/json");
                    } else {
                        newHeaders.add(header);
                    }
                }
                new Thread(new Exploit(BurpExtender.this, responses[0].getHttpService(), newHeaders, iRequestInfo.getUrl().toString())).start();

            }
        });
        return menus;
    }
}
