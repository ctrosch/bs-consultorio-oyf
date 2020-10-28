/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.global.auditoria.AuditoriaListener;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

/**
 *
 * @author Claudio
 */
@Entity
@DiscriminatorValue("VT")
@EntityListeners(AuditoriaListener.class)
public class ItemCircuitoEducacionVenta extends ItemCircuitoEducacion {

}
