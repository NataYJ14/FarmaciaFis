package farmacia.model.Producto;

import java.util.Objects;

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

    @Override
    public double getPrecio() {
        return precio;
    }

    public void setNombreProducto(String nuevoNombre) {
        this.nombreProducto = nuevoNombre;
    }

    public void setPrecio(double precioNuevo) {
        this.precio = precioNuevo;
    }

    //estos dos métodos son para q el hashmap funcione y si hacen un objeto dos veces, no cause error
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null ||
                getClass() != obj.getClass())
            return false;

        AProducto producto = (AProducto) obj;

        return Objects.equals(
                nombreProducto,
                producto.nombreProducto);
    }

    public int hashCode() {
        return Objects.hash(nombreProducto);
    }

}
