import { useState, useEffect, useCallback } from "react";

// ================================================================
// CONFIGURACIÓN — cambia esto si el back corre en otro puerto
// ================================================================
const API = "http://localhost:8080/api";

const fmtCOP = (n) =>
  new Intl.NumberFormat("es-CO", {
    style: "currency", currency: "COP", minimumFractionDigits: 0,
  }).format(n);

// ================================================================
// CLIENTE HTTP — wrapper sobre fetch con manejo de errores
// ================================================================
async function api(method, path, body) {
  const opts = {
    method,
    headers: { "Content-Type": "application/json" },
  };
  if (body) opts.body = JSON.stringify(body);
  const res = await fetch(API + path, opts);
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Error en el servidor");
  return data;
}

const get = (path) => api("GET", path);
const post = (path, body) => api("POST", path, body);
const put = (path, body) => api("PUT", path, body);
const del = (path) => api("DELETE", path);

// ================================================================
// ESTILOS BASE
// ================================================================
const S = {
  page: {
    minHeight: "100vh",
    background: "var(--color-background-tertiary)",
    fontFamily: "var(--font-sans)",
    color: "var(--color-text-primary)",
  },
  nav: {
    display: "flex", alignItems: "center", justifyContent: "space-between",
    padding: "0 28px", height: 56,
    background: "var(--color-background-primary)",
    borderBottom: "0.5px solid var(--color-border-tertiary)",
    position: "sticky", top: 0, zIndex: 20,
  },
  navBrand: { fontWeight: 500, fontSize: 15, display: "flex", alignItems: "center", gap: 8 },
  navBtn: (active) => ({
    position: "relative", display: "flex", alignItems: "center", gap: 6,
    padding: "6px 12px", borderRadius: "var(--border-radius-md)",
    border: active ? "0.5px solid var(--color-border-secondary)" : "0.5px solid transparent",
    background: active ? "var(--color-background-secondary)" : "transparent",
    color: "var(--color-text-primary)", cursor: "pointer", fontSize: 13,
    fontFamily: "var(--font-sans)",
  }),
  card: {
    background: "var(--color-background-primary)",
    border: "0.5px solid var(--color-border-tertiary)",
    borderRadius: "var(--border-radius-lg)",
  },
  badge: (tipo) => {
    const map = {
      compuesto: ["var(--color-background-info)", "var(--color-text-info)", "var(--color-border-info)"],
      individual: ["var(--color-background-success)", "var(--color-text-success)", "var(--color-border-success)"],
      CONFIRMADA: ["var(--color-background-success)", "var(--color-text-success)", "var(--color-border-success)"],
      CANCELADA: ["var(--color-background-danger)", "var(--color-text-danger)", "var(--color-border-danger)"],
      PENDIENTE: ["var(--color-background-warning)", "var(--color-text-warning)", "var(--color-border-warning)"],
    };
    const [bg, text, border] = map[tipo] || map.individual;
    return {
      fontSize: 11, padding: "2px 9px", borderRadius: 99, whiteSpace: "nowrap",
      background: bg, color: text, border: `0.5px solid ${border}`,
    };
  },
};

