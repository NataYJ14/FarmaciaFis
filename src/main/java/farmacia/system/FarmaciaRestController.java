package farmacia.system;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import farmacia.controller.ClienteController;
import farmacia.controller.VentasController;
import farmacia.model.Inventario.*;
import farmacia.model.Producto.*;
import farmacia.model.Usuario.*;
import farmacia.model.Ventas.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // permite peticiones desde el frontend React
public class FarmaciaRestController {

    // ----------------------------------------------------------------
    // Las mismas clases Java que ya tienes — no cambia nada en ellas
    // ----------------------------------------------------------------
    private final Catalogo catalogo;
    private final ClienteController clienteController;
    private final VentasController ventasController;

    // Spring crea este bean una sola vez (singleton), así que el estado
    // (cliente, carrito, órdenes) persiste durante toda la sesión del servidor.
    public FarmaciaRestController() {
        this.catalogo = new Catalogo();
        this.clienteController = new ClienteController(catalogo);
        this.ventasController = new VentasController(catalogo, clienteController);
        inicializarCatalogo();
    }

    // ----------------------------------------------------------------
    // Productos iniciales de la farmacia
    // ----------------------------------------------------------------
    private void inicializarCatalogo() {
        // Productos individuales
        catalogo.crearProductoIndividual("Paracetamol 500mg", 1200, 100);
        catalogo.crearProductoIndividual("Ibuprofeno 400mg", 1800, 80);
        catalogo.crearProductoIndividual("Amoxicilina 500mg", 8500, 50);
        catalogo.crearProductoIndividual("Loratadina 10mg", 2500, 60);
        catalogo.crearProductoIndividual("Omeprazol 20mg", 3200, 45);
        catalogo.crearProductoIndividual("Metformina 500mg", 4100, 40);
        catalogo.crearProductoIndividual("Vitamina C 1000mg", 3800, 70);
        catalogo.crearProductoIndividual("Alcohol Antiséptico", 5500, 35);
        catalogo.crearProductoIndividual("Suero Oral", 2800, 55);
        catalogo.crearProductoIndividual("Termómetro Digital", 18000, 20);

        // Productos compuestos (un producto individual en cantidad)
        // crearProductoCompuesto(nombreComp, nombreInd, precioInd, cantidadInd,
        // stockComp, stockInd)
        catalogo.crearProductoCompuesto(
                "Kit Gripe", // nombre del compuesto
                "Paracetamol 500mg", // producto individual base
                1200, 3, // precio unitario, cantidad incluida
                15, 100 // stock del compuesto, stock del individual
        );
        catalogo.crearProductoCompuesto(
                "Pack Vitaminas",
                "Vitamina C 1000mg",
                3800, 2,
                20, 70);
        catalogo.crearProductoCompuesto(
                "Kit Primeros Auxilios",
                "Alcohol Antiséptico",
                5500, 2,
                10, 35);
    }

