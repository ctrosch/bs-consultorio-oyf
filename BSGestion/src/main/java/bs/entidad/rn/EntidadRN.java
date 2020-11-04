/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.ContactoDAO;
import bs.entidad.dao.EntidadDAO;
import bs.entidad.modelo.Contacto;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.DireccionEntregaEntidadPK;
import bs.entidad.modelo.EntidadActividadComercial;
import bs.entidad.modelo.EntidadCamion;
import bs.entidad.modelo.EntidadChofer;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.EntidadHorario;
import bs.entidad.modelo.EntidadObraSocial;
import bs.entidad.modelo.EntidadTransporte;
import bs.entidad.modelo.ImpuestoPorEntidad;
import bs.entidad.modelo.ParametroEntidad;
import bs.entidad.modelo.RetencionPorEntidad;
import bs.entidad.modelo.TipoEntidad;
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
public class EntidadRN implements Serializable {

    @EJB
    private EntidadDAO entidadDAO;
    @EJB
    private ContactoDAO contactoDAO;
    @EJB
    private ParametroEntidadRN parametroEntidadRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;
    @EJB
    private CategoriaRN categoriaRN;
    @EJB
    private DireccionEntregaRN direccionEntregaRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EntidadComercial guardar(EntidadComercial e, boolean esNuevo) throws Exception {

        preSave(e);

        control(e, esNuevo);

        if (esNuevo) {
            if (entidadDAO.getObjeto(EntidadComercial.class, e.getNrocta()) != null) {
                modificarNroCuenta(e);
                //throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + e.getNrocta());
            }
            generoDireccionEntrega(e);

            entidadDAO.crear(e);
        } else {
            e = (EntidadComercial) entidadDAO.editar(e);
        }

//        e = getEntidad(e.getNrocta());
        return e;
    }

    private void generoDireccionEntrega(EntidadComercial e) {

        if (e.getDireccionesDeEntrega() != null) {
            e.getDireccionesDeEntrega().clear();
        }
        nuevoItemDireccionEntrega(e, true);

    }

    private void preSave(EntidadComercial e) {

        //Si es alumno o profesor asignamos los nombres completos para buscar correctamente.
        if (e.getTipo().getCodigo().equals("4")
                || e.getTipo().getCodigo().equals("5")
                || e.getTipo().getCodigo().equals("6")
                || e.getTipo().getCodigo().equals("7")) {
            e.setNombreFantasia(e.getApellido() + " " + e.getNombre());
            e.setRazonSocial(e.getApellido() + " " + e.getNombre());
        }

        //quito los guiones de los números de documentos
        if (e.getNrodoc() != null) {
            e.setNrodoc(e.getNrodoc().replace("-", ""));
        }
        if (e.getNrodo1() != null) {
            e.setNrodo1(e.getNrodo1().replace("-", ""));
        }
        if (e.getNrodo2() != null) {
            e.setNrodo2(e.getNrodo2().replace("-", ""));
        }
        if (e.getNrodo3() != null) {
            e.setNrodo3(e.getNrodo3().replace("-", ""));
        }
        if (e.getCoordenadaLatitud() != null) {
            e.setCoordenadaLatitud(e.getCoordenadaLatitud().setScale(6));
        }
        if (e.getCoordenadaLongitud() != null) {
            e.setCoordenadaLongitud(e.getCoordenadaLongitud().setScale(6));
        }

    }

