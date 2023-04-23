package emu;

import emu.gui.ConsoleKeyHandler;
import emu.gui.SmartScroller;
import emu.ui.Console;

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
	private static final int CONSOLE_BORDER_WIDTH = 10;

	public Console console = null;

	public final Stack<Character> keysPressed = new Stack<>();

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

	public boolean echoMode = false;
	public boolean noCpuMode = false;

	/**
	 * readMemory() in this case is how the emulated machine gets input from the keyboard
	 */
	@Override
	public short readMemory(int address) {
		int ea = effectiveAddress(address);
		short value = 0;

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

				value = (short) (keysPressed.peek() & 0xFF);
			}
		}

		if ( (ea == 0 && value != 0) || (ea == 1 && value != -1))
			System.out.printf("readMemory ea:%d, value:%d\n", ea, value);

		return value;
	}

	/**
	 * writeMemory() is how the emulated machine writes to the screen
	 * and how it tells the console it read a character
	 */

	@Override
	public void writeMemory(int address, short value) {
		int ea = effectiveAddress(address);
		char c = (char) value;

		if (ea == CHAR_OUT) {
			console.print(String.valueOf(c));
			console.repaint();
		} else if (ea == CHARGOT) {
			// can dispose of character now
			keysPressed.pop();
		}
	}

	public void createAndShowGUI(final ConsoleDevice consoleDevice) {
		console = new Console(80, 25, DEFAULT_FONT, false);

		JFrame frame = new JFrame("Console");
		frame.addKeyListener(new ConsoleKeyHandler(this));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(console, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	public void handleKeyEcho(char c) {
		if (echoMode) {
			console.print(String.valueOf(c));
			console.repaint();
		}
	}

	public void showGUI(final ConsoleDevice consoleDevice) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(consoleDevice);
			}
		});
	}

}
