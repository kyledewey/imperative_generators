public class ParenExpIterator implements GenIterator<Exp> {
    private final GenIterator<Exp> around;
    public ParenExpIterator(final int depth) {
        if (depth <= 0) {
            around = new EmptyIterator<Exp>();
        } else {
            around = new ExpIterator(depth - 1);
        }
    }

    public boolean hasNext() {
        return around.hasNext();
    }

    public Exp next() {
        return new ParenExp(around.next());
    }

    public void reset() {
        around.reset();
    }
}
