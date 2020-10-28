/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tarea.web;

import bs.tarea.modelo.Area;
import bs.tarea.rn.AreaRN;
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
@ManagedBean (name="conversoresTarea")
@ViewScoped
public class ConversoresTarea implements Serializable{

    @EJB private AreaRN areaRN;
    

    /** Creates a new instance of ConversoresBean */
    public ConversoresTarea() {
        
    }

    public Converter getArea() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Area p = areaRN.getArea(value);
                return p;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Area) value).getCodigo() + "";
                }
            }
        };
    }
       
}
