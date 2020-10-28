/*
 * To change this template, choose Tools | Templates
 * Utilizamos esta interface para poder implementar la generacion de comprobantes de stock desde
   los diferentes módulos del sistema
 */
package bs.stock.modelo;

import java.math.BigDecimal;

/**
 *
 * @author Claudio
 */
public interface ItemDetalleModelo {

    public Integer getId();

    public void setId(Integer id);

    public int getNroitm();

    public void setNroitm(int nroitm);

    public Producto getProducto();

    public void setProducto(Producto producto);

    public String getDescripcion();

    public void setDescripcion(String descripcion);

    public Deposito getDeposito();

    public void setDeposito(Deposito deposito);

    public BigDecimal getCantidad();

    public void setCantidad(BigDecimal cantidad);

    public UnidadMedida getUnidadMedida();

    public void setUnidadMedida(UnidadMedida unidadMedida);

    public String getAtributo1();

    public void setAtributo1(String atributo1);

    public String getAtributo2();

    public void setAtributo2(String atributo2);

    public String getAtributo3();

    public void setAtributo3(String atributo3);

    public String getAtributo4();

    public void setAtributo4(String atributo4);

    public String getAtributo5();

    public void setAtributo5(String atributo5);

    public String getAtributo6();

    public void setAtributo6(String atributo6);

    public String getAtributo7();

    public void setAtributo7(String atributo7);

    public double getStockComprometer();

    public void setStockComprometer(double stockComprometer);

    public double getStockDescomprometer();

    public void setStockDescomprometer(double stockDescomprometer);

    public double getStockDescontar();

    public void setStockDescontar(double stockDescontar);

}
