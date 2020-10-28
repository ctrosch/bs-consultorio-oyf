/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.Rubro03DAO;
import bs.stock.modelo.Rubro03;
import bs.stock.modelo.RubroPK;
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
public class Rubro03RN implements Serializable {

    @EJB
    Rubro03DAO rubroDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Rubro03 rubro03, boolean esNuevo) throws ExcepcionGeneralSistema {

        if (esNuevo) {

            RubroPK idPK = new RubroPK(rubro03.getTippro(), rubro03.getCodigo());

            if (rubroDAO.getObjeto(Rubro03.class, idPK) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un rubro con el código" + rubro03.getCodigo());
            }
            rubroDAO.crear(rubro03);
        } else {
            rubroDAO.editar(rubro03);
        }
    }

    public Rubro03 getRubro03(RubroPK idPK) {

        return rubroDAO.getObjeto(Rubro03.class, idPK);
    }

    public Rubro03 getRubro03(String tipo, String codigo) {

        RubroPK idPK = new RubroPK(tipo, codigo);
        return rubroDAO.getObjeto(Rubro03.class, idPK);
    }

    public void eliminar(Rubro03 rubro03) throws Exception {

        rubroDAO.eliminar(rubro03);

    }

    public List<Rubro03> getLista(boolean mostrarDebaja) {

        return rubroDAO.getListaByBusqueda("", mostrarDebaja, 15);
    }

    public List<Rubro03> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return rubroDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, 15);
    }

    public void setDeBajaAll() {

        rubroDAO.setDeBajaAll(Rubro03.class);
    }

}
