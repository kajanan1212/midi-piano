package piano;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import midi.Midi;
import music.NoteEvent;

public class PianoPlayer {
	private final BlockingQueue<NoteEvent> queue, delayQueue;
	private final PianoMachine machine;
	private final PianoApplet applet;
	private boolean isPlayback;
	
	public PianoPlayer(PianoApplet applet) {
		this.applet = applet;
		isPlayback = false;
		queue = new LinkedBlockingQueue<>();
		delayQueue = new LinkedBlockingQueue<>();
		machine = new PianoMachine(this);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				processQueue();
			}
		},"processQueue").start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				processDelayQueue();
			}
		},"processDelayQueue").start();
	
	}
	
	public void request(NoteEvent event) {
		try {
			queue.put(event);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void nextInstrument() {
		machine.nextInstrument();
	}
	
	public String requestInstrumentName() {
		return machine.getInstrumentName();
	}
	
	public void requestPlayback() {
		machine.requestPlayback();
	}
	
	public void toggleRecording() {
		machine.toggleRecording();
	}
	
	public void playbackRecording(List<NoteEvent> lastRecording) {
		if(isPlayback) {return;}
		for(NoteEvent event:lastRecording) {
			try {
				delayQueue.put(event);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		if(lastRecording.isEmpty()) {
			applet.setPlaybackColor(Color.GREEN);
		}
		else {
			isPlayback = true;
		}
	}
	
	public void processQueue() {
		while(true) {
			try {
				NoteEvent e = queue.take();
				e.execute(machine);
			} catch (InterruptedException e1) {
				System.out.println(e1.getMessage());
			}	
		}
	}
	
	public void processDelayQueue() {
		while(true) {
			try {
				NoteEvent e = delayQueue.take();
				Midi.wait(e.getDelay());
				queue.put(e);
				if(isPlayback && delayQueue.isEmpty()) {
					Thread.sleep(200);
					isPlayback = false;
					applet.setPlaybackColor(Color.GREEN);
				}
			} catch (InterruptedException e1) {
				System.out.println(e1.getMessage());
			}	
		}
	}
}
