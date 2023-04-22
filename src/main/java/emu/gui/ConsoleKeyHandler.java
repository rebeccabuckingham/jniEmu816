package emu.gui;

import emu.ConsoleDevice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

public class ConsoleKeyHandler implements KeyListener, ActionListener {
	ConsoleDevice consoleDevice;

	public ConsoleKeyHandler(ConsoleDevice consoleDevice) {
		this.consoleDevice = consoleDevice;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		//System.out.println("actionPerformed: " + actionEvent.toString());
	}

	/** Handle the key typed event from the text field. */
	public void keyTyped(KeyEvent e) {
		//System.out.println("KEY TYPED: " + e.toString());
		consoleDevice.addKeyPressed(e.getKeyChar());
	}

	/** Handle the key pressed event from the text field. */
	public void keyPressed(KeyEvent e) {
		// shift + F13: dump to stderr the contents of the buffer
		if (e.getKeyCode() == 61440 && e.getModifiersEx() == 64) {
			System.out.println("consoleDevice.charAvailable(): " + consoleDevice.charAvailable());
			Stack<Character> keyStack = consoleDevice.getKeyStack();
			keyStack.forEach(c -> {
				if (c < ' ')
					System.out.printf("char: %02x\n", (int) c);
				else
					System.out.println("char: '" + c + "'");

			});
		}

		// alt + F13: empty the buffer
		if (e.getKeyCode() == 61440 && e.getModifiersEx() == 512) {
			consoleDevice.getKeyStack().clear();
		}

//		System.out.println("KEY PRESSED: " + e.toString());
//		System.out.println("modifiers: " + e.getModifiersEx());
//		System.out.println("keyCode:   " + e.getKeyCode());
	}

	/** Handle the key released event from the text field. */
	public void keyReleased(KeyEvent e) {
	//	System.out.println("KEY RELEASED: " + e.toString());
	}
}