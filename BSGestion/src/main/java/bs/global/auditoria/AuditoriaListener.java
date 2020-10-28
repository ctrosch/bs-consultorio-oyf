/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.auditoria;

import bs.global.modelo.Auditoria;
import java.util.Date;
import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author Claudio Si la clase no implementa la interfaz IAuditableEntity, no
 * actualiza datos
 */
public class AuditoriaListener {

    @Inject
    private AuditoriaBean auditoriaBean;

    @PrePersist
    public void onPrePersist(Object o) {
        if (o instanceof IAuditableEntity) {

            IAuditableEntity e = (IAuditableEntity) o;
            if (e.getAuditoria() == null) {
                e.setAuditoria(new Auditoria());
            }

            e.getAuditoria().setFechaAlta(new Date());
            e.getAuditoria().setFechaModificacion(new Date());
            e.getAuditoria().setUltimaOperacion("A");
            e.getAuditoria().setUsuario(auditoriaBean.getUsuario());

        }
    }

    @PreUpdate
    public void onPreUpdate(Object o) {

//        System.err.println("onPreUpdate" + usuarioSessionBean.getUsuario());
        if (o instanceof IAuditableEntity) {

            IAuditableEntity e = (IAuditableEntity) o;
            if (e.getAuditoria() == null) {
                e.setAuditoria(new Auditoria());
            }

            e.getAuditoria().setFechaModificacion(new Date());
            e.getAuditoria().setUltimaOperacion("M");
            e.getAuditoria().setUsuario(auditoriaBean.getUsuario());
        }

    }
}
