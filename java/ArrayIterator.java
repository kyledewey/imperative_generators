import java.util.ArrayList;

public class ArrayIterator<A> implements GenIterator<A> {
    // begin instance variables
    private final ArrayList<A> as;
    private int index;
    // end instance variables
    
    public ArrayIterator(final ArrayList<A> as) {
        this.as = as;
        index = 0;
    }

    public boolean hasNext() {
        return index < as.size();
    }

    public A next() {
        return as.get(index++);
    }

    public void reset() {
        index = 0;
    }
} // ArrayIterator
