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
@DiscriminatorValue("A")
public class ItemAplicacionTaller extends ItemMovimientoTaller {
         
    @Override
    public String toString() {
        return "ItemAplicacionTaller{" + "id=" + getId()  + '}';
    }
    
}
