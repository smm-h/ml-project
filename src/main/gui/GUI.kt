package src.main.gui;


import javax.swing.*;
import java.awt.*;

public class MainWindow {
    private final JFrame frame = new JFrame("MainWindow");
    private final JMenuBar menuBar = Menu.INSTANCE.getJMenuBar();

    private JPanel panel;
    private JSplitPane split;
    private JPanel treePanel;
    private JTree tree;
    private JPanel tabsPanel;
    private JTabbedPane tabs;

    public static void main(String[] args) {
        try {
            /*
             * javax.swing.plaf.metal.MetalLookAndFeel
             * javax.swing.plaf.nimbus.NimbusLookAndFeel
             * com.sun.java.swing.plaf.motif.MotifLookAndFeel
             * com.sun.java.swing.plaf.windows.WindowsLookAndFeel
             * com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel
             */
//            for (UIManager.LookAndFeelInfo i : UIManager.getInstalledLookAndFeels())
//                System.out.println(i.getClassName());
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        MainWindow mainWindow = new MainWindow();
//        new FontUIResource(new Font("Arial", Font.BOLD, 20));
//        mainWindow.tree.putClientProperty("JComponent.sizeVariant", "large");
        mainWindow.panel.setMinimumSize(new Dimension(640, 480));
        mainWindow.frame.setContentPane(mainWindow.panel);
        mainWindow.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.frame.setSize(640, 480);
        mainWindow.frame.setLocationByPlatform(true);
//        mainWindow.frame.pack();
        mainWindow.frame.setVisible(true);
        mainWindow.frame.setJMenuBar(mainWindow.menuBar);
    }
}
