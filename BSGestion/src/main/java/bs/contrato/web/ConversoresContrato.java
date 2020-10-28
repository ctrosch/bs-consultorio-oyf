/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.web;

import bs.contrato.modelo.EstadoContrato;
import bs.contrato.modelo.TipoContrato;
import bs.contrato.rn.EstadoContratoRN;
import bs.contrato.rn.TipoContratoRN;
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
@ManagedBean(name = "conversoresContrato")
@ViewScoped
public class ConversoresContrato implements Serializable {

    @EJB
    private TipoContratoRN tipoContratoRN;
    @EJB
    private EstadoContratoRN estadoRN;

    public Converter getTipoContrato() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return tipoContratoRN.getTipoContrato(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((TipoContrato) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getEstado() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return estadoRN.getEstadoContrato(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((EstadoContrato) value).getCodigo() + "";
                }
            }
        };
    }

}
