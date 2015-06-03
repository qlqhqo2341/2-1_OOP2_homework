package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.io.*;

import struct.*;
import working.*;

public class MainFrame extends JFrame {
	Object selectedObj;
	CppClass cppClass;
	JMenuBar menuBar;
	JMenuItem open, save, exit;

	JTextArea bodyTextArea;
	JTable table;
	PresentPanel prePanel;
	ListPanel listPanel;

	JFileChooser jfile;

	MainFrame me;
	Listener listener;

	public MainFrame() {
		setTitle("JJ_Class_Viewer");
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jfile = new JFileChooser();
		me = this;
		listener = new Listener();

		selectedObj = null;
		initMenu();
		setJMenuBar(menuBar);

		setLayout(new BorderLayout());

		listPanel = new ListPanel();
		listPanel.tree.addTreeSelectionListener(listener);
		add(listPanel, BorderLayout.WEST);

		prePanel = new PresentPanel();
		bodyTextArea = prePanel.bodyTextArea;
		bodyTextArea.addFocusListener(listener);
		table = prePanel.table;
		add(prePanel, BorderLayout.CENTER);

		setVisible(true);
	}

	// 파일을 읽어 Parsing을 사용해 CppClass를 만듭니다.
	private boolean readCppClass(File file) {

		try {
			String readText = IO.read(file);

			if (readText == null)
				throw new Exception();

			Parser parse = new Parser(readText);
			cppClass = parse.getCppClass();

			if (cppClass == null)
				throw new Exception();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("i can't read and make cppClass");

			e.printStackTrace();
			return false;
		}

		listPanel.MakingTree(cppClass);
		repaint();
		setVisible(true);

		return true;
	}

	// 메뉴를 만듭니다.
	private void initMenu() {

		menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menuBar.add(menu);

		open = new JMenuItem("open");
		open.addActionListener(listener);
		menu.add(open);

		save = new JMenuItem("save");
		save.addActionListener(listener);
		menu.add(save);

		exit = new JMenuItem("exit");
		exit.addActionListener(listener);
		menu.add(exit);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainFrame();
	}

	private class Listener implements ActionListener, FocusListener,
			TreeSelectionListener {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource().equals(open)) {
				int returnVal = jfile.showOpenDialog(me);
				if (returnVal == JFileChooser.APPROVE_OPTION)
					readCppClass(jfile.getSelectedFile());

			} else if (e.getSource().equals(save)) {
				String allText = cppClass.makeAllBody();

				int returnVal = jfile.showSaveDialog(me);
				if (returnVal == JFileChooser.APPROVE_OPTION)
					IO.write(jfile.getSelectedFile(), allText);

			} else if (e.getSource().equals(exit)) {
				System.exit(0);
			}
		}

		@Override
		public void focusLost(FocusEvent e) {

			// TODO Auto-generated method stub

			if (selectedObj instanceof Method) {
				Method met = (Method) selectedObj;
				met.setBody(cppClass.getFields(), bodyTextArea.getText());
			}

		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			// TODO Auto-generated method stub
			JTree tree = listPanel.tree;
			DefaultListModel listModel = listPanel.listModel;

			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			CardLayout layout = (CardLayout) prePanel.getLayout();
			listModel.removeAllElements();
			
			if(selNode == null)
				return;
			
			selectedObj = selNode.getUserObject();
			if (selectedObj instanceof Method) {
				bodyTextArea.setText(((Method) selectedObj).getBody());

				for (Field fie : ((Method) selectedObj).getFields())
					listModel.addElement(fie);
				layout.show(prePanel, "text");

				focusLost(null);
			}

			else if (selectedObj instanceof Field) {
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				int rowCount = model.getRowCount();

				for (int i = 0; i < rowCount; i++)
					model.removeRow(0);

				Method[] mets = ((Field) selectedObj).getMethods();
				for (Method v : mets) {
					String[] row = { v.toString(), v.getType(), v.getAccess() };
					model.addRow(row);
				}
				layout.show(prePanel, "table");
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
		}
	}
}

class ListPanel extends JPanel {
	JTree tree;
	DefaultMutableTreeNode root;
	JList list;
	DefaultListModel listModel;

	public ListPanel() {

		setSize(400, 600);
		setLayout(new GridLayout(0, 1));

		root = new DefaultMutableTreeNode("Please open the Cpp File. (File -> open)");
		tree = new JTree(root);
		tree.setSize(400, 300);
		add(new JScrollPane(tree));

		listModel = new DefaultListModel();
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(list));

		setVisible(true);
	}

	// CppClass로 트리를 설정합니다.
	public void MakingTree(CppClass obj) {

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();

		root.setUserObject(obj);
		root.removeAllChildren();

		for (Method v : obj.getMethods()) {
			DefaultMutableTreeNode m = new DefaultMutableTreeNode(v);
			root.add(m);
		}
		for (Field v : obj.getFields()) {
			DefaultMutableTreeNode f = new DefaultMutableTreeNode(v);
			root.add(f);
		}
		tree.setModel(new DefaultTreeModel(root));
		tree.expandRow(0);
	}

}

class PresentPanel extends JPanel {
	JTextArea bodyTextArea;
	JTable table;
	DefaultTableModel tableModel;
	CardLayout layout;

	public PresentPanel() {
		// TODO Auto-generated constructor stub
		setSize(600, 600);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		layout = new CardLayout();
		setLayout(layout);

		bodyTextArea = new JTextArea();
		bodyTextArea.setEditable(true);
		bodyTextArea.setText("Please select Method or Field");
		bodyTextArea.setLineWrap(true);
		add(bodyTextArea, "text");

		String[] column = { "Name", "Type", "Access" };
		tableModel = new DefaultTableModel(column, 0);
		table = new JTable(tableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		add(table, "table");
		setVisible(true);
	}

}