// ================================================================
// COMPONENTE PRINCIPAL
// ================================================================
export default function App() {
  const [pagina, setPagina] = useState("registro");
  const [cliente, setCliente] = useState(null);
  const [catalogo, setCatalogo] = useState([]);
  const [carrito, setCarrito] = useState({ items: [], total: 0 });
  const [ordenes, setOrdenes] = useState([]);
  const [busqueda, setBusqueda] = useState("");
  const [notif, setNotif] = useState(null);
  const [cargando, setCargando] = useState(false);

  // ---- helpers ----
  const notificar = (msg, tipo = "ok") => {
    setNotif({ msg, tipo });
    setTimeout(() => setNotif(null), 3000);
  };

  const conCarga = async (fn) => {
    setCargando(true);
    try { await fn(); }
    catch (e) { notificar(e.message, "err"); }
    finally { setCargando(false); }
  };

  // ---- carga de datos desde el back ----
  const refrescarCatalogo = useCallback(async () => {
    const data = await get("/catalogo");
    setCatalogo(data);
  }, []);

  const refrescarCarrito = async () => {
  try {
    const res = await get("/carrito");
    
    // 🔥 FORZAMOS UN NUEVO ESTADO: Clonamos de forma estricta todo lo que viene de Java
    // Esto destruye cualquier intento de React de "ahorrar" renderizado.
    setCarrito(JSON.parse(JSON.stringify(res)));

  } catch (error) {
    console.error("Error al refrescar el carrito en el frontend:", error);
  }
};

  const refrescarOrdenes = useCallback(async () => {
    const data = await get("/ordenes");
    setOrdenes(data);
  }, []);

  // Al entrar a cada página, refresca los datos correspondientes
  useEffect(() => {
    if (!cliente) return;
    conCarga(refrescarCarrito);
    if (pagina === "catalogo") conCarga(refrescarCatalogo);
    if (pagina === "carrito") conCarga(refrescarCarrito);
    if (pagina === "ordenes") conCarga(refrescarOrdenes);
  }, [pagina, cliente]);

  // ================================================================
  // ACCIONES — cada una llama al back y refresca el estado local
  // ================================================================
  const registrarCliente = async (form) => {
    await conCarga(async () => {
      const data = await post("/cliente", form);
      setCliente(data.cliente);
      await refrescarCatalogo();
      await refrescarCarrito();
      setPagina("catalogo");
    });
  };

  const guardarCliente = async (form) => {
    // El back no expone PUT /cliente porque el modelo Java no lo tiene,
    // así que actualizamos solo el estado local del front.
    setCliente(form);
    notificar("Datos actualizados");
  };

  const agregarAlCarrito = async (nombre) => {
    await conCarga(async () => {
      await post("/carrito/agregar", {
        nombreProducto: nombre,
        cantidad: 1
      });
      await refrescarCarrito();
      notificar(`${nombre} agregado al carrito`);
    });
  };

  const agregarUnidad = async (nombre) => {
    await conCarga(async () => {
      // 1. Le avisamos al backend que aumente la cantidad
      await put(`/carrito/aumentar/${nombre}`);

      // 2. Traemos el carrito limpio usando tu función de confianza
      await refrescarCarrito();
    });
  };

  const quitarUnidad = async (nombre) => {
    await conCarga(async () => {
      // 1. Le avisamos al backend que disminuya la cantidad
      await put(`/carrito/disminuir/${nombre}`);

      // 2. Traemos el carrito limpio usando tu función de confianza
      await refrescarCarrito();
    });
  };

  const vaciarCarrito = async () => {
    await conCarga(async () => {
      await del("/carrito");
      setCarrito({ items: [], total: 0 });
    });
  };

  const confirmarCompra = async () => {
    await conCarga(async () => {
      const data = await post("/compra/confirmar");
      setCarrito({ items: [], total: 0 });
      await refrescarOrdenes();
      notificar(`¡${data.orden ? `Orden #${data.orden.id} confirmada!` : "Compra confirmada!"}`);
      setPagina("ordenes");
    });
  };

  const cancelarOrden = async (id) => {
    await conCarga(async () => {
      await put(`/ordenes/${id}/cancelar`);
      await refrescarOrdenes();
      notificar(`Orden #${id} cancelada`);
    });
  };

  const filtrados = catalogo.filter(p =>
    busqueda === "" || p.nombre.toLowerCase().includes(busqueda.toLowerCase())
  );

  // ================================================================
  // RENDER
  // ================================================================
  if (pagina === "registro") {
    return (
      <>
        <style>{globalStyles}</style>
        <RegisterPage onSubmit={registrarCliente} cargando={cargando} />
      </>
    );
  }

  const NAV = [
    { id: "catalogo", icon: "ti-layout-grid", label: "Catálogo" },
    { id: "carrito", icon: "ti-shopping-cart", label: "Carrito", badge: carrito.items.length },
    { id: "perfil", icon: "ti-user", label: "Mis datos" },
    { id: "ordenes", icon: "ti-receipt", label: "Órdenes" },
  ];

  return (
    <div style={S.page}>
      <style>{globalStyles}</style>

      {/* BARRA DE CARGA */}
      {cargando && (
        <div style={{
          position: "fixed", top: 0, left: 0, right: 0, height: 2, zIndex: 100,
          background: "var(--color-border-info)",
          animation: "progress 1.2s ease infinite",
        }} />
      )}

      {/* NAV */}
      <nav style={S.nav}>
        <span style={S.navBrand}>
          <i className="ti ti-pill" style={{ fontSize: 20 }} aria-hidden="true" />
          Farmacia
        </span>
        <div style={{ display: "flex", gap: 4 }}>
          {NAV.map(item => (
            <button key={item.id} onClick={() => setPagina(item.id)} style={S.navBtn(pagina === item.id)}>
              <i className={`ti ${item.icon}`} style={{ fontSize: 15 }} aria-hidden="true" />
              {item.label}
              {item.badge > 0 && (
                <span style={{
                  position: "absolute", top: 3, right: 3,
                  background: "var(--color-background-danger)",
                  color: "var(--color-text-danger)",
                  borderRadius: 99, fontSize: 10, padding: "1px 5px",
                  fontWeight: 500, lineHeight: 1.6,
                }}>{item.badge}</span>
              )}
            </button>
          ))}
        </div>
        <span style={{ fontSize: 13, color: "var(--color-text-secondary)" }}>
          Hola, <strong>{cliente?.nombre}</strong>
        </span>
      </nav>

      {/* PÁGINAS */}
      {pagina === "catalogo" && (
        <CatalogoPage
          filtrados={filtrados}
          busqueda={busqueda}
          setBusqueda={setBusqueda}
          onAgregar={agregarAlCarrito}
          cargando={cargando}
        />
      )}
      {pagina === "carrito" && (
        <CarritoPage
          carrito={carrito}
          onAdd={agregarUnidad}
          onRemove={quitarUnidad}
          onVaciar={vaciarCarrito}
          onConfirmar={confirmarCompra}
          irCatalogo={() => setPagina("catalogo")}
          cargando={cargando}
        />
      )}
      {pagina === "perfil" && (
        <PerfilPage cliente={cliente} onSave={guardarCliente} />
      )}
      {pagina === "ordenes" && (
        <OrdenesPage ordenes={ordenes} onCancelar={cancelarOrden} cargando={cargando} />
      )}

      {/* NOTIFICACIÓN */}
      {notif && (
        <div style={{
          position: "fixed", bottom: 24, right: 24, zIndex: 999,
          background: notif.tipo === "err" ? "var(--color-background-danger)" : "var(--color-background-success)",
          color: notif.tipo === "err" ? "var(--color-text-danger)" : "var(--color-text-success)",
          border: `0.5px solid ${notif.tipo === "err" ? "var(--color-border-danger)" : "var(--color-border-success)"}`,
          padding: "12px 20px", borderRadius: "var(--border-radius-md)",
          fontWeight: 500, fontSize: 14, display: "flex", alignItems: "center", gap: 8,
        }}>
          <i className={`ti ${notif.tipo === "err" ? "ti-alert-circle" : "ti-circle-check"}`}
            style={{ fontSize: 16 }} aria-hidden="true" />
          {notif.msg}
        </div>
      )}
    </div>
  );
}

// ================================================================
// REGISTRO
// ================================================================
function RegisterPage({ onSubmit, cargando }) {
  const [form, setForm] = useState({ nombre: "", edad: "", cedula: "" });
  const [errors, setErrors] = useState({});

  const validate = () => {
    const e = {};
    if (!form.nombre.trim()) e.nombre = "Requerido";
    if (!form.edad || isNaN(form.edad) || +form.edad < 1) e.edad = "Edad inválida";
    if (!form.cedula.trim()) e.cedula = "Requerido";
    return e;
  };

  const handleSubmit = () => {
    const e = validate();
    if (Object.keys(e).length) return setErrors(e);
    onSubmit({ nombre: form.nombre.trim(), edad: +form.edad, cedula: form.cedula.trim() });
  };

  const campos = [
    { key: "nombre", label: "Nombre completo", type: "text", placeholder: "Ej. María García" },
    { key: "edad", label: "Edad", type: "number", placeholder: "Ej. 28" },
    { key: "cedula", label: "Cédula", type: "text", placeholder: "Ej. 1234567890" },
  ];

  return (
    <div style={{
      minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center",
      background: "var(--color-background-tertiary)", padding: 24,
    }}>
      <div style={{ ...S.card, padding: "44px 48px", width: "100%", maxWidth: 420 }}>
        <div style={{ textAlign: "center", marginBottom: 36 }}>
          <div style={{
            width: 52, height: 52, borderRadius: "var(--border-radius-lg)",
            background: "var(--color-background-secondary)",
            border: "0.5px solid var(--color-border-tertiary)",
            display: "flex", alignItems: "center", justifyContent: "center",
            margin: "0 auto 16px",
          }}>
            <i className="ti ti-pill" style={{ fontSize: 26 }} aria-hidden="true" />
          </div>
          <h1 style={{ fontSize: 22, fontWeight: 500, margin: 0 }}>Farmacia</h1>
          <p style={{ color: "var(--color-text-secondary)", fontSize: 14, margin: "6px 0 0" }}>
            Ingresa tus datos para continuar
          </p>
        </div>

        {campos.map(({ key, label, type, placeholder }) => (
          <div key={key} style={{ marginBottom: 20 }}>
            <label style={{
              display: "block", fontSize: 13, fontWeight: 500, marginBottom: 6,
            }}>{label}</label>
            <input
              type={type}
              placeholder={placeholder}
              value={form[key]}
              onChange={e => { setForm({ ...form, [key]: e.target.value }); setErrors({ ...errors, [key]: null }); }}
              onKeyDown={e => e.key === "Enter" && handleSubmit()}
              style={{
                width: "100%", boxSizing: "border-box",
                borderColor: errors[key] ? "var(--color-border-danger)" : undefined,
              }}
            />
            {errors[key] && (
              <p style={{ color: "var(--color-text-danger)", fontSize: 12, margin: "4px 0 0" }}>{errors[key]}</p>
            )}
          </div>
        ))}

        <button onClick={handleSubmit} disabled={cargando} style={{ width: "100%", marginTop: 8, opacity: cargando ? 0.6 : 1 }}>
          {cargando ? "Conectando..." : "Ingresar"}
          <i className="ti ti-arrow-right" style={{ marginLeft: 6, fontSize: 14, verticalAlign: -2 }} aria-hidden="true" />
        </button>
      </div>
    </div>
  );
}

// ================================================================
// CATÁLOGO
// ================================================================
function CatalogoPage({ filtrados, busqueda, setBusqueda, onAgregar, cargando }) {
  return (
    <div style={{ maxWidth: 960, margin: "0 auto", padding: "32px 24px" }}>
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 24 }}>
        <h2 style={{ fontSize: 22, fontWeight: 500, margin: 0 }}>Catálogo</h2>
        <span style={{ fontSize: 13, color: "var(--color-text-secondary)" }}>
          {filtrados.length} producto{filtrados.length !== 1 ? "s" : ""}
        </span>
      </div>

      <div style={{ position: "relative", marginBottom: 28 }}>
        <i className="ti ti-search" style={{
          position: "absolute", left: 12, top: "50%", transform: "translateY(-50%)",
          color: "var(--color-text-secondary)", fontSize: 16, pointerEvents: "none",
        }} aria-hidden="true" />
        <input
          type="text"
          placeholder="Buscar medicamento..."
          value={busqueda}
          onChange={e => setBusqueda(e.target.value)}
          style={{ paddingLeft: 36, width: "100%", boxSizing: "border-box" }}
        />
      </div>

      {cargando && filtrados.length === 0 ? (
        <Spinner />
      ) : filtrados.length === 0 ? (
        <div style={{ textAlign: "center", padding: "72px 0" }}>
          <i className="ti ti-search-off" style={{ fontSize: 44, color: "var(--color-text-secondary)", display: "block", marginBottom: 12 }} aria-hidden="true" />
          <p style={{ color: "var(--color-text-secondary)" }}>Sin resultados para "{busqueda}"</p>
        </div>
      ) : (
        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(190px, 1fr))", gap: 16 }}>
          {filtrados.map(prod => (
            <ProductCard key={prod.nombre} prod={prod} onAgregar={() => onAgregar(prod.nombre)} />
          ))}
        </div>
      )}
    </div>
  );
}

