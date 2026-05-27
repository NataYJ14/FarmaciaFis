package farmacia.model.Ventas;

import java.util.ArrayList;

public class CarritoDeCompra {
    private double precioActual;
    private ArrayList<ItemCarrito> productos;

    public CarritoDeCompra() {
        productos = new ArrayList<>();
    }

    public void agregarProducto(ItemCarrito nuevoItem) {
        // 1. Buscamos si el producto ya existía en el carrito
        ItemCarrito existente = buscarItemPorNombre(nuevoItem.getProducto().getNombreProducto());

        if (existente != null) {
            existente.setCantidad(existente.getCantidadProducto() + nuevoItem.getCantidadProducto());
        } else {
            productos.add(nuevoItem);
        }
        this.precioActual = actualizarPrecioActual();
    }

    public void quitarProducto(ItemCarrito itemAQuitar) {
        ItemCarrito existente = buscarItemPorNombre(itemAQuitar.getProducto().getNombreProducto());

        if (existente != null) {
            if (existente.getCantidadProducto() > 1) {
                existente.setCantidad(existente.getCantidadProducto() - 1);
            } else {
                productos.remove(existente);
            }
        }
        this.precioActual = actualizarPrecioActual();
    }

    public void vaciarCarrito(){
        productos.clear();
        precioActual=0;
    }

    public ArrayList<ItemCarrito> mostrarCarrito(){
        return productos;
    }

    public Orden generarOrden(double idOrden){
        Orden nuevaOrden= new Orden(productos, precioActual,idOrden);
        return nuevaOrden;
    }

    public double actualizarPrecioActual(){
        double total=0;
        for(int i= 0;i<productos.size();i++){
            total+=productos.get(i).getSubtotal();
        }
        return total;
    }

    public double getTotal(){
        return precioActual;
    }

    private ItemCarrito buscarItemPorNombre(String nombre) {
        for (ItemCarrito prod : productos) {
            if (prod.getProducto().getNombreProducto().equalsIgnoreCase(nombre)) {
                return prod;
            }
        }
        return null; // No se encontró
    }
}
