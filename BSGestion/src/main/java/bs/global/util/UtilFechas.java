package bs.global.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class UtilFechas implements Serializable {

    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static String getFechaSQL(Date fechaMovimientoDesde) {

        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return "'" + sdf.format(fechaMovimientoDesde) + "'";

    }

    public static Map<String, String> getMeses() {

        Map<String, String> meses = new HashMap<String, String>();

        meses.put("1", "Enero");
        meses.put("2", "Febrero");
        meses.put("3", "Marzo");
        meses.put("4", "Abril");
        meses.put("5", "Mayo");
        meses.put("6", "Junio");
        meses.put("7", "Julio");
        meses.put("8", "Agosto");
        meses.put("9", "Septiembre");
        meses.put("10", "Octubre");
        meses.put("11", "Noviembre");
        meses.put("12", "Diciembre");

        return meses;

    }

    public static Map<String, String> getAnios() {

        Map<String, String> anios = new HashMap<String, String>();

        Calendar c = new GregorianCalendar();

        for (int i = 2010; i <= c.get(Calendar.YEAR); i++) {
            anios.put(String.valueOf(i), String.valueOf(i));
        }

        return anios;
    }

    public static Date getDate(int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return c.getTime();
    }

    public static String diferenciaEntreFechasFormateada(Date fInicial, Date fFinal) {

        GregorianCalendar jCal = new java.util.GregorianCalendar();
        jCal.setTime(fFinal);
        String patron = "%02d:%02d:%02d";

        long milisegundos = fFinal.getTime() - fInicial.getTime();

        long horas = milisegundos / 3600000;
        long restoHora = milisegundos % 3600000;

        long minutos = restoHora / (1000 * 60);
        long restoMinutos = restoHora % (1000 * 60);

        long segundos = (long) restoMinutos / 1000;

        return String.format(patron, horas, minutos, segundos);
    }

    public static long cuantosMinutos(Date fInicial, Date fFinal) {

        long milisegundos = fFinal.getTime() - fInicial.getTime();
        long minutos = milisegundos / (1000 * 60);

        return minutos;
    }

    public static int daysDifference(Date date1, Date date2) {

        // si algún parámetro es null devolvemos 0
        if (date1 == null || date2 == null) {

            return 0;

        }

        date1 = setMidnight(date1); // seteamos la fecha a medianoche (00:00:00.0000)

        date2 = setMidnight(date2); // seteamos la fecha a medianoche (00:00:00.0000)

        Calendar calendar1 = Calendar.getInstance(); // creamos la instancia del calendario

        Calendar calendar2 = Calendar.getInstance(); // creamos la instancia del calendario

        // comprobamos cuál es mayor para setearlo correctamente
        if (date2.compareTo(date1) > 0) {

            calendar1.setTime(date1);

            calendar2.setTime(date2);

        } else {

            calendar1.setTime(date2);

            calendar2.setTime(date1);

        }

        int days = 0;

        // mientras la fecha del calendario 2 sea mayor que la fecha del calendario 1
        while (calendar1.compareTo(calendar2) < 0) {

            calendar1.add(Calendar.DAY_OF_MONTH, 1); // suma un día al calendario 1

            days++;

        }

        if (date1.compareTo(date2) > 0) {

            days = days * (-1);

        }

        return days;

    }

    public static Date setMidnight(Date date) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);

        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();

    }

    public static Date sumar(Date fecha, int field, int cantidad) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.add(field, cantidad);

        return calendar.getTime();

    }
}
