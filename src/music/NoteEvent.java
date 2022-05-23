package music;

public abstract class NoteEvent {
	protected final Pitch pitch;
	protected final int delay;
	
	public NoteEvent(Pitch pitch) {
		this(pitch, 0);
	}
	
	public NoteEvent(Pitch pitch, int delay) {
		this.pitch = pitch;
		this.delay = delay;
	}
	
	public Pitch getPitch() {
		return pitch;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public abstract NoteEvent delayed(int delay);
	public abstract void execute(MusicMachine m);

}
