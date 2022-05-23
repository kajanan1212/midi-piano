package music;

public class BeginNote extends NoteEvent{

	public BeginNote(Pitch pitch) {
		this(pitch, 0);
	}
	
	public BeginNote(Pitch pitch, int delay) {
		super(pitch,delay);
	}

	@Override
	public BeginNote delayed(int delay) {
		return new BeginNote(pitch, delay);
	}

	@Override
	public void execute(MusicMachine m) {
		m.beginNote(this);
	}

}
