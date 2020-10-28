/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.rn;

import bs.taller.dao.EquipoTipoDAO;
import bs.taller.modelo.EquipoTipo;
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
public class EquipoTipoRN implements Serializable {

    @EJB
    private EquipoTipoDAO equipoTipoDAO;

    public EquipoTipo getEquipoTipo(Integer id) {

        return equipoTipoDAO.getObjeto(EquipoTipo.class, id);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(EquipoTipo e, boolean esNuevo) throws Exception {

        if (e.getCodigo() == null) {
            equipoTipoDAO.crear(e);
        } else {
            equipoTipoDAO.editar(e);
        }
    }

    public void eliminar(EquipoTipo estadoEntidad) throws Exception {

        equipoTipoDAO.eliminar(estadoEntidad);
    }

    public List<EquipoTipo> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return equipoTipoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }
}
