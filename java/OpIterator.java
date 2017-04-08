import java.util.ArrayList;

public class OpIterator extends ArrayIterator<Operator> {
    public OpIterator() {
        super(new ArrayList<Operator>() {{
            add(Operator.OPERATOR_PLUS);
            add(Operator.OPERATOR_MINUS);
            add(Operator.OPERATOR_TIMES);
            add(Operator.OPERATOR_DIV);
        }});
    }
}
