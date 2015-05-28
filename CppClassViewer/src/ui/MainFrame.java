package ui;

import java.awt.*;
import javax.swing.*;


public class MainFrame extends JFrame{
	public MainFrame(){
		setTitle("JJ_Class_Viewer");
		setSize(1000,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(0,2));
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			new MainFrame();
	}

}
