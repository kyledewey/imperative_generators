import java.util.ArrayList;

public class ExpIterator extends OrIterator<Exp> {
    public ExpIterator(final int depth) {
        super(new ArrayList<GenIterator<Exp>>() {{
            add(new IntExpIterator());
            add(new BinopExpIterator(depth));
            add(new ParenExpIterator(depth));
        }});
    }
}
