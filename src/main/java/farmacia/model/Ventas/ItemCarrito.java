package farmacia.model.Ventas;

import farmacia.model.Producto.IProducto;

public class ItemCarrito {
    private IProducto iProducto;
    private int cantidadProducto;
    private double subTotal;

    public ItemCarrito(IProducto producto, int cantidad) {
        this.iProducto = producto;
        this.cantidadProducto = cantidad;
    }

    //no se si privado o publico
    private double calcularSubtotal() {
        subTotal= iProducto.calcularPrecio() * cantidadProducto;
        return subTotal;
    }

    public void agregarUnidad(){
        cantidadProducto+=1;
        subTotal=calcularSubtotal();
    }

    public void eliminarUnidad(){
        if(cantidadProducto>0){
            cantidadProducto-=1;
        }
        subTotal=calcularSubtotal();
    }

    public IProducto getProducto() {
        return iProducto;
    }

    public int getCantidadProducto(){
        return cantidadProducto;
    }

    public double getSubTotal(){
        return subTotal;
    }
}
