// Tipos de las respuestas de la API.
export interface NodoDendrograma {
  articuloId: string | null;
  altura: number;
  izquierda: NodoDendrograma | null;
  derecha: NodoDendrograma | null;
}

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

export interface ArticuloResumen {
  id: string | number;
  titulo: string;
}

export interface Articulo {
  id: string;
  titulo: string;
  autores: string[];
  abstractCrudo: string;
  abstractProcesado: string;
}

export interface PasoTraza {
  numero: number;
  descripcion: string;
  datos: string;
}

export interface CoincidenciaBusqueda {
  articuloId: string;
  posiciones: number[];
}

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
