import javax.swing.*;
import java.awt.*;
public class ImagePanel extends JPanel
	{
		String str="";
		ImagePanel()
		{
			str="background.jpg";
		}
		ImagePanel(String link)
		{
			str=link;
		}
		public void paintComponent(Graphics g)
		{
			g.drawImage(new ImageIcon(str).getImage(),0,0,null);
		}
	}