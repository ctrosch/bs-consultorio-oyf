/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.ventas.modelo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author ctrosch
 */
@Entity
@DiscriminatorValue("B")
public class ItemBonificacionVenta extends ItemMovimientoVenta {

}
