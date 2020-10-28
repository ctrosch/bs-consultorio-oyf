/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.web.informe;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Stock;
import bs.stock.rn.StockRN;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 * Bean utilizado para consulta stock desde otros módulos del sistema
 */
@ManagedBean
@ViewScoped
public class ConsultaStockByProductoDeposito extends InformeBase implements Serializable {

    @EJB private StockRN stockRN;

    private List<Stock> resultado;
    private Stock stockSeleccionado;

    /** Creates a new instance of ST_ConsultaStockByProductoDeposito */
    public ConsultaStockByProductoDeposito() {

    }
    
    public void verStockByProductoDeposito(Producto p, Deposito d){

        resultado = stockRN.getStockByProductoDeposito(p, d);
    }

    public void seleccionarStock(Stock s){

        stockSeleccionado = s;

    }

    public List<Stock> getResultado() {
        return resultado;
    }

    public void setResultado(List<Stock> resultado) {
        this.resultado = resultado;
    }

    public Stock getStockSeleccionado() {
        return stockSeleccionado;
    }

    public void setStockSeleccionado(Stock stockSeleccionado) {
        this.stockSeleccionado = stockSeleccionado;
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}
