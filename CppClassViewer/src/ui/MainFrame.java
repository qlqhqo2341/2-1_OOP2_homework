package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.sun.javafx.tk.FileChooserType;

import java.io.*;

import struct.*;
import working.*;

public class MainFrame extends JFrame implements ActionListener {
	CppClass cppClass;
	JMenuBar menuBar;
	JMenuItem open, save, exit;

	JTextArea bodyTextArea;
	ListPanel listPanel;

	JFileChooser jfile;

	public MainFrame() {
		setTitle("JJ_Class_Viewer");
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jfile = new JFileChooser();

		initMenu();
		setJMenuBar(menuBar);

		listPanel = new ListPanel();
		add(listPanel, BorderLayout.WEST);

		bodyTextArea = new JTextArea();
		bodyTextArea.setSize(600,600);
		bodyTextArea.setEditable(true);
		bodyTextArea.setText("Please select Method or Field");
		add(bodyTextArea, BorderLayout.CENTER);

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
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainFrame();
	}

	
}

class ListPanel extends JPanel {
	JTree tree;
	TreeModel model;
	DefaultMutableTreeNode root;
	JList<String> list;

	public ListPanel() {
		setSize(400, 600);

		root = new DefaultMutableTreeNode(
				"Please open the Cpp File. (File - open)");

		tree = new JTree(root);

		add(tree);

		list = new JList();
		add(list);

		setVisible(true);
	}

	public void setRootTree(String msg) {
		remove(tree);
		root = new DefaultMutableTreeNode(msg);
		tree = new JTree(root);
		add(tree);
	}

	public void MakingTree(CppClass obj) {

		remove(tree);
		root = new DefaultMutableTreeNode(obj.getName());
		tree = new JTree(root);

		for (Method v : obj.getMethods()) {
			DefaultMutableTreeNode m = new DefaultMutableTreeNode(v.getName());
			root.add(m);
		}
		for (Field v : obj.getFields()) {
			DefaultMutableTreeNode f = new DefaultMutableTreeNode(v.getName());
			root.add(f);
		}
		
		JScrollPane scroll = new JScrollPane(tree);
		//scroll.setSize(600,300);
		add(scroll);

	}

}