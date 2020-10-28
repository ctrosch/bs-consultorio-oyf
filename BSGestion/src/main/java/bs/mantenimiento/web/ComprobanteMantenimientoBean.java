/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.web;

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
public class ComprobanteMantenimientoBean extends ComprobanteBean implements Serializable {

    /**
     * Creates a new instance of ComprobanteProveedorBean
     */
    public ComprobanteMantenimientoBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                MODULO = "MT";
                super.iniciar();
                buscar();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void onConceptoMantenimientoSelect(SelectEvent event) {
        super.onConceptoSelect(event);
    }

    public void onFormularioMantenimientoSelect(SelectEvent event) {
        super.onFormularioSelect(event);
    }

}
