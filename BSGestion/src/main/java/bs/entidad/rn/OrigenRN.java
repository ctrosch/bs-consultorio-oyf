/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.OrigenDAO;
import bs.entidad.modelo.Origen;
import bs.entidad.modelo.OrigenPK;
import bs.entidad.modelo.TipoEntidad;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
public class OrigenRN implements Serializable {

    @EJB
    OrigenDAO categoriaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Origen guardar(Origen c, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (categoriaDAO.getObjeto(Origen.class, c.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + c.getCodigo());
            }
            categoriaDAO.crear(c);
        } else {
            c = (Origen) categoriaDAO.editar(c);
        }

        return c;
    }

    public Origen getOrigen(String codigo, String tipo) {

        OrigenPK idPK = new OrigenPK(codigo, tipo);
        return categoriaDAO.getObjeto(Origen.class, idPK);
    }

    public Origen getOrigen(String codigo, TipoEntidad tipo) {

        OrigenPK idPK = new OrigenPK(codigo, tipo.getCodigo());
        return categoriaDAO.getObjeto(Origen.class, idPK);
    }

    public List<Origen> getLista(boolean mostrarDebaja) {

        return categoriaDAO.getOrigenByBusqueda(null, "", null, mostrarDebaja, 15);

    }

    public void eliminar(Origen categoria) throws Exception {

        categoriaDAO.eliminar(categoria);
    }

    public List<Origen> getOrigenByBusqueda(String txtBusqueda, TipoEntidad tipo, boolean mostrarDeBaja) {

        return categoriaDAO.getOrigenByBusqueda(null, "", tipo, mostrarDeBaja, 15);

    }

}
