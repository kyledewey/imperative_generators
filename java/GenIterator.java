public interface GenIterator<A> {
    public boolean hasNext();
    public A next();
    public void reset();
}
