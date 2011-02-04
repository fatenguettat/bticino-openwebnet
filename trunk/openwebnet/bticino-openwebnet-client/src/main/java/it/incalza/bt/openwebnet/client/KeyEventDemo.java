package it.incalza.bt.openwebnet.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;

public class KeyEventDemo extends JFrame implements KeyListener, ActionListener
{

	public static final String COMANDO_1 = "";
	public static final String COMANDO_2 = "";
	public static final String COMANDO_3 = "";
	public static final String COMANDO_4 = "";
	public static final String COMANDO_5 = "";
	public static final String COMANDO_6 = "";
	public static final String COMANDO_7 = "";
	public static final String COMANDO_8 = "";

	private static final long serialVersionUID = -4949580537359166179L;
	private static final Logger logger = Logger.getLogger(KeyEventDemo.class);
	static JTextArea displayArea;
	JTextField typingArea;
	static boolean stopSend;
	static final String newline = System.getProperty("line.separator");

	public static void main(String[] args)
	{
		/* Use an appropriate Look and Feel */
		try
		{
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		}
		catch (UnsupportedLookAndFeelException ex)
		{
			ex.printStackTrace();
		}
		catch (IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
		catch (InstantiationException ex)
		{
			ex.printStackTrace();
		}
		catch (ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		// Schedule a job for event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				createAndShowGUI();
			}
		});
	}

	/**
	 * Create the GUI and show it. For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI()
	{
		// Create and set up the window.
		KeyEventDemo frame = new KeyEventDemo("KeyEventDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set up the content pane.
		frame.addComponentsToPane();

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private void addComponentsToPane()
	{

		JButton button = new JButton("Clear");
		JButton onOff = new JButton("On/Off");
		button.addActionListener(this);

		onOff.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (stopSend)
				{
					stopSend = false;
					logger.info("#### KEY LISTNER AVVIATO ####");
				}
				else
				{
					stopSend = true;
					logger.info("#### KEY LISTNER STOPPATO ####");
				}

			}
		});

		typingArea = new JTextField(20);
		typingArea.addKeyListener(this);

		// Uncomment this if you wish to turn off focus
		// traversal. The focus subsystem consumes
		// focus traversal keys, such as Tab and Shift Tab.
		// If you uncomment the following line of code, this
		// disables focus traversal and the Tab events will
		// become available to the key event listener.
		// typingArea.setFocusTraversalKeysEnabled(false);

		displayArea = new JTextArea();
		displayArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(displayArea);
		scrollPane.setPreferredSize(new Dimension(375, 125));

		getContentPane().add(typingArea, BorderLayout.PAGE_START);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(onOff, BorderLayout.AFTER_LINE_ENDS);
		getContentPane().add(button, BorderLayout.PAGE_END);
	}

	public KeyEventDemo(String name)
	{
		super(name);
	}

	/** Handle the key typed event from the text field. */
	public void keyTyped(KeyEvent e)
	{
		displayInfo(e, "KEY TYPED: ");
	}

	/** Handle the key pressed event from the text field. */
	public void keyPressed(KeyEvent e)
	{
		displayInfo(e, "KEY PRESSED: ");
	}

	/** Handle the key released event from the text field. */
	public void keyReleased(KeyEvent e)
	{
		displayInfo(e, "KEY RELEASED: ");
	}

	/** Handle the button click. */
	public void actionPerformed(ActionEvent e)
	{
		// Clear the text components.
		displayArea.setText("");
		typingArea.setText("");

		// Return the focus to the typing area.
		typingArea.requestFocusInWindow();
	}

	/*
	 * We have to jump through some hoops to avoid
	 * trying to print non-printing characters
	 * such as Shift. (Not only do they not print,
	 * but if you put them in a String, the characters
	 * afterward won't show up in the text area.)
	 */
	private void displayInfo(KeyEvent e, String keyStatus)
	{

		// You should only rely on the key char if the event
		// is a key typed event.
		int id = e.getID();
		String keyString;
		if (id == KeyEvent.KEY_TYPED)
		{
			char c = e.getKeyChar();
			keyString = "key character = '" + c + "'";
		}
		else
		{
			int keyCode = e.getKeyCode();
			keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")";

			if (KeyEvent.getKeyText(keyCode).equals(COMANDO_1))
			{
				logger.info("#### COMANDO_1 LETTO ####");
				Client.getInstance().sendComand(Client.LUCE_DEMO_1);
			}
			else if (KeyEvent.getKeyText(keyCode).equals(COMANDO_2))
			{
				logger.info("#### COMANDO_2 LETTO ####");
				Client.getInstance().sendComand(Client.LUCE_DEMO_2);
			}
			else if (KeyEvent.getKeyText(keyCode).equals(COMANDO_3))
			{
				logger.info("#### COMANDO_3 LETTO ####");
				Client.getInstance().sendComand(Client.LUCE_DEMO_3);
			}
			else if (KeyEvent.getKeyText(keyCode).equals(COMANDO_4))
			{
				logger.info("#### COMANDO_4 LETTO ####");
				Client.getInstance().sendComand(Client.LUCE_DEMO_4);
			}
			else if (KeyEvent.getKeyText(keyCode).equals(COMANDO_5))
			{
				logger.info("#### COMANDO_5 LETTO ####");
				Client.getInstance().sendComand(Client.LUCE_DEMO_5);
			}
			else if (KeyEvent.getKeyText(keyCode).equals(COMANDO_6))
			{
				logger.info("#### COMANDO_6 LETTO ####");
				Client.getInstance().sendComand(Client.LUCE_DEMO_6);
			}
			else if (KeyEvent.getKeyText(keyCode).equals(COMANDO_7))
			{
				logger.info("#### COMANDO_7 LETTO ####");
				Client.getInstance().sendComand(Client.LUCE_DEMO_7);
			}
			else if (KeyEvent.getKeyText(keyCode).equals(COMANDO_8))
			{
				logger.info("#### COMANDO_8 LETTO ####");
				Client.getInstance().sendComand(Client.LUCE_DEMO_8);
			}

		}

		int modifiersEx = e.getModifiersEx();
		String modString = "extended modifiers = " + modifiersEx;
		String tmpString = KeyEvent.getModifiersExText(modifiersEx);
		if (tmpString.length() > 0)
		{
			modString += " (" + tmpString + ")";
		}
		else
		{
			modString += " (no extended modifiers)";
		}

		String actionString = "action key? ";
		if (e.isActionKey())
		{
			actionString += "YES";
		}
		else
		{
			actionString += "NO";
		}

		String locationString = "key location: ";
		int location = e.getKeyLocation();
		if (location == KeyEvent.KEY_LOCATION_STANDARD)
		{
			locationString += "standard";
		}
		else if (location == KeyEvent.KEY_LOCATION_LEFT)
		{
			locationString += "left";
		}
		else if (location == KeyEvent.KEY_LOCATION_RIGHT)
		{
			locationString += "right";
		}
		else if (location == KeyEvent.KEY_LOCATION_NUMPAD)
		{
			locationString += "numpad";
		}
		else
		{ // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
			locationString += "unknown";
		}

		logger.info(keyStatus + newline + "    " + keyString + newline + "    " + modString + newline + "    " + actionString + newline + "    " + locationString + newline);
		// displayArea.setCaretPosition(displayArea.getDocument().getLength());
	}
}