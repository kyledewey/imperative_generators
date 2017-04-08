import java.util.ArrayList;

public class OrIterator<A> implements GenIterator<A> {
    // begin instance variables
    private final ArrayList<GenIterator<A>> iterators;
    private int index;
    // end instance variables
        
    public OrIterator(final ArrayList<GenIterator<A>> iterators) {
        this.iterators = iterators;
        index = 0;
        determineNextIterator();
    }

    private void determineNextIterator() {
        while (index < iterators.size() &&
               !iterators.get(index).hasNext()) {
            index++;
        }
    }
    
    public boolean hasNext() {
        return (index < iterators.size() &&
                iterators.get(index).hasNext());
    }

    public A next() {
        final A retval = iterators.get(index).next();
        determineNextIterator();
        return retval;
    } // next

    public void reset() {
        for (final GenIterator<A> iterator : iterators) {
            iterator.reset();
        }
        index = 0;
        determineNextIterator();
    }
} // OrIterator
