/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.Rubro02DAO;
import bs.stock.modelo.Rubro02;
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
public class Rubro02RN implements Serializable {

    @EJB
    Rubro02DAO rubroDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Rubro02 rubro02, boolean esNuevo) throws ExcepcionGeneralSistema {

        if (esNuevo) {

            RubroPK idPK = new RubroPK(rubro02.getTippro(), rubro02.getCodigo());

            if (rubroDAO.getObjeto(Rubro02.class, idPK) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un rubro con el código" + rubro02.getCodigo());
            }
            rubroDAO.crear(rubro02);
        } else {
            rubroDAO.editar(rubro02);
        }
    }

    public Rubro02 getRubro02(RubroPK idPK) {

        return rubroDAO.getObjeto(Rubro02.class, idPK);
    }

    public Rubro02 getRubro02(String tipo, String codigo) {

        RubroPK idPK = new RubroPK(tipo, codigo);
        return rubroDAO.getObjeto(Rubro02.class, idPK);
    }

    public void eliminar(Rubro02 rubro02) throws Exception {

        rubroDAO.eliminar(rubro02);

    }

    public List<Rubro02> getLista(boolean mostrarDebaja) {

        return rubroDAO.getListaByBusqueda(null, "", mostrarDebaja, 15);
    }

    public List<Rubro02> getListaByBusqueda(TipoProducto tipoProduccto, String txtBusqueda, boolean mostrarDebaja) {

        if (tipoProduccto == null) {
            return rubroDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 0);
        } else {
            return rubroDAO.getListaByBusqueda(tipoProduccto.getCodigo(), txtBusqueda, mostrarDebaja, 0);
        }
    }

    public void setDeBajaAll() {

        rubroDAO.setDeBajaAll(Rubro02.class);
    }

}
