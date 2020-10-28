/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.FormularioPK;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioRN;
import bs.taller.modelo.CodigoCircuitoTaller;
import bs.taller.modelo.Equipo;
import bs.taller.modelo.EquipoMarca;
import bs.taller.modelo.EquipoModelo;
import bs.taller.modelo.EquipoTipo;
import bs.taller.modelo.Tecnico;
import bs.taller.modelo.TipoMantenimiento2;
import bs.taller.rn.CodigoCircuitoTallerRN;
import bs.taller.rn.EquipoMarcaRN;
import bs.taller.rn.EquipoModeloRN;
import bs.taller.rn.EquipoRN;
import bs.taller.rn.EquipoTipoRN;
import bs.taller.rn.TecnicoRN;
import bs.taller.rn.TipoMantenimiento2RN;
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
@ManagedBean(name = "conversoresTaller")
@ViewScoped
public class ConversoresTaller implements Serializable {

    @EJB
    private ComprobanteRN comprobanteRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private CodigoCircuitoTallerRN codigoCircuitoServiceRN;
    @EJB
    private EquipoMarcaRN equipoMarcaRN;
    @EJB
    private EquipoModeloRN equipoModeloRN;
    @EJB
    private EquipoTipoRN equipoTipoRN;
    @EJB
    private EquipoRN equipoRN;
    @EJB
    private TecnicoRN tecnicoRN;
    @EJB
    private TipoMantenimiento2RN tipoMantenimientoRN;

    /**
     * Creates a new instance of ConversoresBean
     */
    public ConversoresTaller() {

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
                    c = comprobanteRN.getComprobante("TA", codcom[1]);
                } catch (ExcepcionGeneralSistema ex) {
                    Logger.getLogger(ConversoresTaller.class.getName()).log(Level.SEVERE, null, ex);
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

                FormularioPK idPK = new FormularioPK("SV", value);
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

    public Converter getCodigoCircuito() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return codigoCircuitoServiceRN.getCodigoCircuitoService(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((CodigoCircuitoTaller) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getTecnico() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return tecnicoRN.getTecnico(Integer.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Tecnico) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getEquipo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return equipoRN.getEquipo(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Equipo) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getEquipoMarca() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return equipoMarcaRN.getEquipoMarca(Integer.parseInt(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((EquipoMarca) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getEquipoModelo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return equipoModeloRN.getEquipoModelo(Integer.parseInt(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((EquipoModelo) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getEquipoTipo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return equipoTipoRN.getEquipoTipo(Integer.parseInt(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((EquipoTipo) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getTipoMantenimiento() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return tipoMantenimientoRN.getTipoMantenimiento(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((TipoMantenimiento2) value).getCodigo() + "";
                }
            }
        };
    }

}
