/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.wsafip.dao;

import bs.global.dao.BaseDAO;
import bs.wsafip.modelo.WebServices;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless
public class WebServiceDAO extends BaseDAO {

    public WebServices getObjeto(String codigo){
        
        return getObjeto(WebServices.class, codigo);
    }
}
