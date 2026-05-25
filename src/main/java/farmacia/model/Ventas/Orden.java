package farmacia.model.Ventas;

import java.util.ArrayList;
import java.util.Date;

public class Orden {
    private Date fecha;
    private ArrayList<ItemCarrito> listaDeItems;
    private double precioTotal;
    private EstadoOrden estado;

    public Orden(Date fecha, ArrayList<ItemCarrito> listaDeItems,double precioTotal){
        this.fecha=new Date();
        this.listaDeItems=listaDeItems;
        this.precioTotal=precioTotal;
        estado=EstadoOrden.PENDIENTE;
    }

    public String mostrarResumen(){
        String resumen="";
        return resumen; //esto lo hice pq me estresa ver un error xd, 
        // la variable no sirve de nada, se puede borrar :v
    }

    public void cancelarOrden(){
        estado=EstadoOrden.CANCELADA;
    }

    public void confirmarOrden(){
        estado=EstadoOrden.CONFIRMADA;
    }
}
