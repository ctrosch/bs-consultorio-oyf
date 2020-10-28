/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.CarreraDAO;
import bs.educacion.modelo.Arancel;
import bs.educacion.modelo.Carrera;
import bs.educacion.modelo.ItemArancel;
import bs.educacion.modelo.ItemCarreraSucursal;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
public class CarreraRN implements Serializable {

    @EJB
    private CarreraDAO carreraDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Carrera guardar(Carrera carrera, boolean esNuevo) throws Exception {

        preSave(carrera, esNuevo);
        control(carrera, esNuevo);
        reorganizarNroItem(carrera);

        if (esNuevo) {

            if (carreraDAO.getObjeto(Carrera.class, carrera.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una carrera con ese codigo " + carrera.getCodigo());
            }

            carreraDAO.crear(carrera);

        } else {

            carrera = (Carrera) carreraDAO.editar(carrera);

        }

        return carrera;

    }

    public void preSave(Carrera carrera, boolean esNuevo) throws Exception {

        if (esNuevo) {

        }

        if (carrera.getSucursales() != null) {

            carrera.getSucursales().forEach(i -> {
                i.setCarrera(carrera);
            });
        }
        if (carrera.getAranceles() != null) {

            for (Arancel a : carrera.getAranceles()) {
                if (a.getCodigo() == null || a.getCodigo().isEmpty()) {
                    a.setCodigo(carrera.getCodigo() + "-" + a.getAnioLectivo());
                }
            }
        }

    }

    public void control(Carrera carrera, boolean esNuevo) throws ExcepcionGeneralSistema {

        String sError = "";

        if (carrera.getUnidadNegocio() == null) {
            sError += "La Unidad de Negocio es Obligatoria | ";
        }

        if (carrera.getCodigo() == null || carrera.getCodigo().isEmpty()) {
            sError += "El código es obligatorio | ";
        }

        if (carrera.getTitulo() == null || carrera.getTitulo().isEmpty()) {
            sError += "El titulo es obligatorio | ";
        }

        if (carrera.getArea() == null) {
            sError += "El Área es obligatoria | ";
        }

        if (carrera.getTipoCarrera() == null) {
            sError += "El tipo de carrera es obligatorio | ";
        }

        if (carrera.getAranceles() != null) {

            for (Arancel a : carrera.getAranceles()) {

                if (a.getAnioLectivo() <= 0) {
                    sError += "El año de referencia del arancél es obligatorio | ";
                }

                if (a.getItems() == null || a.getItems().isEmpty()) {
                    sError += "El arancél " + a.getAnioLectivo() + " debe tener al menos 1 item | ";
                } else {

                    for (ItemArancel ia : a.getItems()) {

                        if (ia.getConcepto() == null) {
                            sError += "El item " + ia.getNroitm() + " del arancél " + a.getAnioLectivo() + " debe tener un concepto seleccionado | ";
                        }

                        if (ia.getPeriodo() == null) {
                            sError += "El item " + ia.getNroitm() + " del arancél " + a.getAnioLectivo() + " debe tener un período seleccionado | ";
                        }

                        if (ia.getCantidad() <= 0) {
                            sError += "El item " + ia.getNroitm() + " del arancél " + a.getAnioLectivo() + " debe tener un valor para el campo cantidad mayor o igual a 1 | ";
                        }

                        if (ia.getImporte() <= 0) {
                            sError += "El item " + ia.getNroitm() + " del arancél " + a.getAnioLectivo() + " debe tener un importe asignado | ";
                        }

                    }
                }
            }
        }

        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public void eliminar(Carrera carrera) throws Exception {

        carreraDAO.eliminar(carrera);

    }

    public Carrera duplicar(Carrera a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Carrera nulo, nada para duplicar");
        }

        Carrera carrera = a.clone();
        carrera.setCodigo("");
        carrera.setTitulo(a.getTitulo() + "( Duplicado)");

        return carrera;
    }

    public Carrera getCarrera(String id) {

        return carreraDAO.getCarrera(id);
    }

    public List<Carrera> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return carreraDAO.getCarreraByBusqueda(null, txtBusqueda, mostrarDebaja, 10);
    }

    public List<Carrera> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return carreraDAO.getCarreraByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Carrera> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return carreraDAO.getCarreraByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public String getProximoCodigo(String codUnidadNegocio) {
        int codigo = carreraDAO.getMaxCodigo(codUnidadNegocio);

        String nroCodigo = "00000000" + String.valueOf(codigo);
        return codUnidadNegocio + nroCodigo.substring(nroCodigo.length() - 3, nroCodigo.length());

    }

