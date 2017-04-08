import java.util.ArrayList;

public class IntIterator extends ArrayIterator<Integer> {
    // begin constants
    private static final ArrayList<Integer> ints =
        new ArrayList<Integer>() {{
            add(new Integer(0));
            add(new Integer(1));
            add(new Integer(2));
        }};
    // end constants
    public IntIterator() {
        super(ints);
    }
}

