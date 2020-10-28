/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.auditoria;

import bs.seguridad.modelo.Usuario;

/**
 *
 * @author "Claudio Trosch"
 */
public class EventoAuditoria {

    Usuario usuario;

    public EventoAuditoria(Object o) {

//        System.err.println("Evento auditoria");
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
