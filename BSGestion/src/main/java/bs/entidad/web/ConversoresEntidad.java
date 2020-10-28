/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.ActividadComercial;
import bs.entidad.modelo.Categoria;
import bs.entidad.modelo.Contacto;
import bs.entidad.modelo.ContactoSector;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadCamion;
import bs.entidad.modelo.EntidadChofer;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.EstadoEntidad;
import bs.entidad.modelo.Origen;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.ActividadComercialRN;
import bs.entidad.rn.CategoriaRN;
import bs.entidad.rn.ContactoRN;
import bs.entidad.rn.ContactoSectorRN;
import bs.entidad.rn.DireccionEntregaRN;
import bs.entidad.rn.EntidadRN;
import bs.entidad.rn.EstadoEntidadRN;
import bs.entidad.rn.OrigenRN;
import bs.entidad.rn.TipoEntidadRN;
import java.io.Serializable;
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
@ManagedBean(name = "conversoresEntidad")
@ViewScoped
public class ConversoresEntidad implements Serializable {

    @EJB
    private EntidadRN entidadRN;
    @EJB
    private CategoriaRN categoriaRN;
    @EJB
    private OrigenRN origenRN;
    @EJB
    private ContactoRN contactoRN;
    @EJB
    private EstadoEntidadRN estadoEntidadRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;
    @EJB
    private DireccionEntregaRN direccionEntregaRN;
    @EJB
    private ActividadComercialRN actividadComercialRN;
    @EJB
    private ContactoSectorRN contactoSectorRN;

    /**
     * Creates a new instance of ConversoresBean
     */
    public ConversoresEntidad() {

    }

    public Converter getEntidadComercial() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return entidadRN.getEntidad(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((EntidadComercial) value).getNrocta() + "";
                }
            }
        };
    }

    public Converter getDireccionEntrega() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }
                String a[] = value.split(",");
                return direccionEntregaRN.getDireccionEntregaEntidad(a[1], a[0]);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    DireccionEntregaEntidad de = (DireccionEntregaEntidad) value;
                    return de.getNrocta() + "," + de.getCodigo();
                }
            }
        };
    }

    public Converter getEstadoEntidad() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return estadoEntidadRN.getEstadoEntidad(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((EstadoEntidad) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getCategoria() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                String a[] = value.split(",");
                String codtip[] = a[0].split("=");
                String codigo[] = a[1].split("=");

                return categoriaRN.getCategoria(codtip[1], codigo[1]);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Categoria) value).toString();
                }
            }
        };
    }

    public Converter getOrigen() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                String a[] = value.split(",");
                String codtip[] = a[0].split("=");
                String codigo[] = a[1].split("=");

                return origenRN.getOrigen(codtip[1], codigo[1]);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Origen) value).toString();
                }
            }
        };
    }

    public Converter getContacto() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return contactoRN.getContacto(Integer.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Contacto) value).getId() + "";
                }
            }
        };
    }

    public Converter getTipoEntidad() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return tipoEntidadRN.getTipoEntidad(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((TipoEntidad) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getActividad() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }
                String a[] = value.split(",");
                String codigo[] = a[0].split("=");
                String codtip[] = a[1].split("=");

                return actividadComercialRN.getActividadComercial(codigo[1], codtip[1]);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((ActividadComercial) value).toString() + "";
                }
            }
        };
    }

    public Converter getSector() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return contactoSectorRN.getContactoSector(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((ContactoSector) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getChofer() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return entidadRN.getChofer(Integer.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((EntidadChofer) value).getId() + "";
                }
            }
        };
    }

    public Converter getCamion() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return entidadRN.getCamion(Integer.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((EntidadCamion) value).getId() + "";
                }
            }
        };
    }

}
