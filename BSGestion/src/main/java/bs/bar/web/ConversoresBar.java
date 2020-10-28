/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.web;

import bs.bar.modelo.EstadoBar;
import bs.bar.modelo.Mesa;
import bs.bar.modelo.Mozo;
import bs.bar.modelo.Salon;
import bs.bar.rn.EstadoBarRN;
import bs.bar.rn.MesaRN;
import bs.bar.rn.MozoRN;
import bs.bar.rn.SalonRN;
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
@ManagedBean(name = "conversoresBar")
@ViewScoped
public class ConversoresBar implements Serializable {

    @EJB
    private SalonRN salonRN;
    @EJB
    private MesaRN mesaRN;
    @EJB
    private MozoRN mozoRN;
    @EJB
    private EstadoBarRN estadoBarRN;

    /**
     * Creates a new instance of ConversoresBean
     */
    public ConversoresBar() {

    }

    public Converter getSalon() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return salonRN.getSalon(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Salon) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getMesa() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return mesaRN.getMesa(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Mesa) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getMozo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return mozoRN.getMozo(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Mozo) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getEstadoBar() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return estadoBarRN.getEstadoBar(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((EstadoBar) value).getCodigo() + "";
                }
            }
        };
    }

}
