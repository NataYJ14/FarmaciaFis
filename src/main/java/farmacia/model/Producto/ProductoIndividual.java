package farmacia.model.Producto;

public class ProductoIndividual extends AProducto{

    public ProductoIndividual(String nombre, double precioUnitario) {
        super(nombre);
        this.precio = precioUnitario;
    }

}
