/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.wsafip.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.ventas.modelo.ItemImpuestoVenta;
import bs.ventas.modelo.ItemPercepcionVenta;
import bs.ventas.modelo.ItemProductoVenta;
import bs.ventas.modelo.MovimientoVenta;
import bs.wsafip.modelo.ParametroWS;
import bs.wsafip.modelo.WebServices;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Claudio
 */
public class WSFEv1 {

//    public static final String WSDL_HOMO = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx?WSDL";
//    public static final String WSDL_PRODUCCION = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx?WSDL";
    private static final Logger LOG = Logger.getLogger(WSFEv1.class.getName());
    private WSAA wsaa;
    ActiveXComponent wsfev1;
//    private DecimalFormat df;

    ParametroWS parametro = null;
    WebServices wsWSFE = null;
    String excepcion = "";

    public WSFEv1(ParametroWS p, WebServices wsWSFE) {

        this.parametro = p;
        this.wsWSFE = wsWSFE;
        this.excepcion = "";

        DecimalFormatSymbols symbolos = new DecimalFormatSymbols();
        symbolos.setDecimalSeparator('.');
        symbolos.setGroupingSeparator(',');

    }

    public void conectar() throws ExcepcionGeneralSistema {

        if (parametro == null) {
            throw new ExcepcionGeneralSistema("WSFEv1 - No se pude conectar - Parametros nulos");
        }

        if (wsWSFE == null) {
            throw new ExcepcionGeneralSistema("WSFEv1 - No se puede conectar - WebService nulo");
        }

        if (wsaa == null) {

            wsaa = new WSAA();
            wsaa.obtenerTicketAcceso(parametro);

            /* Instanciar WSFEv1: WebService de Factura Electrónica version 1 */
            wsfev1 = new ActiveXComponent("WSFEv1");

            /* Establecer parametros de uso: */
            Dispatch.put(wsfev1, "Cuit", new Variant(parametro.getCuitWS()));
            Dispatch.put(wsfev1, "Token", new Variant(wsaa.getToken()));
            Dispatch.put(wsfev1, "Sign", new Variant(wsaa.getSign()));

            /* Conectar al websrvice (cambiar URL para producción) */
            Dispatch.call(wsfev1, "Conectar", new Variant(""),
                    new Variant((parametro.getModoPrueba().equals("S") ? wsWSFE.getWsdlPrueba() : wsWSFE.getWsdlProduccion())));

//            System.out.println("Conexión a WSAA exitosa!");
        }

        excepcion = Dispatch.get(wsfev1, "Excepcion").toString();

        if (!excepcion.isEmpty()) {
            generarXML();
            LOG.log(Level.SEVERE, "Excepción:" + excepcion, excepcion);
            throw new ExcepcionGeneralSistema("WSFEv1 - Conectar: " + excepcion);
        }

    }

    public int tomarProximoNumero(MovimientoVenta m) throws ExcepcionGeneralSistema {

        conectar();

        /* Consultar último comprobante autorizado en AFIP */
        String tipo_cbte = m.getFormulario().getCodigoDGI();
        String pto_vta = m.getPuntoVenta().getCodigo();

        Variant ult = Dispatch.call(wsfev1, "CompUltimoAutorizado",
                new Variant(tipo_cbte),
                new Variant(pto_vta));

        excepcion = Dispatch.get(wsfev1, "Excepcion").toString();

        if (!excepcion.isEmpty()) {
            generarXML();
            System.out.println("Excepcion: " + excepcion);
            throw new ExcepcionGeneralSistema("WSFEv1: " + excepcion);
        }

        Integer proxNum = 1 + Integer.parseInt(ult.toString());

        return proxNum;

    }

