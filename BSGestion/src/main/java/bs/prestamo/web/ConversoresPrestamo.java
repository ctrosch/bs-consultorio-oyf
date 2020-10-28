/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.rn.ComprobanteRN;
import bs.prestamo.modelo.AmortizacionPrestamo;
import bs.prestamo.modelo.Clasificacion01;
import bs.prestamo.modelo.Clasificacion02;
import bs.prestamo.modelo.Clasificacion03;
import bs.prestamo.modelo.Clasificacion04;
import bs.prestamo.modelo.Clasificacion05;
import bs.prestamo.modelo.EstadoPrestamo;
import bs.prestamo.modelo.Financiador;
import bs.prestamo.modelo.LineaCredito;
import bs.prestamo.modelo.Prestamo;
import bs.prestamo.modelo.Promotor;
import bs.prestamo.rn.AmortizacionPrestamoRN;
import bs.prestamo.rn.Clasificacion01RN;
import bs.prestamo.rn.Clasificacion02RN;
import bs.prestamo.rn.Clasificacion03RN;
import bs.prestamo.rn.Clasificacion04RN;
import bs.prestamo.rn.Clasificacion05RN;
import bs.prestamo.rn.EstadoPrestamoRN;
import bs.prestamo.rn.FinanciadorRN;
import bs.prestamo.rn.LineaCreditoRN;
import bs.prestamo.rn.PrestamoRN;
import bs.prestamo.rn.PromotorRN;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author ctrosch
 */
@ManagedBean(name = "conversoresPrestamo")
@ViewScoped
public class ConversoresPrestamo implements Serializable {

    @EJB
    private AmortizacionPrestamoRN amortizacionPrestamoRN;
    @EJB
    private Clasificacion01RN clasificacion01RN;
    @EJB
    private Clasificacion02RN clasificacion02RN;
    @EJB
    private Clasificacion03RN clasificacion03RN;
    @EJB
    private Clasificacion04RN clasificacion04RN;
    @EJB
    private Clasificacion05RN clasificacion05RN;
    @EJB
    private EstadoPrestamoRN estadoPrestamoRN;
    @EJB
    private FinanciadorRN financiadorRN;
    @EJB
    private LineaCreditoRN lineaCreditoRN;
    @EJB
    private PromotorRN promotorRN;
    @EJB
    private PrestamoRN prestamoRN;
    @EJB
    private ComprobanteRN comprobanteRN;

    /**
     * Creates a new instance of ConversoresBean
     */
    public ConversoresPrestamo() {

    }

    public Converter getComprobante() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Comprobante c = null;
                try {
                    c = comprobanteRN.getComprobante("PD", value);
                } catch (ExcepcionGeneralSistema ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                return c;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Comprobante) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getAmortizacion() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                AmortizacionPrestamo a = amortizacionPrestamoRN.getAmortizacion(value);
                return a;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((AmortizacionPrestamo) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getClasificacion01() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Clasificacion01 d = clasificacion01RN.getClasificacion01(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Clasificacion01) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getClasificacion02() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Clasificacion02 d = clasificacion02RN.getClasificacion02(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Clasificacion02) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getClasificacion03() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Clasificacion03 d = clasificacion03RN.getClasificacion03(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Clasificacion03) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getClasificacion04() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Clasificacion04 d = clasificacion04RN.getClasificacion04(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Clasificacion04) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getClasificacion05() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Clasificacion05 d = clasificacion05RN.getClasificacion05(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Clasificacion05) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getEstadoPrestamo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                EstadoPrestamo d = estadoPrestamoRN.getEstadoPrestamo(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((EstadoPrestamo) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getFinanciador() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Financiador d = financiadorRN.getFinanciador(Integer.valueOf(value));
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Financiador) value).getId() + "";
                }
            }
        };
    }

    public Converter getLineaCredito() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                LineaCredito d = lineaCreditoRN.getLineaCredito(Integer.valueOf(value));
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((LineaCredito) value).getId() + "";
                }
            }
        };
    }

    public Converter getPromotor() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Promotor d = promotorRN.getPromotor(Integer.valueOf(value));
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Promotor) value).getId() + "";
                }
            }
        };
    }

    public Converter getPrestamo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Prestamo p = prestamoRN.getPrestamo(Integer.valueOf(value));
                return p;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Prestamo) value).getId() + "";
                }
            }
        };
    }
}
