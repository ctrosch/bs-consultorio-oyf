/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.web;

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
public class ComprobanteContabilidadBean extends ComprobanteBean implements Serializable {

    /**
     * Creates a new instance of ComprobanteCompraBean
     */
    public ComprobanteContabilidadBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                MODULO = "CG";
                super.iniciar();
                buscar();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void onConceptoContabilidadSelect(SelectEvent event) {
        super.onConceptoSelect(event);
    }

    public void onFormularioContabilidadSelect(SelectEvent event) {
        super.onFormularioSelect(event);
    }

}
