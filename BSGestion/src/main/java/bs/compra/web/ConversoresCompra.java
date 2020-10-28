/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.compra.web;

import bs.compra.modelo.CodigoCircuitoCompra;
import bs.compra.rn.CodigoCircuitoCompraRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.FormularioPK;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioRN;
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
@ManagedBean (name="conversoresCompra")
@ViewScoped
public class ConversoresCompra implements Serializable{

    @EJB private ComprobanteRN comprobanteRN;   
    @EJB private FormularioRN formularioRN;
    @EJB private CodigoCircuitoCompraRN codigoCircuitoCompraRN;
   

    /** Creates a new instance of ConversoresBean */
    public ConversoresCompra() {
        
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
                    c = comprobanteRN.getComprobante(modcom[1], codcom[1]);
                } catch (ExcepcionGeneralSistema ex) {
                    Logger.getLogger(ConversoresCompra.class.getName()).log(Level.SEVERE, null, ex);
                }
                return c;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return "modulo=" + ((Comprobante) value).getModulo() + ", codigo=" + ((Comprobante) value).getCodigo()+"";
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
                
                FormularioPK idPK = new FormularioPK("CO", value);                
                return formularioRN.getFormulario(idPK);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Formulario) value).getCodigo()+ "";
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
                
                return codigoCircuitoCompraRN.getCodigoCircuitoCompra(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((CodigoCircuitoCompra) value).getCodigo()+ "";
                }
            }
        };
    }
        
}
