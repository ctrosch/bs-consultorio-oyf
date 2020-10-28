package bs.global.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

public class JsfUtil implements Serializable {

    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "Seleccionar");
            i++;
        }

        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }

    public static void ensureAddErrorMessage(Exception ex, String defaultMsg) {
        String msg = ex.getLocalizedMessage();
        if (msg != null && msg.length() > 0) {
            addErrorMessage(msg);
        } else {
            addErrorMessage(defaultMsg);
        }
    }

    public static void addErrorMessages(List<String> messages) {

        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (String message : messages) {
            addErrorMessage(message);
        }
    }

    public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addErrorMessage(String tit, String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, tit, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addInfoMessages(List<String> messages) {

        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (String message : messages) {
            addInfoMessage(message);
        }
    }

    public static void addInfoMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addInfoMessage(String tit, String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, tit, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addInfoMessage(String clienteId, String tit, String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, tit, msg);
        FacesContext.getCurrentInstance().addMessage(clienteId, facesMsg);
    }

    public static void addWarningMessages(List<String> messages) {

        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (String message : messages) {
            addWarningMessage(message);
        }
    }

    public static void addWarningMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Atención", msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addWarningMessage(String tit, String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, tit, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addWarningMessage(String clienteId, String tit, String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, tit, msg);
        FacesContext.getCurrentInstance().addMessage(clienteId, facesMsg);
    }

    public static void addFatalMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error Fatal", msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addFatalMessage(String tit, String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_FATAL, tit, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static String getRequestParameter(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
    }

    public static Object getObjectFromRequestParameter(String requestParameterName, Converter converter, UIComponent component) {
        String theId = JsfUtil.getRequestParameter(requestParameterName);
        return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
    }

    public static <T> List<T> arrayToList(T[] arr) {
        if (arr == null) {
            return new ArrayList<T>();
        }
        return Arrays.asList(arr);
    }

    public static <T> Set<T> arrayToSet(T[] arr) {
        if (arr == null) {
            return new HashSet<T>();
        }
        return new HashSet(Arrays.asList(arr));
    }

    public static Object[] collectionToArray(Collection<?> c) {
        if (c == null) {
            return new Object[0];
        }
        return c.toArray();
    }

    public static <T> List<T> setToList(Set<T> set) {
        return new ArrayList<T>(set);
    }

    public static String getAsConvertedString(Object object, Converter converter) {
        return converter.getAsString(FacesContext.getCurrentInstance(), null, object);
    }

    public static String getCollectionAsString(Collection<?> collection) {
        if (collection == null || collection.size() == 0) {
            return "(No Items)";
        }
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (Object item : collection) {
            if (i > 0) {
                sb.append("<br />");
            }
            sb.append(item);
            i++;
        }
        return sb.toString();
    }

    public static Object getManagedBean(String bean) {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (facesContext != null) {
            return facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, bean);
        } else {
            return null;
        }
    }

    public static Object getObjeto(String sBean, Class objeto) {

        sBean = "#{" + sBean + "}";
        FacesContext context = FacesContext.getCurrentInstance();

        if (context != null) {
            return context.getApplication().evaluateExpressionGet(context, sBean, objeto);
        } else {
            return null;
        }
    }

    public static String digitoVerificador(String codigo) {
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
    }

    public static String getCadenaAlfanumAleatoria(int longitud) {
        String cadenaAleatoria = "";
        long milis = new java.util.GregorianCalendar().getTimeInMillis();
        Random r = new Random(milis);
        int i = 0;
        while (i <= longitud) {
            char c = (char) r.nextInt(255);
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')) {
                cadenaAleatoria += c;
                i++;
            }
        }
        return cadenaAleatoria;
    }

    public static String generarEnLetras(BigDecimal importe) {

        NumeroToLetras nl = new NumeroToLetras();
        String enLetrasEntero = nl.convertirLetras(importe);

        return " " + enLetrasEntero;
    }

    /**
     * Validate given email with regular expression.
     *
     * @param email email for validation
     * @return true valid email, otherwise false
     */
    public static boolean validateEmail(String email) {

        // Compiles the given regular expression into a pattern.
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

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

}
