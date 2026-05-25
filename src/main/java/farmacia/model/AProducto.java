package farmacia.model;

public abstract class AProducto implements IProducto {

    protected String nombreProducto;

    public AProducto(String nombre) {
        this.nombreProducto = nombre;
    }

    @Override
    public String getNombreProducto() {
        return nombreProducto;
    }

}
