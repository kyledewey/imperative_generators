public class BinopExp implements Exp {
    public final Exp left;
    public final Operator op;
    public final Exp right;

    public BinopExp(final Exp left, final Operator op, final Exp right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public String toString() {
        return left.toString() + op.toString() + right.toString();
    }
}
