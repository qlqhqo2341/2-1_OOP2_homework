package ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;

import struct.*;


public class MainFrame extends JFrame{
	CppClass cppClass;
	JTextArea bodyTextArea;
	ListPanel listPanel;
	
	public MainFrame(){
		setTitle("JJ_Class_Viewer");
		setSize(1000,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		listPanel=new ListPanel();
		add(listPanel);
		
		bodyTextArea=new JTextArea();
		add(bodyTextArea);
		
		
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			new MainFrame();
	}
}

class ListPanel extends JPanel{
	JTree tree;	
	TreeModel model;
	DefaultMutableTreeNode root;
	JList<String> list;
	
	public ListPanel(){
		setSize(400,600);
		
		root=new DefaultMutableTreeNode("파일을 로드해 주세요.");
		tree = new JTree(root);
				
		add(tree);
		
		
		
		list=new JList<String>();
		
		
		add(list);
		
		setVisible(true);
	}
	
	public void MakingTree(CppClass obj){
		
		remove(tree);
		root = new DefaultMutableTreeNode(obj.getName());
		tree=new JTree(root);

		for(Method v : obj.getMethods()){
			DefaultMutableTreeNode m = new DefaultMutableTreeNode(v.getName());
			root.add(m);
		}
		for(Field v : obj.getFields()){
			DefaultMutableTreeNode f = new DefaultMutableTreeNode(v.getName());
			root.add(f);
		}
		add(tree);
		
	}

}