/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.taller.modelo;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author Claudio
 */

@Entity
@DiscriminatorValue("P")
public class ItemProductoTaller extends ItemMovimientoTaller{
    
   
}