    // ================================================================
    // CATÁLOGO
    // GET /api/catalogo → devuelve todos los productos con su stock
    // ================================================================
    @GetMapping("/catalogo")
    public ResponseEntity<List<Map<String, Object>>> getCatalogo() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map.Entry<IProducto, Integer> entry : catalogo.getStockProductos().entrySet()) {
            resultado.add(productoAMapa(entry.getKey(), entry.getValue()));
        }
        return ResponseEntity.ok(resultado);
    }

    // ================================================================
    // CLIENTE
    // POST /api/cliente → crear cliente
    // GET /api/cliente → obtener datos del cliente actual
    // ================================================================
    @PostMapping("/cliente")
    public ResponseEntity<Map<String, Object>> crearCliente(@RequestBody Map<String, Object> body) {
        String nombre = (String) body.get("nombre");
        int edad = ((Number) body.get("edad")).intValue();
        String cedula = (String) body.get("cedula");

        clienteController.crearCliente(nombre, edad, cedula);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("mensaje", "Cliente creado");
        resp.put("cliente", clienteAMapa(clienteController.getCliente()));
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/cliente")
    public ResponseEntity<?> getCliente() {
        Cliente c = clienteController.getCliente();
        if (c == null)
            return ResponseEntity.status(404).body(error("No hay cliente registrado"));
        return ResponseEntity.ok(clienteAMapa(c));
    }

    // ================================================================
    // CARRITO
    // GET /api/carrito → ver carrito + total
    // POST /api/carrito/agregar → agregar producto
    // PUT /api/carrito/aumentar/{nombre} → +1 unidad
    // PUT /api/carrito/disminuir/{nombre} → -1 unidad (elimina si llega a 0)
    // DELETE /api/carrito → vaciar carrito
    // ================================================================
    @GetMapping("/carrito")
public ResponseEntity<?> getCarrito() {
    if (clienteController.getCliente() == null)
        return ResponseEntity.status(400).body(error("No hay cliente registrado"));

    List<Map<String, Object>> items = new ArrayList<>();
    double totalAcumulado = 0.0; // 🔥 1. Empezamos el contador en cero absoluto

    for (ItemCarrito item : clienteController.verCarrito()) {
        items.add(itemAMapa(item));
        
        // 🔥 2. Sumamos el subtotal de cada ítem en caliente (cantidad * precio)
        totalAcumulado += item.getSubtotal(); 
    }

    Map<String, Object> resp = new LinkedHashMap<>();
    resp.put("items", items);
    
    // 🔥 3. Enviamos el total fresco recién calculado en lugar del método viejo
    resp.put("total", totalAcumulado); 
    
    return ResponseEntity.ok(resp);
}

    @PostMapping("/carrito/agregar")
    public ResponseEntity<Map<String, Object>> agregarAlCarrito(@RequestBody Map<String, Object> body) {
        String nombre = (String) body.get("nombreProducto");
        int cantidad = ((Number) body.get("cantidad")).intValue();

        boolean ok = clienteController.agregarProductoACarrito(nombre, cantidad);
        if (!ok)
            return ResponseEntity.status(400).body(error("Sin stock disponible o producto no encontrado"));

        return ResponseEntity.ok(carritoActual());
    }

    @PutMapping("/carrito/aumentar/{nombre}")
    public ResponseEntity<?> aumentarCantidad(@PathVariable String nombre) {
        boolean ok = clienteController.aumentarCantidad(nombre);
        if (!ok)
            return ResponseEntity.status(400).body(error("Stock máximo alcanzado"));

        // 1. Forzamos la actualización interna del precio en el objeto real
        CarritoDeCompra carrito = clienteController.getCliente().getCarrito();
        carrito.actualizarPrecioActual();

        // 🔄 CORRECCIÓN: Usamos tu método carritoActual() que ya tiene el formato
        // con las llaves "items" y "total" que tu frontend sabe leer.
        return ResponseEntity.ok(carritoActual());
    }

    @PutMapping("/carrito/disminuir/{nombre}")
    public ResponseEntity<?> disminuirCantidad(@PathVariable String nombre) {
        ItemCarrito item = clienteController.buscarItemCarrito(nombre);
        if (item == null)
            return ResponseEntity.status(404).body(error("Producto no está en el carrito"));

        clienteController.disminuirCantidad(nombre);

        // 1. Forzamos la actualización interna del precio en el objeto real
        CarritoDeCompra carrito = clienteController.getCliente().getCarrito();
        carrito.actualizarPrecioActual();

        // 🔄 CORRECCIÓN: Devolvemos el mapa con la estructura exacta que espera React
        return ResponseEntity.ok(carritoActual());
    }

    @DeleteMapping("/carrito")
    public ResponseEntity<Map<String, Object>> vaciarCarrito() {
        clienteController.vaciarCarrito();
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("mensaje", "Carrito vaciado");
        return ResponseEntity.ok(resp);
    }

    // ================================================================
    // COMPRA
    // POST /api/compra/confirmar → confirmar compra y generar orden
    // ================================================================
    @PostMapping("/compra/confirmar")
public ResponseEntity<Map<String, Object>> confirmarCompra() {
    // 1. Primero guardamos los ítems del carrito actual ANTES de que confirmarCompra() lo vacíe
    List<ItemCarrito> itemsEnCarrito = new ArrayList<>(clienteController.verCarrito());

    // 2. Se ejecuta tu lógica original de confirmación
    boolean ok = ventasController.confirmarCompra();
    if (!ok)
        return ResponseEntity.status(400).body(error("No se pudo confirmar: carrito vacío o sin stock"));

    // 3. Obtenemos la última orden recién creada
    ArrayList<Orden> ordenes = clienteController.verOrdenes();
    Orden ultima = ordenes.get(ordenes.size() - 1);

    // 4. 🔥 EL TRUCO: Calculamos el total real sumando los subtotales de los ítems
    double totalRealOrden = 0.0;
    for (ItemCarrito item : itemsEnCarrito) {
        totalRealOrden += item.getSubtotal(); // cantidad * precio
    }

    // 5. Corregimos el total en el objeto Orden para que persista correctamente
    ultima.setTotal(totalRealOrden); 

    // 6. Armamos la respuesta para el frontend con el total corregido
    Map<String, Object> resp = new LinkedHashMap<>();
    resp.put("mensaje", "Compra confirmada");
    resp.put("orden", ordenAMapa(ultima)); // Tu método ordenAMapa leerá el nuevo total corregido
    
    return ResponseEntity.ok(resp);
}

    // ================================================================
    // ÓRDENES
    // GET /api/ordenes → ver todas las órdenes del cliente
    // PUT /api/ordenes/{id}/cancelar → cancelar una orden
    // ================================================================
    @GetMapping("/ordenes")
    public ResponseEntity<?> getOrdenes() {
        if (clienteController.getCliente() == null)
            return ResponseEntity.status(400).body(error("No hay cliente registrado"));

        List<Map<String, Object>> lista = new ArrayList<>();
        for (Orden o : clienteController.verOrdenes()) {
            lista.add(ordenAMapa(o));
        }
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/ordenes/{id}/cancelar")
    public ResponseEntity<Map<String, Object>> cancelarOrden(@PathVariable int id) {
        boolean ok = clienteController.cancelarOrden(id);
        if (!ok)
            return ResponseEntity.status(400).body(error("Orden no encontrada o ya cancelada"));

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("mensaje", "Orden " + id + " cancelada");
        return ResponseEntity.ok(resp);
    }

    // ================================================================
    // HELPERS — convierten objetos Java en mapas serializables a JSON
    // ================================================================

    private Map<String, Object> productoAMapa(IProducto p, int stock) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nombre", p.getNombreProducto());
        m.put("precio", p.getPrecio());
        m.put("stock", stock);
        m.put("tipo", p instanceof ProductoCompuesto ? "compuesto" : "individual");
        return m;
    }

    private Map<String, Object> itemAMapa(ItemCarrito item) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nombre", item.getProducto().getNombreProducto());
        m.put("precio", item.getProducto().getPrecio());
        m.put("cantidad", item.getCantidadProducto());
        m.put("subtotal", item.getSubtotal());
        return m;
    }

    private Map<String, Object> ordenAMapa(Orden o) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (ItemCarrito item : o.getListaItems())
            items.add(itemAMapa(item));

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", (int) o.getIdOrden());
        m.put("items", items);
        m.put("total", o.getPrecioTotal());
        m.put("estado", o.getEstadoOrden().toString());
        return m;
    }

    private Map<String, Object> clienteAMapa(Cliente c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nombre", c.getNombreCliente());
        m.put("edad", c.getEdad());
        m.put("cedula", c.getCedula());
        return m;
    }

    private Map<String, Object> carritoActual() {
        List<Map<String, Object>> itemsList = new ArrayList<>();
        double totalAcumulado = 0.0; // 🔥 Empezamos en cero estricto

        // Recorremos la lista real de ítems en tu carrito
        for (ItemCarrito item : clienteController.verCarrito()) {
            itemsList.add(itemAMapa(item));

            // 🔥 AQUÍ ESTÁ EL TRUCO: Sumamos el subtotal (que ya da 5600), NO el precio
            // base
            totalAcumulado += item.getSubtotal();
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("items", itemsList);

        // 🔥 ENVIAMOS EL TOTAL REAL ACUMULADO
        respuesta.put("total", totalAcumulado);

        return respuesta;
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("error", msg);
        return m;
    }
}
