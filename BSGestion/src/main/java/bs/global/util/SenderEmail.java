/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.util;


import bs.administracion.modelo.CorreoElectronico;
import bs.global.web.MailFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Claudio
 */
public class SenderEmail extends Thread{

    MailFactory mailFactoryBean;
    List<CorreoElectronico> listaCorreos;

    public SenderEmail(MailFactory mailFactoryBean,List<CorreoElectronico> listaCorreos ) {
        this.mailFactoryBean = mailFactoryBean;
        this.listaCorreos = listaCorreos;
    }

    @Override
    public void run() {

        try {

            if(listaCorreos==null) return;
            if(listaCorreos.isEmpty()){                
                return;
            }
            mailFactoryBean.enviarCorreosElectronicos(listaCorreos);

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "run", e);                        
        }
    }
}

