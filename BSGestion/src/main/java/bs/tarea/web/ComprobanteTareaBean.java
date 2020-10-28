/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.web;

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
public class ComprobanteTareaBean extends ComprobanteBean implements Serializable {

    public ComprobanteTareaBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                MODULO = "TA";
                super.iniciar();
                buscar();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void onConceptoTareaSelect(SelectEvent event) {
        super.onConceptoSelect(event);
    }

    public void onFormularioTareaSelect(SelectEvent event) {
        super.onFormularioSelect(event);
    }

}
