/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.TipoProductoDAO;
import bs.stock.modelo.Atributo;
import bs.stock.modelo.AtributoValor;
import bs.stock.modelo.Rubro01;
import bs.stock.modelo.Rubro02;
import bs.stock.modelo.Rubro03;
import bs.stock.modelo.RubroPK;
import bs.stock.modelo.TipoProducto;
import bs.stock.modelo.UnidadMedida;
import java.io.Serializable;
import java.util.ArrayList;
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
public class TipoProductoRN implements Serializable {

    @EJB
    TipoProductoDAO tipoProductoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TipoProducto guardar(TipoProducto tipoProducto, boolean esNuevo) throws Exception {

        asignarTipoProducto(tipoProducto);
        control(tipoProducto);

        if (esNuevo) {
            if (tipoProductoDAO.getObjeto(UnidadMedida.class, tipoProducto.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe unidad de medida con el código" + tipoProducto.getCodigo());
            }
            tipoProductoDAO.crear(tipoProducto);
        } else {
            tipoProducto = (TipoProducto) tipoProductoDAO.editar(tipoProducto);
        }

//        tipoProducto = getTipoProducto(tipoProducto.getCodigo());
        return tipoProducto;
    }

    private void control(TipoProducto tipoProducto) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (tipoProducto.getCodigo() == null || tipoProducto.getCodigo().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (tipoProducto.getDescripcion() == null || tipoProducto.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (tipoProducto.getRubro01() != null) {

            for (Rubro01 item : tipoProducto.getRubro01()) {

                if (item.getTippro() == null || item.getTippro().isEmpty()) {
                    sErrores += "- Existe un rubro que tienen el tipo de producto vacío\n";
                }

                if (item.getCodigo() == null || item.getCodigo().isEmpty()) {
                    sErrores += "- Existe un rubro con el código vacío\n";
                }

                if (item.getDescripcion() == null || item.getDescripcion().isEmpty()) {
                    sErrores += "- Existe un rubro con la descripción vacía\n";
                }

            }
        }

        if (tipoProducto.getRubro02() != null) {

            for (Rubro02 item : tipoProducto.getRubro02()) {

                if (item.getTippro() == null || item.getTippro().isEmpty()) {
                    sErrores += "- Existe un rubro que tienen el tipo de producto vacío\n";
                }

                if (item.getCodigo() == null || item.getCodigo().isEmpty()) {
                    sErrores += "- Existe un rubro con el código vacío\n";
                }

                if (item.getDescripcion() == null || item.getDescripcion().isEmpty()) {
                    sErrores += "- Existe un rubro con la descripción vacía\n";
                }

            }
        }

        if (tipoProducto.getAtributos() != null) {

            for (Atributo item : tipoProducto.getAtributos()) {

                if (item.getNombre() == null || item.getNombre().isEmpty()) {
                    sErrores += "- Existe un atributo que tienen el nombre vacío\n";
                }
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public TipoProducto getTipoProducto(String codigo) {

        return tipoProductoDAO.getObjeto(TipoProducto.class, codigo);
    }

    public void eliminar(TipoProducto tipoProducto) throws Exception {

        tipoProductoDAO.eliminar(tipoProducto);

    }

    public List<TipoProducto> getLista(boolean mostrarDebaja) {

        return tipoProductoDAO.getListaByBusqueda(null, "", mostrarDebaja, 0);
    }

    public List<TipoProducto> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return tipoProductoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<TipoProducto> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoProductoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public void asignarTipoProducto(TipoProducto tipo) {

        if (tipo.getRubro01() != null) {

            for (Rubro01 item : tipo.getRubro01()) {
                item.setTippro(tipo.getCodigo());
                item.setTipoProducto(tipo);
            }
        }

        if (tipo.getRubro02() != null) {

            for (Rubro02 item : tipo.getRubro02()) {
                item.setTippro(tipo.getCodigo());
                item.setTipoProducto(tipo);
            }
        }

        if (tipo.getRubro03() != null) {

            for (Rubro03 item : tipo.getRubro03()) {
                item.setTippro(tipo.getCodigo());
                item.setTipoProducto(tipo);
            }
        }

    }

    public void reorganizarNroItem(TipoProducto tipo) {

        //Reorganizamos los números de items
        int i;

        if (tipo.getRubro01() != null) {

            i = 1;
            for (Rubro01 item : tipo.getRubro01()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (tipo.getRubro02() != null) {

            i = 1;
            for (Rubro02 item : tipo.getRubro02()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (tipo.getRubro03() != null) {

            i = 1;
            for (Rubro03 item : tipo.getRubro03()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (tipo.getAtributos() != null) {

            i = 1;
            for (Atributo item : tipo.getAtributos()) {
                item.setNroitm(i);
                i++;
            }
        }
    }

    public void nuevoRubro01(TipoProducto tipo) throws ExcepcionGeneralSistema {

        if (tipo == null) {
            throw new ExcepcionGeneralSistema("No existe un tipo de producto seleccionado para agregarle un rubro");
        }

        if (tipo.getRubro01() == null) {
            tipo.setRubro01(new ArrayList<Rubro01>());
        }

        Rubro01 rubro = new Rubro01();
        rubro.setNroitm(tipo.getRubro01().size() + 1);
        rubro.setTippro(tipo.getCodigo());
        rubro.setTipoProducto(tipo);

        tipo.getRubro01().add(rubro);
        reorganizarNroItem(tipo);

    }

    public void eliminarRubro01(TipoProducto tipo, Rubro01 rubro01) throws ExcepcionGeneralSistema, Exception {

        if (rubro01 == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (Rubro01 item : tipo.getRubro01()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == rubro01.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            Rubro01 remove = tipo.getRubro01().remove(indiceItemBorrar);
            if (remove != null && remove.getTippro() != null && remove.getCodigo() != null) {

                RubroPK idPK = new RubroPK(remove.getTippro(), remove.getCodigo());

                if (tipoProductoDAO.getObjeto(Rubro01.class, idPK) != null) {
                    tipoProductoDAO.eliminar(Rubro01.class, idPK);
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(tipo);
    }

    public void nuevoRubro02(TipoProducto tipo) throws ExcepcionGeneralSistema {

        if (tipo == null) {
            throw new ExcepcionGeneralSistema("No existe un tipo de producto seleccionado para agregarle un rubro");
        }

        if (tipo.getRubro02() == null) {
            tipo.setRubro02(new ArrayList<Rubro02>());
        }

        Rubro02 rubro = new Rubro02();
        rubro.setNroitm(tipo.getRubro02().size() + 1);
        rubro.setTippro(tipo.getCodigo());
        rubro.setTipoProducto(tipo);

        tipo.getRubro02().add(rubro);

        reorganizarNroItem(tipo);

    }

    public void eliminarRubro02(TipoProducto tipo, Rubro02 rubro02) throws ExcepcionGeneralSistema, Exception {

        if (rubro02 == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (Rubro02 item : tipo.getRubro02()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == rubro02.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            Rubro02 remove = tipo.getRubro02().remove(indiceItemBorrar);
            if (remove != null && remove.getTippro() != null && remove.getCodigo() != null) {

                RubroPK idPK = new RubroPK(remove.getTippro(), remove.getCodigo());

                if (tipoProductoDAO.getObjeto(Rubro02.class, idPK) != null) {
                    tipoProductoDAO.eliminar(Rubro02.class, idPK);
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(tipo);
    }

    //----------------------------------------------------------------------------
    public void nuevoRubro03(TipoProducto tipo) throws ExcepcionGeneralSistema {

        if (tipo == null) {
            throw new ExcepcionGeneralSistema("No existe un tipo de producto seleccionado para agregarle un rubro");
        }

        if (tipo.getRubro03() == null) {
            tipo.setRubro03(new ArrayList<Rubro03>());
        }

        Rubro03 rubro = new Rubro03();
        rubro.setNroitm(tipo.getRubro03().size() + 1);
        rubro.setTippro(tipo.getCodigo());
        rubro.setTipoProducto(tipo);

        tipo.getRubro03().add(rubro);
        reorganizarNroItem(tipo);

    }

    public void eliminarRubro03(TipoProducto tipo, Rubro03 rubro03) throws ExcepcionGeneralSistema, Exception {

        if (rubro03 == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (Rubro03 item : tipo.getRubro03()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == rubro03.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            Rubro03 remove = tipo.getRubro03().remove(indiceItemBorrar);
            if (remove != null && remove.getTippro() != null && remove.getCodigo() != null) {

                RubroPK idPK = new RubroPK(remove.getTippro(), remove.getCodigo());

                if (tipoProductoDAO.getObjeto(Rubro03.class, idPK) != null) {
                    tipoProductoDAO.eliminar(Rubro03.class, idPK);
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(tipo);
    }

    //----------------------------------------------------------------------------
    public void nuevoAtributo(TipoProducto tipo) throws ExcepcionGeneralSistema {
        if (tipo == null) {
            throw new ExcepcionGeneralSistema("No existe un tipo de producto seleccionado para agregarle un atributo");
        }

        if (tipo.getAtributos() == null) {
            tipo.setAtributos(new ArrayList<Atributo>());
        }

        Atributo atributo = new Atributo();
        atributo.setNroitm(tipo.getAtributos().size() + 1);
        atributo.setTipoProducto(tipo);

        tipo.getAtributos().add(atributo);
        reorganizarNroItem(tipo);
    }

    public void eliminarAtributo(TipoProducto tipo, Atributo atributo) throws ExcepcionGeneralSistema {

        if (atributo == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (Atributo item : tipo.getAtributos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == atributo.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            Atributo remove = tipo.getAtributos().remove(indiceItemBorrar);
            if (remove != null && remove.getId() != null) {

                if (tipoProductoDAO.getObjeto(Atributo.class, remove.getId()) != null) {
                    tipoProductoDAO.eliminar(Atributo.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(tipo);
    }

    //----------------------------------------------------------------------------
    public void nuevoAtributoValor(Atributo atributo) throws ExcepcionGeneralSistema {
        if (atributo == null) {
            throw new ExcepcionGeneralSistema("No existe un atributo seleccionado para agregarle un valor posible");
        }

        if (atributo.getValores() == null) {
            atributo.setValores(new ArrayList<AtributoValor>());
        }

        AtributoValor valor = new AtributoValor();
        valor.setNroitm(atributo.getValores().size() + 1);
        valor.setAtributo(atributo);

        atributo.getValores().add(valor);

    }

    public void eliminarAtributoValor(Atributo atributo, AtributoValor valor) throws ExcepcionGeneralSistema {

        if (atributo == null) {
            throw new ExcepcionGeneralSistema("Atributo vacío, nada para borrar");
        }

        if (valor == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para borrar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (AtributoValor item : atributo.getValores()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == valor.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            AtributoValor remove = atributo.getValores().remove(indiceItemBorrar);
            if (remove != null) {
                if (remove.getId() != null) {
                    if (tipoProductoDAO.getObjeto(AtributoValor.class, remove.getId()) != null) {
                        tipoProductoDAO.eliminar(AtributoValor.class, remove.getId());
                    }
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }
}
