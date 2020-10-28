/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.web;

import bs.produccion.modelo.CodigoCircuitoProduccion;
import bs.produccion.modelo.DepartamentoProduccion;
import bs.produccion.modelo.Operario;
import bs.produccion.modelo.Planta;
import bs.produccion.modelo.Sector;
import bs.produccion.modelo.TipoOperario;
import bs.produccion.rn.CodigoCircuitoProduccionRN;
import bs.produccion.rn.DepartamentoProduccionRN;
import bs.produccion.rn.OperarioRN;
import bs.produccion.rn.PlantaRN;
import bs.produccion.rn.SectorRN;
import bs.produccion.rn.TipoOperarioRN;
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
@ManagedBean(name = "conversoresProduccion")
@ViewScoped
public class ConversoresProduccion implements Serializable {

    @EJB
    private CodigoCircuitoProduccionRN codigoCircuitoRN;
    @EJB
    private DepartamentoProduccionRN departamentoRN;
    @EJB
    private PlantaRN plantaRN;
    @EJB
    private SectorRN sectorRN;
    @EJB
    private OperarioRN operarioRN;
    @EJB
    private TipoOperarioRN tipoOperarioRN;

    /**
     * Creates a new instance of ConversoresBean
     */
    public ConversoresProduccion() {

    }

    public Converter getCodigoCircuito() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                CodigoCircuitoProduccion c = codigoCircuitoRN.getCodigoCircuitoProduccion(value);
                return c;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((CodigoCircuitoProduccion) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getDepartamento() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                DepartamentoProduccion d = departamentoRN.getDepartamentoProduccion(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((DepartamentoProduccion) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getOperario() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Operario d = operarioRN.getOperario(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Operario) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getTipoOperario() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                TipoOperario d = tipoOperarioRN.getTipoOperario(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((TipoOperario) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getPlanta() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Planta d = plantaRN.getPlanta(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Planta) value).getCodigo() + "";
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

                Sector d = sectorRN.getSector(value);
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Sector) value).getCodigo() + "";
                }
            }
        };
    }
}
