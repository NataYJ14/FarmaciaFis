package farmacia.model.Ventas;

import java.util.ArrayList;

public class CarritoDeCompra {
    private double precioActual;
    private ArrayList<ItemCarrito> productos;

    public CarritoDeCompra() {
        productos = new ArrayList<>();
    }

    public void agregarProducto(ItemCarrito item){
        productos.add(item);
        precioActual=actualizarPrecioActual();
    }

    public void quitarProducto(ItemCarrito item){
        productos.remove(item);
        precioActual=actualizarPrecioActual();
    }

    public ArrayList<ItemCarrito> mostrarCarrito(){
        return productos;
    }

    public Orden generarOrden(){
        Orden nuevaOrden= new Orden(productos, precioActual);
        return nuevaOrden;
    }

    private double actualizarPrecioActual(){
        double total=0;
        for(int i= 0;i<productos.size();i++){
            total+=productos.get(i).getSubTotal();
        }
        return total;
    }

    public double getPrecioActual(){
        return precioActual;
    }
}
