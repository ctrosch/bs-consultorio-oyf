/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.impuesto.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.ArchivoDescargable;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.global.util.StringUtil;
import bs.proveedores.modelo.ItemImpuestoProveedor;
import bs.proveedores.modelo.ItemPercepcionProveedor;
import bs.proveedores.modelo.ItemProductoProveedor;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.proveedores.rn.MovimientoProveedorRN;
import bs.ventas.modelo.ItemImpuestoVenta;
import bs.ventas.modelo.ItemProductoVenta;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.MovimientoVentaRN;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CitiVentaBean extends InformeBase implements Serializable {

    @EJB
    private MovimientoVentaRN ventaRN;
    @EJB
    private MovimientoProveedorRN compraRN;

    List<MovimientoVenta> movimientosVenta;
    List<MovimientoProveedor> movimientosCompra;

    private List<ArchivoDescargable> archivos;
    private Map<String, String> anios;
    private Map<String, String> meses;

    private Map<String, String> filtroVenta;
    private Map<String, String> filtroCompra;

    public CitiVentaBean() {

        fechaMovimientoDesde = new Date();
        fechaMovimientoHasta = new Date();

        archivos = new ArrayList<ArchivoDescargable>();
        filtroVenta = new HashMap<String, String>();
        filtroCompra = new HashMap<String, String>();

        meses = JsfUtil.getMeses();
        anios = JsfUtil.getAnios();
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {

        if (fechaMovimientoDesde == null) {
            throw new ExcepcionGeneralSistema("Ingrese la fecha desde válida");
        }

        if (fechaMovimientoHasta == null) {
            throw new ExcepcionGeneralSistema("Ingrese la fecha hasta válida");
        }

        if (fechaMovimientoDesde.after(fechaMovimientoHasta)) {
            throw new ExcepcionGeneralSistema("La fecha de movimiento desde es mayor a fecha de movimiento hasta");
        }
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {

        filtroVenta.clear();
        filtroVenta.put("movimientoReversion", " IS NULL");
        filtroVenta.put("comprobante.comprobanteInterno =", "'N'");
        filtroVenta.put("comprobante.seIncluyeEnCiti = ", "'S'");

        if (fechaMovimientoDesde != null) {
            filtroVenta.put("fechaMovimiento >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {
            filtroVenta.put("fechaMovimiento <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }

        //-----------------------------------------
        filtroCompra.clear();
        filtroCompra.put("movimientoReversion", " IS NULL");
        filtroCompra.put("movimientoReversion", " IS NULL");
        filtroCompra.put("comprobante.comprobanteInterno =", "'N'");
        filtroCompra.put("comprobante.seIncluyeEnCiti = ", "'S'");

        if (fechaMovimientoDesde != null) {
            filtroCompra.put("fechaMovimiento >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {
            filtroCompra.put("fechaMovimiento <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }
    }

    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {

        fechaMovimientoDesde = new Date();
        fechaMovimientoHasta = new Date();
    }

    public void generarArchivos() {
        try {
            archivos.clear();

            validarDatos();
            cargarParametros();

            movimientosVenta = ventaRN.getListaByBusqueda(filtroVenta, -1);
            movimientosCompra = compraRN.getListaByBusqueda(filtroCompra, -1);

            generarTXTComprobanteVenta();
            generarTXTIvaVenta();
            generarTXTComprobanteCompra();
            generarTXTIvaCompra();
            generarTXTIvaImportacion();

            JsfUtil.addInfoMessage("Archivos generados correctamente");
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("generarArchivos: " + ex);
        }
    }

    public void generarTXTComprobanteVenta() {

        BufferedWriter bw = null;

        try {

            StringUtil sUtil = new StringUtil();
            Calendar cal1 = new GregorianCalendar();
            cal1.setTime(fechaMovimientoDesde);

            //Crear un objeto File se encarga de crear o abrir acceso a un archivo que se especifica en su constructor
            File archivo = new File(aplicacionBean.getParametro().getPathCarpetaTemporales() + "VT_CITI_COMPROBANTE_" + cal1.get(Calendar.YEAR) + "_" + (cal1.get(Calendar.MONTH) + 1) + ".txt");

            bw = new BufferedWriter(new FileWriter(archivo));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            String linea = "";

            for (MovimientoVenta m : movimientosVenta) {

                if (!m.getComprobante().getSeIncluyeEnCiti().equals("S")) {
                    continue;
                }

//                if(m.getItemTotal()==null || m.getItemTotal().isEmpty()){
//                   continue;
//                }
                int cantidadAlicuotaIva = 1;
                BigDecimal importeTotal = BigDecimal.ZERO;
                BigDecimal importeExento = BigDecimal.ZERO;

                if (m.getItemTotal() != null && !m.getItemTotal().isEmpty()) {
                    importeTotal = m.getItemTotal().get(0).getImporte();
                }

                if (m.getItemProducto() != null) {
                    for (ItemProductoVenta ip : m.getItemProducto()) {

                        if (ip.getConcepto().getCodigo().equals("V000")) {
                            importeExento = importeExento.add(ip.getImporte());
                        }
                    }
                }

                if (m.getItemImpuesto() != null) {

                    cantidadAlicuotaIva = m.getItemImpuesto().size();

                    if (importeExento.compareTo(BigDecimal.ZERO) > 0) {
                        cantidadAlicuotaIva++;
                    }
                }

                importeTotal = importeTotal.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
                importeExento = importeExento.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);

                String codigoOperacion = " ";

                if (importeExento.compareTo(BigDecimal.ZERO) > 0) {
                    codigoOperacion = "E";
                }

                linea = sdf.format(m.getFechaMovimiento())
                        + sUtil.right("000" + (m.getFormulario().getCodigoDGI() == null ? "" : m.getFormulario().getCodigoDGI()), 3)
                        + sUtil.right("00000" + m.getPuntoVenta().getCodigo(), 5)
                        + sUtil.right("00000000000000000000" + m.getNumeroFormulario(), 20)
                        + sUtil.right("00000000000000000000" + m.getNumeroFormulario(), 20)
                        + sUtil.right("00" + m.getTipoDocumento().getCodigo(), 2)
                        + sUtil.right("00000000000000000000" + m.getNrodoc(), 20)
                        + sUtil.left(m.getRazonSocial() + "                              ", 30)
                        + sUtil.right("000000000000000" + importeTotal.toString(), 15) //Importe total de la operación
                        + sUtil.right("000000000000000", 15) //Importe no int gravado
                        + sUtil.right("000000000000000", 15) //Percepción a no cat
                        + sUtil.right("000000000000000" + importeExento.toString(), 15)//Importe op exentas
                        + sUtil.right("000000000000000", 15) //Importe percepciones
                        + sUtil.right("000000000000000", 15) //Imp. Percepciones IB
                        + sUtil.right("000000000000000", 15) //Imp. impuestos munic
                        + sUtil.right("000000000000000", 15) //Imp. impuestos internos
                        + "PES"
                        + "0001000000"
                        + cantidadAlicuotaIva
                        + codigoOperacion
                        + sUtil.right("000000000000000", 15) //Otros tributos
                        + sdf.format(m.getFechaVencimiento())
                        + "\r\n"
                        + linea;

//                System.out.print(linea);
                //Escribimos en el archivo con el metodo write
            }

            bw.write(linea);

            //Cerramos la conexion
            bw.close();

            InputStream stream = new FileInputStream(archivo);
            StreamedContent file = new DefaultStreamedContent(stream, "text/plain", archivo.getName());
            ArchivoDescargable a = new ArchivoDescargable("Comprobantes de Venta", null, file);
            archivos.add(a);

        } catch (IOException e) {

            log.log(Level.SEVERE, "Error generando archivos", e.getStackTrace());
            JsfUtil.addErrorMessage("Error generando archivos:" + e);
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void generarTXTIvaVenta() {

        BufferedWriter bw = null;

        try {

            StringUtil sUtil = new StringUtil();
            Calendar cal1 = new GregorianCalendar();
            cal1.setTime(fechaMovimientoDesde);

            //Crear un objeto File se encarga de crear o abrir acceso a un archivo que se especifica en su constructor
            File archivo = new File(aplicacionBean.getParametro().getPathCarpetaTemporales() + "VT_CITI_IVA_" + cal1.get(Calendar.YEAR) + "_" + (cal1.get(Calendar.MONTH) + 1) + ".txt");

            bw = new BufferedWriter(new FileWriter(archivo));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            String linea = "";

            for (MovimientoVenta m : movimientosVenta) {

                if (!m.getComprobante().getSeIncluyeEnCiti().equals("S")) {
                    continue;
                }

                BigDecimal importeGravado = BigDecimal.ZERO;
                BigDecimal importeExento = BigDecimal.ZERO;
                BigDecimal importeLiquidado = BigDecimal.ZERO;
                String alicuotaIVA = "0003";

                if (m.getItemProducto() != null) {
                    for (ItemProductoVenta ip : m.getItemProducto()) {
                        if (ip.getConcepto().getCodigo().equals("V000")) {
                            importeExento = importeExento.add(ip.getImporte());
                        }
                    }
                }

                importeExento = importeExento.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);

                if (importeExento.compareTo(BigDecimal.ZERO) > 0) {

                    linea = sUtil.right("000" + (m.getFormulario().getCodigoDGI() == null ? "" : m.getFormulario().getCodigoDGI()), 3)
                            + sUtil.right("00000" + m.getPuntoVenta().getCodigo(), 5)
                            + sUtil.right("00000000000000000000" + m.getNumeroFormulario(), 20)
                            + sUtil.right("000000000000000", 15) //Importe Neto gravado
                            + sUtil.right("0003", 4) //Alicuota IVA
                            + sUtil.right("000000000000000", 15) //Importe liquidado
                            + "\r\n" + linea;
                }

                if (m.getItemImpuesto() != null) {

                    for (ItemImpuestoVenta i : m.getItemImpuesto()) {

                        if (i.getConcepto().getCodigo().equals("IVA001")) {
                            importeGravado = i.getImporte().divide(new BigDecimal("0.21"), 2, BigDecimal.ROUND_HALF_UP);
                            alicuotaIVA = "0005";
                        }

                        if (i.getConcepto().getCodigo().equals("IVA002")) {
                            importeGravado = i.getImporte().divide(new BigDecimal("0.105"), 2, BigDecimal.ROUND_HALF_UP);
                            alicuotaIVA = "0004";
                        }

                        importeGravado = importeGravado.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
                        importeLiquidado = i.getImporte().multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);

                        linea = sUtil.right("000" + (m.getFormulario().getCodigoDGI() == null ? "" : m.getFormulario().getCodigoDGI()), 3)
                                + sUtil.right("00000" + m.getPuntoVenta().getCodigo(), 5)
                                + sUtil.right("00000000000000000000" + m.getNumeroFormulario(), 20)
                                + sUtil.right("000000000000000" + importeGravado.toString(), 15) //Importe Neto gravado
                                + alicuotaIVA //Alicuota IVA
                                + sUtil.right("000000000000000" + importeLiquidado.toString(), 15) //Importe liquidado
                                + "\r\n" + linea;
                    }
                }
            }

            //Escribimos en el archivo con el metodo write
            bw.write(linea);

            bw.close();
            InputStream stream = new FileInputStream(archivo);
            StreamedContent file = new DefaultStreamedContent(stream, "text/plain", archivo.getName());
            ArchivoDescargable a = new ArchivoDescargable("Alícuotas de Venta", null, file);
            archivos.add(a);

        } catch (IOException e) {

            log.log(Level.SEVERE, "Error generando archivos", e.getStackTrace());
            JsfUtil.addErrorMessage("Error generando archivos:" + e);
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void generarTXTComprobanteCompra() {

        BufferedWriter bw = null;

        try {

            StringUtil sUtil = new StringUtil();
            Calendar cal1 = new GregorianCalendar();
            cal1.setTime(fechaMovimientoDesde);

            //Crear un objeto File se encarga de crear o abrir acceso a un archivo que se especifica en su constructor
            File archivo = new File(aplicacionBean.getParametro().getPathCarpetaTemporales() + "CO_CITI_COMPROBANTE_" + cal1.get(Calendar.YEAR) + "_" + (cal1.get(Calendar.MONTH) + 1) + ".txt");

            bw = new BufferedWriter(new FileWriter(archivo));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            String linea = "";

            for (MovimientoProveedor m : movimientosCompra) {

                if (!m.getComprobante().getSeIncluyeEnCiti().equals("S")) {
                    continue;
                }

                int cantidadAlicuotaIva = 1;
                BigDecimal importeTotal = BigDecimal.ZERO;
                BigDecimal importeExento = BigDecimal.ZERO;
                BigDecimal importePercepcionIVA = BigDecimal.ZERO;
                BigDecimal importePercepcionIB = BigDecimal.ZERO;
                BigDecimal importeOtraPercepcion = BigDecimal.ZERO;
                BigDecimal importeIVA = BigDecimal.ZERO;
                BigDecimal importeImpuestoInterno = BigDecimal.ZERO;

                String nroDespachoImportacion = "";

                if (m.getItemTotal() != null && !m.getItemTotal().isEmpty()) {
                    importeTotal = m.getItemTotal().get(0).getImporte();
                }

                if (m.getItemProducto() != null) {
                    for (ItemProductoProveedor ip : m.getItemProducto()) {

                        if (ip.getConcepto().getCodigo().equals("C000")) {
                            importeExento = importeExento.add(ip.getImporte());
                        }
                    }
                }

                if (m.getItemPercepcion() != null) {
                    for (ItemPercepcionProveedor iper : m.getItemPercepcion()) {

                        if (iper.getConcepto().getCodigo().equals("IBR001")) {
                            importePercepcionIB = importePercepcionIB.add(iper.getImporte());
                        }

                        if (iper.getConcepto().getCodigo().equals("IVA001")) {
                            importePercepcionIVA = importePercepcionIVA.add(iper.getImporte());
                        }

                        if (iper.getConcepto().getCodigo().equals("GAN001")) {
                            importeOtraPercepcion = importeOtraPercepcion.add(iper.getImporte());
                        }

                        if (iper.getConcepto().getCodigo().equals("INT001")) {
                            importeImpuestoInterno = importeImpuestoInterno.add(iper.getImporte());
                        }

                        if (iper.getConcepto().getCodigo().equals("ITC")) { //Combustibles
                            importeImpuestoInterno = importeImpuestoInterno.add(iper.getImporte());
                        }
                    }
                }

                if (m.getItemImpuesto() != null) {

                    cantidadAlicuotaIva = m.getItemImpuesto().size();

                    if (importeExento.compareTo(BigDecimal.ZERO) > 0) {
                        cantidadAlicuotaIva++;
                    }
                }

                ///////////////Para el caso de despacho de importación//////
//                if (m.getComprobante().getCodigo().equals("Despacho de importacion")) {
//                    nroDespachoImportacion = m.getNumeroOriginal();
//                }
                ////////////Credito fiscal Computable , es el IVA/////////
                if (m.getItemImpuesto() != null) {

                    for (ItemImpuestoProveedor i : m.getItemImpuesto()) {
                        if (i.getConcepto().getCodigo().equals("IVA001")) {
                            importeIVA = importeIVA.add(i.getImporte());
                        }
                        if (i.getConcepto().getCodigo().equals("IVA002")) {
                            importeIVA = importeIVA.add(i.getImporte());
                        }
                        if (i.getConcepto().getCodigo().equals("IVA003")) {
                            importeIVA = importeIVA.add(i.getImporte());
                        }
                        if (i.getConcepto().getCodigo().equals("IVA004")) {
                            importeIVA = importeIVA.add(i.getImporte());
                        }

                    }
                }

                importeTotal = importeTotal.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
                importeExento = importeExento.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
                importePercepcionIVA = importePercepcionIVA.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
                importePercepcionIB = importePercepcionIB.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
                importeOtraPercepcion = importeOtraPercepcion.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
                importeIVA = importeIVA.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
                importeImpuestoInterno = importeImpuestoInterno.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);

                String codigoOperacion = " ";

                if (importeExento.compareTo(BigDecimal.ZERO) > 0) {
                    codigoOperacion = "E";
                }

                if (!nroDespachoImportacion.isEmpty()) {
                    codigoOperacion = "X";
                }

                linea = sdf.format(m.getFechaMovimiento())
                        + sUtil.right("000" + (m.getFormulario().getCodigoDGI() == null ? "" : m.getFormulario().getCodigoDGI()), 3)
                        + sUtil.right("00000" + m.getPuntoVentaOriginal(), 5)
                        + sUtil.right("00000000000000000000" + m.getNumeroOriginal(), 20)
                        + sUtil.right("00000000000000000000" + nroDespachoImportacion, 16)
                        + sUtil.right("00" + m.getTipoDocumento().getCodigo(), 2)
                        + sUtil.right("00000000000000000000" + m.getNrodoc(), 20)
                        + sUtil.left(m.getRazonSocial() + "                              ", 30)
                        + sUtil.right("000000000000000" + importeTotal.toString(), 15) //Importe total de la operación
                        + sUtil.right("000000000000000", 15) ////Importe no int gravado o sea lo No Gravado
                        + sUtil.right("000000000000000" + importeExento.toString(), 15)//Importe op exentas
                        + sUtil.right("000000000000000" + importePercepcionIVA.toString(), 15) //Importe percepciones IVA
                        + sUtil.right("000000000000000" + importeOtraPercepcion.toString(), 15) //Percepción de otros impuestos nacionales
                        + sUtil.right("000000000000000" + importePercepcionIB.toString(), 15) //Imp. Percepciones IB
                        + sUtil.right("000000000000000", 15) //Imp. impuestos munic
                        + sUtil.right("000000000000000" + importeImpuestoInterno.toString(), 15) //Imp. impuestos internos (Por lo general en facturas de combustibles)
                        + "PES"
                        + "0001000000"
                        + cantidadAlicuotaIva
                        + codigoOperacion
                        + sUtil.right("000000000000000" + importeIVA.toString(), 15) //Crédito Fiscal Computable
                        + sUtil.right("000000000000000", 15) //Otros tributos
                        + sUtil.right("00000000000", 11) //CUIT emisor/corredor -> para cartas de porte -  Empresas agro
                        + sUtil.left("                              ", 30) //Denominación del emisor/corredor -> para cartas de porte -  Empresas agro
                        + sUtil.right("000000000000000", 15) //IVA Comisión
                        + "\r\n"
                        + linea;

            }

            bw.write(linea);

            //Cerramos la conexion
            bw.close();

            InputStream stream = new FileInputStream(archivo);
            StreamedContent file = new DefaultStreamedContent(stream, "text/plain", archivo.getName());
            ArchivoDescargable a = new ArchivoDescargable("Comprobantes de Compras", null, file);
            archivos.add(a);

        } catch (IOException e) {

            log.log(Level.SEVERE, "Error generando archivos", e.getStackTrace());
            JsfUtil.addErrorMessage("Error generando archivos:" + e);
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void generarTXTIvaCompra() {

        BufferedWriter bw = null;

        try {

            StringUtil sUtil = new StringUtil();
            Calendar cal1 = new GregorianCalendar();
            cal1.setTime(fechaMovimientoDesde);

            //Crear un objeto File se encarga de crear o abrir acceso a un archivo que se especifica en su constructor
            File archivo = new File(aplicacionBean.getParametro().getPathCarpetaTemporales() + "CO_CITI_IVA_" + cal1.get(Calendar.YEAR) + "_" + (cal1.get(Calendar.MONTH) + 1) + ".txt");

            bw = new BufferedWriter(new FileWriter(archivo));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            String linea = "";

            for (MovimientoProveedor m : movimientosCompra) {

                if (!m.getComprobante().getSeIncluyeEnCiti().equals("S")) {
                    continue;
                }

                BigDecimal importeGravado = BigDecimal.ZERO;
                BigDecimal importeExento = BigDecimal.ZERO;
                BigDecimal importeLiquidado = BigDecimal.ZERO;
                String alicuotaIVA = "0003";

                if (m.getItemProducto() != null) {
                    for (ItemProductoProveedor ip : m.getItemProducto()) {
                        if (ip.getConcepto().getCodigo().equals("C000")) {
                            importeExento = importeExento.add(ip.getImporte());
                        }
                    }
                }

                importeExento = importeExento.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);

                if (importeExento.compareTo(BigDecimal.ZERO) > 0) {

                    linea = sUtil.right("000" + (m.getFormulario().getCodigoDGI() == null ? "" : m.getFormulario().getCodigoDGI()), 3)
                            + sUtil.right("00000" + m.getPuntoVentaOriginal(), 5)
                            + sUtil.right("00000000000000000000" + m.getNumeroOriginal(), 20)
                            + sUtil.right("00" + m.getTipoDocumento().getCodigo(), 2)
                            + sUtil.right("00000000000000000000" + m.getNrodoc(), 20)
                            + sUtil.right("000000000000000", 15) //Importe Neto gravado
                            + sUtil.right("0003", 4) //Alicuota IVA
                            + sUtil.right("000000000000000", 15) //Importe liquidado
                            + "\r\n" + linea;
                }

                if (m.getItemImpuesto() != null) {

                    for (ItemImpuestoProveedor i : m.getItemImpuesto()) {

                        if (i.getConcepto().getCodigo().equals("IVA001")) {
                            importeGravado = i.getImporte().divide(new BigDecimal("0.21"), 2, BigDecimal.ROUND_HALF_UP);
                            alicuotaIVA = "0005";
                        }

                        if (i.getConcepto().getCodigo().equals("IVA002")) {
                            importeGravado = i.getImporte().divide(new BigDecimal("0.105"), 2, BigDecimal.ROUND_HALF_UP);
                            alicuotaIVA = "0004";
                        }

                        if (i.getConcepto().getCodigo().equals("IVA003")) {
                            importeGravado = i.getImporte().divide(new BigDecimal("0.27"), 2, BigDecimal.ROUND_HALF_UP);
                            alicuotaIVA = "0006";
                        }

                        if (i.getConcepto().getCodigo().equals("IVA004")) {
                            importeGravado = i.getImporte().divide(new BigDecimal("0.025"), 2, BigDecimal.ROUND_HALF_UP);
                            alicuotaIVA = "0009";
                        }

                        importeGravado = importeGravado.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
                        importeLiquidado = i.getImporte().multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);

                        linea = sUtil.right("000" + (m.getFormulario().getCodigoDGI() == null ? "" : m.getFormulario().getCodigoDGI()), 3)
                                + sUtil.right("00000" + m.getPuntoVentaOriginal(), 5)
                                + sUtil.right("00000000000000000000" + m.getNumeroOriginal(), 20)
                                + sUtil.right("00" + m.getTipoDocumento().getCodigo(), 2)
                                + sUtil.right("00000000000000000000" + m.getNrodoc(), 20)
                                + sUtil.right("000000000000000" + importeGravado.toString(), 15) //Importe Neto gravado
                                + alicuotaIVA //Alicuota IVA
                                + sUtil.right("000000000000000" + importeLiquidado.toString(), 15) //Importe liquidado
                                + "\r\n" + linea;
                    }
                }
            }

            //Escribimos en el archivo con el metodo write
            bw.write(linea);

            bw.close();
            InputStream stream = new FileInputStream(archivo);
            StreamedContent file = new DefaultStreamedContent(stream, "text/plain", archivo.getName());
            ArchivoDescargable a = new ArchivoDescargable("Alícuotas de Compras", null, file);
            archivos.add(a);

        } catch (IOException e) {

            log.log(Level.SEVERE, "Error generando archivos", e.getStackTrace());
            JsfUtil.addErrorMessage("Error generando archivos:" + e);
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void generarTXTIvaImportacion() {

        BufferedWriter bw = null;

        try {

            StringUtil sUtil = new StringUtil();
            Calendar cal1 = new GregorianCalendar();
            cal1.setTime(fechaMovimientoDesde);

            //Crear un objeto File se encarga de crear o abrir acceso a un archivo que se especifica en su constructor
            File archivo = new File(aplicacionBean.getParametro().getPathCarpetaTemporales() + "REGINFO_CV_COMPRAS_IMPORTACIONES_" + cal1.get(Calendar.YEAR) + "_" + (cal1.get(Calendar.MONTH) + 1) + ".txt");

            bw = new BufferedWriter(new FileWriter(archivo));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            String linea = "";

            for (MovimientoProveedor m : movimientosCompra) {

                if (!m.getComprobante().getSeIncluyeEnCiti().equals("S") || !m.getComprobante().getCodigo().equals("Despacho de importacion")) {
                    continue;
                }

                BigDecimal importeGravado = BigDecimal.ZERO;
                BigDecimal importeExento = BigDecimal.ZERO;
                BigDecimal importeLiquidado = BigDecimal.ZERO;
                String nroDespachoImportacion = m.getNumeroOriginal();
                String alicuotaIVA = "0003";

                if (m.getItemProducto() != null) {
                    for (ItemProductoProveedor ip : m.getItemProducto()) {
                        if (ip.getConcepto().getCodigo().equals("C000")) {
                            importeExento = importeExento.add(ip.getImporte());
                        }
                    }
                }

                importeExento = importeExento.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);

                if (importeExento.compareTo(BigDecimal.ZERO) > 0) {

                    linea = sUtil.right("0000000000000000" + nroDespachoImportacion, 16)
                            + sUtil.right("000000000000000", 15) //Importe Neto gravado
                            + sUtil.right("0003", 4) //Alicuota IVA
                            + sUtil.right("000000000000000", 15) //Importe liquidado
                            + "\r\n" + linea;
                }

                if (m.getItemImpuesto() != null) {

                    for (ItemImpuestoProveedor i : m.getItemImpuesto()) {

                        if (i.getConcepto().getCodigo().equals("IVA001")) {
                            importeGravado = i.getImporte().divide(new BigDecimal("0.21"), 2, BigDecimal.ROUND_HALF_UP);
                            alicuotaIVA = "0005";
                        }

                        if (i.getConcepto().getCodigo().equals("IVA002")) {
                            importeGravado = i.getImporte().divide(new BigDecimal("0.105"), 2, BigDecimal.ROUND_HALF_UP);
                            alicuotaIVA = "0004";
                        }

                        if (i.getConcepto().getCodigo().equals("IVA003")) {
                            importeGravado = i.getImporte().divide(new BigDecimal("0.27"), 2, BigDecimal.ROUND_HALF_UP);
                            alicuotaIVA = "0006";
                        }

                        if (i.getConcepto().getCodigo().equals("IVA004")) {
                            importeGravado = i.getImporte().divide(new BigDecimal("0.025"), 2, BigDecimal.ROUND_HALF_UP);
                            alicuotaIVA = "0009";
                        }

                        importeGravado = importeGravado.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);
                        importeLiquidado = i.getImporte().multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP);

                        linea = sUtil.right("0000000000000000" + nroDespachoImportacion, 16)
                                + sUtil.right("000000000000000" + importeGravado.toString(), 15) //Importe Neto gravado
                                + alicuotaIVA //Alicuota IVA
                                + sUtil.right("000000000000000" + importeLiquidado.toString(), 15) //Importe liquidado
                                + "\r\n" + linea;
                    }
                }
            }

            //Escribimos en el archivo con el metodo write
            bw.write(linea);

            bw.close();
            InputStream stream = new FileInputStream(archivo);
            StreamedContent file = new DefaultStreamedContent(stream, "text/plain", archivo.getName());
            ArchivoDescargable a = new ArchivoDescargable("Alícuotas de Compras Importaciones", null, file);
            archivos.add(a);

        } catch (IOException e) {

            log.log(Level.SEVERE, "Error generando archivos", e.getStackTrace());
            JsfUtil.addErrorMessage("Error generando archivos:" + e);
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public List<MovimientoVenta> getMovimientosVenta() {
        return movimientosVenta;
    }

    public void setMovimientosVenta(List<MovimientoVenta> movimientosVenta) {
        this.movimientosVenta = movimientosVenta;
    }

    public List<MovimientoProveedor> getMovimientosCompra() {
        return movimientosCompra;
    }

    public void setMovimientosCompra(List<MovimientoProveedor> movimientosCompra) {
        this.movimientosCompra = movimientosCompra;
    }

    public MovimientoVentaRN getVentaRN() {
        return ventaRN;
    }

    public void setVentaRN(MovimientoVentaRN ventaRN) {
        this.ventaRN = ventaRN;
    }

    public List<ArchivoDescargable> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<ArchivoDescargable> archivos) {
        this.archivos = archivos;
    }

    public Map<String, String> getFiltroVenta() {
        return filtroVenta;
    }

    public void setFiltroVenta(Map<String, String> filtroVenta) {
        this.filtroVenta = filtroVenta;
    }

    public Map<String, String> getFiltroCompra() {
        return filtroCompra;
    }

    public void setFiltroCompra(Map<String, String> filtroCompra) {
        this.filtroCompra = filtroCompra;
    }

    public Map<String, String> getAnios() {
        return anios;
    }

    public void setAnios(Map<String, String> anios) {
        this.anios = anios;
    }

    public Map<String, String> getMeses() {
        return meses;
    }

    public void setMeses(Map<String, String> meses) {
        this.meses = meses;
    }

}
