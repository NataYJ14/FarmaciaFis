package farmacia.system;

import farmacia.model.Inventario.Catalogo;

public class Sistema {
    public static Catalogo
    inicializarCatalogo() {

        Catalogo catalogo =
            new Catalogo();

        catalogo.crearProductoIndividual(
            "Acetaminofen",
            500,
            100
        );

        catalogo.crearProductoIndividual(
            "Ibuprofeno",
            800,
            50
        );

        catalogo.crearProductoCompuesto(
            "Caja de Pañales", 
            "Pañales", 
            2000, 
            30, 
            10, 
            30
        );

        return catalogo;
    }

}