    public void asignarCodigo(Carrera carrera) throws Exception {
        String sErrores = "";

        if (carrera.getUnidadNegocio() == null) {
            sErrores += "- Seleccione la unidad de negocio para poder generar el código de carrera \n";
            carrera.setCodigo("");
            return;
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

        String codUnidadNegocio = carrera.getUnidadNegocio() != null ? carrera.getUnidadNegocio().getCodigo() : "";

        String codigo = getProximoCodigo(codUnidadNegocio);

        carrera.setCodigo(codigo);

    }

    public void reorganizarNroItem(Carrera carrera) {

        //Reorganizamos los números de items
        int i;

        if (carrera.getSucursales() != null) {

            i = 1;
            for (ItemCarreraSucursal item : carrera.getSucursales()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (carrera.getAranceles() != null) {
            for (Arancel a : carrera.getAranceles()) {

                if (a.getItems() != null) {

                    i = 1;
                    for (ItemArancel item : a.getItems()) {
                        item.setNroitm(i);
                        i++;
                    }
                }
            }
        }
    }

    public void nuevoArancel(Carrera carrera) throws ExcepcionGeneralSistema {

        if (carrera == null) {
            throw new ExcepcionGeneralSistema("No existe un carrera seleccionado para agregar aranceles");
        }

        if (carrera.getAranceles() == null) {
            carrera.setAranceles(new ArrayList<>());
        }

        Arancel arancel = new Arancel();
        arancel.setCarrera(carrera);

        carrera.getAranceles().add(arancel);

    }

    public void eliminarArancel(Carrera carrrera, Arancel item) throws ExcepcionGeneralSistema {

        if (carrrera == null) {
            throw new ExcepcionGeneralSistema("No existe un Carrera seleccionado para quitar una condición de pago");
        }

        if (item == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

//        for (Arancel e : carrrera.getAranceles()) {
//
//            if (e.getNroitm() >= 0) {
//
//                if (e.getNroitm() == e.getNroitm()) {
//                    indiceItemBorrar = i;
//                }
//            }
//            i++;
//        }
//
//        //Borramos los items productos
//        if (indiceItemBorrar >= 0) {
//            ItemArancel remove = carrrera.getAranceles().remove(indiceItemBorrar);
//            if (remove != null) {
//
//                if (remove.getId() != null) {
//                    carreraDAO.eliminar(ItemArancel.class, remove.getId());
//                }
//                fItemBorrado = true;
//            }
//        }
        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(carrrera);

    }

    public void nuevoItemArancel(Arancel arancel) throws ExcepcionGeneralSistema {

        if (arancel == null) {
            throw new ExcepcionGeneralSistema("No existe un arancel seleccionado para agregar items");
        }

        if (arancel.getItems() == null) {
            arancel.setItems(new ArrayList<>());
        }

        ItemArancel item = new ItemArancel();
        item.setArancel(arancel);
        item.setNroitm(arancel.getItems().size() + 1);

        arancel.getItems().add(item);

    }

    public void eliminarArancel(Carrera carrera, Arancel arancel, ItemArancel item) throws ExcepcionGeneralSistema {

        if (arancel == null) {
            throw new ExcepcionGeneralSistema("No existe un arancel seleccionado para quitar un item");
        }

        if (item == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemArancel e : arancel.getItems()) {

            if (e.getNroitm() >= 0) {

                if (e.getNroitm() == e.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemArancel remove = arancel.getItems().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    carreraDAO.eliminar(ItemArancel.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(carrera);

    }

    public void nuevoItemSucursal(Carrera carrera) throws ExcepcionGeneralSistema {

        if (carrera == null) {
            throw new ExcepcionGeneralSistema("No existe una carrera seleccionado para agregar una sucursal");
        }

        if (carrera.getSucursales() == null) {
            carrera.setSucursales(new ArrayList<>());
        }

        ItemCarreraSucursal item = new ItemCarreraSucursal();
        item.setNroitm(carrera.getSucursales().size() + 1);
        item.setCarrera(carrera);

        carrera.getSucursales().add(item);

    }

    public void eliminarItemSucursal(Carrera carrera, ItemCarreraSucursal item) throws ExcepcionGeneralSistema {

        if (carrera == null) {
            throw new ExcepcionGeneralSistema("No existe un Carrera seleccionado para quitar una sucursal");
        }

        if (item == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemCarreraSucursal e : carrera.getSucursales()) {

            if (e.getNroitm() >= 0) {

                if (e.getNroitm() == e.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemCarreraSucursal remove = carrera.getSucursales().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    carreraDAO.eliminar(ItemCarreraSucursal.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(carrera);

    }

    public int getCantidadRegistros() {

        return carreraDAO.getCantidadRegistros();
    }

}
