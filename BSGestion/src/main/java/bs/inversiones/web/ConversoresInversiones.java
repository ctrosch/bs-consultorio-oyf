/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.inversiones.web;

import bs.inversiones.modelo.Proyecto;
import bs.inversiones.rn.ProyectoRN;
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
@ManagedBean (name="conversoresInversiones")
@ViewScoped
public class ConversoresInversiones {
    
    
    @EJB private ProyectoRN proyectoRN;
        
    
    public Converter getProyecto() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }
                
                return proyectoRN.getProyecto(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Proyecto) value).getCodigo() + "";
                }
            }
        };
    }
        
    
}
