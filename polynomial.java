import java.util.*;
import java.math.BigInteger;

class Main {
    public static void main(String[] args) {
        //System.out.println("Try programiz.pro");

        List<List<String>> points = new ArrayList<>();  // Use String first, decode later
        String jsonString = """
        {
"keys": {
    "n": 10,
    "k": 7
  },
  "1": {
    "base": "7",
    "value": "420020006424065463"
  },
  "2": {
    "base": "7",
    "value": "10511630252064643035"
  },
  "3": {
    "base": "2",
    "value": "101010101001100101011100000001000111010010111101100100010"
  },
  "4": {
    "base": "8",
    "value": "31261003022226126015"
  },
  "5": {
    "base": "7",
    "value": "2564201006101516132035"
  },
  "6": {
    "base": "15",
    "value": "a3c97ed550c69484"
  },
  "7": {
    "base": "13",
    "value": "134b08c8739552a734"
  },
  "8": {
    "base": "10",
    "value": "23600283241050447333"
  },
  "9": {
    "base": "9",
    "value": "375870320616068547135"
  },
  "10": {
    "base": "6",
    "value": "30140555423010311322515333"
  }
}
    """;

        jsonString = jsonString.replace("{", "")
                               .replace("}", "")
                               .replace("\"", "")
                               .trim();

        String[] lines = jsonString.split("\n");

        int k = 0;
        List<String> keys = new ArrayList<>();
        List<String> bases = new ArrayList<>();
        List<String> values = new ArrayList<>();

        String currentKey = null;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (!line.contains(":")) continue;

            String[] parts = line.split(":");
            String keyPart = parts[0].trim();
            String valuePart = parts.length > 1 ? parts[1].trim().replace(",", "") : "";

            if (keyPart.equals("k")) {
                k = Integer.parseInt(valuePart);
            } else if (keyPart.matches("\\d+")) {
                currentKey = keyPart;
                keys.add(currentKey);
            } else if (keyPart.equals("base")) {
                bases.add(valuePart);
            } else if (keyPart.equals("value")) {
                values.add(valuePart);
            }
        }

        // Collect raw points
        for (int i = 0; i < keys.size(); i++) {
            List<String> in = new ArrayList<>();
            in.add(keys.get(i));    // x
            in.add(bases.get(i));   // base
            in.add(values.get(i));  // value
            points.add(in);
        }

        // Decode to BigInteger points
        List<List<BigInteger>> x_y_points = decode(points);
        //System.out.println("Decoded Points (x, y): " + x_y_points);

        BigInteger ans = lagrange(x_y_points, k);
        System.out.println("Secret constant term: " + ans);
    }

    // Decode values to base-10 BigInteger
    public static List<List<BigInteger>> decode(List<List<String>> p) {
        List<List<BigInteger>> points = new ArrayList<>();
        List<BigInteger> x_values = new ArrayList<>();
        List<BigInteger> y_values = new ArrayList<>();

        for (List<String> lst : p) {
            BigInteger x = new BigInteger(lst.get(0));   // x = key
            int base = Integer.parseInt(lst.get(1));     // base
            String valueStr = lst.get(2);                // value in given base

            // Convert value from its base to BigInteger
            BigInteger y = new BigInteger(valueStr, base);

            x_values.add(x);
            y_values.add(y);

           // System.out.println("Decoded: x=" + x + " base=" + base + " value=" + valueStr + " -> y=" + y);
        }

        points.add(x_values);
        points.add(y_values);
        return points;
    }

    // Lagrange interpolation using BigInteger
    public static BigInteger lagrange(List<List<BigInteger>> points, int k) {
        List<BigInteger> x_values = points.get(0);
        List<BigInteger> y_values = points.get(1);

        BigInteger constant = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    num = num.multiply(BigInteger.ZERO.subtract(x_values.get(j)));
                    den = den.multiply(x_values.get(i).subtract(x_values.get(j)));
                }
            }

            BigInteger term = y_values.get(i).multiply(num).divide(den);
            constant = constant.add(term);

           // System.out.println("i=" + i + " num=" + num + " den=" + den + " term=" + term + " partial_constant=" + constant);
        }

        return constant;
    }
}