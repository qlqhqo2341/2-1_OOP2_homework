package ui;

import java.awt.*;
import javax.swing.*;


public class MainFrame extends JFrame{
	
	public MainFrame(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(screenSize.width/4, screenSize.height/4,
				screenSize.width/2, screenSize.height/2);	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(0,2));
		
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			new MainFrame();
	}

}
