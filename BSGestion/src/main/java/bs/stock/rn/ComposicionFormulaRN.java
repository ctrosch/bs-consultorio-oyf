/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.ComposicionFormulaDAO;
import bs.stock.modelo.ComposicionFormula;
import bs.stock.modelo.ComposicionFormulaPK;
import bs.stock.modelo.ItemComposicionFormulaComponente;
import bs.stock.modelo.ItemComposicionFormulaProceso;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
public class ComposicionFormulaRN implements Serializable {

    @EJB
    private ComposicionFormulaDAO composicionFormulaDAO;
    @EJB
    private FormulaRN formulaRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ComposicionFormula guardar(ComposicionFormula e, boolean esNuevo) throws ExcepcionGeneralSistema {

        borrarItemNoValidos(e);

        if (esNuevo) {
            if (composicionFormulaDAO.getComposicionFormula(new ComposicionFormulaPK(e.getArtcod(), e.getCodfor())) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + e.getArtcod() + "-" + e.getCodfor());
            }
            composicionFormulaDAO.crear(e);
        } else {
            e = (ComposicionFormula) composicionFormulaDAO.editar(e);
        }

        return e;
    }

    public ComposicionFormula nuevo() {

        ComposicionFormula c = new ComposicionFormula();
        c.setFechaInicio(new Date());
        c.setCodfor("STD");
        c.setFormula(formulaRN.getFormula("STD"));

        return c;
    }

    public void nuevoItemComponente(ComposicionFormula composicion) throws ExcepcionGeneralSistema {

        if (composicion == null) {
            throw new ExcepcionGeneralSistema("No existe una composición seleccionada para agregarle un item");
        }

        if (composicion.getArtcod() == null || composicion.getArtcod().isEmpty()) {
            throw new ExcepcionGeneralSistema("Primero seleccione el producto principal");
        }

        if (composicion.getCodfor() == null || composicion.getCodfor().isEmpty()) {
            throw new ExcepcionGeneralSistema("Primero seleccione la formula principal");
        }

        if (composicion.getItemsComponente() == null) {
            composicion.setItemsComponente(new ArrayList<ItemComposicionFormulaComponente>());
        }

        ItemComposicionFormulaComponente itemComponente = new ItemComposicionFormulaComponente();
        itemComponente.setNroitm(composicion.getItemsComponente().size() + 1);
        itemComponente.setComposicionFormula(composicion);
        composicion.getItemsComponente().add(itemComponente);

        reorganizarNroItem(composicion);
    }

    public void eliminarItemComponente(ComposicionFormula composicion, ItemComposicionFormulaComponente itemComponente) throws ExcepcionGeneralSistema, Exception {

        if (itemComponente == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemComposicionFormulaComponente item : composicion.getItemsComponente()) {

            if (item.getNroitm() >= 0) {
                if (item.getNroitm() == itemComponente.getNroitm()) {
                    if (itemComponente.getProductoComponente() == null) {
                        indiceItemBorrar = i;
                    } else if (item.getProductoComponente().equals(itemComponente.getProductoComponente())) {
                        indiceItemBorrar = i;
                    }
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar
                >= 0) {
            ItemComposicionFormulaComponente remove = composicion.getItemsComponente().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    composicionFormulaDAO.eliminar(ItemComposicionFormulaComponente.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(composicion);
        composicion = (ComposicionFormula) composicionFormulaDAO.editar(composicion);

    }

    public void nuevoItemProceso(ComposicionFormula composicion) throws ExcepcionGeneralSistema {

        if (composicion == null) {
            throw new ExcepcionGeneralSistema("No existe una composición seleccionada para agregarle un item");
        }

        if (composicion.getArtcod() == null || composicion.getArtcod().isEmpty()) {
            throw new ExcepcionGeneralSistema("Primero seleccione el producto principal");
        }

        if (composicion.getCodfor() == null || composicion.getCodfor().isEmpty()) {
            throw new ExcepcionGeneralSistema("Primero seleccione la formula principal");
        }

        if (composicion.getItemsProceso() == null) {
            composicion.setItemsProceso(new ArrayList<ItemComposicionFormulaProceso>());
        }

        ItemComposicionFormulaProceso itemProceso = new ItemComposicionFormulaProceso();
        itemProceso.setNroitm(composicion.getItemsProceso().size() + 1);
        itemProceso.setComposicionFormula(composicion);
        composicion.getItemsProceso().add(itemProceso);

        reorganizarNroItem(composicion);
    }

    public void eliminarItemProceso(ComposicionFormula composicion, ItemComposicionFormulaProceso itemProceso) throws ExcepcionGeneralSistema, Exception {

        if (itemProceso == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemComposicionFormulaProceso item : composicion.getItemsProceso()) {

            if (item.getNroitm() >= 0) {
                if (item.getNroitm() == itemProceso.getNroitm()) {
                    if (itemProceso.getProductoComponente() == null) {
                        indiceItemBorrar = i;
                    } else if (item.getProductoComponente().equals(itemProceso.getProductoComponente())) {
                        indiceItemBorrar = i;
                    }
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemComposicionFormulaProceso remove = composicion.getItemsProceso().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    composicionFormulaDAO.eliminar(ItemComposicionFormulaProceso.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(composicion);
        composicion = (ComposicionFormula) composicionFormulaDAO.editar(composicion);

    }

    private void borrarItemNoValidos(ComposicionFormula c) {

//        if(c.getItemsComponentes()== null || c.getItemsComponentes().isEmpty()){
//            return;
//        }
//
//        String indiceBorrar[] = new String[c.getItemsComponentes().size()];
//
//        //Seteamos los valores en -1
//        for(int i=0;i<indiceBorrar.length;i++){
//            indiceBorrar[i] = "N";
//        }
//
//        for(int i = 0 ; i < c.getItemsComponentes().size(); i++ ){
//
//            ComposicionFormulaItem ic = c.getItemsComponentes().get(i);
//
//            if(ic.getProducto()==null){
//                indiceBorrar[i] = "S" ;
//                continue;
//            }
//        }
//
//        for(int i=0;i<indiceBorrar.length;i++){
//
//            if(indiceBorrar[i].equals("S")){
//                System.err.println("borra item");
//                ComposicionFormulaItem remove = c.getItemsComponentes().remove(i);
//            }
//        }
    }

    public ComposicionFormula getComposicionFormula(ComposicionFormulaPK idPK) {
        return composicionFormulaDAO.getComposicionFormula(idPK);
    }

    public ComposicionFormula getComprosicionFormula(String artcod, String formul) {

        ComposicionFormulaPK idPK = new ComposicionFormulaPK(artcod, formul);
        return composicionFormulaDAO.getComposicionFormula(idPK);

    }

    public void eliminar(ComposicionFormula composicionFormula) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<ComposicionFormula> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {
        return composicionFormulaDAO.getListaByBusqueda(filtro, "", "", txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<ComposicionFormula> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {
        return composicionFormulaDAO.getListaByBusqueda(null, "", "", txtBusqueda, mostrarDebaja, 15);
    }

    public void reorganizarNroItem(ComposicionFormula c) {

        //Reorganizamos los números de items
        int i;

        if (c.getItemsComponente() != null) {

            i = 1;
            for (ItemComposicionFormulaComponente item : c.getItemsComponente()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (c.getItemsProceso() != null) {

            i = 1;
            for (ItemComposicionFormulaProceso item : c.getItemsProceso()) {
                item.setNroitm(i);
                i++;
            }
        }
    }

    public void borrarItemsComposicionFormula() {

        composicionFormulaDAO.borrarItemsComponentes();
        composicionFormulaDAO.borrarItemsProceso();

    }

}
