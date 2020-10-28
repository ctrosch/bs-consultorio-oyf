/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.inversiones.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.inversiones.dao.ProyectoDAO;
import bs.inversiones.modelo.Proyecto;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Agustin
 */
@Stateless
public class ProyectoRN implements Serializable {

    @EJB
    private ProyectoDAO proyectoDAO;

    public Proyecto getProyecto(String value) {

        return proyectoDAO.getObjeto(Proyecto.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Proyecto c, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (proyectoDAO.getObjeto(Proyecto.class, c.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad " + c.getClass().getName() + " con el código" + c.getCodigo());
            }
            proyectoDAO.crear(c);
        } else {
            proyectoDAO.editar(c);
        }
    }

    public void eliminar(Proyecto proyecto) throws Exception {

        proyectoDAO.eliminar(proyecto);
    }

    public List<Proyecto> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return proyectoDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, 15);
    }

    public List<Proyecto> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return proyectoDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantMax);
    }

    public void setDeBajaAll() {

        proyectoDAO.setDeBajaAll(Proyecto.class);

    }

}