    public void autorizar(MovimientoVenta m) throws ExcepcionGeneralSistema {

        conectar();

        m.setInstanciaCAE("B");

        /* CAE */
        String tipo_cbte = m.getFormulario().getCodigoDGI();
        String pto_vta = m.getPuntoVenta().getCodigo();
        String fecha_cbte = new SimpleDateFormat("yyyyMMdd").format(m.getFechaMovimiento());
        String fecha_serv_desde = fecha_cbte;
        String fecha_serv_hasta = fecha_cbte;

        String fecha_venc_pago = new SimpleDateFormat("yyyyMMdd").format(m.getFechaVencimiento());
        String concepto = m.getTipoExportacion();

        String tipo_doc = m.getTipoDocumento().getCodigo();
        String nro_doc = m.getNrodoc();

        if (m.getCondicionDeIva().getCodigo().equals("C")
                && m.getItemTotal().get(0).getImporte().compareTo(new BigDecimal(1000)) <= 0) {
            tipo_doc = "99";
            nro_doc = "0";
        }

//        System.err.println("tipo_cbte "+tipo_cbte +"tipo_doc "+tipo_doc+"nro_doc "+nro_doc);
        int cbt_desde = m.getNumeroFormulario();
        int cbt_hasta = m.getNumeroFormulario();

        String moneda_id = "PES";
        String moneda_ctz = "1.000";

        BigDecimal total_concepto = BigDecimal.ZERO;
        BigDecimal total_iva = BigDecimal.ZERO;
        BigDecimal total_trib = BigDecimal.ZERO;
        BigDecimal total_exento = BigDecimal.ZERO;

        for (ItemProductoVenta ipv : m.getItemProducto()) {

            if (ipv.getConcepto().getCodigo().equals("V000")) {
                total_exento = total_exento.add(ipv.getImporte()).setScale(2, RoundingMode.HALF_UP);
            } else {
                total_concepto = total_concepto.add(ipv.getImporte()).setScale(2, RoundingMode.HALF_UP);
            }
        }

        for (ItemImpuestoVenta iiv : m.getItemImpuesto()) {
            total_iva = total_iva.add(iiv.getImporte()).setScale(2, RoundingMode.HALF_UP);
        }

        for (ItemPercepcionVenta ipv : m.getItemPercepcion()) {
            total_trib = total_trib.add(ipv.getImporte()).setScale(2, RoundingMode.HALF_UP);
        }

        String imp_tot_conc = "0.00";
        String imp_neto = total_concepto.toString();
        String imp_iva = total_iva.toString();
        String imp_trib = total_trib.toString();
        String imp_op_ex;

        //Comprobantes tipo C
        if (m.getFormulario().getCodigoDGI().equals("11")
                || m.getFormulario().getCodigoDGI().equals("12")
                || m.getFormulario().getCodigoDGI().equals("13")
                || m.getFormulario().getCodigoDGI().equals("211")
                || m.getFormulario().getCodigoDGI().equals("212")
                || m.getFormulario().getCodigoDGI().equals("213")) {
            imp_op_ex = "0.00";
            imp_neto = total_exento.toString();
        } else {
            imp_op_ex = total_exento.toString();
        }

        //------------------------------------------------------------------
        String imp_total = m.getItemTotal().get(0).getImporte().setScale(2, RoundingMode.HALF_UP).toString();

        Variant ok = Dispatch.call(wsfev1, "CrearFactura",
                new Variant(concepto),
                new Variant(tipo_doc),
                new Variant(nro_doc),
                new Variant(tipo_cbte),
                new Variant(pto_vta),
                new Variant(cbt_desde),
                new Variant(cbt_hasta),
                new Variant(imp_total.replace(',', '.')),
                new Variant(imp_tot_conc.replace(',', '.')),
                new Variant(imp_neto.replace(',', '.')),
                new Variant(imp_iva.replace(',', '.')),
                new Variant(imp_trib.replace(',', '.')),
                new Variant(imp_op_ex.replace(',', '.')),
                new Variant(fecha_cbte),
                new Variant(fecha_venc_pago),
                new Variant(fecha_serv_desde),
                new Variant(fecha_serv_hasta),
                new Variant(moneda_id),
                new Variant(moneda_ctz));

        agregoComprobantesAsociados(m);
        agregoDatosIVA(m);
        agregoDatosPercepcion(m);
        agregarOpcionales(m); //Para factura de crédito


        /* Habilito reprocesamiento automático (predeterminado): */
        Dispatch.put(wsfev1, "Reprocesar", new Variant(true));

        /* Solicito CAE (llamando al webservice de AFIP): */
        Variant cae = null;
        try {
            cae = Dispatch.call(wsfev1, "CAESolicitar");
        } catch (Exception e) {
            generarXML();
            LOG.log(Level.SEVERE, "Error al solicitar CAE " + e.getMessage());
        }

        /* Mostrar mensajes XML enviados y recibidos (depuración) */
//        System.out.println("XmlRequest: "  +Dispatch.get(wsfev1, "XmlRequest").toString());
//        System.out.println("XmlResponse: " +Dispatch.get(wsfev1, "XmlResponse").toString());
        excepcion = Dispatch.get(wsfev1, "Excepcion").toString();

        if (!excepcion.isEmpty()) {
            generarXML();
            LOG.log(Level.SEVERE, "CAESolicitar Excepción:" + excepcion, excepcion);
            throw new ExcepcionGeneralSistema("WSFEv1: " + excepcion);
        }

        String errmsg = Dispatch.get(wsfev1, "ErrMsg").toString();

        if (!errmsg.isEmpty()) {
            generarXML();
            LOG.log(Level.SEVERE, "CAESolicitar Error: " + errmsg, errmsg);
            throw new ExcepcionGeneralSistema("WSFEv1: " + errmsg);
        }

        String obs = Dispatch.get(wsfev1, "Obs").toString();

        /* datos devueltos */
        String resultado = Dispatch.get(wsfev1, "Resultado").toString();
        String fechaVencimiento = Dispatch.get(wsfev1, "Vencimiento").toString();

//        System.out.println("Resultado: " + resultado);
//        System.out.println("Vencimeinto CAE: " + fechaVencimiento);
//        System.out.println("CAE: " + cae.toString());
        if (resultado.equals("R")) {
            generarXML();
            LOG.log(Level.SEVERE, "Comprobante rechazado: " + errmsg + " Obs: " + obs, errmsg + "-" + obs);
            throw new ExcepcionGeneralSistema("Observaciones: " + obs + " Errores: " + errmsg);
        }
        /**
         * Si no hubiere inconvenientes, la llamada debe devolver el CAE y se
         * establece el atributo WSFEv1.Resultado = "A" (Aceptado) y
         * WSFEv1.Vencimiento del CAE (fch_venc_cae). Sino, devuelve cae = "":
         * La interfase no pudo procesar la respuesta del WebService, o bien la
         * conexión a internet esta caída, las direcciones de los servidores son
         * incorrectas, o el servicio web rechazo los datos de la factura a
         * generar. Se establece el atributo WSFEv1.Resultado = "R" (Rechazado)
         * y WSFEv1.Obs (o WSFEv1.ErrMsg) con los diversos motivos de rechazo
         * proporcionados por el webservice (ver WSFE.Motivo).
         */
        m.setInstanciaCAE("C");
        m.setNumeroCAE(cae.toString());

        try {
            m.setVencimientoCAE(new SimpleDateFormat("yyyyMMdd").parse(fechaVencimiento));
        } catch (ParseException ex) {

            LOG.log(Level.SEVERE, null, ex);
        }

        m.getFormulario().setUltimoNumero(m.getNumeroFormulario());

//        System.err.println("formulario:" + m.getFormulario().getUltimoNumero());
    }

