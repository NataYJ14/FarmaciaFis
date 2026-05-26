package farmacia.model.Inventario;

import java.util.ArrayList;
import java.util.HashMap;

import farmacia.model.Producto.IProducto;
import farmacia.model.Producto.ProductoCompuesto;
import farmacia.model.Producto.ProductoIndividual;

public class Catalogo {
    HashMap<IProducto, Integer> stockProductos;

    public Catalogo(){
        stockProductos=new HashMap<>();
    }

    public void crearProductoIndividual(String nombreProducto, double precio, int stock){
        IProducto nuevoProducto= new ProductoIndividual(nombreProducto,precio);
        stockProductos.put(nuevoProducto, stock);
    }

    public void crearProductoCompuesto(String nombreProductoComp, String nombreProductoInd, double precio,int cantidadIndividual, int stock){
        IProducto nuevoProducto= new ProductoCompuesto(nombreProducto);
        stockProductos.put(nuevoProducto, stock);
    }

    public void agregarProductoComp(){
        
    }

    public void eliminarProducto(String nombreProducto){

    }

    public void modificarProducto(){
        //aqui no estoy segura de que deberiamos recibir como parametro, no solo se cambia el nombre
    }

    public ArrayList<IProducto> mostrarProductos(){
        return catalogoProductos;
    }

    public void cambiarStock(String nombreProducto, String nuevoStock){

    }

    //este lo añadí yo, no estoy segura si es privado o publico, pero es importante buscar productos
    public IProducto bucarProducto(String nombreProducto){
        IProducto aaa= new ProductoCompuesto(nombreProducto);
        return aaa; //esto esta RE MAL, pero no me gusta q aparezcan errores :p
    } 
}
