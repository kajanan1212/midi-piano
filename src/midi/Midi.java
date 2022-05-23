package midi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class Midi {
    private Synthesizer synthesizer;

    public final static Instrument DEFAULT_INSTRUMENT = Instrument.PIANO;
    
    private LinkedList<Instrument> instruments;
    
    private Instrument instrument;
    
    private final Map<midi.Instrument, MidiChannel> channels = new HashMap<>();
    
    private int nextChannel = 0;
    
    private static final int VELOCITY = 100; 

    private void checkRep() {
        assert synthesizer != null;
        assert channels != null;
        assert nextChannel >= 0;
    }
    
    public Midi() {
        try {
        	instruments = new LinkedList<>(Arrays.asList(Instrument.values()));
        	instrument = DEFAULT_INSTRUMENT;
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			synthesizer.loadAllInstruments(synthesizer.getDefaultSoundbank());
			checkRep();
		} catch (MidiUnavailableException e) {
			System.out.println(e.getMessage());
		}
    }
    
	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}
	
	public Instrument nextInstrument() {
		instruments.addLast(instruments.removeFirst());
		return instruments.getFirst();
    }

	public void play(int note, int duration, Instrument instrument) {
        MidiChannel channel = getChannel(instrument);
        synchronized (channel) {
            channel.noteOn(note, VELOCITY);
        }
        wait(duration);
        synchronized (channel) {
            channel.noteOff(note);
        }
    }

    public void beginNote(int note, Instrument instrument) {
        MidiChannel channel = getChannel(instrument);
        synchronized (channel) {
            channel.noteOn(note, VELOCITY);
        }
    }

    public void beginNote(int note) {
        beginNote(note, instrument);
    }

    public void endNote(int note, Instrument instrument) {
        MidiChannel channel = getChannel(instrument);
        synchronized (channel) {
            channel.noteOff(note, VELOCITY);
        }
    }

    public void endNote(int note) {
        endNote (note, instrument);
    }

    public static void wait(int duration) {
        long now = System.currentTimeMillis();
        long end = now + duration;
        while (now < end) {
            try {
                Thread.sleep((int) (end - now));
            } catch (InterruptedException e) {
            }
            now = System.currentTimeMillis();
        }
    }
    
    private MidiChannel getChannel(Instrument instrument) {
        synchronized (channels) {
            MidiChannel channel = channels.get(instrument);
            if (channel != null) return channel;
            
            channel = allocateChannel();
            patchInstrumentIntoChannel(channel, instrument);            
            channels.put(instrument, channel);
            checkRep();
            return channel;
        }        
    }

    private MidiChannel allocateChannel() {
        MidiChannel[] channels = synthesizer.getChannels();
        if (nextChannel >= channels.length) throw new RuntimeException("tried to use too many instruments: limited to " + channels.length);
        MidiChannel channel = channels[nextChannel];
        nextChannel = (nextChannel + 1) % channels.length;
        return channel;
    }
    
    private void patchInstrumentIntoChannel(MidiChannel channel, Instrument instrument) {
        channel.programChange(0, instrument.ordinal());        
    }
}
