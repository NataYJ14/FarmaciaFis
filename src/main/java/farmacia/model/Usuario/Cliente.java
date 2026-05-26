package farmacia.model.Usuario;

import java.util.ArrayList;

import farmacia.model.Ventas.CarritoDeCompra;
import farmacia.model.Ventas.ItemCarrito;
import farmacia.model.Ventas.Orden;

public class Cliente {
    private String nombreCliente;
    private int edad;
    private String cedula;
    private CarritoDeCompra carrito;
    private ArrayList<Orden> ordenes;

    // no se bien como inicializar el carrito ni las ordenes, pero probablemente van
    // acá
    public Cliente(String nombreCliente, int edad, String cedula) {
        this.nombreCliente = nombreCliente;
        this.edad = edad;
        this.cedula = cedula;
        this.carrito = new CarritoDeCompra();
        this.ordenes= new ArrayList<>();
    }

    public ArrayList<Orden> verOrdenes() {
        return ordenes;
    }

    public void agregarAlCarrito(ItemCarrito item) {
        carrito.agregarProducto(item);
    }

    public void eliminarDelCarrito(ItemCarrito item) {
        carrito.quitarProducto(item);
    }

    public void vaciarCarrito() {
        carrito.vaciarCarrito();
    }

    public void realizarCompra(Orden nuevaOrden) {
        ordenes.add(nuevaOrden);
    }

    public void cancelarOrden(Orden ordenACancelar) {
        Orden orden=buscarOrden(ordenACancelar.getIdOrden());
        orden.cancelarOrden();
    }

    public Orden buscarOrden(double idOrden) {
        for (Orden orden : ordenes) {
            if (orden.getIdOrden() == idOrden) {
                return orden;
            }
        }
        return null;
    }

    public CarritoDeCompra getCarrito(){
        return carrito;
    }
}