    public EntidadComercial nuevo(String codtip) throws Exception {

        TipoEntidad tipo = tipoEntidadRN.getTipoEntidad(codtip);

        ParametroEntidad p = parametroEntidadRN.getParametro(codtip);

        EntidadComercial entidad = new EntidadComercial(tipo);
        entidad.setNrocta(getProximoNroCuenta(tipo.getCodigo()));
        entidad.setNrosub(entidad.getNrocta());

        entidad.setEmpresa(p.getEmpresa());
        entidad.setSucursal(p.getSucursal());
        entidad.setCanalVenta(p.getCanalVenta());
        entidad.setUnidadNegocio(p.getUnidadNegocio());

        entidad.setEstado(p.getEstado());
        entidad.setComprador(p.getComprador());
        entidad.setCondicionPagoProveedor(p.getCondicionPagoProveedor());
        entidad.setListaCosto(p.getListaCosto());
        entidad.setVendedor(p.getVendedor());
        entidad.setRepartidor(p.getRepartidor());
        entidad.setCobrador(p.getCobrador());
        entidad.setCondicionDePagoVentas(p.getCondicionDePagoVentas());
        entidad.setListaDePrecioVenta(p.getListaDePrecioVenta());
        entidad.setZona(p.getZona());
        entidad.setCategoria(categoriaRN.getCategoria("01", tipo));
        entidad.setCondicionDeIva(p.getCondicionDeIva());
        entidad.setTipoDocumento(p.getTipoDocumento());
        entidad.setNrodoc(p.getNrodoc());
        entidad.setTipoDocumento1(p.getTipoDocumento1());
        entidad.setTipoDocumento2(p.getTipoDocumento2());
        entidad.setTipoDocumento3(p.getTipoDocumento3());
        entidad.setLimiteDeCreditoVenta(p.getLimiteDeCreditoVenta());
        entidad.setTipoIngresosBrutos(p.getTipoIngresosBrutos());
        entidad.setPideCodigoAutorizacion(p.getPideCodigoAutorizacion());

        return entidad;
    }

    public void control(EntidadComercial entidad, boolean esNuevo) throws ExcepcionGeneralSistema, Exception {

        if (entidad != null) {

            if (entidad.getTipo() == null) {
                throw new ExcepcionGeneralSistema("Tipo de entidad vacía");
            }

            if (entidad.getTipoDocumento() == null) {
                throw new ExcepcionGeneralSistema("Debe seleccionar el tipo de documento");
            }

            if (entidad.getNrodoc() == null) {
                throw new ExcepcionGeneralSistema("Debe ingresar el número de documento");
            }

            if (entidad.getLocalidad() == null) {
                throw new ExcepcionGeneralSistema("La localidad no puede estar vacía");
            }

            ParametroEntidad p = parametroEntidadRN.getParametro(entidad.getTipo().getCodigo());

            if (esNuevo) {

                if (p.getValidaDuplicidad().equals("S") && entidad.getNrodoc() != null && !entidad.getNrodoc().isEmpty()) {

                    List<EntidadComercial> aux = getListaByBusqueda(entidad.getNrodoc(), entidad.getTipo(), true);

                    if (!aux.isEmpty()) {

                        throw new ExcepcionGeneralSistema("No es posible guarda los datos, ya existe " + aux.get(0).getTipo().getDescripcion() + " " + aux.get(0).getNombreComplete() + " con el mismo número de documento");

                    }
                }

            }

            if (entidad.getHorarios() != null && !entidad.getHorarios().isEmpty()) {
                for (EntidadHorario horario : entidad.getHorarios()) {
                    if (horario.getAtiendeTurnoMañana().equals("S") && horario.getAtiendeTurnoTarde().equals("S")) {
                        if (horario.getHoraInicioMañana().after(horario.getHoraFinMañana())) {
                            throw new ExcepcionGeneralSistema("En el dia " + horario.getDiaSemana() + " la hora inicio de la mañana es mayor que la hora fin de la mañana");
                        }
                        if (horario.getHoraFinMañana().after(horario.getHoraInicioTarde())) {
                            throw new ExcepcionGeneralSistema("En el dia " + horario.getDiaSemana() + " la hora fin de la mañana es mayor que la hora inicio de la tarde");
                        }

                        if (horario.getHoraInicioTarde().after(horario.getHoraFinTarde())) {
                            throw new ExcepcionGeneralSistema("En el dia " + horario.getDiaSemana() + " la hora inicio de la tarde es mayor que la hora fin de la tarde");
                        }
                        continue;
                    }

                    if (horario.getAtiendeTurnoMañana().equals("S")) {
                        if (horario.getHoraInicioMañana().after(horario.getHoraFinMañana())) {
                            throw new ExcepcionGeneralSistema("En el dia " + horario.getDiaSemana() + " la hora inicio de la mañana es mayor que la hora fin de la mañana");
                        }

                    }

                    if (horario.getAtiendeTurnoTarde().equals("S")) {
                        if (horario.getHoraInicioTarde().after(horario.getHoraFinTarde())) {
                            throw new ExcepcionGeneralSistema("En el dia " + horario.getDiaSemana() + " la hora inicio de la tarde es mayor que la hora fin de la tarde");
                        }
                    }

                }
            }
        }
    }

