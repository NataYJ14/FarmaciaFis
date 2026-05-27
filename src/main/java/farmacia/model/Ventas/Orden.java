package farmacia.model.Ventas;

import java.util.ArrayList;
import java.util.Date;
import com.google.gson.Gson;

public class Orden {
    private double idOrden;
    private Date fecha;
    private ArrayList<ItemCarrito> listaDeItems;
    private double precioTotal;
    private EstadoOrden estado;

    public Orden(ArrayList<ItemCarrito> listaDeItems,double precioTotal,double idOrden){
        this.fecha=new Date();
        this.listaDeItems=new ArrayList<>(listaDeItems);
        this.precioTotal=precioTotal;
        estado=EstadoOrden.PENDIENTE;
        this.idOrden=idOrden;
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

    public double getIdOrden(){
        return idOrden;
    }

    public Date getFecha(){
        return fecha;
    }
    
    public ArrayList<ItemCarrito> getListaItems(){
        return listaDeItems;
    }

    public double getPrecioTotal(){
        return precioTotal;
    }

    public EstadoOrden getEstadoOrden(){
        return estado;
    }


}