    public int consultarUltimoComprobante(String tipo_cbte, String pto_vta) throws ExcepcionGeneralSistema {

        conectar();

        Variant ult = Dispatch.call(wsfev1, "CompUltimoAutorizado",
                new Variant(tipo_cbte),
                new Variant(pto_vta));

        excepcion = Dispatch.get(wsfev1, "Excepcion").toString();

        if (!excepcion.isEmpty()) {
            generarXML();
            System.out.println("Excepcion: " + excepcion);
            throw new ExcepcionGeneralSistema("WSFEv1: " + excepcion);
        }

        Integer proxNum = Integer.parseInt(ult.toString());

        return proxNum;

    }

    public String consultarComprobante(String tipo_cbte, String pto_vta, int nro_cbte) throws ExcepcionGeneralSistema {

        String datos = "";

        conectar();

        Variant comprobante = Dispatch.call(wsfev1, "CompConsultar",
                new Variant(tipo_cbte),
                new Variant(pto_vta),
                new Variant(nro_cbte));

        String excepcion = Dispatch.get(wsfev1, "Excepcion").toString();

        if (!excepcion.isEmpty()) {
            LOG.log(Level.SEVERE, "Excepción:" + excepcion, excepcion);
            throw new ExcepcionGeneralSistema("WSFEv1: " + excepcion);
        }

        String errmsg = Dispatch.get(wsfev1, "ErrMsg").toString();

        if (!errmsg.isEmpty()) {
            LOG.log(Level.SEVERE, "CAESolicitar Error: " + errmsg, errmsg);
            throw new ExcepcionGeneralSistema("WSFEv1: " + errmsg);
        }

        //obtener datos del encabezado (a partir de actualización 1.17a)
        String tipo_doc = Dispatch.call(wsfev1, "ObtenerCampoFactura", new Variant("tipo_doc")).toString();
        String nro_doc = Dispatch.call(wsfev1, "ObtenerCampoFactura", new Variant("nro_doc")).toString();
        String cae = Dispatch.call(wsfev1, "ObtenerCampoFactura", new Variant("cae")).toString();
        String imp_total = Dispatch.call(wsfev1, "ObtenerCampoFactura", new Variant("imp_total")).toString();
        String imp_neto = Dispatch.call(wsfev1, "ObtenerCampoFactura", new Variant("imp_neto")).toString();
        String imp_iva = Dispatch.call(wsfev1, "ObtenerCampoFactura", new Variant("imp_iva")).toString();
        String imp_trib = Dispatch.call(wsfev1, "ObtenerCampoFactura", new Variant("imp_trib")).toString();
        String imp_op_ex = Dispatch.call(wsfev1, "ObtenerCampoFactura", new Variant("imp_op_ex")).toString();

        //obtener primer alicuota de IVA
//        imp_iva1 = wsfev1.ObtenerCampoFactura("iva", 0, "importe")
//        //obtener primer tributo
//        imp_trib1 = wsfev1.ObtenerCampoFactura("tributos", 0, "importe")
//        //obtener primer opcional
//        valor_opcional1 = wsfev1.ObtenerCampoFactura("opcionales", 0, "valor")
//        //obtener primer código de observacion de AFIP
//        obs_code1 = wsfev1.ObtenerCampoFactura("obs", 0, "code")
//        //pruebo obtener el segundo mensaje de observacion inexistente
//        obs_code2 = wsfev1.ObtenerCampoFactura("obs", 1, "msg")
        datos += "Tipo doc: " + tipo_doc + "\n"
                + "CUIT:     " + nro_doc + "\n"
                + "----------------------------------------------------\n"
                + "CAE:      " + cae + "\n"
                + "----------------------------------------------------\n"
                + "Neto:     " + imp_neto + "\n"
                + "Iva:      " + imp_iva + "\n"
                + "Tributos  " + imp_trib + "\n"
                + "Exento    " + imp_op_ex + "\n"
                + "----------------------------------------------------\n"
                + "Total    " + imp_total + "\n";

        return datos;
    }