    public void eliminar(EntidadComercial e) throws Exception {

        entidadDAO.eliminar(e);
    }

    public void eliminarContacto(EntidadComercial entidad, Contacto contacto) throws Exception {

        if (contacto == null) {
            throw new ExcepcionGeneralSistema("Contacto nulo, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceContacto = -1;

        for (Contacto itemContacto : entidad.getContactos()) {

            if (itemContacto.getId() != null && contacto.getId() != null) {

                if (itemContacto.getId() == contacto.getId()) {
                    indiceContacto = i;
                }
            }
//            else if (itemContacto.getNombre() != null && itemContacto.getNombre() != null) {
//
//                if (itemContacto.getNombre().equals(contacto.getNombre())) {
//                    indiceContacto = i;
//                }
//            }
            i++;
        }

        if (indiceContacto >= 0) {
            Contacto remove = entidad.getContactos().remove(indiceContacto);
            if (remove != null) {

                if (remove.getId() != null) {

                    contactoDAO.eliminar(Contacto.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    public EntidadComercial getEntidad(String nrocta) {

        return entidadDAO.getEntidad(nrocta);
    }

    public List<EntidadComercial> getEntidadByTipo(TipoEntidad tipo, boolean mostrarDebaja) {

        return entidadDAO.getEntidadByBusqueda(null, "", tipo, mostrarDebaja, 15);
    }

    public List<EntidadComercial> getListaByBusqueda(String txtBusqueda, TipoEntidad tipo, boolean mostrarDebaja) {

        return entidadDAO.getEntidadByBusqueda(null, txtBusqueda, tipo, mostrarDebaja, 15);
    }

    public List<EntidadComercial> getListaByBusqueda(String txtBusqueda, TipoEntidad tipo, boolean mostrarDebaja, int cantMax) {

        return entidadDAO.getEntidadByBusqueda(null, txtBusqueda, tipo, mostrarDebaja, cantMax);
    }

    public List<EntidadComercial> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, TipoEntidad tipo, boolean mostrarDebaja) {

        return entidadDAO.getEntidadByBusqueda(filtro, txtBusqueda, tipo, mostrarDebaja, 15);
    }

    public List<EntidadComercial> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, TipoEntidad tipo, boolean mostrarDebaja, int cantMax) {

        return entidadDAO.getEntidadByBusqueda(filtro, txtBusqueda, tipo, mostrarDebaja, cantMax);
    }

    public String getProximoNroCuenta(String codTipo) {

        int nroCta = entidadDAO.getMaxNroCuenta(codTipo);

        String nroCuenta = "00000000" + String.valueOf(nroCta);
        return codTipo + nroCuenta.substring(nroCuenta.length() - 5, nroCuenta.length());

    }

    public List<String> getNotasByBusqueda(String txtBusqueda) {

        return entidadDAO.getNotasByBusqueda(txtBusqueda, 100);
    }

    public void refresh(EntidadComercial entidad) {

        entidadDAO.refresh(entidad);
    }

    public int getCantidadByTipo(String tipo) {

        return entidadDAO.getCantidadByTipo(tipo);

    }

    public void nuevoItemDireccionEntrega(EntidadComercial entidad, boolean copioDatos) {

        if (entidad != null) {

            if (entidad.getDireccionesDeEntrega() == null) {
                entidad.setDireccionesDeEntrega(new ArrayList<DireccionEntregaEntidad>());
            }

            String newCodigo = direccionEntregaRN.generarCodigo(entidad);
            String newDescrp = "Dirección " + newCodigo;

            DireccionEntregaEntidad direccionEntrega = new DireccionEntregaEntidad(newCodigo, entidad.getNrocta());
            direccionEntrega.setDescripcion(newDescrp);
            direccionEntrega.setEntidad(entidad);

            if (copioDatos) {

                direccionEntrega.setDireccion(entidad.getDireccion());
                direccionEntrega.setNumero(entidad.getNumero());
                direccionEntrega.setDepartamentoPiso(entidad.getDepartamentoPiso());
                direccionEntrega.setDepartamentoNumero(entidad.getDepartamentoNumero());
                direccionEntrega.setLocalidad(entidad.getLocalidad());
                direccionEntrega.setProvincia(entidad.getProvincia());
            }

            entidad.getDireccionesDeEntrega().add(direccionEntrega);
        }
    }

    public void eliminarItemDireccionEntrega(DireccionEntregaEntidad direccionEntrega) throws ExcepcionGeneralSistema, Exception {

        if (direccionEntrega == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (direccionEntrega.getEntidad() == null) {
            throw new ExcepcionGeneralSistema("La dirección de entrega no tiene una entidad asociada");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (DireccionEntregaEntidad item : direccionEntrega.getEntidad().getDireccionesDeEntrega()) {

            if (item.getCodigo() != null) {

                if (item.getCodigo().equals(direccionEntrega.getCodigo())) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            DireccionEntregaEntidad remove = direccionEntrega.getEntidad().getDireccionesDeEntrega().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getCodigo() != null && remove.getNrocta() != null) {
                    DireccionEntregaEntidadPK idPK = new DireccionEntregaEntidadPK(remove.getCodigo(), remove.getNrocta());
                    entidadDAO.eliminar(DireccionEntregaEntidad.class, idPK);
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    public void asignarProvinciaDireccionEntrega(EntidadComercial entidad) {
        if (entidad.getDireccionesDeEntrega() != null) {

            for (DireccionEntregaEntidad item : entidad.getDireccionesDeEntrega()) {

                item.setProvincia(item.getLocalidad().getProvincia());

            }
        }
    }

    public void nuevoItemRetencion(EntidadComercial entidad) {

        if (entidad != null) {

            if (entidad.getRetenciones() == null) {
                entidad.setRetenciones(new ArrayList<RetencionPorEntidad>());
            }

            RetencionPorEntidad itemRetencion = new RetencionPorEntidad();
            itemRetencion.setNroitm(entidad.getRetenciones().size() + 1);
            itemRetencion.setEntidadComercial(entidad);

            entidad.getRetenciones().add(itemRetencion);
        }
    }

    public void eliminarItemRetencion(EntidadComercial entidad, RetencionPorEntidad itemRetencion) throws ExcepcionGeneralSistema, Exception {

        if (itemRetencion == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (RetencionPorEntidad item : entidad.getRetenciones()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemRetencion.getNroitm()
                        && item.getConceptoRetencion().equals(itemRetencion.getConceptoRetencion())) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            RetencionPorEntidad remove = entidad.getRetenciones().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    entidadDAO.eliminar(RetencionPorEntidad.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(entidad);
        entidad = guardar(entidad, false);

    }

    public void nuevoItemImpuesto(EntidadComercial entidad) {

        if (entidad != null) {

            if (entidad.getImpuestos() == null) {
                entidad.setImpuestos(new ArrayList<ImpuestoPorEntidad>());
            }

            ImpuestoPorEntidad itemImpuesto = new ImpuestoPorEntidad();
            itemImpuesto.setNroitm(entidad.getRetenciones().size() + 1);
            itemImpuesto.setEntidadComercial(entidad);

            entidad.getImpuestos().add(itemImpuesto);
        }
    }

    public void eliminarItemImpuesto(EntidadComercial entidad, ImpuestoPorEntidad itemImpuesto) throws ExcepcionGeneralSistema, Exception {

        if (itemImpuesto == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ImpuestoPorEntidad item : entidad.getImpuestos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemImpuesto.getNroitm()
                        && item.getConceptoVenta().equals(itemImpuesto.getConceptoVenta())) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ImpuestoPorEntidad remove = entidad.getImpuestos().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    entidadDAO.eliminar(ImpuestoPorEntidad.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(entidad);
        entidad = guardar(entidad, false);

    }

    public void nuevoItemActividad(EntidadComercial entidad) {

        if (entidad != null) {

            if (entidad.getActividades() == null) {
                entidad.setActividades(new ArrayList<EntidadActividadComercial>());
            }

            EntidadActividadComercial itemActividad = new EntidadActividadComercial();
            itemActividad.setNroitm(entidad.getActividades().size() + 1);
            itemActividad.setEntidadComercial(entidad);

            entidad.getActividades().add(itemActividad);
        }
    }

    public void eliminarItemActividad(EntidadComercial entidad, EntidadActividadComercial itemActividad) throws ExcepcionGeneralSistema, Exception {

        if (itemActividad == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (EntidadActividadComercial item : entidad.getActividades()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemActividad.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            EntidadActividadComercial remove = entidad.getActividades().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    entidadDAO.eliminar(EntidadActividadComercial.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(entidad);
        entidad = guardar(entidad, false);

    }

    public void nuevoItemTransporte(EntidadComercial entidad) {

        if (entidad != null) {

            if (entidad.getTransportes() == null) {
                entidad.setTransportes(new ArrayList<EntidadTransporte>());
            }

            EntidadTransporte itemTransporte = new EntidadTransporte();
            itemTransporte.setNroitm(entidad.getTransportes().size() + 1);
            itemTransporte.setEntidadComercial(entidad);

            entidad.getTransportes().add(itemTransporte);
        }
    }

    public void eliminarItemTransporte(EntidadComercial entidad, EntidadTransporte itemTransporte) throws ExcepcionGeneralSistema, Exception {

        if (itemTransporte == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (EntidadTransporte item : entidad.getTransportes()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemTransporte.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            EntidadTransporte remove = entidad.getTransportes().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    entidadDAO.eliminar(EntidadTransporte.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(entidad);
        entidad = guardar(entidad, false);

    }

    public void nuevoItemObraSocial(EntidadComercial entidad) {

        if (entidad != null) {

            if (entidad.getObraSociales() == null) {
                entidad.setObraSociales(new ArrayList<EntidadObraSocial>());
            }

            EntidadObraSocial itemObraSocial = new EntidadObraSocial();
            itemObraSocial.setNroitm(entidad.getObraSociales().size() + 1);
            itemObraSocial.setPaciente(entidad);

            entidad.getObraSociales().add(itemObraSocial);
        }
    }

    public void eliminarItemObraSocial(EntidadComercial entidad, EntidadObraSocial itemObraSocial) throws ExcepcionGeneralSistema, Exception {

        if (itemObraSocial == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (EntidadObraSocial item : entidad.getObraSociales()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemObraSocial.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            EntidadObraSocial remove = entidad.getObraSociales().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    entidadDAO.eliminar(EntidadObraSocial.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(entidad);
        entidad = guardar(entidad, false);

    }

    public void nuevoItemCamion(EntidadComercial entidad) {

        if (entidad != null) {

            if (entidad.getCamiones() == null) {
                entidad.setCamiones(new ArrayList<EntidadCamion>());
            }

            EntidadCamion itemCamion = new EntidadCamion();
            itemCamion.setNroitm(entidad.getCamiones().size() + 1);
            itemCamion.setEntidadComercial(entidad);

            entidad.getCamiones().add(itemCamion);
        }
    }

    public void eliminarItemCamion(EntidadComercial entidad, EntidadCamion itemCamion) throws ExcepcionGeneralSistema, Exception {

        if (itemCamion == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (EntidadCamion item : entidad.getCamiones()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemCamion.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            EntidadCamion remove = entidad.getCamiones().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    entidadDAO.eliminar(EntidadCamion.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(entidad);
        entidad = guardar(entidad, false);

    }

    public void nuevoItemChofer(EntidadComercial entidad) {

        if (entidad != null) {

            if (entidad.getChoferes() == null) {
                entidad.setChoferes(new ArrayList<EntidadChofer>());
            }

            EntidadChofer itemChofer = new EntidadChofer();
            itemChofer.setNroitm(entidad.getChoferes().size() + 1);
            itemChofer.setEntidadComercial(entidad);

            entidad.getChoferes().add(itemChofer);
        }
    }

    public void eliminarItemChofer(EntidadComercial entidad, EntidadChofer itemChofer) throws ExcepcionGeneralSistema, Exception {

        if (itemChofer == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (EntidadChofer item : entidad.getChoferes()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemChofer.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            EntidadChofer remove = entidad.getChoferes().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    entidadDAO.eliminar(EntidadChofer.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(entidad);
        entidad = guardar(entidad, false);

    }

    public void nuevoItemHorario(EntidadComercial entidad) {

        if (entidad != null) {

            if (entidad.getHorarios() == null) {
                entidad.setHorarios(new ArrayList<EntidadHorario>());
            }

            EntidadHorario itemHorario = new EntidadHorario();
            itemHorario.setNroitm(entidad.getHorarios().size() + 1);
            itemHorario.setProfesionalMedico(entidad);

            entidad.getHorarios().add(itemHorario);
        }
    }

    public void eliminarItemHorario(EntidadComercial entidad, EntidadHorario itemHorario) throws ExcepcionGeneralSistema, Exception {

        if (itemHorario == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (EntidadHorario item : entidad.getHorarios()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemHorario.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            EntidadHorario remove = entidad.getHorarios().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    entidadDAO.eliminar(EntidadHorario.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(entidad);
        entidad = guardar(entidad, false);

    }

    private void reorganizarNroItem(EntidadComercial entidad) {

        //Reorganizamos los números de items
        int i;

        if (entidad.getRetenciones() != null) {

            i = 1;
            for (RetencionPorEntidad item : entidad.getRetenciones()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (entidad.getImpuestos() != null) {

            i = 1;
            for (ImpuestoPorEntidad item : entidad.getImpuestos()) {
                item.setNroitm(i);
                i++;
            }
        }
        if (entidad.getActividades() != null) {

            i = 1;
            for (EntidadActividadComercial item : entidad.getActividades()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (entidad.getTransportes() != null) {

            i = 1;
            for (EntidadTransporte item : entidad.getTransportes()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (entidad.getObraSociales() != null) {

            i = 1;
            for (EntidadObraSocial item : entidad.getObraSociales()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (entidad.getCamiones() != null) {

            i = 1;
            for (EntidadCamion item : entidad.getCamiones()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (entidad.getChoferes() != null) {

            i = 1;
            for (EntidadChofer item : entidad.getChoferes()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (entidad.getHorarios() != null) {

            i = 1;
            for (EntidadHorario item : entidad.getHorarios()) {
                item.setNroitm(i);
                i++;
            }
        }

    }

    private void modificarNroCuenta(EntidadComercial entidad) {

        entidad.setNrocta(getProximoNroCuenta(entidad.getTipo().getCodigo()));
        entidad.setNrosub(entidad.getNrocta());

    }

    public EntidadChofer getChofer(Integer id) {
        return entidadDAO.getObjeto(EntidadChofer.class, id);
    }

    public EntidadCamion getCamion(Integer id) {

        return entidadDAO.getObjeto(EntidadCamion.class, id);

    }

}
