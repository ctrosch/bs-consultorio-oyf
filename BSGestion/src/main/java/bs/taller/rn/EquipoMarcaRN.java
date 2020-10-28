/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.rn;

import bs.taller.dao.EquipoMarcaDAO;
import bs.taller.modelo.EquipoMarca;
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
public class EquipoMarcaRN implements Serializable {

    @EJB
    private EquipoMarcaDAO equipoMarcaDAO;

    public EquipoMarca getEquipoMarca(Integer id) {

        return equipoMarcaDAO.getObjeto(EquipoMarca.class, id);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(EquipoMarca e, boolean esNuevo) throws Exception {

        if (e.getCodigo() == null) {
            equipoMarcaDAO.crear(e);
        } else {
            equipoMarcaDAO.editar(e);
        }
    }

    public void eliminar(EquipoMarca estadoEntidad) throws Exception {

        equipoMarcaDAO.eliminar(estadoEntidad);
    }

    public List<EquipoMarca> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return equipoMarcaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }
}
