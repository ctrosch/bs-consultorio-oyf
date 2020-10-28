/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.web;

import bs.global.util.JsfUtil;
import bs.global.web.ComprobanteBean;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ComprobanteProduccionBean extends ComprobanteBean implements Serializable {

    public ComprobanteProduccionBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                MODULO = "PD";
                super.iniciar();
                buscar();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void onConceptoStockSelect(SelectEvent event) {
        super.onConceptoSelect(event);
    }

    public void onFormularioStockSelect(SelectEvent event) {
        super.onFormularioSelect(event);
    }
}
