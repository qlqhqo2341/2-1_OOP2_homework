package ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import struct.CppClass;
import struct.Field;
import struct.Method;


public class MainFrame extends JFrame{
	CppClass cppClass;
	
	public MainFrame(){
		setTitle("JJ_Class_Viewer");
		setSize(1000,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(new ListPanel());
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			new MainFrame();
	}
}

class ListPanel extends JPanel{
	JTree tree;	
	DefaultMutableTreeNode root;
	
	public ListPanel(){
		setSize(400,600);
		
		tree = new JTree(root);
		add(tree);
	}
	
	public void MakingTree(CppClass obj){
		root = new DefaultMutableTreeNode(obj.getName());


		tree.removeAll();

		for(Method v : obj.getMethods()){
			DefaultMutableTreeNode m = new DefaultMutableTreeNode(v.getName());
			root.add(m);
		}
		for(Field v : obj.getFields()){
			DefaultMutableTreeNode f = new DefaultMutableTreeNode(v.getName());
			root.add(f);
		}
	}
	
	
}