import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ChatWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	static int width = 940;
	static int height = width / 16 * 9;
	static final JPanel panel = new JPanel();
	static final JPanel panel2 = new JPanel();
	static final JTextArea textarea = new JTextArea();
	// To Make Scrollable panel
	static final JScrollPane scroll = new JScrollPane(panel);
	static final JTextField mgfd = new JTextField();
	static final JButton sendbtn = new JButton("send");
	static final JButton attachbtn = new JButton("Attatch");
	static ChatWindow cw;

	public ChatWindow() {
		cw = getter();
		// to set a title of the frame as the user's name
		this.setTitle(Client.username);
		// to specify the size of the frame
		this.setSize(width, height);
		// to make the frame resizeable
		this.setResizable(true);
		// to make the frame visible
		this.setVisible(true);
		// to exit from the application once its closed
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		scroll.setPreferredSize(new Dimension(width - 32, height - 100));
		mgfd.setPreferredSize(new Dimension(width - 32, 25));

		panel2.add(mgfd);
		panel2.add(sendbtn);
		panel2.add(attachbtn);
		
		//Creates a layout manager that will lay out components along the given axis , to represent the msgs verticaly		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
		this.add(scroll);
		this.add(panel2);

		// this Layout Manager is for the Frame itself , devides the frame into 2 rows and 1 column
		this.setLayout(new GridLayout(2, 1));
		
		// The Scrollbar will be displayed when needed
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		textarea.setEditable(false);

		sendbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendMsg();
			}
		});

		attachbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendDoc();
			}
		});

	}

	public ChatWindow getter() { 
		return this;
	}

	public static void writeToChat(JPanel comp) {
		panel.add(comp);
		ChatWindow.cw.validate();

	}

	public static void addComponentToChat(JPanel comp) {
		panel.add(comp);
		ChatWindow.cw.validate();
	}

	public static void sendMsg() {
		String text = mgfd.getText();
		try {
			if (!text.equals("")) {
				Client.dos.writeBoolean(false); 
				Client.dos.flush();
				Client.dos.writeUTF(Client.username + ":" + text);
				Client.dos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mgfd.setText("");
	}

	public static void sendDoc() {
		boolean flag = true;
		javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
		fileChooser.setDialogTitle("Choose a File to send");

		// null means that it will appear in the middle of the screen if the clicks open or double clicks on the file , the the condition is satisfied , and will return the chosen file
		if (fileChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
			// the chosen file will be returned to fileToSend Object
			Client.fileToSend = fileChooser.getSelectedFile();

			try {
				// Initialize an object from FileInputStream to read from and points to the chosen file
				Client.fis = new FileInputStream(Client.fileToSend.getAbsolutePath());
				
				// Initialize an object from DataOutputStream to Write to the server
				Client.dos = new DataOutputStream(Client.socket.getOutputStream());
				
				// Gets the File name and Store into a Variable Called fname
				Client.fname = Client.fileToSend.getName();
				
				// Converting The File Name into An Array of Bytes Called fnameBytes to be able to send through the DataOutputStream
				Client.fnameBytes = Client.fname.getBytes(); 
				
				// Initialize an Byte Array Called fcontentBytes and give the size of the chosen file to send the appropriate amount of bytes , We Converted it from long to int
				Client.fcontentBytes = new byte[ (int) Client.fileToSend.length()];
				
				// Read the Contents of the Chosen file into our byte array
				Client.fis.read(Client.fcontentBytes);
				
				// A flag to indicate that we are sending a file not a text
				Client.dos.writeBoolean(flag); // i will send file
				
				// To flush the Stream
				Client.dos.flush();
				
				// We First Send the length of the file name , to specify how much data we are gonna be Sending
				Client.dos.writeInt(Client.fnameBytes.length);
				
				// To Flush the Stream
				Client.dos.flush();
				
				// Then We Send The File Name 
				Client.dos.write(Client.fnameBytes);
				
				// To Flush the Stream
				Client.dos.flush();

				// Now We Send the length of the Content
				Client.dos.writeInt(Client.fcontentBytes.length);
				
				// To Flush the Stream
				Client.dos.flush();
				
				// Then We Send the Content of the file
				Client.dos.write(Client.fcontentBytes);
				
				// To Flush The Stream
				Client.dos.flush();
				
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			}

		}
	}

}
