/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.web;

import bs.mantenimiento.modelo.Actividad;
import bs.mantenimiento.modelo.Defecto;
import bs.mantenimiento.modelo.EventoMantenimiento;
import bs.mantenimiento.modelo.PlanActividad;
import bs.mantenimiento.rn.ActividadRN;
import bs.mantenimiento.rn.DefectoRN;
import bs.mantenimiento.rn.EventoMantenimientoRN;
import bs.mantenimiento.rn.PlanActividadRN;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Claudio
 */
@ManagedBean(name = "conversoresMantenimiento")
@ViewScoped
public class ConversoresMantenimiento implements Serializable {

    @EJB
    private ActividadRN actividadRN;

    @EJB
    private PlanActividadRN planActividadRN;

    @EJB
    private DefectoRN defectoRN;

    @EJB
    private EventoMantenimientoRN eventoRN;

    public Converter getActividad() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return actividadRN.getActividad(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Actividad) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getPlanActividad() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return planActividadRN.getPlanActividad(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((PlanActividad) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getDefecto() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return defectoRN.getDefecto(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Defecto) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getEvento() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return eventoRN.getEventoMantenimiento(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((EventoMantenimiento) value).getCodigo() + "";
                }
            }
        };
    }

}
