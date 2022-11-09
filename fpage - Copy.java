import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;

public class fpage extends JFrame implements ActionListener{
	String actualsize(long size){
        if(size<1024)
            return size+" Bytes";
        else if(size>1024 && size <1024*1024){
            return ((int)size/1024)+" KB";
        }else if(size>1024*1024 && size<1024*1024*2014){
            return ((int)size/(1024*1024))+" MB";
        }

        return "";
    }
	
	String path="C:\\";
	String host="192.168.43.1";
	
	JProgressBar progressBar=new JProgressBar();
	
	ImagePanel panel=new ImagePanel("background.jpg");
	
	static int width,height;
	
	JLabel lblyouripaddressmsg=new JLabel("Your IP Address",JLabel.CENTER);
	JLabel lblyouripaddress=new JLabel("0.0.0.0",JLabel.CENTER);
	JLabel lblmessage=new JLabel("Receiver is ON until Application is Running",JLabel.CENTER);
	
	Icon logoImage=new ImageIcon("onesharelogo.png");
	JLabel lbllogo=new JLabel(logoImage);
	
	Icon logoImage1=new ImageIcon("close.png");
	JButton lblpower=new JButton(logoImage1);
	
	Icon logoopendir=new ImageIcon("opendir.png");
	JButton btnopendir=new JButton(logoopendir);
	
	JButton btnexit=new JButton("Exit");
	
	Icon logoselectfile=new ImageIcon("selectfiles.png"); JButton btnselectfile=new JButton(logoselectfile);
	JLabel lblenterip=new JLabel("Enter IP address of Receiver Device");
	JTextField txtip=new JTextField();
	JLabel lblfilepath=new JLabel("");
	
	Icon logosend=new ImageIcon("send.png"); JButton btnsend=new JButton(logosend);
	
	JLabel lbl=new JLabel("Processing...");
	
	Connection con;
	PreparedStatement pst;
	{
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		Dimension dim=toolkit.getScreenSize();
		height=(int)dim.getHeight();
		width=(int)dim.getWidth();
	}
	
