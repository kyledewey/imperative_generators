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
