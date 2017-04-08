public class IntExp implements Exp {
    public final int theInt;
    public IntExp(final int theInt) {
        this.theInt = theInt;
    }

    public String toString() {
        return Integer.toString(theInt);
    }
}
