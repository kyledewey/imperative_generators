public class IntExpIterator implements GenIterator<Exp> {
    private final GenIterator<Integer> around;
    public IntExpIterator() {
        around = new IntIterator();
    }

    public boolean hasNext() {
        return around.hasNext();
    }

    public Exp next() {
        return new IntExp(around.next().intValue());
    }

    public void reset() {
        around.reset();
    }
}
