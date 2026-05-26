package farmacia.model.Ventas;

import java.util.ArrayList;
import java.util.Date;
import com.google.gson.Gson;

public class Orden {
    private Date fecha;
    private ArrayList<ItemCarrito> listaDeItems;
    private double precioTotal;
    private EstadoOrden estado;

    public Orden(ArrayList<ItemCarrito> listaDeItems,double precioTotal){
        this.fecha=new Date();
        this.listaDeItems=listaDeItems;
        this.precioTotal=precioTotal;
        estado=EstadoOrden.PENDIENTE;
    }

    public String mostrarResumen(){
        Gson gson = new Gson();
        String resumen = gson.toJson(this);
        return resumen;
    }

    public void cancelarOrden(){
        estado=EstadoOrden.CANCELADA;
    }

    public void confirmarOrden(){
        estado=EstadoOrden.CONFIRMADA;
    }


}
