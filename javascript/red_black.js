var red = "RED";
var black = "BLACK";

function dotColor(color) {
    if (color === black) {
        return "gray";
    } else {
        return red;
    }
}

function RBLeaf() {
    this.nodeType = "leaf";
    this.color = black;
    this.processDot = function (parent, dotMaker) {
        dotMaker.addNode(parent, "leaf", dotColor(this.color));
    };
}

function RBNode(color, left, item, right) {
    this.color = color;
    this.left = left;
    this.item = item;
    this.right = right;
    this.processDot = function (parent, dotMaker) {
        var me = dotMaker.addNode(parent, "" + item, dotColor(this.color));
        this.left.processDot(me, dotMaker);
        this.right.processDot(me, dotMaker);
    };
}

// returns a generator which gives back integers
// in the given inclusive range
function between(min, max) {
    if (min <= max) {
        return Or(Singleton(min),
                  between(min + 1, max));
    } else {
        return empty;
    }
}

function inRange(beLessThan, beGreaterThan, min, max) {
    return And(between(min, max),
               function (candidate) {
                   if (beLessThan.every(x => candidate < x) &&
                       beGreaterThan.every(x => candidate > x)) {
                       return Singleton(candidate);
                   } else {
                       return empty;
                   }
               });
}

// colorConstraint: unconstrained | black

function validColors(colorConstraint) {
    if (colorConstraint === "unconstrained") {
        return Or(Singleton(red), Singleton(black));
    } else {
        return Singleton(black);
    }
}

function immutablePush(array, what) {
    var copy = array.map(x => x);
    copy.push(what);
    return copy;
}

function childColorConstraint(color) {
    if (color === black) {
        return "unconstrained";
    } else {
        return "black";
    }
}

// { node: node,
//   numBlack: number of black nodes within }

// Takes:
// -Depth bound
// -Values I must be less than
// -Values I must be greater than
// -Min
// -Max
// -colorConstraint
function isRedBlack(depth, beLessThan, beGreaterThan, min, max, colorConstraint) {
    var baseCase = Singleton({ node: new RBLeaf(), numBlack: 1 });
    if (depth > 0) {
        var nested =
            And(inRange(beLessThan, beGreaterThan, min, max),
                function (item) {
                    return And(validColors(colorConstraint),
                               function (color) {
                                   var childColor = childColorConstraint(color);
                                   var decDepth = depth - 1;
                                   return And(isRedBlack(decDepth,
                                                         immutablePush(beLessThan, item),
                                                         beGreaterThan,
                                                         min,
                                                         max,
                                                         childColor),
                                              function (left) {
                                                  return And(isRedBlack(decDepth,
                                                                        beLessThan,
                                                                        immutablePush(beGreaterThan, item),
                                                                        min,
                                                                        max,
                                                                        childColor),
                                                             function (right) {
                                                                 if (left.numBlack === right.numBlack) {
                                                                     var retBlack = left.numBlack;
                                                                     if (color === black) {
                                                                         retBlack++;
                                                                     }
                                                                     return Singleton(
                                                                         { node: new RBNode(color, left.node, item, right.node),
                                                                           numBlack: retBlack
                                                                         });
                                                                 } else {
                                                                     return empty;
                                                                 }
                                                             });
                                              });
                               });
                });
        return Or(baseCase, nested);
    } else {
        return baseCase;
    }
} // isRedBlack

// -Depth bound
// -Min
// -Max
function initialIsRedBlack(depth, min, max) {
    return And(isRedBlack(depth, [], [], min, max, "black"),
               struct => Singleton(struct.node));
}
