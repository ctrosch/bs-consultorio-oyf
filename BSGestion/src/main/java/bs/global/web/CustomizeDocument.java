/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.web;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import java.io.File;
import java.io.IOException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CustomizeDocument {

    /**
     * Creates a new instance of CustomizeDocument
     */
    public CustomizeDocument() {
        
    }
    
    public void postProcessXLS(Object document) {
//        HSSFWorkbook wb = (HSSFWorkbook) document;
//        HSSFSheet sheet = wb.getSheetAt(0);
//        HSSFRow header = sheet.getRow(0);
//         
//        HSSFCellStyle cellStyle = wb.createCellStyle();  
//        cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
//        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//         
//        for(int i=0; i < 50;i++) {
//            HSSFCell cell = header.getCell(i);
//             
//            cell.setCellStyle(cellStyle);
//        }
    }
     
    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        Document pdf = (Document) document;
        pdf.open();
        pdf.setPageSize(PageSize.A4);
 
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String logo = servletContext.getRealPath("") + File.separator + "resources" + File.separator +  "image" + File.separator + "logo.png";
         
        pdf.add(Image.getInstance(logo));
    }
    
}
