/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author ctrosch
 */
@Entity
@DiscriminatorValue("H")
public class ItemMicroseguroPrestamo extends ItemMovimientoPrestamo {

}
