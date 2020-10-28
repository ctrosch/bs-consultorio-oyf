/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.taller.dao.EquipoDAO;
import bs.taller.modelo.Equipo;
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
public class EquipoRN implements Serializable {

    @EJB
    private EquipoDAO equipoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Equipo c, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (equipoDAO.getObjeto(Equipo.class, c.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un equipo con el código " + c.getCodigo());
            }
            equipoDAO.crear(c);

        } else {
            equipoDAO.editar(c);
        }
    }

    public void eliminar(Equipo codigoCircuitoService) throws Exception {

        equipoDAO.eliminar(codigoCircuitoService);
    }

    public Equipo getEquipo(String codigo) {

        return equipoDAO.getObjeto(Equipo.class, codigo);

    }

    public List<Equipo> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return equipoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 50);
    }

    public List<Equipo> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return equipoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 50);
    }

    public List<Equipo> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return equipoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public String getProximoNroEquipo() {

        int nroEquipo = equipoDAO.getMaxNroEquipo();

        String codigoEquipo = "00000" + String.valueOf(nroEquipo);
        return codigoEquipo.substring(codigoEquipo.length() - 6, codigoEquipo.length());

    }

}
