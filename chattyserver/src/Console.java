import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Console {
	static final JFrame frame = new JFrame("Server Console");
	static final JPanel panel = new JPanel();

	public static final JTextArea textarea = new JTextArea();
	public static final JScrollPane scroll = new JScrollPane(textarea);
	static final int width = 600;
	static final int height = 600;

	public Console() {
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		scroll.setPreferredSize(new Dimension(width - 32, height - 64));
		textarea.setEditable(false);

		panel.add(scroll);
		frame.add(panel);
	}

	public void writeToConsole(String text) {
		textarea.append(text + "\n");
	}
}