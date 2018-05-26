// e \in Exp ::= i | plus(e1, e2)

// AST:
// { nodeType: String,
//   ... }

function Literal(i) {
    this.nodeType = "literal";
    this.i = i;
    this.toString = function() {
        return i.toString();
    };
    this.processDot = function (parent, dotMaker) {
        dotMaker.addNode(parent, "" + i);
    };
}

function Plus(e1, e2) {
    this.nodeType = "plus";
    this.e1 = e1;
    this.e2 = e2;
    this.toString = function() {
        return "(" + e1.toString() + " + " + e2.toString() + ")";
    };
    this.processDot = function (parent, dotMaker) {
        var me = dotMaker.addNode(parent, "+");
        this.e1.processDot(me, dotMaker);
        this.e2.processDot(me, dotMaker);
    };
}

function exp(depth) {
    const literals = Or(Singleton(new Literal(1)),
                        Singleton(new Literal(2)),
                        Singleton(new Literal(3)));
    if (depth > 0) {
        var nested = And(exp(depth - 1),
                         e1 => And(exp(depth - 1),
                                   e2 => Singleton(new Plus(e1, e2))));
        return Or(literals, nested);
    } else {
        return literals;
    }
}
