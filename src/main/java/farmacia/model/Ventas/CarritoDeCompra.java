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
        actualizarPrecioActual();
    }

    public void quitarProducto(ItemCarrito item){
        productos.remove(item);
        actualizarPrecioActual();
    }

    public ArrayList<ItemCarrito> mostrarCarrito(){
        return productos;
    }

    public void generarOrden(){
        //generar la orden con los datos (new Orden(datos))
    }

    private void actualizarPrecioActual(){
        //idk volver a calcular el precio
    }
}
