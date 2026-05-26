package farmacia.model.Inventario;

import java.util.HashMap;

import farmacia.model.Producto.IProducto;
import farmacia.model.Producto.ProductoCompuesto;
import farmacia.model.Producto.ProductoIndividual;

public class Catalogo {
    private HashMap<IProducto, Integer> stockProductos;

    public Catalogo() {
        stockProductos = new HashMap<>();
    }

    public void crearProductoIndividual(String nombreProducto, double precio, int stock) {
        IProducto nuevoProducto = new ProductoIndividual(nombreProducto, precio);
        stockProductos.put(nuevoProducto, stock);
    }

    public void crearProductoCompuesto(String nombreProductoComp, String nombreProductoInd, double precioInd,
            int cantidadIndividual, int stockComp, int stockInd) {
        ProductoCompuesto nuevoProductoComp = new ProductoCompuesto(nombreProductoComp);
        ProductoIndividual nuevoProductoInd = new ProductoIndividual(nombreProductoInd, precioInd);
        nuevoProductoComp.agregarProdIndividual(nuevoProductoInd, cantidadIndividual);
        stockProductos.put(nuevoProductoComp, stockComp);
        stockProductos.put(nuevoProductoInd, stockInd);
    }

    public void eliminarProducto(String nombreProducto) {
        IProducto productoEliminar = null;
        productoEliminar = bucarProducto(nombreProducto);
        if (productoEliminar != null) {
            stockProductos.remove(productoEliminar);
        }
    }

    public void modificarNombreProducto(String nombreProducto, String nuevoNombre) {
        IProducto productoModificar = null;
        productoModificar = bucarProducto(nombreProducto);
        if (productoModificar != null) {
            productoModificar.setNombreProducto(nuevoNombre);
        }
    }

    public void modificarStock(String nombreProducto, int nuevoStock) {
        IProducto productoModificar = null;
        productoModificar = bucarProducto(nombreProducto);
        if (productoModificar != null) {
            stockProductos.put(productoModificar, nuevoStock);
        }
    }

    public void modificarCantidadProducto(String nombreProdComp, int nuevaCantidad) {
        ProductoCompuesto productoModificar = null;
        productoModificar = (ProductoCompuesto) bucarProducto(nombreProdComp);
        if (productoModificar != null) {
            productoModificar.setCantidadProducto(nuevaCantidad);
        }
    }

    public IProducto bucarProducto(String nombreProducto) {
        for (IProducto producto : stockProductos.keySet()) {
            if (producto.getNombreProducto().equalsIgnoreCase(nombreProducto)) {
                return producto;
            }
        }
        return null;
    }

    public int consultarStock(String nombreProducto) {
        IProducto result=bucarProducto(nombreProducto);
        return stockProductos.get(result);
    }
}
