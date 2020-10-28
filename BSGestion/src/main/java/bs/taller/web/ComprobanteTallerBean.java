/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.web;

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
public class ComprobanteTallerBean extends ComprobanteBean implements Serializable {

    public ComprobanteTallerBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                MODULO = "TL";
                super.iniciar();
                buscar();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void onConceptoTallerSelect(SelectEvent event) {
        super.onConceptoSelect(event);
    }

    public void onFormularioTallerSelect(SelectEvent event) {
        super.onFormularioSelect(event);
    }

}
