package farmacia.controller;

import farmacia.model.Inventario.Catalogo;
import farmacia.model.Usuario.Cliente;
import farmacia.model.Ventas.CarritoDeCompra;
import farmacia.model.Ventas.ItemCarrito;
import farmacia.model.Ventas.Orden;

import java.util.ArrayList;

public class VentasController {
    private Catalogo catalogo;
    private ClienteController clienteController;
    private int contadorOrdenes;
    public VentasController(Catalogo catalogo,ClienteController clienteController) {
        this.catalogo = catalogo;
        this.clienteController = clienteController;
        contadorOrdenes = 1;
    }

    public boolean confirmarCompra() {
        Cliente cliente = clienteController.getCliente();
        if (cliente == null) {
            return false;
        }
        CarritoDeCompra carrito = cliente.getCarrito();
        if (carrito.mostrarCarrito().isEmpty()) {
            return false;
        }
        ArrayList<ItemCarrito> items = carrito.mostrarCarrito();
        // VALIDAR STOCK
        for (ItemCarrito item : items) {
            int stock = catalogo.consultarStock(item.getProducto().getNombreProducto());
            if (stock < item.getCantidadProducto()) {
                return false;
            }
        }
        // GENERAR ORDEN
        Orden orden = carrito.generarOrden(contadorOrdenes);
        contadorOrdenes++;
        // CONFIRMAR
        orden.confirmarOrden();
        // REDUCIR STOCK
        for (ItemCarrito item : items) {
            catalogo.reducirStock(item.getProducto(),item.getCantidadProducto());
        }
        // GUARDAR ORDEN
        cliente.realizarCompra(orden);
        // LIMPIAR CARRITO
        cliente.vaciarCarrito();
        return true;
    }
}
