package farmacia.model;

public class ItemCarrito {
    private IProducto iProducto;
    private int cantidadProducto;

    public ItemCarrito(IProducto producto, int cantidad) {
        this.iProducto = producto;
        this.cantidadProducto = cantidad;
    }

    public double calcularSubtotal() {
        return iProducto.calcularPrecio() * cantidadProducto;
    }

    public IProducto getProducto() {
        return iProducto;
    }
}
