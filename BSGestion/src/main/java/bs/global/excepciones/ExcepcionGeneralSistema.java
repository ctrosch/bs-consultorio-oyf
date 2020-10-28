/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.excepciones;

import com.sun.mail.handlers.message_rfc822;

/**
 *
 * @author Caio
 */
public class ExcepcionGeneralSistema extends Exception {

    public ExcepcionGeneralSistema(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
    
    

}