function ProductCard({ prod, onAgregar }) {
  const agotado = prod.stock === 0;
  return (
    <div style={{ ...S.card, padding: "18px 16px", display: "flex", flexDirection: "column" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 10 }}>
        <p style={{ fontWeight: 500, fontSize: 14, margin: 0, lineHeight: 1.4, flex: 1, paddingRight: 8 }}>
          {prod.nombre}
        </p>
        <span style={S.badge(prod.tipo)}>{prod.tipo}</span>
      </div>
      <p style={{ fontSize: 19, fontWeight: 500, margin: "4px 0 2px" }}>{fmtCOP(prod.precio)}</p>
      <p style={{ fontSize: 12, color: "var(--color-text-secondary)", margin: "0 0 16px" }}>
        {agotado ? "Agotado" : `${prod.stock} en stock`}
      </p>
      <button onClick={onAgregar} disabled={agotado} style={{ marginTop: "auto", opacity: agotado ? 0.45 : 1, cursor: agotado ? "not-allowed" : "pointer" }}>
        <i className="ti ti-plus" style={{ fontSize: 13, marginRight: 5, verticalAlign: -1 }} aria-hidden="true" />
        {agotado ? "Sin stock" : "Agregar"}
      </button>
    </div>
  );
}

// ================================================================
// CARRITO
// ================================================================
function CarritoPage({ carrito, onAdd, onRemove, onVaciar, onConfirmar, irCatalogo, cargando }) {
  if (cargando && carrito.items.length === 0) return <Spinner />;

  if (carrito.items.length === 0) {
    return (
      <div style={{ textAlign: "center", padding: "80px 24px" }}>
        <i className="ti ti-shopping-cart-off" style={{ fontSize: 52, color: "var(--color-text-secondary)", display: "block", marginBottom: 16 }} aria-hidden="true" />
        <p style={{ color: "var(--color-text-secondary)", marginBottom: 20 }}>El carrito está vacío</p>
        <button onClick={irCatalogo}>Ver catálogo</button>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 720, margin: "0 auto", padding: "32px 24px" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 24 }}>
        <h2 style={{ fontSize: 22, fontWeight: 500, margin: 0 }}>Mi carrito</h2>
        <button onClick={onVaciar} style={{ color: "var(--color-text-danger)", borderColor: "var(--color-border-danger)", fontSize: 13 }}>
          <i className="ti ti-trash" style={{ fontSize: 14, marginRight: 5, verticalAlign: -1 }} aria-hidden="true" />
          Vaciar
        </button>
      </div>

      <div style={{ display: "flex", flexDirection: "column", gap: 12, marginBottom: 28 }}>
        {carrito.items.map(item => (
          <div key={item.nombre} style={{ ...S.card, padding: "16px 20px", display: "flex", alignItems: "center", gap: 16 }}>
            <div style={{ flex: 1 }}>
              <p style={{ fontWeight: 500, margin: 0, fontSize: 15 }}>{item.nombre}</p>
              <p style={{ color: "var(--color-text-secondary)", fontSize: 13, margin: "3px 0 0" }}>
                {fmtCOP(item.precio)} c/u
              </p>
            </div>
            <div style={{
              display: "flex", alignItems: "center", gap: 4,
              border: "0.5px solid var(--color-border-secondary)",
              borderRadius: "var(--border-radius-md)", padding: "4px 6px",
            }}>
              <button
                onClick={() => onRemove(item.nombre)}
                disabled={cargando}
                style={{ border: "none", background: "none", cursor: "pointer", padding: "2px 4px", display: "flex", color: "var(--color-text-primary)" }}
                aria-label="Quitar unidad"
              >
                <i className="ti ti-minus" style={{ fontSize: 14 }} aria-hidden="true" />
              </button>
              <span style={{ minWidth: 28, textAlign: "center", fontWeight: 500, fontSize: 15, padding: "0 4px" }}>
                {item.cantidad}
              </span>
              <button
                onClick={() => onAdd(item.nombre)}
                disabled={cargando}
                style={{ border: "none", background: "none", cursor: "pointer", padding: "2px 4px", display: "flex", color: "var(--color-text-primary)" }}
                aria-label="Agregar unidad"
              >
                <i className="ti ti-plus" style={{ fontSize: 14 }} aria-hidden="true" />
              </button>
            </div>
            <p style={{ fontWeight: 500, fontSize: 15, minWidth: 110, textAlign: "right", margin: 0 }}>
              {fmtCOP(item.subtotal)}
            </p>
          </div>
        ))}
      </div>

      <div style={{ ...S.card, padding: "20px 24px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <div>
          <p style={{ fontSize: 12, color: "var(--color-text-secondary)", margin: 0 }}>Total a pagar</p>
          <p style={{ fontSize: 24, fontWeight: 500, margin: "4px 0 0" }}>{fmtCOP(carrito.total)}</p>
        </div>
        <button
          onClick={onConfirmar}
          disabled={cargando}
          style={{
            background: "var(--color-background-success)",
            color: "var(--color-text-success)",
            borderColor: "var(--color-border-success)",
            padding: "10px 24px", fontSize: 14, fontWeight: 500,
            opacity: cargando ? 0.6 : 1,
          }}
        >
          <i className="ti ti-check" style={{ fontSize: 15, marginRight: 7, verticalAlign: -2 }} aria-hidden="true" />
          {cargando ? "Procesando..." : "Confirmar compra"}
        </button>
      </div>
    </div>
  );
}

// ================================================================
// PERFIL
// ================================================================
function PerfilPage({ cliente, onSave }) {
  const [editando, setEditando] = useState(false);
  const [form, setForm] = useState({ ...cliente });

  const guardar = () => {
    if (!form.nombre.trim() || !form.cedula.trim() || +form.edad < 1) return;
    onSave({ ...form, edad: +form.edad });
    setEditando(false);
  };

  const campos = [
    { key: "nombre", label: "Nombre completo", type: "text" },
    { key: "edad", label: "Edad", type: "number" },
    { key: "cedula", label: "Cédula", type: "text" },
  ];

  return (
    <div style={{ maxWidth: 520, margin: "0 auto", padding: "32px 24px" }}>
      <h2 style={{ fontSize: 22, fontWeight: 500, marginBottom: 24 }}>Mis datos</h2>
      <div style={{ ...S.card, padding: "28px 32px" }}>
        <div style={{ display: "flex", alignItems: "center", gap: 16, marginBottom: 28 }}>
          <div style={{
            width: 52, height: 52, borderRadius: 9999,
            background: "var(--color-background-info)",
            display: "flex", alignItems: "center", justifyContent: "center",
            fontWeight: 500, fontSize: 17, color: "var(--color-text-info)",
          }}>
            {cliente.nombre.slice(0, 2).toUpperCase()}
          </div>
          <div>
            <p style={{ fontWeight: 500, margin: 0, fontSize: 16 }}>{cliente.nombre}</p>
            <p style={{ color: "var(--color-text-secondary)", fontSize: 13, margin: "2px 0 0" }}>CC: {cliente.cedula}</p>
          </div>
        </div>

        {editando ? (
          <>
            {campos.map(({ key, label, type }) => (
              <div key={key} style={{ marginBottom: 18 }}>
                <label style={{ display: "block", fontSize: 13, fontWeight: 500, marginBottom: 6 }}>{label}</label>
                <input
                  type={type}
                  value={form[key]}
                  onChange={e => setForm({ ...form, [key]: e.target.value })}
                  style={{ width: "100%", boxSizing: "border-box" }}
                />
              </div>
            ))}
            <div style={{ display: "flex", gap: 10, marginTop: 22 }}>
              <button onClick={guardar}>Guardar cambios</button>
              <button onClick={() => { setForm({ ...cliente }); setEditando(false); }}>Cancelar</button>
            </div>
          </>
        ) : (
          <>
            <table style={{ width: "100%", fontSize: 14, borderCollapse: "collapse" }}>
              {[["Nombre", cliente.nombre], ["Edad", `${cliente.edad} años`], ["Cédula", cliente.cedula]].map(([k, v]) => (
                <tr key={k} style={{ borderBottom: "0.5px solid var(--color-border-tertiary)" }}>
                  <td style={{ padding: "13px 0", color: "var(--color-text-secondary)", width: "40%" }}>{k}</td>
                  <td style={{ padding: "13px 0", fontWeight: 500 }}>{v}</td>
                </tr>
              ))}
            </table>
            <button onClick={() => setEditando(true)} style={{ marginTop: 24 }}>
              <i className="ti ti-edit" style={{ fontSize: 14, marginRight: 6, verticalAlign: -1 }} aria-hidden="true" />
              Editar datos
            </button>
          </>
        )}
      </div>
    </div>
  );
}

// ================================================================
// ÓRDENES
// ================================================================
function OrdenesPage({ ordenes, onCancelar, cargando }) {
  if (cargando && ordenes.length === 0) return <Spinner />;

  if (ordenes.length === 0) {
    return (
      <div style={{ textAlign: "center", padding: "80px 24px" }}>
        <i className="ti ti-receipt-off" style={{ fontSize: 52, color: "var(--color-text-secondary)", display: "block", marginBottom: 16 }} aria-hidden="true" />
        <p style={{ color: "var(--color-text-secondary)" }}>Todavía no tienes órdenes</p>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 820, margin: "0 auto", padding: "32px 24px" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 24 }}>
        <h2 style={{ fontSize: 22, fontWeight: 500, margin: 0 }}>Mis órdenes</h2>
        <span style={{ fontSize: 13, color: "var(--color-text-secondary)" }}>
          {ordenes.length} orden{ordenes.length !== 1 ? "es" : ""}
        </span>
      </div>
      <div style={{ display: "flex", flexDirection: "column", gap: 14 }}>
        {[...ordenes].reverse().map(orden => (
          <OrdenCard key={orden.id} orden={orden} onCancelar={() => onCancelar(orden.id)} cargando={cargando} />
        ))}
      </div>
    </div>
  );
}

