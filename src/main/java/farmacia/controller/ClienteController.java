package farmacia.controller;

import farmacia.model.Usuario.Cliente;
import farmacia.model.Ventas.EstadoOrden;
import farmacia.model.Ventas.ItemCarrito;
import farmacia.model.Ventas.Orden;

import java.util.ArrayList;
import java.util.HashMap;

import farmacia.model.Inventario.Catalogo;
import farmacia.model.Producto.IProducto;

public class ClienteController {
    private Cliente cliente;
    private Catalogo catalogo;

    public ClienteController(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    public void crearCliente(String nombreCliente, int edad, String cedula) {
        cliente = new Cliente(nombreCliente, edad, cedula);
    }

    public boolean agregarProductoACarrito(String nombreProducto,int cantidad) {
        if (cliente == null) {
            return false;
        }
        IProducto producto = catalogo.buscarProducto(nombreProducto);
        if (producto == null) {
            return false;
        }
        int stock = catalogo.consultarStock(nombreProducto);
        ItemCarrito itemExistente = buscarItemCarrito(nombreProducto);
        // SI YA EXISTE
        if (itemExistente != null) {
            int cantidadNueva = itemExistente.getCantidadProducto()+ cantidad;
            if (cantidadNueva > stock) {
                return false;
            }
            for (int i = 0; i < cantidad; i++) {
                itemExistente.agregarUnidad();
            }
            return true;
        }
        // SI NO EXISTE
        if (stock < cantidad) {
            return false;
        }
        ItemCarrito item = new ItemCarrito(producto,cantidad);
        cliente.agregarAlCarrito(item);
        return true;
    }

    public boolean disminuirCantidad(String nombreProducto) {
        ItemCarrito item = buscarItemCarrito(nombreProducto);
        item.eliminarUnidad();
        if (item.getCantidadProducto() <= 0) {
            cliente.eliminarDelCarrito(item);
            return true;
        }else{
            return false;
        }
        
    }

    public boolean aumentarCantidad(String nombreProducto) {
        ItemCarrito item = buscarItemCarrito(nombreProducto);
        int stock = catalogo.consultarStock(nombreProducto);
        if (item.getCantidadProducto() < stock) {
            item.agregarUnidad();
            return true;
        }else{
            return false;
        }
    }

    public void vaciarCarrito() {
        cliente.vaciarCarrito();
    }

    public ArrayList<ItemCarrito> verCarrito() {
        return cliente.getCarrito().mostrarCarrito();
    }

    public double obtenerTotalCarrito() {
        return cliente.getCarrito().getPrecioActual();
    }

    public ArrayList<Orden> verOrdenes() {
        return cliente.verOrdenes();
    }

    public boolean cancelarOrden(int idOrden) {
        Orden orden = cliente.buscarOrden(idOrden);
        if (orden == null) {
            return false;
        }
        if (orden.getEstadoOrden() == EstadoOrden.CANCELADA) {
            return false;
        }
        for (ItemCarrito item : orden.getListaItems()) {
            catalogo.aumentarStock(item.getProducto(), item.getCantidadProducto());
        }
        orden.cancelarOrden();
        return true;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public ItemCarrito buscarItemCarrito(String nombreProducto) {
        for (ItemCarrito item : cliente.getCarrito().mostrarCarrito()) {
            if (item.getProducto().getNombreProducto().equalsIgnoreCase(nombreProducto)) {
                return item;
            }
        }
        return null;
    }

    public HashMap<IProducto, Integer> getCatalogo(){
        return catalogo.getStockProductos();
    }


}
