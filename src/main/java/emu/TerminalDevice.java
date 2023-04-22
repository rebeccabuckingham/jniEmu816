package emu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

/*
 * alternative to the ConsoleDevice: simulate a Terminal by sitting on port 2000.
 * takes advantage of built-in ANSI emulation under telnet.
 */
public class TerminalDevice extends BusDeviceBase {
	private final static int CHAR_READY_FLAG = 0;			// memory location that's set when there's input waiting
	private final static int CHAR_OUT = 0;						// memory location to write to screen
	private final static int CURRENT_CHAR = 1;				// get char that's waiting, will *not* move the pointer
	private final static int CHARGOT = 2;

	private static final int port = 2000;
	private final ServerSocket serverSocket;
	private final Stack<Character> keysPressed;

	public Stack<Character> getKeyStack() { return keysPressed; }

	public static void main(String[] args) throws Exception {
		TerminalDevice td = new TerminalDevice( 0xD000, 4);
		td.runServer();
	}

	public TerminalDevice(int startAddress, int size) throws Exception {
		super("Socket Terminal Device", startAddress, size);
		keysPressed = new Stack<>();
		serverSocket = new ServerSocket(port);
	}

	private boolean isConnected = false;

	public boolean connected() {
		return isConnected;
	}

	// going to want to wrap this in a thread...
	public void runServer() {
		Thread t = new Thread(new Server());
		t.start();
	}

	OutputStream out;

	private class Server implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					Socket socket = serverSocket.accept();
					InputStream in = socket.getInputStream();
					out = socket.getOutputStream();

					isConnected = true;
					System.out.println("looks like we're connected");

					while (true) {
						if (in.available() > 0) {
							int c = in.read();
							keysPressed.push((char) c);
						}
					}
				}
			} catch (IOException e) {

			}
		}
	}

	private synchronized boolean charAvailable() {
		return keysPressed.size() > 0;
	}

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

				value = (short) (getKeyStack().peek() & 0xFF);
			}
		}

		if ( (ea == 0 && value != 0) || (ea == 1 && value != -1))
			System.out.printf("readMemory ea:%d, value:%d\n", ea, value);

		return value;
	}

	@Override
	public void writeMemory(int address, short value) {
		int ea = effectiveAddress(address);
		char c = (char) value;

		if (ea == CHAR_OUT) {
			if (out != null) {
				try {
					out.write((int) c);
				} catch (Exception e) {
					System.out.println("Could not write to socket: " + e.getMessage());
				}
			} else {
				System.out.println("TerminalDevice: output suppressed, no client connected yet!");
			}
		} else if (ea == CHARGOT) {
			// can dispose of character now
			keysPressed.pop();
		}
	}
}
