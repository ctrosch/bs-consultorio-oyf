/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.rn;

import bs.taller.dao.EquipoModeloDAO;
import bs.taller.modelo.EquipoModelo;
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
public class EquipoModeloRN implements Serializable {

    @EJB
    private EquipoModeloDAO equipoModeloDAO;

    public EquipoModelo getEquipoModelo(Integer id) {

        return equipoModeloDAO.getObjeto(EquipoModelo.class, id);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(EquipoModelo e, boolean esNuevo) throws Exception {

        if (e.getCodigo() == null) {
            equipoModeloDAO.crear(e);
        } else {
            equipoModeloDAO.editar(e);
        }
    }

    public void eliminar(EquipoModelo estadoEntidad) throws Exception {

        equipoModeloDAO.eliminar(estadoEntidad);
    }

    public List<EquipoModelo> getListaByBusqueda(Integer codMarca, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return equipoModeloDAO.getListaByBusqueda(codMarca, null, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<EquipoModelo> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return equipoModeloDAO.getListaByBusqueda(null, filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<EquipoModelo> getListaByBusqueda(Integer codMarca, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return equipoModeloDAO.getListaByBusqueda(codMarca, null, txtBusqueda, mostrarDebaja, cantMax);
    }
}
