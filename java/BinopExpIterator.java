public class BinopExpIterator implements GenIterator<Exp> {
    private final GenIterator<Exp> around;

    public BinopExpIterator(final int depth) {
        if (depth <= 0) {
            around = new EmptyIterator<Exp>();
        } else {
            around = new ThreeIterator<Exp, Operator, Exp, Exp>(new ExpIterator(depth - 1),
                                                                new OpIterator(),
                                                                new ExpIterator(depth - 1)) {
                    protected Exp makeResult(final Exp left, final Operator op, final Exp right) {
                        return new BinopExp(left, op, right);
                    }
                };
        }
    }

    public boolean hasNext() {
        return around.hasNext();
    }

    public Exp next() {
        return around.next();
    }

    public void reset() {
        around.reset();
    }
}
