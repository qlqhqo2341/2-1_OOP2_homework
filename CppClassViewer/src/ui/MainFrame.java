package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.io.*;

import struct.*;
import working.*;

public class MainFrame extends JFrame implements ActionListener, FocusListener, TreeSelectionListener{
	
	Object selectedObj;
	CppClass cppClass;
	JMenuBar menuBar;
	JMenuItem open, save, exit;

	JTextArea bodyTextArea;
	JTable table;
	ListPanel listPanel;

	JFileChooser jfile;

	public MainFrame() {
		setTitle("JJ_Class_Viewer");
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jfile = new JFileChooser();

		selectedObj=null;
		initMenu();
		setJMenuBar(menuBar);
		
		setLayout(new BorderLayout());

		listPanel = new ListPanel();
		listPanel.tree.addTreeSelectionListener(this);
		add(listPanel, BorderLayout.WEST);

		bodyTextArea = new JTextArea();
		bodyTextArea.setSize(600,600);
		bodyTextArea.setEditable(true);
		bodyTextArea.setText("Please select Method or Field");
		bodyTextArea.setLineWrap(true);
		bodyTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		bodyTextArea.addFocusListener(this);
		add(bodyTextArea, BorderLayout.CENTER);
		
		//StartTest
		readCppClass(new File("Queue.cpp"));

		
		setVisible(true);
	}

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

	private void initMenu() {
		menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menuBar.add(menu);

		open = new JMenuItem("open");
		open.addActionListener(this);
		menu.add(open);

		save = new JMenuItem("save");
		save.addActionListener(this);
		menu.add(save);

		exit = new JMenuItem("exit");
		exit.addActionListener(this);
		menu.add(exit);

	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(open)) {
			int returnVal = jfile.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION)
				readCppClass(jfile.getSelectedFile());
			
		} else if (e.getSource().equals(save)) {
			String allText = cppClass.makeAllBody();

			int returnVal = jfile.showSaveDialog(this);
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
		
		if(selectedObj instanceof Method){
			focusLost(null);
		}
		
		
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		selectedObj = selNode.getUserObject();
		
		if(selectedObj instanceof Method){
			bodyTextArea.setText(((Method) selectedObj).getBody());
			bodyTextArea.setVisible(true);
			
			listModel.removeAllElements();
			for(Field fie : ((Method) selectedObj).getFields())
				listModel.addElement(fie);
		}
		else if(selectedObj instanceof Field){
			
		}
		
	}
	
	@Override
	public void focusGained(FocusEvent e) {}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainFrame();
	}

	
}

class ListPanel extends JPanel {
	JTree tree;
	DefaultMutableTreeNode root;
	JList list;
	DefaultListModel listModel;

	public ListPanel() {
		
		setSize(400,600);
		setLayout(new GridLayout(0, 1));
		
		root = new DefaultMutableTreeNode(
				"Please open the Cpp File. (File -> open)");
		tree = new JTree(root);
		tree.setSize(400,300);
		add(new JScrollPane(tree));

		listModel = new DefaultListModel();
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(list));
		
		setVisible(true);
	}
	

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

		tree.expandRow(0);
	}

}