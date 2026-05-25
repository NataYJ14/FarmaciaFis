package farmacia.model.Usuario;

import java.util.ArrayList;

import farmacia.model.Ventas.CarritoDeCompra;
import farmacia.model.Ventas.Orden;

public class Cliente {
    private String nombreCliente;
    private String edad;
    private String cedula;
    private CarritoDeCompra carrito;
    private ArrayList<Orden> ordenes;

    //no se bien como inicializar el carrito ni las ordenes, pero probablemente van acá
    public Cliente(String nombreCliente, String edad, String cedula){ 
        this.nombreCliente=nombreCliente;
        this.edad=edad;
        this.cedula=cedula;
    }

    public ArrayList<Orden> verOrdenes(){
        return ordenes;
    }

    public void realizarCompra(){
        //probablemente añadir algo a la lista de ordenes, aun no se como
    }
}
