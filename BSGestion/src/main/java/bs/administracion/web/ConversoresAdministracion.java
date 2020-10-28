/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.web;

import bs.administracion.modelo.Plantilla;
import bs.administracion.modelo.Reporte;
import bs.administracion.modelo.Vista;
import bs.administracion.rn.PlantillaRN;
import bs.administracion.rn.ReporteRN;
import bs.administracion.rn.VistaRN;
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
@ManagedBean(name = "conversoresAdministracion")
@ViewScoped
public class ConversoresAdministracion implements Serializable {

    @EJB
    private ReporteRN reporteRN;
    @EJB
    private VistaRN vistaRN;
    @EJB
    private PlantillaRN plantillaRN;

    /**
     * Creates a new instance of ConversoresBean
     */
    public ConversoresAdministracion() {

    }

    public Converter getReporte() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return reporteRN.getReporte(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Reporte) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getVista() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return vistaRN.getVista(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Vista) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getPlantilla() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return plantillaRN.getPlantilla(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Plantilla) value).getCodigo() + "";
                }
            }
        };
    }

}
