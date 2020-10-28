///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package bs.produccion.modelo;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.Date;
//import javax.persistence.Column;
//import javax.persistence.EmbeddedId;
//import javax.persistence.Entity;
//import javax.persistence.Lob;
//import javax.persistence.Table;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//
///**
// *
// * @author ctrosch
// */
//@Entity
//@Table(name = "pdrcto")
//public class CostosProduccion implements Serializable {
//    private static final long serialVersionUID = 1L;
//    @EmbeddedId
//    protected CostosProduccionPK pD_CostosPK;
//    @Column(name = "PDRCTO_TIPFOR", length = 6)
//    private String pdrctoTipfor;
//    @Column(name = "PDRCTO_ARTFOR", length = 30)
//    private String pdrctoArtfor;
//    @Column(name = "PDRCTO_TIPORI", length = 6)
//    private String pdrctoTipori;
//    @Column(name = "PDRCTO_ARTORI", length = 30)
//    private String pdrctoArtori;
//    @Column(name = "PDRCTO_CNTREQ", precision = 20, scale = 8)
//    private BigDecimal pdrctoCntreq;
//    @Column(name = "PDRCTO_CNTREA", precision = 20, scale = 8)
//    private BigDecimal pdrctoCntrea;
//    @Column(name = "PDRCTO_UNIMED", length = 6)
//    private String pdrctoUnimed;
//    @Column(name = "PDRCTO_STOCKS", length = 1)
//    private String pdrctoStocks;
//    @Column(name = "PDRCTO_TIPAPL", length = 6)
//    private String pdrctoTipapl;
//    @Column(name = "PDRCTO_ARTAPL", length = 30)
//    private String pdrctoArtapl;
//    @Column(name = "PDRCTO_FORAPL", length = 8)
//    private String pdrctoForapl;
//    @Column(name = "PDRCTO_CTOCAL", precision = 20, scale = 8)
//    private BigDecimal pdrctoCtocal;
//    @Column(name = "PDRCTO_CTOFIN", precision = 20, scale = 8)
//    private BigDecimal pdrctoCtofin;
//    @Column(name = "PDRCTO_UNIREF", precision = 20, scale = 8)
//    private BigDecimal pdrctoUniref;
//    @Column(name = "PDRCTO_UNIREP", precision = 20, scale = 8)
//    private BigDecimal pdrctoUnirep;
//    @Column(name = "PDRCTO_UNIUCO", precision = 20, scale = 8)
//    private BigDecimal pdrctoUniuco;
//    @Column(name = "PDRCTO_UNIPRD", precision = 20, scale = 8)
//    private BigDecimal pdrctoUniprd;
//    @Column(name = "PDRCTO_PREREP", precision = 20, scale = 8)
//    private BigDecimal pdrctoPrerep;
//    @Column(name = "PDRCTO_PREUCO", precision = 20, scale = 8)
//    private BigDecimal pdrctoPreuco;
//    @Column(name = "PDRCTO_PREPRD", precision = 20, scale = 8)
//    private BigDecimal pdrctoPreprd;
//    @Column(name = "PDRCTO_PORCEN", precision = 15, scale = 7)
//    private BigDecimal pdrctoPorcen;
//    @Column(name = "PDRCTO_DEPTOS", length = 6)
//    private String pdrctoDeptos;
//    @Column(name = "PDRCTO_CORTE1", precision = 20, scale = 8)
//    private BigDecimal pdrctoCorte1;
//    @Column(name = "PDRCTO_CORTE2", precision = 20, scale = 8)
//    private BigDecimal pdrctoCorte2;
//    @Column(name = "PDRCTO_CORTE3", precision = 20, scale = 8)
//    private BigDecimal pdrctoCorte3;
//    @Column(name = "PDRCTO_CORTE4", precision = 20, scale = 8)
//    private BigDecimal pdrctoCorte4;
//    @Column(name = "PDRCTO_CORTE5", precision = 20, scale = 8)
//    private BigDecimal pdrctoCorte5;
//    @Column(name = "PDRCTO_CORTE6", precision = 20, scale = 8)
//    private BigDecimal pdrctoCorte6;
//    @Column(name = "PDRCTO_CORTE7", precision = 20, scale = 8)
//    private BigDecimal pdrctoCorte7;
//    @Column(name = "PDRCTO_CORTE8", precision = 20, scale = 8)
//    private BigDecimal pdrctoCorte8;
//    @Column(name = "PDRCTO_UNILIS", precision = 20, scale = 8)
//    private BigDecimal pdrctoUnilis;
//    @Column(name = "PDRCTO_PRELIS", precision = 20, scale = 8)
//    private BigDecimal pdrctoPrelis;
//    @Column(name = "PDRCTO_CODLIS", length = 10)
//    private String pdrctoCodlis;
//    @Column(name = "PDRCTO_MONEDA", length = 6)
//    private String pdrctoMoneda;
//    @Column(name = "PDRCTO_FECLIS")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date pdrctoFeclis;
//    @Column(name = "PDRCTO_MUESTR")
//    private Character pdrctoMuestr;
//    @Column(name = "PDRCTO_CODCOF", length = 6)
//    private String pdrctoCodcof;
//    @Column(name = "PDRCTO_FECALT")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date pdrctoFecalt;
//    @Column(name = "PDRCTO_FECMOD")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date pdrctoFecmod;
//    @Column(name = "PDRCTO_USERID", length = 15)
//    private String pdrctoUserid;
//    @Column(name = "PDRCTO_ULTOPR")
//    private Character pdrctoUltopr;
//    @Column(name = "PDRCTO_DEBAJA")
//    private Character pdrctoDebaja;
//    @Column(name = "PDRCTO_HORMOV", length = 10)
//    private String pdrctoHormov;
//    @Column(name = "PDRCTO_MODULE", length = 10)
//    private String pdrctoModule;
//    @Column(name = "PDRCTO_OALIAS", length = 10)
//    private String pdrctoOalias;
//    @Lob
//    @Column(name = "PDRCTO_TSTAMP")
//    private byte[] pdrctoTstamp;
//    @Column(name = "PDRCTO_LOTTRA", length = 6)
//    private String pdrctoLottra;
//    @Column(name = "PDRCTO_LOTREC", length = 6)
//    private String pdrctoLotrec;
//    @Column(name = "PDRCTO_LOTORI", length = 6)
//    private String pdrctoLotori;
//    @Column(name = "PDRCTO_SYSVER", length = 10)
//    private String pdrctoSysver;
//    @Column(name = "PDRCTO_CMPVER", length = 10)
//    private String pdrctoCmpver;
//
//    public CostosProduccion() {
//    }
//
//    public CostosProduccion(CostosProduccionPK pD_CostosPK) {
//        this.pD_CostosPK = pD_CostosPK;
//    }
//
//    public CostosProduccion(int pdrctoNrolot, String pdrctoTippro, String pdrctoArtcod, String pdrctoFormul, int pdrctoNroitm, String pdrctoNivexp, int pdrctoItmexp) {
//        this.pD_CostosPK = new CostosProduccionPK(pdrctoNrolot, pdrctoTippro, pdrctoArtcod, pdrctoFormul, pdrctoNroitm, pdrctoNivexp, pdrctoItmexp);
//    }
//
//    public CostosProduccionPK getPD_CostosPK() {
//        return pD_CostosPK;
//    }
//
//    public void setPD_CostosPK(CostosProduccionPK pD_CostosPK) {
//        this.pD_CostosPK = pD_CostosPK;
//    }
//
//    public String getPdrctoTipfor() {
//        return pdrctoTipfor;
//    }
//
//    public void setPdrctoTipfor(String pdrctoTipfor) {
//        this.pdrctoTipfor = pdrctoTipfor;
//    }
//
//    public String getPdrctoArtfor() {
//        return pdrctoArtfor;
//    }
//
//    public void setPdrctoArtfor(String pdrctoArtfor) {
//        this.pdrctoArtfor = pdrctoArtfor;
//    }
//
//    public String getPdrctoTipori() {
//        return pdrctoTipori;
//    }
//
//    public void setPdrctoTipori(String pdrctoTipori) {
//        this.pdrctoTipori = pdrctoTipori;
//    }
//
//    public String getPdrctoArtori() {
//        return pdrctoArtori;
//    }
//
//    public void setPdrctoArtori(String pdrctoArtori) {
//        this.pdrctoArtori = pdrctoArtori;
//    }
//
//    public BigDecimal getPdrctoCntreq() {
//        return pdrctoCntreq;
//    }
//
//    public void setPdrctoCntreq(BigDecimal pdrctoCntreq) {
//        this.pdrctoCntreq = pdrctoCntreq;
//    }
//
//    public BigDecimal getPdrctoCntrea() {
//        return pdrctoCntrea;
//    }
//
//    public void setPdrctoCntrea(BigDecimal pdrctoCntrea) {
//        this.pdrctoCntrea = pdrctoCntrea;
//    }
//
//    public String getPdrctoUnimed() {
//        return pdrctoUnimed;
//    }
//
//    public void setPdrctoUnimed(String pdrctoUnimed) {
//        this.pdrctoUnimed = pdrctoUnimed;
//    }
//
//    public String getPdrctoStocks() {
//        return pdrctoStocks;
//    }
//
//    public void setPdrctoStocks(String pdrctoStocks) {
//        this.pdrctoStocks = pdrctoStocks;
//    }
//
//    public String getPdrctoTipapl() {
//        return pdrctoTipapl;
//    }
//
//    public void setPdrctoTipapl(String pdrctoTipapl) {
//        this.pdrctoTipapl = pdrctoTipapl;
//    }
//
//    public String getPdrctoArtapl() {
//        return pdrctoArtapl;
//    }
//
//    public void setPdrctoArtapl(String pdrctoArtapl) {
//        this.pdrctoArtapl = pdrctoArtapl;
//    }
//
//    public String getPdrctoForapl() {
//        return pdrctoForapl;
//    }
//
//    public void setPdrctoForapl(String pdrctoForapl) {
//        this.pdrctoForapl = pdrctoForapl;
//    }
//
//    public BigDecimal getPdrctoCtocal() {
//        return pdrctoCtocal;
//    }
//
//    public void setPdrctoCtocal(BigDecimal pdrctoCtocal) {
//        this.pdrctoCtocal = pdrctoCtocal;
//    }
//
//    public BigDecimal getPdrctoCtofin() {
//        return pdrctoCtofin;
//    }
//
//    public void setPdrctoCtofin(BigDecimal pdrctoCtofin) {
//        this.pdrctoCtofin = pdrctoCtofin;
//    }
//
//    public BigDecimal getPdrctoUniref() {
//        return pdrctoUniref;
//    }
//
//    public void setPdrctoUniref(BigDecimal pdrctoUniref) {
//        this.pdrctoUniref = pdrctoUniref;
//    }
//
//    public BigDecimal getPdrctoUnirep() {
//        return pdrctoUnirep;
//    }
//
//    public void setPdrctoUnirep(BigDecimal pdrctoUnirep) {
//        this.pdrctoUnirep = pdrctoUnirep;
//    }
//
//    public BigDecimal getPdrctoUniuco() {
//        return pdrctoUniuco;
//    }
//
//    public void setPdrctoUniuco(BigDecimal pdrctoUniuco) {
//        this.pdrctoUniuco = pdrctoUniuco;
//    }
//
//    public BigDecimal getPdrctoUniprd() {
//        return pdrctoUniprd;
//    }
//
//    public void setPdrctoUniprd(BigDecimal pdrctoUniprd) {
//        this.pdrctoUniprd = pdrctoUniprd;
//    }

