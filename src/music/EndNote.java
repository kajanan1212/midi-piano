package music;

public class EndNote extends NoteEvent{

	public EndNote(Pitch pitch) {
		this(pitch, 0);
	}
	
	public EndNote(Pitch pitch, int delay) {
		super(pitch,delay);
	}

	@Override
	public EndNote delayed(int delay) {
		return new EndNote(pitch, delay);
	}

	@Override
	public void execute(MusicMachine m) {
		m.endNote(this);
	}

}