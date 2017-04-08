public enum Operator {
    OPERATOR_PLUS,
    OPERATOR_MINUS,
    OPERATOR_TIMES,
    OPERATOR_DIV;

    public String toString() {
        if (this == OPERATOR_PLUS) {
            return "+";
        } else if (this == OPERATOR_MINUS) {
            return "-";
        } else if (this == OPERATOR_TIMES) {
            return "*";
        } else if (this == OPERATOR_DIV) {
            return "/";
        } else {
            assert(false);
            return "";
        }
    } // toString
}
