var mainGenerator;

// e \in Exp ::= i | plus(e1, e2)

// AST:
// { nodeType: String,
//   ... }

function Node(id, parent, label) {
    this.id = id;
    this.parent = parent;
    this.label = label;
    this.toString = function () {
        return "Node(" + id + ", " + parent + ", " + label + ")";
    };
}

function DotMaker() {
    this.nextNodeId = 0;
    this.nodes = [];

    // returns the id of the created node
    this.addNode = function (parent, label) {
        var id = this.nextNodeId;
        this.nodes.push(new Node(id, parent, label));
        this.nextNodeId++;
        return id;
    };

    this.toDot = function () {
        var retval = "digraph {\n";
        this.nodes.forEach(function (node) {
            retval += "n" + node.id + " [label=\"" + node.label + "\"];\n";
        });
        this.nodes.forEach(function (node) {
            if (node.parent !== null) {
                retval += "n" + node.parent + " -> n" + node.id + ";\n";
            }
        });
        retval += "}";
        return retval;
    };
}

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

const emptyFlag = { done: true };
const empty = {
    next: function() {
        return emptyFlag;
    }
};

function Singleton(a) {
    var got = false;
    const next = function() {
        if (got) {
            return emptyFlag;
        } else {
            got = true;
            return { done: false, value: a };
        }
    };
    return { next: next };
}

function And(baseGen, makeGen) {
    var curGen = "init";
    const refresh = function() {
        var base = baseGen.next();
        if (base.done) {
            curGen = "empty";
        } else {
            curGen = makeGen(base.value);
        }
    };
    const next = function() {
        if (curGen === "init") {
            refresh();
        }

        if (curGen === "empty") {
            return emptyFlag;
        }
        
        var cur = curGen.next();
        while (cur.done) {
            refresh();
            if (curGen === "empty") {
                return emptyFlag;
            }
            cur = curGen.next();
        }
        return cur;
    };

    return { next: next };
}

function Or2(first, second) {
    var onFirst = true;
    var isEmpty = false;
    
    const next = function() {
        if (isEmpty) {
            return emptyFlag;
        } else if (onFirst) {
            var cur = first.next();
            if (cur.done) {
                onFirst = false;
                return next();
            } else {
                return cur;
            }
        } else {
            var cur = second.next();
            if (cur.done) {
                isEmpty = true;
                return emptyFlag;
            } else {
                return cur;
            }
        }
    };

    return { next: next };
}

function Or() {
    var retval = empty;
    for (var i = arguments.length - 1; i >= 0; i--) {
        retval = Or2(arguments[i], retval);
    }
    return retval;
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

function printAll(gen) {
    var res = gen.next();
    while (!res.done) {
        console.log(res.value.toString());
        res = gen.next();
    }
}

function renderAST(ast) {
    var maker = new DotMaker();
    var viz = new Viz();

    ast.processDot(null, maker);
    var dot = maker.toDot();
    // console.log(dot);
    viz.renderSVGElement(dot)
        .then(function(element) {
            var asXML = (new XMLSerializer()).serializeToString(element);
            document.getElementById("graph_display").innerHTML = asXML;
        });
}

function mainNext() {
    var cur = mainGenerator.next();
    if (cur.done) {
        document.getElementById("graph_display").innerHTML = "<p>No more elements</p>";
    } else {
        renderAST(cur.value);
    }
}

function main() {
    mainGenerator = exp(1);
    mainNext();
}
