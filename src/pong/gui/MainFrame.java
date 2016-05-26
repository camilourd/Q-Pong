package pong.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import pong.agent.Agent;
import pong.agent.BallFollower;
import pong.agent.KeyboardAgent;
import pong.agent.RandomPlayer;
import pong.agent.qpong.QPong;
import pong.agent.qpong.QPongNoDirection;
import pong.environment.Bound;
import pong.environment.Pong;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -2471338664392279677L;
	
	protected JSpinner widthSpinner;
	protected JSpinner heightSpinner;
	
	protected JComboBox<String> player1ComboBox = new JComboBox<String>();
	protected JComboBox<String> player2ComboBox = new JComboBox<String>();
	
	protected JSpinner iter1Spinner;
	protected JSpinner iter2Spinner;
	
	protected JSpinner gamma1Spinner;
	protected JSpinner gamma2Spinner;
	
	protected JButton playButton = new JButton("Play");
	
	public MainFrame() {
		setTitle("Pong");
		addGamePanel();
	}

	private void addGamePanel() {
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		addGameSizePanel(content);
		iter1Spinner = createSpinner(5, 1, 30, 1);
		iter2Spinner = createSpinner(5, 1, 30, 1);
		gamma1Spinner = createSpinner(0.5, 0, 1, 0.01);
		gamma2Spinner = createSpinner(0.5, 0, 1, 0.01);
		addPlayerPanel(content, "Player 1:", player1ComboBox, iter1Spinner, gamma1Spinner);
		addPlayerPanel(content, "Player 2:", player2ComboBox, iter2Spinner, gamma2Spinner);
		addPlayPanel(content);
		this.getContentPane().add(content,  BorderLayout.CENTER);
	}

	private void addGameSizePanel(JPanel panel) {
		JPanel gameSizePanel = new JPanel();
		addLabel(gameSizePanel, "Dimensions: ");
		addLabel(gameSizePanel, "width");
		widthSpinner = createSpinner(49, 3, 49, 1);
		gameSizePanel.add(widthSpinner);
		addLabel(gameSizePanel, "Height");
		heightSpinner = createSpinner(30, 3, 30, 1);
		gameSizePanel.add(heightSpinner);
		panel.add(gameSizePanel);
	}

	private void addLabel(JPanel panel, String text) {
		JLabel widthLabel = new JLabel(text);
		panel.add(widthLabel);
	}
	
	private JSpinner createSpinner(double start, double min, double max, double step) {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(start, min, max, step));
		spinner.setPreferredSize(new Dimension(40, 20));
		return spinner;
	}
	
	private void addPlayerPanel(JPanel panel, String label, JComboBox<String> comboBox, JSpinner iter, JSpinner gamma) {
		JPanel playerPanel = new JPanel();
		addLabel(playerPanel, label);
		addPlayerComboBox(playerPanel, comboBox);
		addLabel(playerPanel, "iterations");
		playerPanel.add(iter);
		addLabel(playerPanel, "gamma");
		playerPanel.add(gamma);
		panel.add(playerPanel);
	}

	private void addPlayerComboBox(JPanel panel, JComboBox<String> comboBox) {
		comboBox.addItem("Random");
		comboBox.addItem("Ball Follower");
		comboBox.addItem("QPong");
		comboBox.addItem("QPong without ball direction");
		comboBox.addItem("Human");
		comboBox.setPreferredSize(new Dimension(100, 20));
		panel.add(comboBox);
	}
	
	private void addPlayPanel(JPanel panel) {
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Agent player1 = createAgent(player1ComboBox, iter1Spinner, gamma1Spinner);
				Agent player2 = createAgent(player2ComboBox, iter2Spinner, gamma2Spinner);
				System.out.println(widthSpinner.getValue().getClass());
				Bound dimension =  new Bound((int) ((double) widthSpinner.getValue()), (int) ((double)heightSpinner.getValue()));
				Pong game = new Pong(dimension, player1, player2);
				GameFrame main = new GameFrame(game);
				if(player1.getClass() == KeyboardAgent.class)
					main.addKeyListener((KeyListener) player1);
				if(player2.getClass() == KeyboardAgent.class)
					main.addKeyListener((KeyListener) player2);
				main.setFocusable(true);
				main.pack();
				main.setLocationRelativeTo(null);
				main.setVisible(true);
				main.play();
			}

			private Agent createAgent(JComboBox<String> comboBox, JSpinner iter, JSpinner gamma) {
				if(comboBox.getSelectedIndex() == 0)
					return new RandomPlayer();
				if(comboBox.getSelectedIndex() == 1)
					return new BallFollower((double) gamma.getValue());
				if(comboBox.getSelectedIndex() == 2)
					return new QPong((double) gamma.getValue(), (int) ((double) iter.getValue()));
				if(comboBox.getSelectedIndex() == 3)
					return new QPongNoDirection((double) gamma.getValue(), (int) ((double) iter.getValue()));
				return new KeyboardAgent();
			}
			
		});
		panel.add(playButton);
	}

}
