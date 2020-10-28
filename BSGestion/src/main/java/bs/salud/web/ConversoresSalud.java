/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.web;

import bs.salud.modelo.EstadoSalud;
import bs.salud.rn.EstadoSaludRN;
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
@ManagedBean(name = "conversoresSalud")
@ViewScoped
public class ConversoresSalud implements Serializable {

    @EJB
    private EstadoSaludRN estadoSaludRN;

    public Converter getEstado() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return estadoSaludRN.getEstadoSalud(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((EstadoSalud) value).getCodigo() + "";
                }
            }
        };
    }

}
