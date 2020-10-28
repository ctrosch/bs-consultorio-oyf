package bs.global.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Numeros implements Serializable {

    public static double redondear(double number) {

        return Math.round(number * 10000) / 10000d;

    }

    public static double redondear(double number, int scale) {

        return Math.round(number * Math.pow(10, scale)) / Math.pow(10, scale);

    }

    public static BigDecimal redondear(BigDecimal number) {

        return number.setScale(4, RoundingMode.HALF_UP);

    }

    public static BigDecimal redondear(BigDecimal number, int scale) {

        return number.setScale(scale, RoundingMode.HALF_UP);

    }

    public static BigDecimal toBigDecimal(double number) {

        return new BigDecimal(number).setScale(4, RoundingMode.HALF_UP);

    }

    public static BigDecimal toBigDecimal(double number, int scale) {

        return new BigDecimal(number).setScale(scale, RoundingMode.HALF_UP);

    }

    public static String digitoVerificador(String codigo) {

        try {
            int nPar = 0;
            int nImpar = 0;
            int nTotal = 0;

            char[] c = codigo.toCharArray();

            for (int i = 0; i < (c.length - 1); i++) {

                int valor = Integer.parseInt(String.valueOf(c[i]));

                if (valor % 2 == 0) {
                    nPar = nPar + valor;
                } else {
                    nImpar = nImpar + valor;
                }
            }

            nImpar = nImpar * 3;
            nTotal = nPar + nImpar;

            int digito = 10 - (nTotal % 10);

            if (digito == 10) {
                digito = 0;
            }

            return String.valueOf(digito);
        } catch (NumberFormatException numberFormatException) {

            return "";
        }
    }

}
