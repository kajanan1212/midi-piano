package piano;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import music.BeginNote;
import music.EndNote;
import music.NoteEvent;
import music.Pitch;

public class PianoApplet extends Applet {
	
	private static final long serialVersionUID = 1L;
	private static final Map<Character,Pitch> key_pitches;
	private Map<Character,JLabel> pitchLabels;
	private JLabel instrument, recording, playback;
	private Set<Character> pressedPitches;
	
	static {
		key_pitches = new LinkedHashMap<>();
		char[] keys = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '='};
		for(int i = 0 ; i < 12; i++) {
			key_pitches.put(keys[i], new Pitch(i));
		}
	}

	public void init() {
		final PianoPlayer player = new PianoPlayer(this);
		
		pressedPitches = new HashSet<>();
		
		pitchLabels = new HashMap<>();
		
		designUI(player.requestInstrumentName());
		
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				char key = (char) e.getKeyCode();
				switch (key) {
				case 'I':
					player.nextInstrument();
					instrument.setText(player.requestInstrumentName());
					return;
				case 'P':
					setPlaybackColor(Color.MAGENTA);
					player.requestPlayback();
					return;
				case 'R':
					toggleRecordingColor();
					player.toggleRecording();
					return;
				}
				if(pitchLabels.containsKey(key)	&& !pressedPitches.contains(key)) {
					pressedPitches.add(key);
					NoteEvent ne = new BeginNote(key_pitches.get(key));
					pitchLabels.get(key).setBackground(Color.LIGHT_GRAY);
					player.request(ne);
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				char key = (char) e.getKeyCode();
				if(pitchLabels.containsKey(key) && pressedPitches.contains(key)) {
					pressedPitches.remove(key);
					NoteEvent ne = new EndNote(key_pitches.get(key));
					pitchLabels.get(key).setBackground(pitchLabels.get(key).getText().length()==1?Color.WHITE:Color.BLACK);
					player.request(ne);
				}
			}
		});
	}

	public void designUI(String InstrumentName) {
		resize((300+(12*60)), 300);
		setLayout(null);
		setFocusable(true);
		add(instrument = createLabel(InstrumentName, Color.YELLOW, 0, 0, 300, 100, false));
		add(recording = createLabel("Recording", Color.GREEN, 0, 100, 150, 200, false));
		add(playback = createLabel("Playback", Color.GREEN, 150, 100, 150, 200, false));
		int x = 300;
		Set<Character> keys = key_pitches.keySet();
		for(Character key: keys) {
			String c = key_pitches.get(key).toString();
			Color color = Color.WHITE;
			if(c.length()!=1) {color = Color.BLACK;}
			pitchLabels.put(key, createLabel(c, color, x, 0, 60, 300, true));
			add(pitchLabels.get(key));
			x += 60;
		}
		setVisible(true);
	}
	
	public JLabel createLabel(String text, Color color, int x, int y, int width, int height, boolean isBorder) {
		JLabel label = new JLabel(text);
		label.setBackground(color);
		label.setForeground(Color.BLACK);
		label.setFont(new Font(Font.SERIF, Font.ROMAN_BASELINE, 24));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBounds(x, y, width, height);
		label.setBorder(isBorder?BorderFactory.createLineBorder(Color.BLACK):null);
		label.setOpaque(true);
		return label;
	}
	
	private void toggleColor(JLabel label, Color colorOne, Color colorTwo) { 
		label.setBackground(label.getBackground().equals(colorOne)?colorTwo:colorOne);
	}
	
	protected void toggleRecordingColor() {
		toggleColor(recording, Color.RED, Color.GREEN);
	}
	
	protected void setPlaybackColor(Color color) {
		playback.setBackground(color);
	}
}