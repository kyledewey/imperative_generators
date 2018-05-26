var mainGenerator;

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
