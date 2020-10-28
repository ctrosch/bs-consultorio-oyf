/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.ActividadComercialDAO;
import bs.entidad.modelo.ActividadComercial;
import bs.entidad.modelo.ActividadComercialPK;
import bs.entidad.modelo.TipoEntidad;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author GUILLERMO
 */
@Stateless
public class ActividadComercialRN implements Serializable {

    @EJB
    ActividadComercialDAO actividadComercialDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ActividadComercial guardar(ActividadComercial a, boolean esNuevo) throws Exception {

        if (esNuevo) {

            ActividadComercialPK idPK = new ActividadComercialPK(a.getCodigo(), a.getCodtip());

            if (actividadComercialDAO.getObjeto(ActividadComercial.class, idPK) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una actividad con el código " + idPK);
            }
            actividadComercialDAO.crear(a);
        } else {
            a = (ActividadComercial) actividadComercialDAO.editar(a);
        }

        return a;
    }

    public ActividadComercial getActividadComercial(String codigo, String tipo) {

        ActividadComercialPK idPK = new ActividadComercialPK(codigo, tipo);
        return actividadComercialDAO.getObjeto(ActividadComercial.class, idPK);
    }

    public ActividadComercial getActividadComercial(String codigo, TipoEntidad tipo) {

        ActividadComercialPK idPK = new ActividadComercialPK(codigo, tipo.getCodigo());
        return actividadComercialDAO.getObjeto(ActividadComercial.class, idPK);
    }

    public List<ActividadComercial> getLista(boolean mostrarDebaja) {

        return actividadComercialDAO.getActividadComercialByBusqueda(null, "", null, mostrarDebaja, 15);

    }

    public void eliminar(ActividadComercial actividadComercial) throws Exception {

        actividadComercialDAO.eliminar(actividadComercial);
    }

    public List<ActividadComercial> getActividadComercialByBusqueda(String txtBusqueda, TipoEntidad tipo, boolean mostrarDeBaja) {

        return actividadComercialDAO.getActividadComercialByBusqueda(null, txtBusqueda, tipo, mostrarDeBaja, 25);

    }

    public List<ActividadComercial> getActividadComercialByBusqueda(Map<String, String> filtro, String txtBusqueda, TipoEntidad tipo, boolean mostrarDeBaja, int cantMax) {

        return actividadComercialDAO.getActividadComercialByBusqueda(filtro, txtBusqueda, tipo, mostrarDeBaja, cantMax);

    }

}
