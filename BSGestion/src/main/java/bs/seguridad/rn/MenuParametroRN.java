/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.seguridad.dao.MenuParametroDAO;
import bs.seguridad.modelo.MenuParametro;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class MenuParametroRN implements Serializable {

    @EJB
    private MenuParametroDAO parametroDAO;

    public MenuParametro getMenuParametro(String value) {
        return parametroDAO.getMenuParametro(value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(MenuParametro e, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (e.getId() != null) {
                if (parametroDAO.getObjeto(MenuParametro.class, e.getId()) != null) {
                    throw new ExcepcionGeneralSistema("Ya existe un parámetro con el código " + e.getId());
                }
            }

            parametroDAO.crear(e);
        } else {
            parametroDAO.editar(e);
        }
    }

    public List<MenuParametro> getLista(boolean mostrarDebaja) {

        return parametroDAO.getListaByBusqueda("", null, mostrarDebaja, 15);

    }

    public void eliminar(MenuParametro v) throws Exception {

        parametroDAO.eliminar(v);

    }

    public List<MenuParametro> getListaByBusqueda(String txtBusqueda, String codMenu, boolean mostrarDebaja) {

        return parametroDAO.getListaByBusqueda(txtBusqueda, codMenu, mostrarDebaja, 15);
    }

}
