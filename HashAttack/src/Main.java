import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        boolean verbose = false;

        if (args.length >= 3) {
            if (Objects.equals(args[2], "-v")) verbose = true;
            System.out.println("additional bits: " + Integer.parseInt(args[0])%8);
        }

        HashAttack ha = new HashAttack();

        if (Objects.equals(args[1], "-p")) {
            for (int i = 0; i < 50; ++i) { ha.preImageAttack(Integer.parseInt(args[0]), verbose); }
        }

        else if (Objects.equals(args[1], "-c")) {
            for (int i = 0; i < 50; ++i) { ha.collisionAttack(Integer.parseInt(args[0]), verbose); }
        }
    }
}
