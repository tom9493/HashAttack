import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HashAttack {
    private int bitMask;

    public void preImageAttack(int numBits, boolean verbose) throws Exception {
        bitMask = getBitMask(numBits);
        String original = makeRandomString() + 1;
        byte[] target;
        byte[] attack = {};
        int i = 0;

        // Get SHA-1 sum at correct bit length
        if (numBits % 8 == 0) {
            target = Arrays.copyOfRange(encrypt(original), 0, numBits / 8);
        } else {
            target = Arrays.copyOfRange(encrypt(original), 0, (numBits/8) + 1);
            target[target.length - 1] &= bitMask;
        }

        // Loop until match is found
        while(!Arrays.equals(attack, target)) {
            String newString = makeRandomString();
            if (!newString.equals(original)) {          // String cannot equal original
                if (numBits % 8 == 0) {
                    attack = Arrays.copyOfRange(encrypt(newString), 0, numBits / 8);
                    if (verbose) { System.out.println("target: " + Arrays.toString(target) + " -- attack: " + Arrays.toString(attack)); }
                } else {
                    attack = Arrays.copyOfRange(encrypt(newString), 0, (numBits/8) + 1);
                    attack[attack.length - 1] &= bitMask;
                    if (verbose) {
                        String s2 = String.format("%8s", Integer.toBinaryString(attack[attack.length - 1] & 0xFF)).replace(' ', '0');
                        System.out.println("ATTACK: " + s2);
                        String s3 = String.format("%8s", Integer.toBinaryString(target[target.length - 1] & 0xFF)).replace(' ', '0');
                        System.out.println("TARGET: " + s3);
                        System.out.println("target: " + Arrays.toString(target) + " -- attack: " + Arrays.toString(attack));
                    }
                }
                ++i;
            }
        }
        System.out.println(i);
    }

    public void collisionAttack(int numBits, boolean verbose) throws Exception {
        bitMask = getBitMask(numBits);
        String original = makeRandomString();
        String newString;
        byte[] array;
        List<byte[]> values = new ArrayList<>();
        int i = 1;

        // Get SHA-1 sum at correct bit length
        if (numBits % 8 == 0) {
            array = Arrays.copyOfRange(encrypt(original), 0, numBits / 8);
        } else {
            array = Arrays.copyOfRange(encrypt(original), 0, (numBits/8) + 1);
            array[array.length - 1] &= bitMask;
        }

        while(!valueFound(values, array, i, verbose)) {
            values.add(array);
            ++i;
            newString = original + i;
            if (numBits % 8 != 0) { array = Arrays.copyOfRange(encrypt(newString), 0, (numBits/8) + 1); }
            else { array = Arrays.copyOfRange(encrypt(newString), 0, (numBits/8)); }
            array[array.length - 1] &= bitMask;
        }
        System.out.println(i);
    }

    private boolean valueFound(List<byte[]> a, byte[] b, int iteration, boolean verbose) {
        for (int i = 0; i < a.size(); ++i) {
            if (Arrays.equals(a.get(i), b)) {
                if (verbose) { System.out.println("MATCH at iteration " + iteration + " and previous iteration " + i); }
                return true;
            }
        }
        return false;
    }

    private int getBitMask(int numBits) {
        int bits = numBits % 8;
        return switch (bits) {
            case 1 -> 0x80;
            case 2 -> 0xC0;
            case 3 -> 0xE0;
            case 4 -> 0xF0;
            case 5 -> 0xF8;
            case 6 -> 0xFC;
            case 7 -> 0xFE;
            default -> 0xFF;
        };
    }

    public byte[] encrypt(String x) throws Exception {
        java.security.MessageDigest d;
        d = java.security.MessageDigest.getInstance("SHA-1");
        d.reset();
        d.update(x.getBytes());
        return d.digest();
    }

    public static String makeRandomString() {
        int l = 97;             // a
        int r = 122;            // z
        int length = 10;
        Random random = new Random();
        StringBuilder buf = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randInt = l + (int)
                    (random.nextFloat() * (r - l + 1));
            buf.append((char) randInt);
        }
        return buf.toString();
    }
}
