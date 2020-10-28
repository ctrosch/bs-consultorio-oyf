/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.DepositoDAO;
import bs.stock.modelo.Deposito;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author ctrosch
 */
@Stateless
public class DepositoRN implements Serializable {

    @EJB
    private DepositoDAO depositoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Deposito deposito, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (depositoDAO.getObjeto(Deposito.class, deposito.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un depósito con el código" + deposito.getCodigo());
            }
            depositoDAO.crear(deposito);

        } else {
            depositoDAO.editar(deposito);
        }
    }

    public void eliminar(Deposito deposito) throws Exception {

        depositoDAO.eliminar(deposito);

    }

    public List<Deposito> getLista(boolean mostrarDebaja) {

        return depositoDAO.getLista(mostrarDebaja);

    }

    public Deposito getDeposito(String id) {
        return depositoDAO.getDeposito(id);
    }

    public List<Deposito> getLista() {
        return depositoDAO.getLista();
    }

    public List<Deposito> getLista(int maxResults, int firstResult) {
        return depositoDAO.getLista(maxResults, firstResult);
    }

    public List<Deposito> getDepositoByBusqueda(String txtBusqueda, boolean mostrarDeBaja) {

        return depositoDAO.getDepositoByBusqueda(null, txtBusqueda, mostrarDeBaja, 15);
    }

    public List<Deposito> getDepositoByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        return depositoDAO.getDepositoByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantMax);
    }

    public void setDeBajaAll() {

        depositoDAO.setDeBajaAll(Deposito.class);
    }
}
