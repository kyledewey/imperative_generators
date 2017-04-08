public class ParenExp implements Exp {
    public final Exp around;
    public ParenExp(final Exp around) {
        this.around = around;
    }

    public String toString() {
        return "(" + around.toString() + ")";
    }
}
