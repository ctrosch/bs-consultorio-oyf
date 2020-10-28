/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.CategoriaDAO;
import bs.entidad.modelo.Categoria;
import bs.entidad.modelo.CategoriaPK;
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
 * @author Claudio
 */
@Stateless
public class CategoriaRN implements Serializable {

    @EJB
    CategoriaDAO categoriaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Categoria guardar(Categoria c, boolean esNuevo) throws Exception {

        if (esNuevo) {

            CategoriaPK idPK = new CategoriaPK(c.getCodigo(), c.getCodtip());

            if (categoriaDAO.getObjeto(Categoria.class, idPK) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + idPK);
            }
            categoriaDAO.crear(c);
        } else {
            c = (Categoria) categoriaDAO.editar(c);
        }

        return c;
    }

    public Categoria getCategoria(String codigo, String tipo) {

        CategoriaPK idPK = new CategoriaPK(codigo, tipo);
        return categoriaDAO.getObjeto(Categoria.class, idPK);
    }

    public Categoria getCategoria(String codigo, TipoEntidad tipo) {

        CategoriaPK idPK = new CategoriaPK(codigo, tipo.getCodigo());
        return categoriaDAO.getObjeto(Categoria.class, idPK);
    }

    public List<Categoria> getLista(boolean mostrarDebaja) {

        return categoriaDAO.getCategoriaByBusqueda(null, "", null, mostrarDebaja, 15);

    }

    public void eliminar(Categoria categoria) throws Exception {

        categoriaDAO.eliminar(categoria);
    }

    public List<Categoria> getCategoriaByBusqueda(String txtBusqueda, TipoEntidad tipo, boolean mostrarDeBaja) {

        return categoriaDAO.getCategoriaByBusqueda(null, "", tipo, mostrarDeBaja, 15);

    }

    public List<Categoria> getCategoriaByBusqueda(Map<String, String> filtro, String txtBusqueda, TipoEntidad tipo, boolean mostrarDeBaja, int cantMax) {

        return categoriaDAO.getCategoriaByBusqueda(filtro, txtBusqueda, tipo, mostrarDeBaja, cantMax);

    }

}
