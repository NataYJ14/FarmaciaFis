package farmacia.model.Producto;

public abstract class AProducto implements IProducto {

    protected String nombreProducto;
    protected double precio;

    public AProducto(String nombre) {
        this.nombreProducto = nombre;
    }

    @Override
    public String getNombreProducto() {
        return nombreProducto;
    }

    public double getPrecio(){
        return precio;
    }

}
