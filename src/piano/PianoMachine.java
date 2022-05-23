package piano;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import midi.Midi;
import music.MusicMachine;
import music.NoteEvent;
import music.Pitch;

public class PianoMachine implements MusicMachine {
	private final Midi midi;
	private final PianoPlayer player;
	private boolean isRecording;
	private List<NoteEvent> recording, lastRecording;
	private Set<Pitch> pitchesPlaying;
	private long recordingStartedTime;
	
	public PianoMachine(PianoPlayer player) {
		isRecording = false;
		lastRecording = new ArrayList<>();
		pitchesPlaying = new HashSet<>();
		this.player = player;
		midi = new Midi();
	}
	
	public void toggleRecording() {
		if(isRecording) {
			lastRecording = recording;
		}
		else {
			recording = new ArrayList<>();
			recordingStartedTime = System.currentTimeMillis();
		}
		isRecording = !isRecording;
	}
	
	@Override
	public void beginNote(NoteEvent event) {
		Pitch pitch = event.getPitch();
		if(pitchesPlaying.contains(pitch)) {return;}
		pitchesPlaying.add(pitch);
		midi.beginNote(pitch.toMidiFrequency());
		addToRecording(event);
	}
	
	@Override
	public void endNote(NoteEvent event) {
		Pitch pitch = event.getPitch();
		if(pitchesPlaying.remove(pitch)) {
			midi.endNote(pitch.toMidiFrequency());
			addToRecording(event);
		}
	}
	
	public void addToRecording(NoteEvent event) {
		if(isRecording) {
			recording.add(event.delayed((int)(-recordingStartedTime + (recordingStartedTime = System.currentTimeMillis()))));
		}
	}

	public void requestPlayback() {
		player.playbackRecording(lastRecording);
	}
	
	public String getInstrumentName() {
		return midi.getInstrument().name();
	}

	public void nextInstrument() {
		midi.setInstrument(midi.nextInstrument());
	}
}