function OrdenCard({ orden, onCancelar, cargando }) {
  const [abierta, setAbierta] = useState(false);
  return (
    <div style={{ ...S.card, overflow: "hidden" }}>
      <div onClick={() => setAbierta(!abierta)} style={{ padding: "16px 22px", display: "flex", alignItems: "center", gap: 16, cursor: "pointer" }}>
        <i className="ti ti-receipt" style={{ fontSize: 20, color: "var(--color-text-secondary)", flexShrink: 0 }} aria-hidden="true" />
        <div style={{ flex: 1 }}>
          <p style={{ fontWeight: 500, margin: 0, fontSize: 15 }}>Orden #{orden.id}</p>
        </div>
        <span style={S.badge(orden.estado)}>{orden.estado}</span>
        <p style={{ fontWeight: 500, fontSize: 15, margin: 0, minWidth: 110, textAlign: "right" }}>
          {fmtCOP(orden.total)}
        </p>
        <i className={`ti ${abierta ? "ti-chevron-up" : "ti-chevron-down"}`} style={{ fontSize: 16, color: "var(--color-text-secondary)", flexShrink: 0 }} aria-hidden="true" />
      </div>

      {abierta && (
        <div style={{ borderTop: "0.5px solid var(--color-border-tertiary)", padding: "16px 22px" }}>
          {orden.items.map(item => (
            <div key={item.nombre} style={{
              display: "flex", justifyContent: "space-between",
              padding: "9px 0", fontSize: 14,
              borderBottom: "0.5px solid var(--color-border-tertiary)",
            }}>
              <span>
                {item.nombre}
                <span style={{ color: "var(--color-text-secondary)", marginLeft: 8 }}>× {item.cantidad}</span>
              </span>
              <span style={{ fontWeight: 500 }}>{fmtCOP(item.subtotal)}</span>
            </div>
          ))}
          {orden.estado !== "CANCELADA" && (
            <button
              onClick={onCancelar}
              disabled={cargando}
              style={{ marginTop: 16, color: "var(--color-text-danger)", borderColor: "var(--color-border-danger)", opacity: cargando ? 0.6 : 1 }}
            >
              <i className="ti ti-x" style={{ fontSize: 14, marginRight: 6, verticalAlign: -1 }} aria-hidden="true" />
              Cancelar orden
            </button>
          )}
        </div>
      )}
    </div>
  );
}

// ================================================================
// UTILIDADES
// ================================================================
function Spinner() {
  return (
    <div style={{ display: "flex", justifyContent: "center", padding: "80px 0" }}>
      <i className="ti ti-loader-2" style={{ fontSize: 32, color: "var(--color-text-secondary)", animation: "spin 1s linear infinite" }} aria-hidden="true" />
    </div>
  );
}

const globalStyles = `
  @import url('https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.19.0/dist/tabler-icons.min.css');
  @keyframes spin     { to { transform: rotate(360deg); } }
  @keyframes progress { 0% { opacity:1 } 50% { opacity:0.4 } 100% { opacity:1 } }
`;
