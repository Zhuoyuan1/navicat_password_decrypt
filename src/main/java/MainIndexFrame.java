import enums.VersionEnum;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.LinkLabel;
import util.DecodeNcx;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * UploadSynFrame 破解navicat密码ui
 *
 * @author lzy
 * @date 2021/01/15 14:03
 */
public class MainIndexFrame {
    private static final double NAVICAT11 = 1.1D;

    /**
     * 解密工具类
     */
    private static DecodeNcx decodeNcx;

    /**
     * navicat11选项
     */
    private static final JRadioButton navicat11 = new JRadioButton("navicat11");

    /**
     * navicat12以上选项
     */
    private static final JRadioButton navicat12more = new JRadioButton("navicat12+", true);

    /**
     * 密文正则
     */
    public static final String PASS_WORD = "^[A-Za-z0-9]+$";

    public void UpLoadFile() {
        JFrame jframe = new JFrame("navicat密码查看工具");
        jframe.setLayout(new BorderLayout());
        JToolBar jToolBar = new JToolBar();
        jframe.setSize(400, 350);
        JPanel jPanel = new JPanel();

        JTextField password = new JTextField("请填入加密密码", 20);
        JButton showButton = new JButton("查看密码");
        JPanel filePanel = new JPanel();
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(navicat11);
        buttonGroup.add(navicat12more);
        jPanel.add(navicat11);
        jPanel.add(navicat12more);
        jPanel.add(password);
        jPanel.add(showButton);
        LinkLabel explain = new LinkLabel("操作说明", "https://blog.csdn.net/kkk123445/article/details/115748954?spm=1001.2014.3001.5501");
        explain.setHorizontalAlignment(SwingConstants.LEFT);
        filePanel.add(explain);
        JLabel jl = new JLabel("<html>&nbsp;&nbsp;&nbsp;&nbsp;导入ncx文件，请选择：</html>");
        jl.setHorizontalAlignment(SwingConstants.LEFT);
        filePanel.add(jl);
        JButton developer = new JButton("选择文件");
        developer.setHorizontalAlignment(SwingConstants.CENTER);
        jToolBar.add(developer);
        filePanel.add(jToolBar);

        final JSplitPane vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // 分隔条左侧的高度为
        vSplitPane.setDividerLocation(80);
        vSplitPane.setResizeWeight(0.6);
        // 分隔条的宽度为
        vSplitPane.setDividerSize(10);
        // 提供UI小部件
        vSplitPane.setOneTouchExpandable(false);
        vSplitPane.setEnabled(false);
        // 在垂直面板上方添加一个标签组件
        vSplitPane.setTopComponent(jPanel);
        // 在垂直面板下方添加一个标签组件
        vSplitPane.setBottomComponent(filePanel);

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);
        scrollPane.setPreferredSize(new Dimension((int) (jframe.getWidth() * 9.6) / 10,
                (int) (200 * 9.6) / 10));
        filePanel.add(scrollPane);

        jframe.add(vSplitPane);
        //单个密码查看
        showButton.addActionListener(e -> {
            if (navicat11.isSelected()) {
                decodeNcx = new DecodeNcx(VersionEnum.native11.name());
            } else {
                decodeNcx = new DecodeNcx(VersionEnum.navicat12more.name());
            }
            if (!Pattern.matches(PASS_WORD, password.getText())) {
                JOptionPane.showMessageDialog(null,
                        "密码应为字母和数字组合，格式不正确！", "提醒", JOptionPane.WARNING_MESSAGE, null);
                return;
            }
            password.setText(decodeNcx.decode(password.getText().trim()));
            password.requestFocus();
        });
        //批量密码查看
        developer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                String result = eventOnImport(new JButton());
                textArea.setText(result);
            }
        });
        jframe.setResizable(false);
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
    }

    /**
     * 文件上传功能
     *
     * @param developer 按钮控件名称
     */
    public static String eventOnImport(JButton developer) {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        /** 过滤文件类型 * */
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ncx", "ncx");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(developer);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            /** 得到选择的文件* */
            File file = chooser.getSelectedFile();
            if (file == null) {
                return "";
            }
            try {
                // List<Map <连接名，Map<属性名，值>>> 要导入的连接
                List<Map<String, Map<String, String>>> configMap = new ArrayList<>();
                //1、创建一个DocumentBuilderFactory的对象
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                //2、创建一个DocumentBuilder的对象
                //创建DocumentBuilder对象
                DocumentBuilder db = dbf.newDocumentBuilder();
                //3、通过DocumentBuilder对象的parser方法加载xml文件到当前项目下
                Document document = db.parse(file);
                //获取所有Connections节点的集合
                NodeList connectList = document.getElementsByTagName("Connection");

                NodeList nodeList = document.getElementsByTagName("Connections");
                NamedNodeMap verMap = nodeList.item(0).getAttributes();
                double version = Double.parseDouble((verMap.getNamedItem("Ver").getNodeValue()));
                if (version <= NAVICAT11) {
                    decodeNcx = new DecodeNcx(VersionEnum.native11.name());
                } else {
                    decodeNcx = new DecodeNcx(VersionEnum.navicat12more.name());
                }
                //配置map
                Map<String, Map<String, String>> connectionMap = new HashMap<>();
                //遍历每一个Connections节点
                for (int i = 0; i < connectList.getLength(); i++) {
                    //通过 item(i)方法 获取一个Connection节点，nodelist的索引值从0开始
                    Node connect = connectList.item(i);
                    //获取Connection节点的所有属性集合
                    NamedNodeMap attrs = connect.getAttributes();
                    //遍历Connection的属性
                    Map<String, String> map = new HashMap<>(0);
                    for (int j = 0; j < attrs.getLength(); j++) {
                        //通过item(index)方法获取connect节点的某一个属性
                        Node attr = attrs.item(j);
                        map.put(attr.getNodeName(), attr.getNodeValue());
                    }
                    connectionMap.put(map.get("ConnectionName") + map.get("ConnType"), map);
                }
                configMap.add(connectionMap);
                return writeConfigFile(configMap);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "导入失败！请导入正确的ncx文件！", "提示",
                        JOptionPane.ERROR_MESSAGE);
                return "";
            }
        }
        return "";
    }

    /**
     * 写入配置文件
     *
     * @param list 读取ncx文件的数据
     */
    private static String writeConfigFile(List<Map<String, Map<String, String>>> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map<String, Map<String, String>> map : list) {
            for (Map.Entry<String, Map<String, String>> valueMap : map.entrySet()) {
                Map<String, String> resultMap = valueMap.getValue();
                String password = decodeNcx.decode(resultMap.getOrDefault("Password", ""));
                stringBuilder.append(resultMap.get("Host")).append("|").append(resultMap.get("Port"))
                        .append(resultMap.get("UserName")).append("|").append(resultMap.get("ConnType")).append(" = ")
                        .append(password).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        MainIndexFrame u = new MainIndexFrame();
        u.UpLoadFile();
    }
}
