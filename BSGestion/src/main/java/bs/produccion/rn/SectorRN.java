/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.produccion.dao.SectorDAO;
import bs.produccion.modelo.Sector;
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
public class SectorRN implements Serializable {

    @EJB
    private SectorDAO departamentoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Sector d, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (departamentoDAO.getObjeto(Sector.class, d.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código" + d.getCodigo());
            }
            departamentoDAO.crear(d);

        } else {
            departamentoDAO.editar(d);
        }
    }

    public void eliminar(Sector deposito) throws Exception {

        departamentoDAO.eliminar(deposito);
    }

    public List<Sector> getLista(boolean mostrarDebaja) {

        return departamentoDAO.getListaByBusqueda("", mostrarDebaja, 0);
    }

    public Sector getSector(String codigo) {
        return departamentoDAO.getSector(codigo);
    }

    public List<Sector> getLista() {
        return departamentoDAO.getListaByBusqueda("", false, 0);
    }

    public List<Sector> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        return departamentoDAO.getListaByBusqueda(txtBusqueda, mostrarDeBaja, cantMax);
    }
}
