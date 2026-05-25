package farmacia.model;

import farmacia.model.API.IProducto;
import java.util.ArrayList;

public class ProductoCompuesto extends AProducto{
    private ArrayList<IProducto> subproductos;
    private double precioTotal;
    
    public ProductoCompuesto(String nombreProducto) {
        super(nombreProducto);
        subproductos = new ArrayList<>();
        this.precioTotal=calcularPrecio();
    }

    public void añadirSubproducto(IProducto producto) {
        subproductos.add(producto);
        this.precioTotal=calcularPrecio();
    }

    @Override
    public double calcularPrecio() {
        double total = 0;
        for(IProducto producto : subproductos) {
            total += producto.calcularPrecio();
        }
        return total;
    }
}
