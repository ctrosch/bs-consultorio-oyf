/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.global.util.JsfUtil;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author GUILLERMO
 */
@ManagedBean
@ViewScoped
public class ActividadComercialClienteBean extends ActividadComercialBean implements Serializable {

    private String CODIGO;

    public ActividadComercialClienteBean() {
        CODTIP = "1";

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (CODIGO == null || CODIGO.isEmpty()) {
                    buscar();
                } else {

                    seleccionar(this.getActividadComercialRN().getActividadComercial(CODIGO, CODTIP));
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

}
