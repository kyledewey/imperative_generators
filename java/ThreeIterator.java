public abstract class ThreeIterator<A, B, C, D> implements GenIterator<D> {
    private final GenIterator<A> itA;
    private A lastA;
    private final GenIterator<B> itB;
    private B lastB;
    private final GenIterator<C> itC;
    private boolean doesHaveNext;
    
    public ThreeIterator(final GenIterator<A> itA,
                         final GenIterator<B> itB,
                         final GenIterator<C> itC) {
        this.itA = itA;
        this.itB = itB;
        this.itC = itC;
        lastA = null;
        lastB = null;

        reset();
    }

    public void reset() {
        itA.reset();
        itB.reset();
        itC.reset();
        doesHaveNext = (itA.hasNext() &&
                        itB.hasNext() &&
                        itC.hasNext());
        if (doesHaveNext) {
            lastA = itA.next();
            lastB = itB.next();
        }
    }
    
    protected abstract D makeResult(final A a, final B b, final C c);

    private void increment() {
        if (!itC.hasNext()) {
            // C is out - try to increment B
            itC.reset();
            if (!itB.hasNext()) {
                // B is out - try to increment A
                itB.reset();
                lastB = itB.next();
                if (!itA.hasNext()) {
                    // A is out - nothing more
                    doesHaveNext = false;
                } else {
                    lastA = itA.next();
                }
            } else {
                lastB = itB.next();
            }
        }
    } // increment
    
    public boolean hasNext() {
        return doesHaveNext;
    }

    public D next() {
        final D retval = makeResult(lastA, lastB, itC.next());
        increment();
        return retval;
    }
} // ThreeIterator
