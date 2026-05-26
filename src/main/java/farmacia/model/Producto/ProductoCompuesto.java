package farmacia.model.Producto;

import java.util.ArrayList;

public class ProductoCompuesto extends AProducto{
    private ProductoIndividual producto;
    private int cantidadProducto;

    public ProductoCompuesto(String nombreProducto) {
        super(nombreProducto);

        this.precio=calcularPrecio();
    }

    @Override
    public double calcularPrecio() {
        double total = 0;
        
        return total;
    }

}
