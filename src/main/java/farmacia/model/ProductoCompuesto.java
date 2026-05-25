package farmacia.model;

import java.util.ArrayList;

public class ProductoCompuesto extends AProducto{
    private ArrayList<IProducto> subproductos;
    private double precioTotal;
    public ProductoCompuesto(String nombreProducto) {
        super(nombreProducto);
        subproductos = new ArrayList<>();
    }
    public void añadirSubproducto(IProducto producto) {
        subproductos.add(producto);
    }
    @Override
    public double calcularPrecio() {
        //desarrollar calcular el precio aqui tambien
        return precioTotal;
    }
}
