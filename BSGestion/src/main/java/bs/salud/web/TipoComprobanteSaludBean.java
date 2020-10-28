/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.web;

import bs.global.web.TipoComprobanteBean;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Guillermo
 */
@ManagedBean
@ViewScoped
public class TipoComprobanteSaludBean extends TipoComprobanteBean implements Serializable {

    public TipoComprobanteSaludBean() {

    }

    @PostConstruct
    public void init() {

        MODULO = "SA";
        super.iniciar();
        super.iniciarVariables();

    }

}
