/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.web;

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
public class ComprobanteStockBean extends ComprobanteBean implements Serializable {

    private String CODIGO;

    public ComprobanteStockBean() {
        MODULO = "ST";
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                buscar();

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(this.getComprobanteRN().getComprobante(MODULO, CODIGO));
                }

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

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

}
