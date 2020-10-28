/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.modelo;

import org.primefaces.model.StreamedContent;

/**
 *
 * @author Claudio
 */
public class ArchivoDescargable {
    
    String nombre;
    String pathDescarga;
    StreamedContent streamedContent;

    public ArchivoDescargable(String nombre, String pathDescarga, StreamedContent streamedContent) {
        this.nombre = nombre;
        this.pathDescarga = pathDescarga;
        this.streamedContent = streamedContent;
    }

    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPathDescarga() {
        return pathDescarga;
    }

    public void setPathDescarga(String pathDescarga) {
        this.pathDescarga = pathDescarga;
    }

    public StreamedContent getStreamedContent() {
        return streamedContent;
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }
    
    
    
}
