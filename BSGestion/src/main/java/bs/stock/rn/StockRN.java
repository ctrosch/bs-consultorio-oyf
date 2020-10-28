/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.compra.rn.CompraRN;
import bs.facturacion.rn.FacturacionRN;
import bs.stock.dao.StockDAO;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.ItemMovimientoStock;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Stock;
import bs.stock.modelo.StockPK;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless
public class StockRN implements Serializable {

    @EJB
    private StockDAO stockDAO;
    @EJB
    private FacturacionRN facturacionRN;
    @EJB
    private DepositoRN depositoRN;
    @EJB
    private CompraRN compraRN;

    public synchronized void guardar(Stock stock) throws Exception {

        Stock stockAux = getStock(stock);
        if (stockAux == null) {
            stockDAO.crear(stock);
        } else {
            stockAux.setStocks(stockAux.getStocks() + stock.getStocks());
            stockDAO.editar(stockAux);
        }
    }

    public Stock getStock(Stock stock) {

        StockPK idPK = new StockPK(stock.getCodigo(), stock.getDeposi(), stock.getAtributo1(), stock.getAtributo2(), stock.getAtributo3(), stock.getAtributo4(), stock.getAtributo5(), stock.getAtributo6(), stock.getAtributo7());
        return stockDAO.getObjeto(Stock.class, idPK);
    }

    public String isProductoDisponible(Stock s) {

        double stockActual = stockDAO.getCantidadStockByProducto(s);
        double stockCompometido = facturacionRN.getComprometidoByProducto(s.getProducto(), s.getDeposito());

        s.setStocks(stockActual);
        s.setComprometido(stockCompometido);

//        System.err.println("Producto " + s.getProducto().getDescripcion());
//        System.err.println("-----------------------------------------------------------");
//        System.err.println("stockActual " + stockActual);
//        System.err.println("stockCompometido " + stockCompometido);
//        System.err.println("stock a Comprometer " + s.getStockComprometer());
//        System.err.println("stock a Descomprometer " + s.getStockDescomprometer());
//        System.err.println("stockDisponible " + s.getStockDisponible());
//        System.err.println("stockResultante " + (s.getStockDisponible() - s.getStockComprometer() + s.getStockDescomprometer() - s.getStockDescontar()));
//        System.err.println("-----------------------------------------------------------");
        //Si el stock resultante es menor a cero no pasa
        boolean hayDisponible = ((s.getStockDisponible() - s.getStockComprometer() + s.getStockDescomprometer() - s.getStockDescontar()) >= 0) && (s.getStockDisponible() >= 0);

        String mensaje = "";

        if (!hayDisponible) {

            DecimalFormat df = new DecimalFormat("0");

            mensaje = "Stock Insuficiente. Hay "
                    + df.format(s.getStocks()) + " " + s.getProducto().getUnidadDeMedida().getCodigo() + " en stock,"
                    + df.format(s.getComprometido()) + " " + s.getProducto().getUnidadDeMedida().getCodigo() + " comprometido,"
                    + df.format(s.getStockDisponible()) + " " + s.getProducto().getUnidadDeMedida().getCodigo() + " disponible/s para "
                    + s.getProducto().getDescripcion() + " en Deposito " + s.getDeposito().getDescripcion()
                    + (s.getProducto().getAdministraAtributo1().equals("S") ? "| Atributo1 " + s.getAtributo1() : "")
                    + (s.getProducto().getAdministraAtributo2().equals("S") ? "| Atributo2 " + s.getAtributo2() : "")
                    + (s.getProducto().getAdministraAtributo3().equals("S") ? "| Atributo3 " + s.getAtributo3() : "")
                    + (s.getProducto().getAdministraAtributo4().equals("S") ? "| Atributo4 " + s.getAtributo4() : "")
                    + (s.getProducto().getAdministraAtributo5().equals("S") ? "| Atributo5 " + s.getAtributo5() : "")
                    + (s.getProducto().getAdministraAtributo6().equals("S") ? "| Atributo6 " + s.getAtributo6() : "")
                    + (s.getProducto().getAdministraAtributo7().equals("S") ? "| Atributo7 " + s.getAtributo7() : "");
        }

//        System.err.println("hayDisponible " + hayDisponible);
//        System.err.println("mensaje " + mensaje);
        return mensaje;
    }

    public Stock getStock(StockPK idPK) {

        return stockDAO.getObjeto(Stock.class, idPK);
    }

    public List<Stock> getStockByProducto(Producto p) {

        return stockDAO.getStockByProducto(p.getCodigo());
    }

    public List<Stock> getStockByProducto(Producto p, boolean conAtributos) {

        if (p == null) {
            return null;
        }

        List<Stock> stocks = new ArrayList<>();
        List<Deposito> depositos = depositoRN.getLista(false);

        if (depositos == null) {
            return null;
        }

        for (Deposito d : depositos) {

            Stock s = new Stock();
            s.setArtcod(p.getCodigo());
            s.setDeposi(d.getCodigo());
            s.setDeposito(d);
            s.setProducto(p);

            s.setStocks(stockDAO.getCantidadStockTotalByProducto(p, d));
            s.setComprometido(facturacionRN.getComprometidoByProducto(p, d));
            s.setCompra(compraRN.getPendienteIngresoByProducto(p, d));

            if (conAtributos) {

                s.setAtributos(stockDAO.getStockByProductoDeposito(s.getCodigo(), s.getDeposi()));

            }
            stocks.add(s);
        }

        return stocks;
    }

    /**
     *
     * @param d deposito
     * @param conAtributos
     * @return Lista de stock con o sin atributos.
     */
    public List<Stock> getStockByDeposito(Deposito d, boolean conAtributos) {

        if (d == null) {
            return new ArrayList<Stock>();
        }

        List<Stock> resultado = stockDAO.getStockByDepositoSinAtributos(d.getCodigo());

        for (Stock s : resultado) {

            s.setComprometido(facturacionRN.getComprometidoByProducto(s.getProducto(), s.getDeposito()));
            if (conAtributos) {
                s.setAtributos(stockDAO.getStockByProductoDeposito(s.getCodigo(), s.getDeposi()));
            }
        }

        return resultado;
    }

    public List<Stock> getStockByDeposito(Deposito d) {

        if (d == null) {
            return new ArrayList<Stock>();
        }

        return stockDAO.getStockByDeposito(d.getCodigo());
    }

    public List<Stock> getStockByProductoDeposito(Producto p, Deposito d) {

        String artcod = "";
        String deposi = "";

        if (p != null) {
            artcod = p.getCodigo();
        }

        if (d != null) {
            deposi = d.getCodigo();
        }

        return stockDAO.getStockByProductoDeposito(artcod, deposi);
    }

    public double getStockAFecha(Producto p, Deposito d, Date fecha) {
        return stockDAO.getCantidadStockAFecha(p, d, fecha);
    }

    public double getCantidadStockTotalByProducto(Producto p, Deposito d) {
        return stockDAO.getCantidadStockTotalByProducto(p, d);
    }

    public List<ItemMovimientoStock> getMovimientosEntreFechas(Producto p, Deposito d, Date fDesde, Date fHasta) {

        return stockDAO.getMovimientosEntreFechas(p, d, fDesde, fHasta);
    }

    public void recalcularStock() {

        stockDAO.recalcularStock();
    }

}
