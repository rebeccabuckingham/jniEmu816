package emu;

import emu.gui.ConsoleKeyHandler;
import emu.gui.SmartScroller;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.util.Stack;

/**
 * this is a *really* simple console that
 * provides a way for the 65816 to write to the screen and read from the keyboard
 */

public class ConsoleDevice extends BusDeviceBase {
	private final static int BUFSIZE = 128;
	private final static int CHAR_READY_FLAG = 0;			// memory location that's set when there's input waiting
	private final static int CHAR_OUT = 0;						// memory location to write to screen
	private final static int CURRENT_CHAR = 1;				// get char that's waiting, will *not* move the pointer
	private final static int CHARGOT = 2;

	private static final int DEFAULT_FONT_SIZE = 16;
	private static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, DEFAULT_FONT_SIZE);

	private final Stack<Character> keysPressed = new Stack<>();

	public ConsoleDevice(int startAddress, int size) {
		super("Console (" + BusDeviceBase.addr24(startAddress) + "," + BusDeviceBase.addr16(size) + ")", startAddress, size);
	}

	public synchronized void addKeyPressed(char c) {
		keysPressed.push(c);
	}

	public synchronized char getKeyPressed() {
		return keysPressed.pop();
	}

	public synchronized boolean charAvailable() {
		return keysPressed.size() > 0;
	}

	public Stack<Character> getKeyStack() { return keysPressed; }


	/**
	 * readMemory() in this case is how the emulated machine gets input from the keyboard
	 */
	@Override
	public short readMemory(int address) {
		int ea = effectiveAddress(address);
		short value = 0;

		if (! keysPressed.isEmpty())
			System.out.println("readMemory stack empty? " + keysPressed.isEmpty());

		if (ea == CHAR_READY_FLAG) {
			value = (short) (charAvailable() ? 1 : 0);
		} else if (ea == CURRENT_CHAR) {
			if (!charAvailable())
				value = (short) -1;
			else {
				keysPressed.forEach(c -> {
					if (c < ' ')
						System.out.printf("char: %02x\n", (int) c);
					else
						System.out.println("char: '" + c + "'");
				});

				value = (short) (getKeyStack().peek() & 0xFF);
			}
		}

		if ( (ea == 0 && value != 0) || (ea == 1 && value != -1))
			System.out.printf("readMemory ea:%d, value:%d\n", ea, value);

		return value;
	}

	/**
	 * writeMemory() is how the emulated machine writes to the screen
	 */

	@Override
	public void writeMemory(int address, short value) {
		int ea = effectiveAddress(address);
		char c = (char) value;

		if (ea == CHAR_OUT) {
			print(String.valueOf(c));
		} else if (ea == CHARGOT) {
			// can dispose of character now
			keysPressed.pop();
		}
	}

	JTextArea display = null;

	public void print(String str) {
		if (display == null) {
			System.err.println("display.print(): display isn't available yet!");
		} else {
			display.append(str);
		}
	}

	public void createAndShowGUI(final ConsoleDevice consoleDevice) {
		JPanel middlePanel = new JPanel();
		middlePanel.setBorder(new TitledBorder(new EtchedBorder(), "Display Area"));

		// create the middle panel components
		display = new JTextArea(25, 80);
		display.setEditable(false); // set textArea non-editable
		display.setFont(DEFAULT_FONT);
		display.setAutoscrolls(true);
		display.setBackground(Color.BLACK);
		display.setCaretColor(Color.WHITE);


		JScrollPane scroll = new JScrollPane(display);
		new SmartScroller(scroll);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		middlePanel.add(scroll);

		JFrame frame = new JFrame("I/O Window");
		display.addKeyListener(new ConsoleKeyHandler(consoleDevice));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(middlePanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// preload the keyboard buffer as a test
//		keysPressed.add((char) 0x0a);
//		keysPressed.add('O');
//		keysPressed.add('L');
//		keysPressed.add('L');
//		keysPressed.add('E');
//		keysPressed.add('H');
	}

	public void showGUI(final ConsoleDevice consoleDevice) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(consoleDevice);
			}
		});
	}


}
