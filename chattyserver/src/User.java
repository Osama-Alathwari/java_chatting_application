
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class User implements Runnable {
	public DataInputStream dis;
	public DataOutputStream dos;
	public Thread usethread = new Thread(this);


	// protected Object userthread;
	public User(DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;

	}

	@Override
	public void run() {
		msgs(usethread, dis, dos);
	}

	@SuppressWarnings("deprecation")
	public static void msgs(Thread usethread, DataInputStream dis, DataOutputStream dos) {
		while (Chatserver.socket.isConnected()) {
			try {
				if (!dis.readBoolean()) {
					String text = dis.readUTF();
					Chatserver.broadcast(text);
				} else {	
					Docs(dis, dos);
				}
			} catch (IOException e) {
				usethread.stop();
				e.printStackTrace();
			}
		}
	}

	public static void Docs(DataInputStream dis, DataOutputStream dos) {
		try {

			// To read the length of the file name sent by user
			int fileNameLength = dis.readInt();

			// Checking if there is a file sent or not
			if (fileNameLength > 0) {

				// Creating a byte array to store the name of the file in it 
				byte[] fileNameBytes = new byte[fileNameLength];

				// To read and store the name of the file into the byte array , 0 => offset , where we start reading from , How much we wanna read
				dis.readFully(fileNameBytes, 0, fileNameLength);

				// To read the length of the file sent by user
				int fileContentLength = dis.readInt();

				// Checking if there is a file sent or not
				if (fileContentLength > 0) {

					// Creating a byte array to store the content of the file in it 
					byte[] fileContentBytes = new byte[fileContentLength];

					// To read and store the content of the file into the byte array , 0 => offset , where we start reading from , How much we wanna read
					dis.readFully(fileContentBytes, 0, fileContentLength);

					// To Broadcast the file to all clients
					Chatserver.broadcastFile(fileNameLength, fileNameBytes, fileContentLength, fileContentBytes);
				}

			}
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

	}
			
	public boolean isConnected() {
		return usethread.isAlive();
	}

}
