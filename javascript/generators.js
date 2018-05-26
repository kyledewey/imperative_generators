function* generator(i) {
    yield i;
    yield i + 1;
}

function printAll(gen) {
    var res = gen.next();
    while (!res.done) {
        console.log(res.value);
        res = gen.next();
    }
}

function main() {
    printAll(generator(10));
}
