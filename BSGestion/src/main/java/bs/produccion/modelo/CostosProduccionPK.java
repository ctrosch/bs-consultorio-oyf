///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package bs.produccion.modelo;
//
//import java.io.Serializable;
//import javax.persistence.Basic;
//import javax.persistence.Column;
//import javax.persistence.Embeddable;
//
///**
// *
// * @author ctrosch
// */
//@Embeddable
//public class CostosProduccionPK implements Serializable {
//    @Basic(optional = false)
//    @Column(name = "PDRCTO_NROLOT", nullable = false)
//    private int pdrctoNrolot;
//    @Basic(optional = false)
//    @Column(name = "PDRCTO_TIPPRO", nullable = false, length = 6)
//    private String pdrctoTippro;
//    @Basic(optional = false)
//    @Column(name = "PDRCTO_ARTCOD", nullable = false, length = 30)
//    private String pdrctoArtcod;
//    @Basic(optional = false)
//    @Column(name = "PDRCTO_FORMUL", nullable = false, length = 8)
//    private String pdrctoFormul;
//    @Basic(optional = false)
//    @Column(name = "PDRCTO_NROITM", nullable = false)
//    private int pdrctoNroitm;
//    @Basic(optional = false)
//    @Column(name = "PDRCTO_NIVEXP", nullable = false, length = 2)
//    private String pdrctoNivexp;
//    @Basic(optional = false)
//    @Column(name = "PDRCTO_ITMEXP", nullable = false)
//    private int pdrctoItmexp;
//
//    public CostosProduccionPK() {
//    }
//
//    public CostosProduccionPK(int pdrctoNrolot, String pdrctoTippro, String pdrctoArtcod, String pdrctoFormul, int pdrctoNroitm, String pdrctoNivexp, int pdrctoItmexp) {
//        this.pdrctoNrolot = pdrctoNrolot;
//        this.pdrctoTippro = pdrctoTippro;
//        this.pdrctoArtcod = pdrctoArtcod;
//        this.pdrctoFormul = pdrctoFormul;
//        this.pdrctoNroitm = pdrctoNroitm;
//        this.pdrctoNivexp = pdrctoNivexp;
//        this.pdrctoItmexp = pdrctoItmexp;
//    }
//
//    public int getPdrctoNrolot() {
//        return pdrctoNrolot;
//    }
//
//    public void setPdrctoNrolot(int pdrctoNrolot) {
//        this.pdrctoNrolot = pdrctoNrolot;
//    }
//
//    public String getPdrctoTippro() {
//        return pdrctoTippro;
//    }
//
//    public void setPdrctoTippro(String pdrctoTippro) {
//        this.pdrctoTippro = pdrctoTippro;
//    }
//
//    public String getPdrctoArtcod() {
//        return pdrctoArtcod;
//    }
//
//    public void setPdrctoArtcod(String pdrctoArtcod) {
//        this.pdrctoArtcod = pdrctoArtcod;
//    }
//
//    public String getPdrctoFormul() {
//        return pdrctoFormul;
//    }
//
//    public void setPdrctoFormul(String pdrctoFormul) {
//        this.pdrctoFormul = pdrctoFormul;
//    }
//
//    public int getPdrctoNroitm() {
//        return pdrctoNroitm;
//    }
//
//    public void setPdrctoNroitm(int pdrctoNroitm) {
//        this.pdrctoNroitm = pdrctoNroitm;
//    }
//
//    public String getPdrctoNivexp() {
//        return pdrctoNivexp;
//    }
//
//    public void setPdrctoNivexp(String pdrctoNivexp) {
//        this.pdrctoNivexp = pdrctoNivexp;
//    }
//
//    public int getPdrctoItmexp() {
//        return pdrctoItmexp;
//    }
//
//    public void setPdrctoItmexp(int pdrctoItmexp) {
//        this.pdrctoItmexp = pdrctoItmexp;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (int) pdrctoNrolot;
//        hash += (pdrctoTippro != null ? pdrctoTippro.hashCode() : 0);
//        hash += (pdrctoArtcod != null ? pdrctoArtcod.hashCode() : 0);
//        hash += (pdrctoFormul != null ? pdrctoFormul.hashCode() : 0);
//        hash += (int) pdrctoNroitm;
//        hash += (pdrctoNivexp != null ? pdrctoNivexp.hashCode() : 0);
//        hash += (int) pdrctoItmexp;
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof CostosProduccionPK)) {
//            return false;
//        }
//        CostosProduccionPK other = (CostosProduccionPK) object;
//        if (this.pdrctoNrolot != other.pdrctoNrolot) {
//            return false;
//        }
//        if ((this.pdrctoTippro == null && other.pdrctoTippro != null) || (this.pdrctoTippro != null && !this.pdrctoTippro.equals(other.pdrctoTippro))) {
//            return false;
//        }
//        if ((this.pdrctoArtcod == null && other.pdrctoArtcod != null) || (this.pdrctoArtcod != null && !this.pdrctoArtcod.equals(other.pdrctoArtcod))) {
//            return false;
//        }
//        if ((this.pdrctoFormul == null && other.pdrctoFormul != null) || (this.pdrctoFormul != null && !this.pdrctoFormul.equals(other.pdrctoFormul))) {
//            return false;
//        }
//        if (this.pdrctoNroitm != other.pdrctoNroitm) {
//            return false;
//        }
//        if ((this.pdrctoNivexp == null && other.pdrctoNivexp != null) || (this.pdrctoNivexp != null && !this.pdrctoNivexp.equals(other.pdrctoNivexp))) {
//            return false;
//        }
//        if (this.pdrctoItmexp != other.pdrctoItmexp) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "isd.produccion.modelo.PD_CostosPK[pdrctoNrolot=" + pdrctoNrolot + ", pdrctoTippro=" + pdrctoTippro + ", pdrctoArtcod=" + pdrctoArtcod + ", pdrctoFormul=" + pdrctoFormul + ", pdrctoNroitm=" + pdrctoNroitm + ", pdrctoNivexp=" + pdrctoNivexp + ", pdrctoItmexp=" + pdrctoItmexp + "]";
//    }
//
//}
