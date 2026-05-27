package farmacia.model.Ventas;

import farmacia.model.Producto.IProducto;

public class ItemCarrito {
    private IProducto iProducto;
    private int cantidadProducto;

    public ItemCarrito(IProducto producto, int cantidad) {
        this.iProducto = producto;
        this.cantidadProducto = cantidad;
    }

    // Eliminamos las variables rígidas y hacemos que se calcule EN EL MOMENTO
    public double getSubtotal() {
        return iProducto.getPrecio() * cantidadProducto;
    }

    public void agregarUnidad(){
        cantidadProducto += 1;
    }

    public void eliminarUnidad(){
        if(cantidadProducto > 0){
            cantidadProducto -= 1;
        }
    }

    public IProducto getProducto() { return iProducto; }
    public int getCantidadProducto(){ return cantidadProducto; }
    
    public void setCantidad(int cantidad){
        this.cantidadProducto = cantidad;
    }
}
