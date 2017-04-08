public class EmptyIterator<A> implements GenIterator<A> {
    public boolean hasNext() { return false; }
    public A next() { assert(false); return null; }
    public void reset() {}
}
