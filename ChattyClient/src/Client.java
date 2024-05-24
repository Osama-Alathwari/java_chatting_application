import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

public class Client {
	public static Socket socket;
	public static DataInputStream dis;
	public static DataOutputStream dos;
	public static FileInputStream fis;

	// An object Representing the file we wanna send
	static File fileToSend;
	
	public static byte[] fnameBytes, fcontentBytes;
	public static String fname;
	public static String username = "";

	// An Array List to hold all files that's being sent
	static ArrayList<MyFile> myFiles = new ArrayList<>();
	
	// To Represent Each File
	static int fileID = 0;

	public Client() throws IOException {
		new ChatWindow();
		try {
			socket = new Socket("127.0.0.1", 25568);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			while (socket.isConnected()) {
				if (!(dis.readBoolean())) {

					JLabel txtLabel = new JLabel();
					txtLabel.setFont(new Font("Arial", Font.BOLD, 15));
					txtLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
					txtLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
					txtLabel.setText(dis.readUTF());
					JPanel txtPanel = new JPanel();
					txtPanel.setLayout(new BoxLayout(txtPanel, BoxLayout.Y_AXIS));
					txtPanel.add(txtLabel);
					ChatWindow.writeToChat(txtPanel);
				} else {
					Docs(dis);
				}

			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("close");
			socket.close();
		}

	}

	public static void main(String[] args) throws IOException {

		username = JOptionPane.showInputDialog("Enter your name");
		new Client();

	}

	public static void Docs(DataInputStream dis) {
				try {
					// To read the length of the file name sent by user
					int fileNameLength = dis.readInt();

					// Checking if there is a file sent or not
					if (fileNameLength > 0) {
						
						// Creating a byte array to store the name of the file in it 
						byte[] fileNameBytes = new byte[fileNameLength];
						
						// To read and store the name of the file into the byte array , 0 => offset , where we start reading from , How much we wanna read
						dis.readFully(fileNameBytes, 0, fileNameBytes.length);
						
						// Creating the File Name from the Array of Bytes
						String fileName = new String(fileNameBytes);

						// To read the length of the file sent by user
						int fileContentLength = dis.readInt();
						
						// Checking if there is a file sent or not
						if (fileContentLength > 0) {
						
							// Creating a byte array to store the content of the file in it 
							byte[] fileContentBytes = new byte[fileContentLength];
						
							// To read and store the content of the file into the byte array , 0 => offset , where we start reading from , How much we wanna read
							dis.readFully(fileContentBytes, 0, fileContentLength);

							// A pannel to hold each File
							JPanel jpFileRow = new JPanel();
						
							//Creates a layout manager that will lay out components along the given axis , to represent the msgs verticaly	
							jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

							// To represent the name of the file 
							JLabel jlFileName = new JLabel(fileName);
						
							// To set the font and the font size of the lable
							jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
						
							// to set a Border for jlFileName and put a space between it and the other elements , for spacing around it
							jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
						
							// Set Alignment in X Direction to be center  
							jlFileName.setAlignmentX(Component.LEFT_ALIGNMENT);

							// Checks if the sent file is a text file , equalsIgnoreCase Compare 2 txt regarding their Case
							if (getFileExtention(fileName).equalsIgnoreCase("txt")) {
						
								// To assign an id to each pannel to be used later with the array list to link each pannel with each file
								jpFileRow.setName(String.valueOf(fileID));
						
								// Add a mouse Listner to the jpFileRow
								jpFileRow.addMouseListener(getMyMouseListener());
						
								// Add the file name
								jpFileRow.add(jlFileName);
						
								// Add the jpFileRow to the frame
								ChatWindow.addComponentToChat(jpFileRow);
								
								// if the sent file is not a text file
							} else {

								// To assign an id to each pannel to be used later with the array list to link each pannel with each file
								jpFileRow.setName(String.valueOf(fileID));

								// Add a mouse Listner
								jpFileRow.addMouseListener(getMyMouseListener());

								// Add the file name
								jpFileRow.add(jlFileName);

								// Add the jpFileRow to the frame
								ChatWindow.addComponentToChat(jpFileRow);
							}
						
							// Add the File into the Array List
							myFiles.add(new MyFile(fileID, fileName, fileContentBytes, getFileExtention(fileName)));
						
							// Increment the ID 
							fileID++;
						}

					}
				} catch (IOException ioEx) {
					ioEx.printStackTrace();
				}

			}

	// A function t
	public static MouseListener getMyMouseListener() {
		return new MouseListener() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) { // e is the Click event
				// Create a jpanel using the e.getSource() and we casted it coz it returns a general object
				JPanel jPanel = (JPanel) e.getSource();
				
				// the id of the pannel that was clicked on
				int fileID = Integer.parseInt(jPanel.getName());

				// Loop through the Array List
				for (MyFile myFile : myFiles) {
					// Checking which pannel was clicked on
					if (myFile.getId() == fileID) {
						// Create a pop up Jframe Containig a preview about the file that was Clicked
						JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtention());
						jfPreview.setVisible(true);
					}
				}
			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {

			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {

			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {

			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {

			}
		};
	}


	// A function to create the pop up frame
	public static JFrame createFrame(String fileName, byte[] fileData, String fileExtention) {
		
		// Create an object and set its title 
		JFrame jFrame = new JFrame("File Downloader");
		
		// Set the size of the frame
		jFrame.setSize(400, 400);

		// Create a pannel to Contain every thing 
		JPanel jPanel = new JPanel();
		
		//Creates a layout manager that will lay out components along the given axis , to represent the msgs verticaly	
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

		JScrollPane jScrollPane = new JScrollPane(jPanel);
		
		// To make the scrolbar appears when needed
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		// Create a jlable and set its title
		JLabel jTitle = new JLabel("File Downlaoder");
		
		// Centering the jTitle Horizontly
		jTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Set the jTitle Font and Font Size
		jTitle.setFont(new Font("Arial", Font.BOLD, 25));
		
		// to set a Border for jTitle and put a space between it and the other elements , for spacing around it
		jTitle.setBorder(new EmptyBorder(20, 0, 10, 0));

		// Create a jlable and set its title
		JLabel jlPrompt = new JLabel("Are You Sure You Want to Download " + fileName);
		
		// Centering the jPrompt Horizontly
		jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Set the jPrompt Font and Font Size
		jlPrompt.setFont(new Font("Arial", Font.BOLD, 25));
		
		// to set a Border for jPrompt and put a space between it and the other elements , for spacing around it
		jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));

		// Create a button a set its title
		JButton yesBtn = new JButton("Yes");
		
		// Set the size of the button
		yesBtn.setPreferredSize(new Dimension(150, 75));
		
		// Set the Font and the font size
		yesBtn.setFont(new Font("Arial", Font.BOLD, 25));

		// Create a button a set its title
		JButton noBtn = new JButton("No");
		
		// Set the size of the button
		noBtn.setPreferredSize(new Dimension(150, 75));
		
		// Set the Font and the font size
		noBtn.setFont(new Font("Arial", Font.BOLD, 25));

		// A lable to display the actual Content
		JLabel jlFileContent = new JLabel();
		
		// Centering in Horizontly
		jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

		// A panel to hold the buttons
		JPanel jpButtons = new JPanel();
		
		// to set a Border for jpButtons and put a space between it and the other elements , for spacing around it
		jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
		
		// Add the buttons to the pannel
		jpButtons.add(yesBtn);
		jpButtons.add(noBtn);

		// if the sent file is a text file 
		if (fileExtention.equalsIgnoreCase("txt")) {
			// display the content of the text file by setting the text of the lable as the content of the text file , we used html tags to allow the text to break into another line
			jlFileContent.setText("<html> <body> <pre>" + new String(fileData) + "</html> </body> </pre>");
		}
		
		// if it wasn't a text file , for example an Image
		else {
			// then we preview it by setting the Icon of the of the lable as the image
			jlFileContent.setIcon(new ImageIcon(fileData));
		}
		
		// Add an action litener for the yes button
		yesBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Create an object from the class File And pass the Path name where we wanna download
				File fileToDownload = new File(fileName);
				try {
					// Create an object from the class FileOutputStream to write into the file 
					FileOutputStream fos = new FileOutputStream(fileToDownload);
					// Writing the data into the file
					fos.write(fileData);
					// Close the FileOutputStream object
					fos.close();

					// Close the frame
					jFrame.dispose();
				} catch (IOException ioEX) {
					ioEX.printStackTrace();
				}
			}
		});
		
		// Add an action litener for the No button
		noBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close the frame
				jFrame.dispose();
			}
		});
		// Add everything to the pannel
		jPanel.add(jTitle);
		jPanel.add(jlPrompt);
		jPanel.add(jlFileContent);
		jPanel.add(jpButtons);

		// Add the ScrollPane to the frame
		jFrame.add(jScrollPane);
		
		// Return the frame
		return jFrame;
	}

	// A function to get the file extension
	public static String getFileExtention(String fileName) {
		int i = fileName.lastIndexOf('.');
		return i > 0 ? fileName.substring(i + 1) : "No Extention Found";
	}
}
