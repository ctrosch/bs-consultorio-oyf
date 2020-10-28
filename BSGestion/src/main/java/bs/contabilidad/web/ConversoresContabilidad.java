/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.web;

import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.modelo.Distribucion;
import bs.contabilidad.modelo.MascaraContabilidad;
import bs.contabilidad.modelo.PeriodoContable;
import bs.contabilidad.modelo.SubCuenta;
import bs.contabilidad.rn.CentroCostoRN;
import bs.contabilidad.rn.CuentaContableRN;
import bs.contabilidad.rn.DistribucionRN;
import bs.contabilidad.rn.MascaraContabilidadRN;
import bs.contabilidad.rn.PeriodoContableRN;
import bs.contabilidad.rn.SubCuentaRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.FormularioPK;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioRN;
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
@ManagedBean(name = "conversoresContabilidad")
@ViewScoped
public class ConversoresContabilidad implements Serializable {

    @EJB
    private ComprobanteRN comprobanteRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private CuentaContableRN cuentaContableRN;
    @EJB
    private DistribucionRN distribucionRN;
    @EJB
    private CentroCostoRN centroCostoRN;
    @EJB
    private SubCuentaRN subCuentaRN;    
    @EJB
    private PeriodoContableRN periodoContableRN;
    @EJB
    private MascaraContabilidadRN mascaraContabilidadRN;

    /**
     * Creates a new instance of ConversoresBean
     */
    public ConversoresContabilidad() {

    }

    public Converter getComprobante() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                String a[] = value.split(",");
                String modcom[] = a[0].split("=");
                String codcom[] = a[1].split("=");

                Comprobante c = null;
                try {
                    c = comprobanteRN.getComprobante("CG", codcom[1]);
                } catch (ExcepcionGeneralSistema ex) {
                    Logger.getLogger(ConversoresContabilidad.class.getName()).log(Level.SEVERE, null, ex);
                }
                return c;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return "modulo=" + ((Comprobante) value).getModulo() + ", codigo=" + ((Comprobante) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getFormulario() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                FormularioPK idPK = new FormularioPK("CG", value);
                return formularioRN.getFormulario(idPK);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Formulario) value).getCodigo() + "";
                }
            }
        };
    }
    
    public Converter getPerioContable() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return periodoContableRN.getPeriodoContable(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((PeriodoContable) value).getCodigo()+ "";
                }
            }
        };
    }

    public Converter getCuentaContable() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return cuentaContableRN.getCuentaContable(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((CuentaContable) value).getNrocta() + "";
                }
            }
        };
    }

    public Converter getMascaraContabilidad() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return mascaraContabilidadRN.getMascaraContabilidad(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((MascaraContabilidad) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getCentroCosto() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return centroCostoRN.getCentroCosto(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((CentroCosto) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getDistribucion() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return distribucionRN.getDistribucion(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Distribucion) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getSubCuenta() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return subCuentaRN.getSubCuenta(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((SubCuenta) value).getCodigo() + "";
                }
            }
        };
    }

}