	fpage()
	{
		setUndecorated(true);
		add(panel);
		
		panel.setLayout(null);
		setSize(700,600);
		setLocation((width/2)-350,(height/2)-300);
		setVisible(true);
		setTitle("IPShare");
		setResizable(true);
		
		lbllogo.setBounds(400,0,300,200); panel.add(lbllogo);
		lblyouripaddressmsg.setBounds(400,200,300,20); panel.add(lblyouripaddressmsg);
		Font font = new Font("Courier", Font.BOLD,24);
		lblyouripaddress.setBounds(400,220,300,20); panel.add(lblyouripaddress);
		lblyouripaddress.setFont(font);
		lblmessage.setBounds(400,240,300,20); panel.add(lblmessage);
		
		btnopendir.setOpaque(false);
		btnopendir.setContentAreaFilled(false);
		btnopendir.setBorderPainted(false);
		btnopendir.setBounds(400,270,300,50); panel.add(btnopendir);
		
		lblpower.setOpaque(false);
		lblpower.setContentAreaFilled(false);
		lblpower.setBorderPainted(false);
		lblpower.setBounds(650,10,40,40); panel.add(lblpower);
		
		progressBar.setBorderPainted(false);
		progressBar.setForeground(Color.decode("#1cb7ff"));
		progressBar.setBounds(30,590,640,5); panel.add(progressBar);
		
		btnselectfile.setOpaque(false);
		btnselectfile.setContentAreaFilled(false);
		btnselectfile.setBorderPainted(false);
		btnselectfile.setBounds(30,490,70,70); panel.add(btnselectfile);
		
		lblenterip.setBounds(100,490,250,30); panel.add(lblenterip);
		Font txtfont = new Font("Courier", Font.BOLD,18);
		txtip.setBounds(100,520,200,30); panel.add(txtip);
		txtip.setFont(txtfont);
		
		btnsend.setOpaque(false);
		btnsend.setContentAreaFilled(false);
		btnsend.setBorderPainted(false);
		btnsend.setBounds(310,490,70,70); panel.add(btnsend);
		
		lblfilepath.setBounds(30,570,640,20); panel.add(lblfilepath);
		
		/* Setting Values */
		txtip.setText(""+Utils.getIPAddress(true));
		progressBar.setValue(100);
		lblyouripaddress.setText(""+Utils.getIPAddress(true)+"");
		
		setIconImage(new ImageIcon("icon.png").getImage());
		
		File file=new File("C:\\IPShare");
		if(!file.exists()){
			file.mkdir();
		}
		File filedir=new File("C:\\IPShare\\received");
		if(!filedir.exists()){
			filedir.mkdir();
		}
		
		lblpower.addActionListener(this);
		btnselectfile.addActionListener(this);
		btnsend.addActionListener(this);
		btnopendir.addActionListener(this);
		
		
		new Thread(){
			public void run(){
				try{
					ServerSocket serverSocket = null;
					serverSocket = new ServerSocket(8001);

					Socket socket = null;
					InputStream in = null;
					OutputStream out = null;
					
					while(true){
						socket = serverSocket.accept();
						in = socket.getInputStream();
						byte[] bytes = new byte[16*1024];
						int count;
						in.read(bytes);
						String info=new String(bytes);
						String infos[]=info.split(",");
						
					
						File file=new File(filedir+"/"+infos[0]);
						file.createNewFile();
						out = new FileOutputStream(file);
						
						System.out.println(Arrays.toString(infos));
						
						long rec=0;
						while ((count = in.read(bytes)) > 0) {
							out.write(bytes, 0, count);
							rec=rec+count;
							lblmessage.setText("Receiving : "+actualsize(rec));
						}
						out.close();
						lblmessage.setText("Received :"+actualsize(rec)+" "+infos[0]);
						rec=0;
						JOptionPane.showMessageDialog(panel, "Received "+infos[0],"File Received", JOptionPane.INFORMATION_MESSAGE);
					}
				}catch(Exception e){}
			}
		}.start();
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==lblpower){			
			dispose();
		}else if(e.getSource()==btnselectfile){
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("C:\\"));
            int status = chooser.showOpenDialog(null);
            if (status == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (file == null) {
                    return;
                }

                path = chooser.getSelectedFile().getAbsolutePath();
				lblfilepath.setText("File : "+path);
            }
		}else if(e.getSource()==btnsend){
			new Thread(){
				public void run(){
					try{
						host=txtip.getText();
						Socket socket = new Socket(host, 8001);
						File file = new File(""+path);
						long length = file.length();
						String filename=file.getName();
						
						System.out.println(""+filename);
						
						byte[] bytes = new byte[16 * 1024];
						InputStream in = new FileInputStream(file);
						OutputStream out = socket.getOutputStream();

						int count;
						
						String info=""+filename+",";
						bytes=info.getBytes();
						out.write(bytes);
						
						int pbval=0;
						
						progressBar.setMaximum((int)length/(1024*16));
						
						bytes = new byte[1024*16];
						while ((count = in.read(bytes)) > 0) {
							out.write(bytes, 0, count);
							
							pbval++;
							//lblmessage.setText("Actual Size "+length+" Sent : "+sent+" Left : "+left);
							//lblmessage.setText("Counter : "+pbval);
							//System.out.println(""+pbval);
							progressBar.setValue(pbval);
						}

						out.close();
						in.close();
						socket.close();
					}catch(Exception eee){}
				}
			}.start();
		}else if(e.getSource()==btnopendir){
			try{
				Runtime.getRuntime().exec("explorer.exe /select,C:\\ipshare\\received\\");
			}catch(Exception eee){}
		}
	}
	
	public static void main(String [] args)
	{
		new fpage();
	}	
	
}