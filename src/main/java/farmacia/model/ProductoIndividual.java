package farmacia.model;

public class ProductoIndividual extends AProducto{

    private double precioUnitario;

    public ProductoIndividual(String nombre, double precioUnitario) {
        super(nombre);
        this.precioUnitario = precioUnitario;
    }

    @Override
    public double calcularPrecio() { //sería más como mostrar precio
        return precioUnitario;
    }

}
