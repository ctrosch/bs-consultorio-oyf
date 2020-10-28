/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.rn.ComprobanteRN;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Formula;
import bs.stock.modelo.GrupoStock;
import bs.stock.modelo.MascaraStock;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Rubro01;
import bs.stock.modelo.Rubro02;
import bs.stock.modelo.Rubro03;
import bs.stock.modelo.TipoProducto;
import bs.stock.modelo.UnidadMedida;
import bs.stock.rn.DepositoRN;
import bs.stock.rn.FormulaRN;
import bs.stock.rn.GrupoStockRN;
import bs.stock.rn.MascaraStockRN;
import bs.stock.rn.ProductoRN;
import bs.stock.rn.Rubro01RN;
import bs.stock.rn.Rubro02RN;
import bs.stock.rn.Rubro03RN;
import bs.stock.rn.TipoProductoRN;
import bs.stock.rn.UnidadMedidaRN;
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
@ManagedBean(name = "conversoresStock")
@ViewScoped
public class ConversoresStock implements Serializable {

    @EJB
    private ComprobanteRN comprobanteRN;
    @EJB
    private ProductoRN productoRN;
    @EJB
    private DepositoRN depositoRN;
    @EJB
    private GrupoStockRN grupoRN;
    @EJB
    private TipoProductoRN tipoProductoRN;
    @EJB
    private UnidadMedidaRN unidadMedidaRN;
    @EJB
    private FormulaRN formulaRN;
    @EJB
    private Rubro01RN rubro01RN;
    @EJB
    private Rubro02RN rubro02RN;
    @EJB
    private Rubro03RN rubro03RN;
    @EJB
    private MascaraStockRN mascaraStockRN;

    /**
     * Creates a new instance of ConversoresBean
     */
    public ConversoresStock() {

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
                    c = comprobanteRN.getComprobante("ST", value);
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

    public Converter getProducto() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Producto p = productoRN.getProducto(value);
                return p;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Producto) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getDeposito() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Deposito d = depositoRN.getDeposito(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Deposito) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getTipoProducto() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                TipoProducto t = tipoProductoRN.getTipoProducto(value);
                return t;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((TipoProducto) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getUnidadMedida() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                UnidadMedida d = unidadMedidaRN.getUnidadMedida(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((UnidadMedida) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getFormula() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Formula e = formulaRN.getFormula(value);
                return e;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Formula) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getGrupo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                GrupoStock f = grupoRN.getFamilia(Integer.valueOf(value));
                return f;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((GrupoStock) value).getId() + "";
                }
            }
        };
    }

    public Converter getRubro01() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                String a[] = value.split(",");
                String tipo[] = a[0].split("=");
                String codigo[] = a[1].split("=");

                Rubro01 t = rubro01RN.getRubro01(tipo[1], codigo[1]);
                return t;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Rubro01) value).toString() + "";
                }
            }
        };
    }

    public Converter getRubro02() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                String a[] = value.split(",");
                String tipo[] = a[0].split("=");
                String codigo[] = a[1].split("=");

                Rubro02 t = rubro02RN.getRubro02(tipo[1], codigo[1]);
                return t;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Rubro02) value).toString() + "";
                }
            }
        };
    }

    public Converter getRubro03() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                String a[] = value.split(",");
                String tipo[] = a[0].split("=");
                String codigo[] = a[1].split("=");

                Rubro03 t = rubro03RN.getRubro03(tipo[1], codigo[1]);
                return t;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Rubro03) value).toString() + "";
                }
            }
        };
    }

    public Converter getMascaraStock() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return mascaraStockRN.getMascaraStock(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((MascaraStock) value).getCodigo() + "";
                }
            }
        };
    }

}
