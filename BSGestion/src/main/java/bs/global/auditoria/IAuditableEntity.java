/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.auditoria;

import bs.global.modelo.Auditoria;

/**
 *
 * @author Claudio
 */
public interface IAuditableEntity {

   public Auditoria getAuditoria();
   public void setAuditoria(Auditoria auditoria);

}
