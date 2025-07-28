import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONTokener;

public class hashira {

    static class Point {
        int x;
        BigInteger y;
        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Fraction {
        BigInteger numerator;
        BigInteger denominator;

        Fraction(BigInteger num, BigInteger den) {
            BigInteger gcd = num.gcd(den);
            if (den.signum() < 0) {
                gcd = gcd.negate();
            }
            this.numerator = num.divide(gcd);
            this.denominator = den.divide(gcd);
        }

        Fraction add(Fraction other) {
            BigInteger newNum = this.numerator.multiply(other.denominator).add(other.numerator.multiply(this.denominator));
            BigInteger newDen = this.denominator.multiply(other.denominator);
            return new Fraction(newNum, newDen);
        }

        BigInteger toBigInteger() {
            return numerator.divide(denominator);
        }
    }

    public static BigInteger lagrangeInterpolation(List<Point> points) {
        Fraction result = new Fraction(BigInteger.ZERO, BigInteger.ONE);
        int k = points.size();

        for (int i = 0; i < k; i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).x);
            BigInteger yi = points.get(i).y;
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = BigInteger.valueOf(points.get(j).x);
                    numerator = numerator.multiply(xj.negate());
                    denominator = denominator.multiply(xi.subtract(xj));
                }
            }

            Fraction term = new Fraction(yi.multiply(numerator), denominator);
            result = result.add(term);
        }

        return result.toBigInteger();
    }

    public static void main(String[] args) {
        try {
            FileReader reader = new FileReader("test2.json");
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);

            JSONObject keys = jsonObject.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            System.out.println("Number of roots provided (n): " + n);
            System.out.println("Minimum required roots (k): " + k);

            List<Point> points = new ArrayList<>();

            for (String key : jsonObject.keySet()) {
                if (!key.equals("keys")) {
                    int x = Integer.parseInt(key);
                    JSONObject obj = jsonObject.getJSONObject(key);
                    int base = Integer.parseInt(obj.getString("base"));
                    String valueStr = obj.getString("value");
                    BigInteger y = new BigInteger(valueStr, base);
                    points.add(new Point(x, y));
                    System.out.println("Point: (" + x + ", " + y + ")");
                }
            }

            points.sort(Comparator.comparingInt(p -> p.x));
            BigInteger secret = lagrangeInterpolation(points.subList(0, k));
            System.out.println("Secret (constant term): " + secret);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
