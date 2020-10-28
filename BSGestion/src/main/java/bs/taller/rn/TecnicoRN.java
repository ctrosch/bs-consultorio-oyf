/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.rn;

import bs.taller.dao.TecnicoDAO;
import bs.taller.modelo.Tecnico;
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

public class TecnicoRN implements Serializable {

    @EJB
    private TecnicoDAO tecnicoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Tecnico tecnico, boolean esNuevo) throws Exception {

        if (tecnico.getCodigo() == null) {
            tecnicoDAO.crear(tecnico);
        } else {
            tecnicoDAO.editar(tecnico);
        }
    }

    public void eliminar(Tecnico operario) throws Exception {

        tecnicoDAO.eliminar(operario);

    }

    public Tecnico getTecnico(Integer cod) {
        return tecnicoDAO.getTecnico(cod);
    }

    public List<Tecnico> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return tecnicoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Tecnico> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return tecnicoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
