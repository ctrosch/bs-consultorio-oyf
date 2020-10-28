package bs.global.util;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UtilSQL implements Serializable{
    
    public static String getFechaSQL(Date fecha){
        
        try {
            Calendar c1 = GregorianCalendar.getInstance();
            String dia = String.valueOf(c1.get(Calendar.DAY_OF_MONTH));
            String mes = String.valueOf(c1.get(Calendar.MONTH)+1);
            String anio = String.valueOf(c1.get(Calendar.YEAR));

            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
            java.sql.Date fecFormatoDate = new java.sql.Date(sdf.parse(anio+"-"+mes+"-"+dia).getTime());

            return sdf.format(fecha);

        } catch (ParseException ex) {            
            return "19000101";
        }
    }
}
