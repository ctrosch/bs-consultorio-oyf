/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.administracion.rn.ParametrosRN;
import bs.entidad.modelo.EntidadComercial;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Moneda;
import bs.global.rn.MonedaRN;
import bs.stock.dao.ProductoDAO;
import bs.stock.modelo.AplicacionProducto;
import bs.stock.modelo.AtributoDefecto;
import bs.stock.modelo.AtributoProducto;
import bs.stock.modelo.EquivalenciaProveedor;
import bs.stock.modelo.GrupoProducto;
import bs.stock.modelo.GrupoStock;
import bs.stock.modelo.ImagenProducto;
import bs.stock.modelo.ParametroStock;
import bs.stock.modelo.Producto;
import bs.stock.modelo.ProductoPresentacion;
import bs.stock.modelo.ProductoSugerido;
import bs.stock.modelo.ProductoSustituto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ProductoRN implements Serializable {

    @EJB
    private ProductoDAO productoDAO;
    @EJB
    private MonedaRN monedaRN;
    @EJB
    private ParametroStockRN parametroStockRN;
    @EJB
    protected ParametrosRN parametrosRN;

    Map<String, String> filtro = new HashMap<String, String>();

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Producto guardar(Producto p, boolean esNuevo) throws Exception {

        control(p);
        reorganizarNroItem(p);

        if (esNuevo) {
            if (productoDAO.getObjeto(Producto.class, p.getCodigo()) != null) {
                asignarCodigoProducto(p);
                //throw new ExcepcionGeneralSistema("Ya existe un producto con el código " + p.getCodigo());
            }
            productoDAO.crear(p);
        } else {
            p = (Producto) productoDAO.editar(p);
        }

//        p = getProducto(p.getCodigo());
        return p;
    }

    public Producto nuevo() throws Exception {

        ParametroStock p = parametroStockRN.getParametro();

        Producto producto = new Producto();

        //Cargamos valores por defecto
        Moneda monedaDefecto = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaPrimaria());

        if (p.getMonedaDeProduccion() == null) {
            producto.setMonedaDeProduccion(monedaDefecto);
        } else {
            producto.setMonedaDeProduccion(p.getMonedaDeProduccion());
        }

//        if (p.getMonedaDeReferencia()== null) {
//            producto.setMonedaReposicion(monedaDefecto);
//        } else {
//            producto.setMonedaReposicion(p.getMonedaDeReferencia());
//        }
        if (p.getMonedaReposicion() == null) {
            producto.setMonedaReposicion(monedaDefecto);
            producto.setMonedaUltimaCompra(monedaDefecto);
        } else {
            producto.setMonedaReposicion(p.getMonedaReposicion());
            producto.setMonedaUltimaCompra(p.getMonedaReposicion());
        }

        producto.setTipoProducto(p.getTipoProducto());
        producto.setRubr01(p.getRubr01());
        producto.setRubr02(p.getRubr02());
        producto.setRubr03(p.getRubr03());
        producto.setUnidadDeMedida(p.getUnidadDeMedida());
        producto.setUnidadDePeso(p.getUnidadDePeso());
        producto.setUnidadDeGarantia(p.getUnidadDeGarantia());
        producto.setGarantia(p.getGarantia());

        producto.setConceptoVenta(p.getConceptoVenta());
        producto.setCuentaContableVenta(p.getCuentaContableVenta());

        producto.setConceptoProveedor(p.getConceptoCompra());
        producto.setCuentaContableCompra(p.getCuentaContableCompra());

        producto.setBienDeUso(p.getBienDeUso());
        producto.setGestionaStock(p.getGestionaStock());
        producto.setAdministraAtributo1(p.getAdministraAtributo1());
        producto.setAdministraAtributo2(p.getAdministraAtributo2());
        producto.setAdministraAtributo3(p.getAdministraAtributo3());
        producto.setAdministraAtributo4(p.getAdministraAtributo4());
        producto.setAdministraAtributo5(p.getAdministraAtributo5());
        producto.setAdministraAtributo6(p.getAdministraAtributo6());
        producto.setAdministraAtributo7(p.getAdministraAtributo7());

        try {
            asignarCodigoProducto(producto);
        } catch (Exception exception) {
        }

        return producto;
    }

    public Producto duplicar(Producto p) throws Exception {

        if (p == null) {
            throw new ExcepcionGeneralSistema("Producto nulo, nada para duplicar");
        }

        Producto producto = new Producto();

        producto.setDescripcion(p.getDescripcion());
        producto.setDescripcionAlternativa(p.getDescripcionAlternativa());
        producto.setDetalle(p.getDetalle());

        producto.setNumeroParte(p.getNumeroParte());
        producto.setNumeroSerie(p.getNumeroSerie());
        producto.setCodigoReferencia(p.getCodigoReferencia());
        producto.setCodigoBarra(p.getCodigoBarra());
        producto.setPesoNeto(p.getPesoNeto());
        producto.setPesoMinimo(p.getPesoMinimo());
        producto.setPesosMaximo(p.getPesosMaximo());

        producto.setStockMinimo(p.getStockMinimo());
        producto.setStockMaximo(p.getStockMaximo());
        producto.setPuntoDePedido(p.getPuntoDePedido());
        producto.setDiasEntrega(p.getDiasEntrega());

        producto.setTipoProducto(p.getTipoProducto());
        producto.setRubr01(p.getRubr01());
        producto.setRubr02(p.getRubr02());
        producto.setRubr03(p.getRubr03());

        producto.setUnidadDeMedida(p.getUnidadDeMedida());
        producto.setUnidadDePeso(p.getUnidadDePeso());
        producto.setUnidadDeGarantia(p.getUnidadDeGarantia());

        producto.setGarantia(p.getGarantia());

        producto.setMonedaDeProduccion(p.getMonedaDeProduccion());
        producto.setFechaProduccion(p.getFechaProduccion());
        producto.setPrecioProduccion(p.getPrecioProduccion());

        producto.setMonedaReposicion(p.getMonedaReposicion());
        producto.setFechaReposicion(p.getFechaReposicion());
        producto.setPrecioReposicion(p.getPrecioReposicion());

        producto.setMonedaUltimaCompra(p.getMonedaUltimaCompra());
        producto.setFechaUltimaCompra(p.getFechaUltimaCompra());
        producto.setPrecioUltimaCompra(p.getPrecioUltimaCompra());

        producto.setUtilidad(producto.getUtilidad());

        producto.setProveedorHabitual(p.getProveedorHabitual());
        producto.setProductoNuevo(p.getProductoNuevo());
        producto.setImagenChica(p.getImagenChica());
        producto.setImagenGrande(p.getImagenGrande());

        producto.setEsKitVenta(p.getEsKitVenta());
        producto.setFormulaComposicionVenta(p.getFormulaComposicionVenta());

        producto.setImprimeCantidad(p.getImprimeCantidad());
        producto.setDisponibleParaPickingCompras(p.getDisponibleParaPickingCompras());
        producto.setDisponibleParaPickingFacturacion(p.getDisponibleParaPickingFacturacion());

        producto.setConceptoVenta(p.getConceptoVenta());
        producto.setCuentaContableVenta(p.getCuentaContableVenta());

        producto.setConceptoProveedor(p.getConceptoProveedor());
        producto.setCuentaContableCompra(p.getCuentaContableCompra());

        producto.setBienDeUso(p.getBienDeUso());
        producto.setGestionaStock(p.getGestionaStock());
        producto.setAdministraAtributo1(p.getAdministraAtributo1());
        producto.setAdministraAtributo2(p.getAdministraAtributo2());
        producto.setAdministraAtributo3(p.getAdministraAtributo3());
        producto.setAdministraAtributo4(p.getAdministraAtributo4());
        producto.setAdministraAtributo5(p.getAdministraAtributo5());
        producto.setAdministraAtributo6(p.getAdministraAtributo6());
        producto.setAdministraAtributo7(p.getAdministraAtributo7());

        if (p.getGrupos() != null) {

            for (GrupoProducto item : p.getGrupos()) {
                nuevoItemGrupo(producto, item.getGrupo());
            }
        }

        if (p.getSustitutos() != null) {

            for (ProductoSustituto item : p.getSustitutos()) {
                nuevoItemSustituto(producto, item.getProductoSustituto());
            }
        }

        if (p.getSugeridos() != null) {

            for (ProductoSugerido item : p.getSugeridos()) {
                nuevoItemSugerido(producto, item.getProductoSugerido());
            }
        }

        if (p.getEquivalenciaProveedor() != null) {

            for (EquivalenciaProveedor item : p.getEquivalenciaProveedor()) {
                nuevoItemEquivalenciaProveedor(producto, item.getProveedor(), item.getCodigo());
            }
        }

        if (p.getAplicaciones() != null) {

            for (AplicacionProducto item : p.getAplicaciones()) {
                nuevoItemAplicacion(producto);
            }
        }

        if (p.getAtributos() != null) {

            for (AtributoProducto item : p.getAtributos()) {
                nuevoItemAtributo(producto, item.getNombre(), item.getValor());
            }
        }

        asignarCodigoProducto(producto);

        return producto;
    }

    public void asignarCodigoProducto(Producto producto) throws Exception {

        ParametroStock p = parametroStockRN.getParametro();

        if (p.getCodigoManual().equals("S")) {
            return;
        }

        String sErrores = "";

        if (producto.getTipoProducto() == null && p.getUtilizaTipoProductoGeneracionCodigo().equals("S")) {
            sErrores += "- Seleccione el tipo para poder generar el código del producto \n";
            producto.setCodigo("");
            return;
        }

        if (producto.getRubr01() == null && p.getUtilizaRubro1GeneracionCodigo().equals("S")) {
            sErrores += " - Seleccione el rubro para poder generar el código del producto \n";
            producto.setCodigo("");
            return;
        }

        if (producto.getRubr02() == null && p.getUtilizaRubro2GeneracionCodigo().equals("S")) {
            sErrores += " -Seleccione el sub-rubro para poder generar el código del producto\n";
            producto.setCodigo("");
            return;
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

        String tipo = producto.getTipoProducto() != null ? producto.getTipoProducto().getCodigo() : "";
        String rubro1 = producto.getRubr01() != null ? producto.getRubr01().getCodigo() : "";
        String rubro2 = producto.getRubr02() != null ? producto.getRubr02().getCodigo() : "";

        String codigo = getProximoCodigo(tipo, rubro1, rubro2);

        producto.setCodigo(codigo);
    }

    public void control(Producto producto) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (producto.getCodigo() == null || producto.getCodigo().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (producto.getDescripcion() == null || producto.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (producto.getCuentaContableVenta() == null) {

            sErrores += "- Debe asignar una cuenta contable de venta \n";
        }

        if (producto.getCuentaContableCompra() == null) {

            sErrores += "- Debe asignar una cuenta contable de compra \n";
        }

        if (producto.getEsKitVenta().equals("S") && producto.getGestionaStock().equals("S")) {

            sErrores += "- El producto no puede gestionar stock y estar definido como kit de venta, modifique uno de los campos \n";
        }

        if (producto.getGrupos() != null) {

            for (GrupoProducto item : producto.getGrupos()) {

                if (item.getGrupo() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item sin grupo asignado \n";
                }
            }
        }

        if (producto.getSustitutos() != null) {

            for (ProductoSustituto item : producto.getSustitutos()) {

                if (item.getProducto() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item sustituto sin producto principal asignado\n";
                }

                if (item.getProductoSustituto() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item sustituto sin producto asignado\n";
                }

            }
        }

        if (producto.getSugeridos() != null) {

            for (ProductoSugerido item : producto.getSugeridos()) {

                if (item.getProducto() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item sugerido sin producto principal asignado\n";
                }

                if (item.getProductoSugerido() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item sugerido sin producto asignado\n";
                }
            }
        }

        if (producto.getEquivalenciaProveedor() != null) {

            for (EquivalenciaProveedor item : producto.getEquivalenciaProveedor()) {

                if (item.getProducto() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item equivalencia con el producto vacío\n";
                    throw new ExcepcionGeneralSistema("");
                }

                if (item.getProveedor() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item equivalencia con el proveedor vacío\n";
                }

                if (item.getCodigo() == null || item.getCodigo().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- Existe un item equivalencia con el código de equivalencia vacío\n";
                }
            }
        }

        if (producto.getAplicaciones() != null) {

            for (AplicacionProducto item : producto.getAplicaciones()) {

                if (item.getProducto() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item aplicación que tiene el producto vacío\n";
                }

            }
        }

        if (producto.getAtributos() != null) {

            for (AtributoProducto item : producto.getAtributos()) {

                if (item.getProducto() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item atributo que tiene el producto vacío\n";

                }

                if (item.getNombre() == null || item.getNombre().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- El nombre del atributo no puede estar vacío\n";

                }

                if (item.getValor() == null || item.getValor().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- El valor del atributo no puede estar vacío\n";
                }
            }
        }

        if (producto.getImagenes() != null) {

            for (ImagenProducto item : producto.getImagenes()) {

                if (item.getArchivo() == null || item.getArchivo().isEmpty()) {

                    sErrores += "- Hay un item imagen con el nombre del archivo vacío\n";
                }
            }
        }

        if (producto.getPresentaciones() != null) {

            for (ProductoPresentacion item : producto.getPresentaciones()) {

                if (item.getProducto() == null) {
                    item.setConError(true);
                    sErrores += "- Hay un item de presentación que tiene el producto vacío \n";
                }

                if (item.getTipoEnvase() == null || item.getTipoEnvase().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- Hay un item de presentación que tiene el tipo de envase vacío \n";
                }

                if (item.getUnidadMedida() == null || item.getUnidadMedida().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- Hay un item de presentación que tiene unidad de medida vacío\n";
                }

                if (item.getUnidadPeso() == null || item.getUnidadPeso().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- Hay un item de presentación que tiene unidad de peso vacío\n";
                }

            }
        }

        if (producto.getAtributosPorDefecto() != null) {

            for (AtributoDefecto item : producto.getAtributosPorDefecto()) {

                if (item.getProducto() == null) {
                    item.setConError(true);
                    sErrores += "- Hay un item de atributo por defecto que tiene el producto vacío \n";
                }

                if (item.getDeposito() == null) {
                    item.setConError(true);
                    sErrores += "- Hay un item de atributo por defecto que tiene el depósito vacío \n";
                }
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(Producto p) {
        productoDAO.eliminar(Producto.class, p.getCodigo());
    }

    public Producto getProducto(String codigo) {

        return productoDAO.getProducto(codigo);
    }

    public Producto getProductoByCodigoBarra(String codigoBarra) {

        return productoDAO.getProductoByCodigoBarra(codigoBarra);
    }

    public List<Producto> getListaByBusqueda(String codTipo, String txtBusqueda, boolean mostrarDebaja) {

        return productoDAO.getListaByBusqueda(codTipo, null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Producto> getListaByBusqueda(String codTipo, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return productoDAO.getListaByBusqueda(codTipo, null, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public List<Producto> getListaByBusqueda(String codTipo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return productoDAO.getListaByBusqueda(codTipo, filtro, txtBusqueda, mostrarDebaja, 50);
    }

    public List<Producto> getListaByBusqueda(String codTipo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return productoDAO.getListaByBusqueda(codTipo, filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public List<Producto> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return productoDAO.getListaByBusqueda(null, filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public String getProximoCodigo(String tippro, String rub01, String rub02) throws Exception {

        ParametroStock parametro = parametroStockRN.getParametro();

        if (parametro == null) {
            //Generación de código de producto por defecto.
            int nroProducto = productoDAO.getProximoCodigoProducto(4, tippro, rub01, rub02);
            String codigo = "0000" + String.valueOf(nroProducto);
            return tippro + rub01 + codigo.substring(codigo.length() - 4, codigo.length());
        } else {

            if (!parametro.getUtilizaTipoProductoGeneracionCodigo().equals("S")) {
                tippro = "";
            }

            if (!parametro.getUtilizaRubro1GeneracionCodigo().equals("S")) {
                rub01 = "";
            }

            if (!parametro.getUtilizaRubro2GeneracionCodigo().equals("S")) {
                rub02 = "";
            }

            int nroProducto = productoDAO.getProximoCodigoProducto(parametro.getCaracteresParaGeneracionCodigo(), tippro, rub01, rub02);
            String codigo = "00000000000" + String.valueOf(nroProducto);
            return tippro + rub01 + codigo.substring(codigo.length() - parametro.getCaracteresParaGeneracionCodigo(), codigo.length());

        }

    }

    public int getCantidadRegistros() {

        return productoDAO.getCantidadRegistros();
    }

    public void reorganizarNroItem(Producto producto) {

        //Reorganizamos los números de items
        int i;

        if (producto.getGrupos() != null) {

            i = 1;
            for (GrupoProducto item : producto.getGrupos()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (producto.getSustitutos() != null) {

            i = 1;
            for (ProductoSustituto item : producto.getSustitutos()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (producto.getSugeridos() != null) {

            i = 1;
            for (ProductoSugerido item : producto.getSugeridos()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (producto.getEquivalenciaProveedor() != null) {

            i = 1;
            for (EquivalenciaProveedor item : producto.getEquivalenciaProveedor()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (producto.getAplicaciones() != null) {

            i = 1;
            for (AplicacionProducto item : producto.getAplicaciones()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (producto.getAtributos() != null) {

            i = 1;
            for (AtributoProducto item : producto.getAtributos()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (producto.getImagenes() != null) {

            i = 1;
            for (ImagenProducto item : producto.getImagenes()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (producto.getPresentaciones() != null) {

            i = 1;
            for (ProductoPresentacion item : producto.getPresentaciones()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (producto.getAtributosPorDefecto() != null) {

            i = 1;
            for (AtributoDefecto item : producto.getAtributosPorDefecto()) {
                item.setNroitm(i);
                i++;
            }
        }

    }

    //----------------------------------------------------------------------------
    public void nuevoItemGrupo(Producto producto) throws ExcepcionGeneralSistema {

        nuevoItemGrupo(producto, null);
    }

    public void nuevoItemGrupo(Producto producto, GrupoStock grupo) throws ExcepcionGeneralSistema {

        if (producto == null) {
            throw new ExcepcionGeneralSistema("No existe un producto seleccionado para agregarle una grupo");
        }

        if (producto.getGrupos() == null) {
            producto.setGrupos(new ArrayList<GrupoProducto>());
        }

        GrupoProducto itemGrupo = new GrupoProducto();
        itemGrupo.setNroitm(producto.getGrupos().size() + 1);
        itemGrupo.setProducto(producto);

        if (grupo != null) {
            itemGrupo.setGrupo(grupo);
        }

        producto.getGrupos().add(itemGrupo);
        reorganizarNroItem(producto);

    }

    public void eliminarItemGrupo(Producto producto, GrupoProducto itemGrupo) throws Exception {

        if (itemGrupo == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (GrupoProducto item : producto.getGrupos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemGrupo.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            GrupoProducto remove = producto.getGrupos().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    productoDAO.eliminar(GrupoProducto.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(producto);
    }

    public void asignarGrupo(Producto producto, GrupoStock grupo) throws ExcepcionGeneralSistema {

        if (existeGrupo(producto, grupo)) {
            eliminarGrupo(producto, grupo);
            throw new ExcepcionGeneralSistema("La grupo seleccionada ya existe para este producto");
        }
    }

    public boolean existeGrupo(Producto producto, GrupoStock grupo) {

        int cont = 0;

        if (producto != null && producto.getGrupos() != null && grupo != null) {

            for (GrupoProducto item : producto.getGrupos()) {

                if (item.getGrupo() != null) {

                    if (item.getGrupo().equals(grupo)) {
                        cont++;
                    }
                }
            }
        }
        return (cont > 1);
    }

    public void eliminarGrupo(Producto producto, GrupoStock grupo) {

        if (producto == null || grupo == null) {
            return;
        }

        if (producto.getGrupos() != null) {

            for (GrupoProducto item : producto.getGrupos()) {

                if (item.getGrupo().equals(grupo)) {
                    item.setGrupo(null);
                    return;
                }
            }
        }
    }

    //----------------------------------------------------------------------------
    public void nuevoItemSustituto(Producto producto) throws ExcepcionGeneralSistema {

        nuevoItemSustituto(producto, null);
    }

    public void nuevoItemSustituto(Producto producto, Producto productoSustituto) throws ExcepcionGeneralSistema {

        if (producto == null) {
            throw new ExcepcionGeneralSistema("No existe un producto seleccionado para agregarle un sustituto");
        }

        if (producto.getSustitutos() == null) {
            producto.setSustitutos(new ArrayList<ProductoSustituto>());
        }

        ProductoSustituto itemSustituto = new ProductoSustituto();
        itemSustituto.setNroitm(producto.getSustitutos().size() + 1);
        itemSustituto.setProducto(producto);

        if (productoSustituto != null) {
            itemSustituto.setProductoSustituto(productoSustituto);
        }

        producto.getSustitutos().add(itemSustituto);
        reorganizarNroItem(producto);
    }

    public void eliminarItemSustituto(Producto producto, ProductoSustituto itemSustituto) throws ExcepcionGeneralSistema {

        if (itemSustituto == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ProductoSustituto item : producto.getSustitutos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemSustituto.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ProductoSustituto remove = producto.getSustitutos().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    productoDAO.eliminar(ProductoSustituto.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(producto);
    }

    public void asignarProductoSustituto(Producto producto, Producto productoSustituto) throws ExcepcionGeneralSistema {

        if (existeProductoSustituto(producto, productoSustituto)) {
            eliminarProductoSustituto(producto, productoSustituto);
            throw new ExcepcionGeneralSistema("El producto sustituto seleccionado ya existe para este producto");
        }
    }

    public void eliminarProductoSustituto(Producto producto, Producto productoSustituto) {

        if (producto == null || productoSustituto == null) {
            return;
        }

        if (producto.getSustitutos() != null) {

            for (ProductoSustituto item : producto.getSustitutos()) {

                if (item.getProductoSustituto().equals(productoSustituto)) {
                    item.setProductoSustituto(null);
                    return;
                }
            }
        }
    }

    public boolean existeProductoSustituto(Producto producto, Producto productoSustituto) {

        int cont = 0;

        if (producto != null && producto.getSustitutos() != null && productoSustituto != null) {

            for (ProductoSustituto item : producto.getSustitutos()) {

                if (item.getProductoSustituto() != null) {

                    if (item.getProductoSustituto().equals(productoSustituto)) {
                        cont++;
                    }
                }
            }
        }
        return (cont > 1);
    }

    //----------------------------------------------------------------------------
    public void nuevoItemSugerido(Producto producto) throws ExcepcionGeneralSistema {

        nuevoItemSugerido(producto, null);

    }

    public void nuevoItemSugerido(Producto producto, Producto productoSugerido) throws ExcepcionGeneralSistema {

        if (producto == null) {
            throw new ExcepcionGeneralSistema("No existe un producto seleccionado para agregarle un sustituto");
        }

        if (producto.getSugeridos() == null) {
            producto.setSugeridos(new ArrayList<ProductoSugerido>());
        }

        ProductoSugerido itemSugerido = new ProductoSugerido();
        itemSugerido.setNroitm(producto.getSugeridos().size() + 1);
        itemSugerido.setProducto(producto);

        if (productoSugerido != null) {
            itemSugerido.setProducto(productoSugerido);
        }

        producto.getSugeridos().add(itemSugerido);
        reorganizarNroItem(producto);
    }

    public void eliminarItemSugerido(Producto producto, ProductoSugerido itemSugerido) throws ExcepcionGeneralSistema {

        if (itemSugerido == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ProductoSugerido item : producto.getSugeridos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemSugerido.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ProductoSugerido remove = producto.getSugeridos().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    productoDAO.eliminar(ProductoSugerido.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(producto);
    }

    public void asignarProductoSugerido(Producto producto, Producto productoSugerido) throws ExcepcionGeneralSistema {

        if (existeProductoSugerido(producto, productoSugerido)) {
            eliminarProductoSugerido(producto, productoSugerido);
            throw new ExcepcionGeneralSistema("El producto sugerido seleccionado ya existe para este producto");
        }
    }

    public void eliminarProductoSugerido(Producto producto, Producto productoSugerido) {

        if (producto == null || productoSugerido == null) {
            return;
        }

        if (producto.getSugeridos() != null) {

            for (ProductoSugerido item : producto.getSugeridos()) {

                if (item.getProductoSugerido().equals(productoSugerido)) {
                    item.setProductoSugerido(null);
                    return;
                }
            }
        }
    }

    public boolean existeProductoSugerido(Producto producto, Producto productoSugerido) {

        int cont = 0;

        if (producto != null && producto.getSugeridos() != null && productoSugerido != null) {

            for (ProductoSugerido item : producto.getSugeridos()) {

                if (item.getProductoSugerido() != null) {

                    if (item.getProductoSugerido().equals(productoSugerido)) {
                        cont++;
                    }
                }
            }
        }
        return (cont > 1);
    }

    //----------------------------------------------------------------------------
    public void nuevoItemEquivalenciaProveedor(Producto producto) throws ExcepcionGeneralSistema {

        nuevoItemEquivalenciaProveedor(producto, null, null);

    }

    public void nuevoItemEquivalenciaProveedor(Producto producto, EntidadComercial proveedor, String codigo) throws ExcepcionGeneralSistema {

        if (producto == null) {
            throw new ExcepcionGeneralSistema("No existe un producto seleccionado para agregarle una equivalencia");
        }

        if (producto.getEquivalenciaProveedor() == null) {
            producto.setEquivalenciaProveedor(new ArrayList<>());
        }

        EquivalenciaProveedor itemEquivalenciaProveedor = new EquivalenciaProveedor();
        itemEquivalenciaProveedor.setNroitm(producto.getEquivalenciaProveedor().size() + 1);
        itemEquivalenciaProveedor.setProducto(producto);

        if (proveedor != null) {
            itemEquivalenciaProveedor.setProveedor(proveedor);
        }

        if (codigo != null && !codigo.isEmpty()) {
            itemEquivalenciaProveedor.setCodigo(codigo);
        }

        producto.getEquivalenciaProveedor().add(itemEquivalenciaProveedor);
        reorganizarNroItem(producto);
    }

    public void eliminarItemEquivalenciaProveedor(Producto producto, EquivalenciaProveedor itemEquivalenciaProveedor) throws ExcepcionGeneralSistema {

        if (itemEquivalenciaProveedor == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (EquivalenciaProveedor item : producto.getEquivalenciaProveedor()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemEquivalenciaProveedor.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            EquivalenciaProveedor remove = producto.getEquivalenciaProveedor().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    productoDAO.eliminar(EquivalenciaProveedor.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(producto);
    }

    public void asignarEntidadEquivalenciaProveedor(Producto producto, EntidadComercial proveedor) throws ExcepcionGeneralSistema {

        if (existeEntidadEquivalenciaProveedor(producto, proveedor)) {
            eliminarEntidadEquivalenciaProveedor(producto, proveedor);
            throw new ExcepcionGeneralSistema("El proveedor seleccionado ya existe para este producto");
        }
    }

    public void eliminarEntidadEquivalenciaProveedor(Producto producto, EntidadComercial proveedor) {

        if (producto == null || proveedor == null) {
            return;
        }

        if (producto.getEquivalenciaProveedor() != null) {

            for (EquivalenciaProveedor item : producto.getEquivalenciaProveedor()) {

                if (item.getProveedor().equals(proveedor)) {
                    item.setProveedor(null);
                    return;
                }
            }
        }
    }

    public boolean existeEntidadEquivalenciaProveedor(Producto producto, EntidadComercial proveedor) {

        int cont = 0;

        if (producto != null && producto.getEquivalenciaProveedor() != null && proveedor != null) {

            for (EquivalenciaProveedor item : producto.getEquivalenciaProveedor()) {

                if (item.getProveedor() != null) {

                    if (item.getProveedor().equals(proveedor)) {
                        cont++;
                    }
                }
            }
        }
        return (cont > 1);
    }

    //----------------------------------------------------------------------------
    public void nuevoItemAtributo(Producto producto, String nombre) throws ExcepcionGeneralSistema {
        nuevoItemAtributo(producto, nombre, null);
    }

    public void nuevoItemAtributo(Producto producto, String nombre, String valor) throws ExcepcionGeneralSistema {

        if (producto == null) {
            throw new ExcepcionGeneralSistema("No existe un producto seleccionado para agregarle un atributo");
        }

        if (producto.getAtributos() == null) {
            producto.setAtributos(new ArrayList<AtributoProducto>());
        }

        AtributoProducto itemAtributo = new AtributoProducto();
        itemAtributo.setNroitm(producto.getAtributos().size() + 1);
        itemAtributo.setNombre(nombre != null ? nombre : "");
        itemAtributo.setProducto(producto);

        itemAtributo.setValor(valor != null ? valor : "");

        producto.getAtributos().add(itemAtributo);
        reorganizarNroItem(producto);
    }

    public void eliminarItemAtributo(Producto producto, AtributoProducto itemAtributo) throws ExcepcionGeneralSistema {

        if (itemAtributo == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (AtributoProducto item : producto.getAtributos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemAtributo.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            AtributoProducto remove = producto.getAtributos().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    productoDAO.eliminar(AtributoProducto.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(producto);
    }

    //----------------------------------------------------------------------------
    public void nuevoItemAplicacion(Producto producto) throws ExcepcionGeneralSistema {

        nuevoItemAplicacion(producto, null, null, null, null, null, null, null, null, null, null);
    }

    public void nuevoItemAplicacion(Producto producto, String campo1, String campo2, String campo3, String campo4, String campo5, String campo6, String campo7, String campo8, String campo9, String campo10) throws ExcepcionGeneralSistema {

        if (producto == null) {
            throw new ExcepcionGeneralSistema("No existe un producto seleccionado para agregarle una aplicación");
        }

        if (producto.getAplicaciones() == null) {
            producto.setAplicaciones(new ArrayList<AplicacionProducto>());
        }

        AplicacionProducto itemAplicacion = new AplicacionProducto();
        itemAplicacion.setNroitm(producto.getAplicaciones().size() + 1);
        itemAplicacion.setProducto(producto);

        itemAplicacion.setCampo1(campo1 != null ? campo1 : "");
        itemAplicacion.setCampo2(campo2 != null ? campo2 : "");
        itemAplicacion.setCampo3(campo3 != null ? campo3 : "");
        itemAplicacion.setCampo4(campo4 != null ? campo4 : "");
        itemAplicacion.setCampo5(campo5 != null ? campo5 : "");
        itemAplicacion.setCampo6(campo6 != null ? campo6 : "");
        itemAplicacion.setCampo7(campo7 != null ? campo7 : "");
        itemAplicacion.setCampo8(campo8 != null ? campo8 : "");
        itemAplicacion.setCampo9(campo9 != null ? campo9 : "");
        itemAplicacion.setCampo10(campo10 != null ? campo10 : "");

        producto.getAplicaciones().add(itemAplicacion);
        reorganizarNroItem(producto);
    }

    public void eliminarItemAplicacion(Producto producto, AplicacionProducto itemAplicacion) throws ExcepcionGeneralSistema {

        if (itemAplicacion == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (AplicacionProducto item : producto.getAplicaciones()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemAplicacion.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            AplicacionProducto remove = producto.getAplicaciones().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    productoDAO.eliminar(AplicacionProducto.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(producto);
    }

    //----------------------------------------------------------------------------
    public void nuevoItemImagen(Producto producto) throws ExcepcionGeneralSistema {

        if (producto == null) {
            throw new ExcepcionGeneralSistema("No existe un producto seleccionado para agregarle una imagen");
        }

        if (producto.getImagenes() == null) {
            producto.setImagenes(new ArrayList<ImagenProducto>());
        }

        ImagenProducto itemImagen = new ImagenProducto();
        itemImagen.setNroitm(producto.getImagenes().size() + 1);
        itemImagen.setUrlBase("");
        itemImagen.setProducto(producto);
        itemImagen.setNombre("Imagen " + itemImagen.getNroitm());

        producto.getImagenes().add(itemImagen);
        reorganizarNroItem(producto);

    }

    public void eliminarItemImagen(Producto producto, ImagenProducto itemImagen) throws ExcepcionGeneralSistema {

        if (itemImagen == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ImagenProducto item : producto.getImagenes()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemImagen.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ImagenProducto remove = producto.getImagenes().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    productoDAO.eliminar(ImagenProducto.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(producto);
    }

    public void setDeBajaAll(String codTip) {

        productoDAO.setDeBajaAll(codTip);
    }

    //----------------------------------------------------------------------------
    public void nuevoItemPresentacion(Producto producto) throws ExcepcionGeneralSistema {
        if (producto == null) {
            throw new ExcepcionGeneralSistema("No existe un producto seleccionado para agregarle una presentación");
        }

        if (producto.getPresentaciones() == null) {
            producto.setPresentaciones(new ArrayList<ProductoPresentacion>());
        }

        ProductoPresentacion itemPresentacion = new ProductoPresentacion();
        itemPresentacion.setNroitm(producto.getPresentaciones().size() + 1);
        itemPresentacion.setProducto(producto);

        producto.getPresentaciones().add(itemPresentacion);

        reorganizarNroItem(producto);

    }

    public void eliminarItemPresentacion(Producto producto, ProductoPresentacion itemPresentacion) throws ExcepcionGeneralSistema, Exception {
        if (producto == null) {
            throw new ExcepcionGeneralSistema("Producto vacío, nada para eliminar");
        }
        if (itemPresentacion == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ProductoPresentacion item : producto.getPresentaciones()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemPresentacion.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ProductoPresentacion remove = producto.getPresentaciones().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    productoDAO.eliminar(ProductoPresentacion.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
        reorganizarNroItem(producto);
    }

    //----------------------------------------------------------------------------
    public void nuevoItemAtributoPorDefecto(Producto producto) throws ExcepcionGeneralSistema {

        nuevoItemAtributoPorDefecto(producto, null, null, null, null, null, null, null);
    }

    public void nuevoItemAtributoPorDefecto(Producto producto, String atributo1, String atributo2, String atributo3, String atributo4, String atributo5, String atributo6, String atributo7) throws ExcepcionGeneralSistema {

        if (producto == null) {
            throw new ExcepcionGeneralSistema("No existe un producto seleccionado para agregarle un atributo por defecto");
        }

        if (producto.getAtributosPorDefecto() == null) {
            producto.setAtributosPorDefecto(new ArrayList<AtributoDefecto>());
        }

        AtributoDefecto itemAtributoPorDefecto = new AtributoDefecto();
        itemAtributoPorDefecto.setNroitm(producto.getAplicaciones().size() + 1);
        itemAtributoPorDefecto.setProducto(producto);

        itemAtributoPorDefecto.setAtributo1(atributo1 != null ? atributo1 : "");
        itemAtributoPorDefecto.setAtributo2(atributo2 != null ? atributo2 : "");
        itemAtributoPorDefecto.setAtributo3(atributo3 != null ? atributo3 : "");
        itemAtributoPorDefecto.setAtributo4(atributo4 != null ? atributo4 : "");
        itemAtributoPorDefecto.setAtributo5(atributo5 != null ? atributo5 : "");
        itemAtributoPorDefecto.setAtributo6(atributo6 != null ? atributo6 : "");
        itemAtributoPorDefecto.setAtributo7(atributo7 != null ? atributo7 : "");

        producto.getAtributosPorDefecto().add(itemAtributoPorDefecto);
        reorganizarNroItem(producto);
    }

    public void eliminarItemAtributoPorDefecto(Producto producto, AtributoDefecto itemAtributoPorDefecto) throws ExcepcionGeneralSistema {

        if (itemAtributoPorDefecto == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (AtributoDefecto item : producto.getAtributosPorDefecto()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemAtributoPorDefecto.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            AtributoDefecto remove = producto.getAtributosPorDefecto().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    productoDAO.eliminar(AtributoDefecto.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(producto);
    }

}
