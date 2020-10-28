/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.ventas.modelo;

import java.math.BigDecimal;

/**
 *
 * @author Claudio
 */
public class ImporteCalculados {
    
    BigDecimal impGravado0000 = BigDecimal.ZERO;
    BigDecimal impGravado2100 = BigDecimal.ZERO;
    BigDecimal impGravado1050 = BigDecimal.ZERO;
    BigDecimal impGravado2700 = BigDecimal.ZERO;

    BigDecimal impIva2100;
    BigDecimal impIva1050;
    BigDecimal impIva2700;

    BigDecimal importeTotal = BigDecimal.ZERO;    

    public ImporteCalculados() {
        
    }

    public BigDecimal getImpGravado0000() {
        return impGravado0000;
    }

    public void setImpGravado0000(BigDecimal impGravado0000) {
        this.impGravado0000 = impGravado0000;
    }

    public BigDecimal getImpGravado2100() {
        return impGravado2100;
    }

    public void setImpGravado2100(BigDecimal impGravado2100) {
        this.impGravado2100 = impGravado2100;
    }

    public BigDecimal getImpGravado1050() {
        return impGravado1050;
    }

    public void setImpGravado1050(BigDecimal impGravado1050) {
        this.impGravado1050 = impGravado1050;
    }

    public BigDecimal getImpGravado2700() {
        return impGravado2700;
    }

    public void setImpGravado2700(BigDecimal impGravado2700) {
        this.impGravado2700 = impGravado2700;
    }

    public BigDecimal getImpIva2100() {
        return impIva2100;
    }

    public void setImpIva2100(BigDecimal impIva2100) {
        this.impIva2100 = impIva2100;
    }

    public BigDecimal getImpIva1050() {
        return impIva1050;
    }

    public void setImpIva1050(BigDecimal impIva1050) {
        this.impIva1050 = impIva1050;
    }

    public BigDecimal getImpIva2700() {
        return impIva2700;
    }

    public void setImpIva2700(BigDecimal impIva2700) {
        this.impIva2700 = impIva2700;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }
    
    
    
}
