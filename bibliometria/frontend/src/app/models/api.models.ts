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
