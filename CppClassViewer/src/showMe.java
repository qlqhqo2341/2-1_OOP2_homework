
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class showMe extends JFrame{
	JButton button;
	String a="�ȳ��Ͻ�dfdf.", b="���S.";
	
	public showMe(){
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		JPanel panel = new JPanel();
		button = new JButton(a);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(e.getSource() == button){
					if(button.getText() == a)
						button.setText(b);
					else
						button.setText(a);
				}
			}
		}
		);
		
		panel.add(button);
		add(panel);
		
		pack();
		
		setVisible(true);		
	}
	
}
