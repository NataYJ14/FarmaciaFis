package farmacia.controller;

import java.util.ArrayList;

import farmacia.model.API.IProducto;
import farmacia.model.ProductoCompuesto;

public class Catalogo {
    private ArrayList<IProducto> catalogoProductos;
    //private ArrayList<int> listaStock; //no se como ponerle las dimensiones :c

    public Catalogo(){
        catalogoProductos=new ArrayList<>();
    }

    public void crearProducto(String nombreProducto){

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