//
//    public BigDecimal getPdrctoPrerep() {
//        return pdrctoPrerep;
//    }
//
//    public void setPdrctoPrerep(BigDecimal pdrctoPrerep) {
//        this.pdrctoPrerep = pdrctoPrerep;
//    }
//
//    public BigDecimal getPdrctoPreuco() {
//        return pdrctoPreuco;
//    }
//
//    public void setPdrctoPreuco(BigDecimal pdrctoPreuco) {
//        this.pdrctoPreuco = pdrctoPreuco;
//    }
//
//    public BigDecimal getPdrctoPreprd() {
//        return pdrctoPreprd;
//    }
//
//    public void setPdrctoPreprd(BigDecimal pdrctoPreprd) {
//        this.pdrctoPreprd = pdrctoPreprd;
//    }
//
//    public BigDecimal getPdrctoPorcen() {
//        return pdrctoPorcen;
//    }
//
//    public void setPdrctoPorcen(BigDecimal pdrctoPorcen) {
//        this.pdrctoPorcen = pdrctoPorcen;
//    }
//
//    public String getPdrctoDeptos() {
//        return pdrctoDeptos;
//    }
//
//    public void setPdrctoDeptos(String pdrctoDeptos) {
//        this.pdrctoDeptos = pdrctoDeptos;
//    }
//
//    public BigDecimal getPdrctoCorte1() {
//        return pdrctoCorte1;
//    }
//
//    public void setPdrctoCorte1(BigDecimal pdrctoCorte1) {
//        this.pdrctoCorte1 = pdrctoCorte1;
//    }
//
//    public BigDecimal getPdrctoCorte2() {
//        return pdrctoCorte2;
//    }
//
//    public void setPdrctoCorte2(BigDecimal pdrctoCorte2) {
//        this.pdrctoCorte2 = pdrctoCorte2;
//    }
//
//    public BigDecimal getPdrctoCorte3() {
//        return pdrctoCorte3;
//    }
//
//    public void setPdrctoCorte3(BigDecimal pdrctoCorte3) {
//        this.pdrctoCorte3 = pdrctoCorte3;
//    }
//
//    public BigDecimal getPdrctoCorte4() {
//        return pdrctoCorte4;
//    }
//
//    public void setPdrctoCorte4(BigDecimal pdrctoCorte4) {
//        this.pdrctoCorte4 = pdrctoCorte4;
//    }
//
//    public BigDecimal getPdrctoCorte5() {
//        return pdrctoCorte5;
//    }
//
//    public void setPdrctoCorte5(BigDecimal pdrctoCorte5) {
//        this.pdrctoCorte5 = pdrctoCorte5;
//    }
//
//    public BigDecimal getPdrctoCorte6() {
//        return pdrctoCorte6;
//    }
//
//    public void setPdrctoCorte6(BigDecimal pdrctoCorte6) {
//        this.pdrctoCorte6 = pdrctoCorte6;
//    }
//
//    public BigDecimal getPdrctoCorte7() {
//        return pdrctoCorte7;
//    }
//
//    public void setPdrctoCorte7(BigDecimal pdrctoCorte7) {
//        this.pdrctoCorte7 = pdrctoCorte7;
//    }
//
//    public BigDecimal getPdrctoCorte8() {
//        return pdrctoCorte8;
//    }
//
//    public void setPdrctoCorte8(BigDecimal pdrctoCorte8) {
//        this.pdrctoCorte8 = pdrctoCorte8;
//    }
//
//    public BigDecimal getPdrctoUnilis() {
//        return pdrctoUnilis;
//    }
//
//    public void setPdrctoUnilis(BigDecimal pdrctoUnilis) {
//        this.pdrctoUnilis = pdrctoUnilis;
//    }
//
//    public BigDecimal getPdrctoPrelis() {
//        return pdrctoPrelis;
//    }
//
//    public void setPdrctoPrelis(BigDecimal pdrctoPrelis) {
//        this.pdrctoPrelis = pdrctoPrelis;
//    }
//
//    public String getPdrctoCodlis() {
//        return pdrctoCodlis;
//    }
//
//    public void setPdrctoCodlis(String pdrctoCodlis) {
//        this.pdrctoCodlis = pdrctoCodlis;
//    }
//
//    public String getPdrctoMoneda() {
//        return pdrctoMoneda;
//    }
//
//    public void setPdrctoMoneda(String pdrctoMoneda) {
//        this.pdrctoMoneda = pdrctoMoneda;
//    }
//
//    public Date getPdrctoFeclis() {
//        return pdrctoFeclis;
//    }
//
//    public void setPdrctoFeclis(Date pdrctoFeclis) {
//        this.pdrctoFeclis = pdrctoFeclis;
//    }
//
//    public Character getPdrctoMuestr() {
//        return pdrctoMuestr;
//    }
//
//    public void setPdrctoMuestr(Character pdrctoMuestr) {
//        this.pdrctoMuestr = pdrctoMuestr;
//    }
//
//    public String getPdrctoCodcof() {
//        return pdrctoCodcof;
//    }
//
//    public void setPdrctoCodcof(String pdrctoCodcof) {
//        this.pdrctoCodcof = pdrctoCodcof;
//    }
//
//    public Date getPdrctoFecalt() {
//        return pdrctoFecalt;
//    }
//
//    public void setPdrctoFecalt(Date pdrctoFecalt) {
//        this.pdrctoFecalt = pdrctoFecalt;
//    }
//
//    public Date getPdrctoFecmod() {
//        return pdrctoFecmod;
//    }
//
//    public void setPdrctoFecmod(Date pdrctoFecmod) {
//        this.pdrctoFecmod = pdrctoFecmod;
//    }
//
//    public String getPdrctoUserid() {
//        return pdrctoUserid;
//    }
//
//    public void setPdrctoUserid(String pdrctoUserid) {
//        this.pdrctoUserid = pdrctoUserid;
//    }
//
//    public Character getPdrctoUltopr() {
//        return pdrctoUltopr;
//    }
//
//    public void setPdrctoUltopr(Character pdrctoUltopr) {
//        this.pdrctoUltopr = pdrctoUltopr;
//    }
//
//    public Character getPdrctoDebaja() {
//        return pdrctoDebaja;
//    }
//
//    public void setPdrctoDebaja(Character pdrctoDebaja) {
//        this.pdrctoDebaja = pdrctoDebaja;
//    }
//
//    public String getPdrctoHormov() {
//        return pdrctoHormov;
//    }
//
//    public void setPdrctoHormov(String pdrctoHormov) {
//        this.pdrctoHormov = pdrctoHormov;
//    }
//
//    public String getPdrctoModule() {
//        return pdrctoModule;
//    }
//
//    public void setPdrctoModule(String pdrctoModule) {
//        this.pdrctoModule = pdrctoModule;
//    }
//
//    public String getPdrctoOalias() {
//        return pdrctoOalias;
//    }
//
//    public void setPdrctoOalias(String pdrctoOalias) {
//        this.pdrctoOalias = pdrctoOalias;
//    }
//
//    public byte[] getPdrctoTstamp() {
//        return pdrctoTstamp;
//    }
//
//    public void setPdrctoTstamp(byte[] pdrctoTstamp) {
//        this.pdrctoTstamp = pdrctoTstamp;
//    }
//
//    public String getPdrctoLottra() {
//        return pdrctoLottra;
//    }
//
//    public void setPdrctoLottra(String pdrctoLottra) {
//        this.pdrctoLottra = pdrctoLottra;
//    }
//
//    public String getPdrctoLotrec() {
//        return pdrctoLotrec;
//    }
//
//    public void setPdrctoLotrec(String pdrctoLotrec) {
//        this.pdrctoLotrec = pdrctoLotrec;
//    }
//
//    public String getPdrctoLotori() {
//        return pdrctoLotori;
//    }
//
//    public void setPdrctoLotori(String pdrctoLotori) {
//        this.pdrctoLotori = pdrctoLotori;
//    }
//
//    public String getPdrctoSysver() {
//        return pdrctoSysver;
//    }
//
//    public void setPdrctoSysver(String pdrctoSysver) {
//        this.pdrctoSysver = pdrctoSysver;
//    }
//
//    public String getPdrctoCmpver() {
//        return pdrctoCmpver;
//    }
//
//    public void setPdrctoCmpver(String pdrctoCmpver) {
//        this.pdrctoCmpver = pdrctoCmpver;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (pD_CostosPK != null ? pD_CostosPK.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof CostosProduccion)) {
//            return false;
//        }
//        CostosProduccion other = (CostosProduccion) object;
//        if ((this.pD_CostosPK == null && other.pD_CostosPK != null) || (this.pD_CostosPK != null && !this.pD_CostosPK.equals(other.pD_CostosPK))) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "isd.produccion.modelo.PD_Costos[pD_CostosPK=" + pD_CostosPK + "]";
//    }
//
//}
