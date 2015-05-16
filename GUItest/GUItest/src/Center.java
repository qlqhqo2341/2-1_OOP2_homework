import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Center extends JFrame{
	JButton button;
	public Center(){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		setBounds(screenSize.width / 4, screenSize.height / 4, 
				screenSize.width / 2, screenSize.height / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("MyFrame");
		
		JPanel panel = new JPanel();
		button=new JButton("¹öÆ°.");
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setTitle(getTitle() + ".");
			}
		});
		panel.add(button);
		add(panel);
		
		setVisible(true);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			new Center();
	}

}
