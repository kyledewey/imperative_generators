public class Main {
    public static void usage() {
        System.out.println("Takes an integer depth");
    }

    public static <A> void printIteratorContents(final GenIterator<A> iterator) {
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toString());
        }
        iterator.reset();
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        } else {
            try {
                printIteratorContents(new ExpIterator(Integer.parseInt(args[0])));
            } catch (final NumberFormatException e) {
                System.out.println("Expected integer, received: " + args[0]);
            }
        }
    } // main
} // Main
