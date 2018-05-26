function Node(id, parent, label, color) {
    this.id = id;
    this.parent = parent;
    this.label = label;
    this.color = color;
}

function DotMaker() {
    this.nextNodeId = 0;
    this.nodes = [];

    // returns the id of the created node
    this.addNode = function (parent, label, color) {
        var id = this.nextNodeId;
        this.nodes.push(new Node(id, parent, label, color));
        this.nextNodeId++;
        return id;
    };

    this.toDot = function () {
        var retval = "digraph {\n";
        this.nodes.forEach(function (node) {
            retval += "n" + node.id + " [label=\"" + node.label + "\"";
            if (node.color) {
                retval += ", style=filled, fillcolor=" + node.color;
            }
            retval += "]\n";
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
