package farmacia.model.Producto;

public class ProductoCompuesto extends AProducto{
    private ProductoIndividual producto;
    private int cantidadProducto;

    public ProductoCompuesto(String nombreProducto) {
        super(nombreProducto);
    }

    public void agregarProdIndividual(ProductoIndividual productoIndividual, int cantidad){
        this.producto=productoIndividual;
        this.cantidadProducto=cantidad;
        this.precio=calcularPrecio();
    }

    public double calcularPrecio() {
        double total;
        total= producto.getPrecio()*cantidadProducto;
        return total;
    }

    public void setCantidadProducto(int nuevaCantidad){
        this.cantidadProducto=nuevaCantidad;
        this.precio=calcularPrecio();
    }
}
