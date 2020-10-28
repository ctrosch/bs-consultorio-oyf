/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.web;

import bs.tesoreria.modelo.Banco;
import bs.tesoreria.modelo.CuentaTesoreria;
import bs.tesoreria.modelo.TipoCuentaTesoreria;
import bs.tesoreria.rn.BancoRN;
import bs.tesoreria.rn.CuentaTesoreriaRN;
import bs.tesoreria.rn.TipoCuentaTesoreriaRN;
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
@ManagedBean (name="conversoresTesoreria")
@ViewScoped
public class ConversoresTesoreria implements Serializable{
    
    @EJB private TipoCuentaTesoreriaRN tipoCuentaTesoreriaRN;
    @EJB private CuentaTesoreriaRN cuentaTesoreriaRN;
    @EJB private BancoRN bancoRN;
   
    


    /** Creates a new instance of ConversoresBean */
    public ConversoresTesoreria() {
        
    }    

    public Converter getTipoCuentaTesoreria() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if ( value == null || value.trim().equals("") ) {
                    return null;
                }

                TipoCuentaTesoreria t = tipoCuentaTesoreriaRN.getTipoCuentaTesoreria(value);
                return t;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((TipoCuentaTesoreria) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getCuentaTesoreria() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if ( value == null || value.trim().equals("") ) {
                    return null;
                }

                CuentaTesoreria t = cuentaTesoreriaRN.getCuentaTesoreria(value);
                return t;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((CuentaTesoreria) value).getCodigo() + "";
                }
            }
        };
    }
    
    public Converter getBanco() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if ( value == null || value.trim().equals("") ) {
                    return null;
                }

                Banco t = bancoRN.getBanco(value);
                return t;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Banco) value).getCodigo() + "";
                }
            }
        };
    }
}