    public void recuperarCAE(MovimientoVenta m) throws ExcepcionGeneralSistema {

        conectar();

        /* Consultar último comprobante autorizado en AFIP */
        String tipo_cbte = m.getFormulario().getCodigoDGI();
        String pto_vta = m.getPuntoVenta().getCodigo();

        Variant ult = Dispatch.call(wsfev1, "CompConsultar",
                new Variant(tipo_cbte),
                new Variant(pto_vta),
                new Variant(m.getNumeroFormulario()));

        String excepcion = Dispatch.get(wsfev1, "Excepcion").toString();

        if (!excepcion.isEmpty()) {
            LOG.log(Level.SEVERE, "Excepción:" + excepcion, excepcion);
            throw new ExcepcionGeneralSistema("WSFEv1: " + excepcion);
        }

        String errmsg = Dispatch.get(wsfev1, "ErrMsg").toString();

        if (!errmsg.isEmpty()) {
            LOG.log(Level.SEVERE, "CAESolicitar Error: " + errmsg, errmsg);
            throw new ExcepcionGeneralSistema("WSFEv1: " + errmsg);
        }

        String fechaVencimiento = Dispatch.get(wsfev1, "Vencimiento").toString();
        String cae = Dispatch.get(wsfev1, "Cae").toString();

//        cae = Dispatch.call(wsfev1, "ObtenerCampoFactura",new Variant("cae")).toString();
        m.setInstanciaCAE("C");
        m.setNumeroCAE(cae.toString());

        try {
            m.setVencimientoCAE(new SimpleDateFormat("yyyyMMdd").parse(fechaVencimiento));
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public void autorizarComprobantePrueba() {
        try {
            wsaa.obtenerTicketAcceso(parametro);
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void agregoDatosIVA(MovimientoVenta m) {

        for (ItemImpuestoVenta iiv : m.getItemImpuesto()) {

            if (iiv.getImporte().compareTo(BigDecimal.ZERO) > 0) {

                Variant iva_id = null;
                String iva_base_imp = "";

                if (iiv.getConcepto().getCodigo().equals("IVA001")) {
                    iva_id = new Variant(5);
                    iva_base_imp = iiv.getBaseImponible().setScale(2, RoundingMode.HALF_UP).toString();
                    //iva_base_imp = iiv.getImporte().multiply(new BigDecimal("100")).divide(new BigDecimal("21"), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).toString();
                }

                if (iiv.getConcepto().getCodigo().equals("IVA002")) {
                    iva_id = new Variant(4);
                    iva_base_imp = iiv.getBaseImponible().setScale(2, RoundingMode.HALF_UP).toString();
                    //iva_base_imp = iiv.getImporte().multiply(new BigDecimal("100")).divide(new BigDecimal("10.5"), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).toString();
                }

                if (iiv.getConcepto().getCodigo().equals("IVA003")) {
                    iva_id = new Variant(6);
                    iva_base_imp = iiv.getBaseImponible().setScale(2, RoundingMode.HALF_UP).toString();
                    //iva_base_imp = iiv.getImporte().multiply(new BigDecimal("100")).divide(new BigDecimal("27"), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).toString();
                }

                String iva_importe = iiv.getImporte().setScale(2, RoundingMode.HALF_UP).toString();

                //System.out.println("iva_id: " + iva_id + " iva_base_imp: " + iva_base_imp + " iva_importe: " + iva_importe);
                Dispatch.call(wsfev1, "AgregarIva", iva_id, new Variant(iva_base_imp.replace(',', '.')), new Variant(iva_importe.replace(',', '.')));
            }
        }

    }

    /**
     * tributo_id: código tipo de impuesto (según tabla de parámetros AFIP)
     * desc: descripción del tributo (por ej. "Impuesto Municipal Matanza")
     * base_imp: base imponible (importe) alic: alicuota (porcentaje) importe:
     * importe liquidado
     *
     * @param m
     */
    private void agregoDatosPercepcion(MovimientoVenta m) {

        String tributo_id;
        String tributo_desc;
        String tributo_base_imp;
        String tributo_alic;
        String tributo_importe;

        for (ItemPercepcionVenta ipv : m.getItemPercepcion()) {

            if (ipv.getImporte().compareTo(BigDecimal.ZERO) > 0) {

                /* Agrego impuestos varios */
                tributo_id = "2";
                tributo_desc = ipv.getConcepto().getDescripcion();
                tributo_base_imp = ipv.getBaseImponible().setScale(2, RoundingMode.HALF_UP).toString();
                tributo_alic = ipv.getAlicuota().toString();
                tributo_importe = ipv.getImporte().setScale(2, RoundingMode.HALF_UP).toString();

//                System.out.println("tributo_id: " +tributo_id + " tributo_desc: " + tributo_desc + " tributo_base_imp: " + tributo_base_imp + " tributo_alic: " + tributo_alic + " tributo_importe:" + tributo_importe  );
                Dispatch.call(wsfev1, "AgregarTributo", new Variant(tributo_id), new Variant(tributo_desc), new Variant(tributo_base_imp.replace(',', '.')), new Variant(tributo_alic.replace(',', '.')), new Variant(tributo_importe.replace(',', '.')));
            }
        }
    }

    private void agregoComprobantesAsociados(MovimientoVenta m) {
        //Para NC Sobre Facturas de Crédito es obligatorio. El cuit del comprobante asociado se refiere al cuit emisor del comprobante
        Variant cbte_asoc_tipo = new Variant();

        if (m.getFormulario().getCodigoDGI().equals("203")
                || m.getFormulario().getCodigoDGI().equals("208")
                || m.getFormulario().getCodigoDGI().equals("213")) {

            if (m.getFormulario().getCodigoDGI().equals("203")) {//NC A

                cbte_asoc_tipo = new Variant("201");// Factura de Credito A

            } else if (m.getFormulario().getCodigoDGI().equals("208")) {// NC B

                cbte_asoc_tipo = new Variant("206"); // Factura de Credito B

            } else if (m.getFormulario().getCodigoDGI().equals("213")) { //NC C

                cbte_asoc_tipo = new Variant("211");// Factura de Credito C
            }

            Variant cbte_asoc_pto_vta = new Variant(m.getPuntoVenta()),
                    cbte_asoc_nro = new Variant(String.valueOf(m.getNumeroCpbteAsociado())),
                    cbte_asoc_cuit = new Variant(m.getEmpresa().getCuit()), //cuit emisor del comprobante
                    cbte_asoc_fecha = new Variant(new SimpleDateFormat("yyyyMMdd").format(m.getFechaCpbteAsociado()));
            Dispatch.call(wsfev1, "AgregarCmpAsoc",
                    cbte_asoc_tipo, cbte_asoc_pto_vta, cbte_asoc_nro, cbte_asoc_cuit, cbte_asoc_fecha);
        }
//        /* Agrego los comprobantes asociados: */
//        if (false) {
//            /* solo nc/nd */
//            Variant cbte_asoc_tipo = new Variant("19"),
//                    cbte_asoc_pto_vta = new Variant("2"),
//                    cbte_asoc_nro = new Variant("1234");
//            Dispatch.call(wsfev1, "AgregarCmpAsoc",
//                    cbte_asoc_tipo, cbte_asoc_pto_vta, cbte_asoc_nro);
        // }

    }

    private void generarXML() {

        try {
            File XmlRequest = new File("C:\\certificados\\XmlRequest.txt");
            FileWriter fileWriter = new FileWriter(XmlRequest, true);
            fileWriter.write(Dispatch.get(wsfev1, "XmlRequest").toString());
            fileWriter.close();

            File XmlResponse = new File("C:\\certificados\\XmlResponse.txt");
            fileWriter = new FileWriter(XmlResponse, true);
            fileWriter.write(Dispatch.get(wsfev1, "XmlRequest").toString());
            fileWriter.close();

        } catch (Exception e) {
            System.out.println("Error guardando archivo");
        }
    }

    public void agregarOpcionales(MovimientoVenta m) {
        Variant dato_opc_cbu = new Variant("2101"); //RG 4367 Factura de Crédito Electrónica MiPyMEs (FCE) - CBU del Emisor
        //Variant dato_opc_alias = new Variant("2102");//RG 4367 Factura de Crédito Electrónica MiPyMEs (FCE) - Alias del Emisor
        //String alias = m.getEmpresa().getAlias();
        String cbu = m.getEmpresa().getCbu();

        //Notas de Creditos
        if (m.getFormulario().getCodigoDGI().equals("203")
                || m.getFormulario().getCodigoDGI().equals("208")
                || m.getFormulario().getCodigoDGI().equals("213")) {
            Dispatch.call(wsfev1, "AgregarOpcional", new Variant("22"), new Variant("S"));
            return;
        }

        // Facturas de Credito y Notas de Debito
        if (m.getFormulario().getCodigoDGI().equals("201")
                || m.getFormulario().getCodigoDGI().equals("202")
                || m.getFormulario().getCodigoDGI().equals("206")
                || m.getFormulario().getCodigoDGI().equals("207")
                || m.getFormulario().getCodigoDGI().equals("211")
                || m.getFormulario().getCodigoDGI().equals("212")) {

            Dispatch.call(wsfev1, "AgregarOpcional", dato_opc_cbu, new Variant(cbu));
            //    Dispatch.call(wsfev1, "AgregarOpcional", dato_opc_alias, new Variant(alias));
        }

    }

}
