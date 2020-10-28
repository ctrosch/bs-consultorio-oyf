/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.tesoreria.dao.TipoCuentaTesoreriaDAO;
import bs.tesoreria.modelo.TipoCuentaTesoreria;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class TipoCuentaTesoreriaRN implements Serializable {

    @EJB
    private TipoCuentaTesoreriaDAO tipoCuentaTesoreriaDAO;

    public TipoCuentaTesoreria getTipoCuentaTesoreria(String value) {
        return tipoCuentaTesoreriaDAO.getObjeto(TipoCuentaTesoreria.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(TipoCuentaTesoreria v, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (tipoCuentaTesoreriaDAO.getObjeto(TipoCuentaTesoreria.class, v.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + v.getCodigo());
            }
            tipoCuentaTesoreriaDAO.crear(v);
        } else {
            tipoCuentaTesoreriaDAO.editar(v);
        }
    }

    public void eliminar(TipoCuentaTesoreria v) throws Exception {

        tipoCuentaTesoreriaDAO.eliminar(v);
    }

    public List<TipoCuentaTesoreria> getTipoCuentaTesoreriaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return tipoCuentaTesoreriaDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<TipoCuentaTesoreria> getTipoCuentaTesoreriaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoCuentaTesoreriaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }
}
