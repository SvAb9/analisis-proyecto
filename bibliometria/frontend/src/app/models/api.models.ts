/** Tipos que comparte el frontend con la API del backend (ver docs/contratos.md). */

/** Nodo del dendrograma. Hoja: articuloId != null y sin hijos. Nodo interno: altura = distancia de fusión. */
export interface NodoDendrograma {
  articuloId: string | null;
  altura: number;
  izquierda: NodoDendrograma | null;
  derecha: NodoDendrograma | null;
}

/** Resultado de un criterio de enlace (Single, Complete o Average). */
export interface ResultadoClustering {
  criterio: string;
  arbol: NodoDendrograma;
  silhouette: number;
  correlacionCofenetica: number;
  tiempoNs: number;
}

export interface ClusteringResponse {
  resultados: ResultadoClustering[];
}

/** Lo mínimo que necesita la pantalla de un artículo del corpus. */
export interface ArticuloResumen {
  id: string | number;
  titulo: string;
}

/** Artículo completo del corpus (GET /api/corpus). */
export interface Articulo {
  id: string;
  titulo: string;
  autores: string[];
  abstractCrudo: string;
  abstractProcesado: string;
}

/** Un paso intermedio de un algoritmo. */
export interface PasoTraza {
  numero: number;
  descripcion: string;
  datos: string;
}

/** Posiciones (base 0) del patrón en el abstract procesado de un artículo. */
export interface CoincidenciaBusqueda {
  articuloId: string;
  posiciones: number[];
}

/** Resultado de un algoritmo de búsqueda sobre los 20 abstracts. */
export interface ResultadoBusqueda {
  algoritmo: string;
  totalOcurrencias: number;
  porArticulo: CoincidenciaBusqueda[];
  comparaciones: number;
  preprocesamientoNs: number;
  busquedaNs: number;
  traza: { algoritmo: string; pasos: PasoTraza[] } | null;
}

export interface BusquedaResponse {
  patron: string;
  articuloTraza: string | null;
  resultados: ResultadoBusqueda[];
}

/** Tiempos de un algoritmo en una pasada por los 20 abstracts. */
export interface MedicionBenchmark {
  algoritmo: string;
  preprocesamientoNs: number;
  busquedaNs: number;
  minimoNs: number;
  maximoNs: number;
  comparaciones: number;
  ocurrencias: number;
}

export interface BenchmarkResponse {
  patron: string;
  largoPatron: number;
  articulos: number;
  caracteres: number;
  repeticiones: number;
  calentamiento: number;
  mediciones: MedicionBenchmark[];
}
