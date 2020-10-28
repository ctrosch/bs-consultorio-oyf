/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.Rubro01DAO;
import bs.stock.modelo.Rubro01;
import bs.stock.modelo.RubroPK;
import bs.stock.modelo.TipoProducto;
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
public class Rubro01RN implements Serializable {

    @EJB
    Rubro01DAO rubroDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Rubro01 rubro01, boolean esNuevo) throws ExcepcionGeneralSistema {

        if (esNuevo) {

            RubroPK idPK = new RubroPK(rubro01.getTippro(), rubro01.getCodigo());

            if (rubroDAO.getObjeto(Rubro01.class, idPK) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un rubro con el código" + rubro01.getCodigo());
            }
            rubroDAO.crear(rubro01);
        } else {
            rubroDAO.editar(rubro01);
        }
    }

    public Rubro01 getRubro01(RubroPK idPK) {

        return rubroDAO.getObjeto(Rubro01.class, idPK);
    }

    public Rubro01 getRubro01(String tipo, String codigo) {

        RubroPK idPK = new RubroPK(tipo, codigo);
        return rubroDAO.getObjeto(Rubro01.class, idPK);
    }

    public void eliminar(Rubro01 rubro01) throws Exception {
        rubroDAO.eliminar(rubro01);
    }

    public List<Rubro01> getLista(boolean mostrarDebaja) {

        return rubroDAO.getListaByBusqueda(null, "", mostrarDebaja, 50);
    }

    public List<Rubro01> getListaByBusqueda(TipoProducto tipoProduccto, String txtBusqueda, boolean mostrarDebaja) {

        if (tipoProduccto == null) {
            return rubroDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 0);
        } else {
            return rubroDAO.getListaByBusqueda(tipoProduccto.getCodigo(), txtBusqueda, mostrarDebaja, 0);
        }
    }

    public void setDeBajaAll() {

        rubroDAO.setDeBajaAll(Rubro01.class);
    }
}
