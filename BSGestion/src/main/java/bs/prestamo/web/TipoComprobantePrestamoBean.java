/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.web;

import bs.global.web.TipoComprobanteBean;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class TipoComprobantePrestamoBean extends TipoComprobanteBean implements Serializable {

    public TipoComprobantePrestamoBean() {

    }

    @PostConstruct
    public void init() {

        MODULO = "PR";
        super.iniciar();
        super.iniciarVariables();

    }

}
