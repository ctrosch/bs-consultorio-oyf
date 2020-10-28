/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.produccion.dao.PlantaDAO;
import bs.produccion.modelo.Planta;
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
public class PlantaRN implements Serializable {

    @EJB
    private PlantaDAO departamentoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Planta d, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (departamentoDAO.getObjeto(Planta.class, d.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código" + d.getCodigo());
            }
            departamentoDAO.crear(d);

        } else {
            departamentoDAO.editar(d);
        }
    }

    public void eliminar(Planta deposito) throws Exception {

        departamentoDAO.eliminar(deposito);
    }

    public List<Planta> getLista(boolean mostrarDebaja) {

        return departamentoDAO.getListaByBusqueda("", mostrarDebaja, 0);
    }

    public Planta getPlanta(String codigo) {
        return departamentoDAO.getPlanta(codigo);
    }

    public List<Planta> getLista() {
        return departamentoDAO.getListaByBusqueda("", false, 0);
    }

    public List<Planta> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        return departamentoDAO.getListaByBusqueda(txtBusqueda, mostrarDeBaja, cantMax);
    }
}
