import { NodoDendrograma } from '../../models/api.models';

/** Medidas del dibujo, en unidades del SVG. */
export const MARGEN = { izq: 210, der: 24, arriba: 12, abajo: 40 };
export const ALTO_FILA = 22;
export const ANCHO_GRAFICO = 520;

export interface Hoja {
  id: string;
  y: number;
}

export interface Marca {
  x: number;
  texto: string;
}

export interface Layout {
  /** Un trazo por fusión: sale de los dos hijos, sube hasta la altura de fusión y los une. */
  trazos: string[];
  hojas: Hoja[];
  marcas: Marca[];
  /** Tamaño del área de dibujo (sin márgenes). */
  anchoGrafico: number;
  altoGrafico: number;
  /** Tamaño total del SVG (con márgenes). */
  ancho: number;
  alto: number;
}

/**
 * Calcula dónde dibujar cada rama. Dendrograma horizontal: las hojas van a la izquierda (distancia 0)
 * y las fusiones se desplazan a la derecha a medida que crece la distancia. El orden de las hojas es
 * el del recorrido del árbol, así que las ramas nunca se cruzan.
 */
export function calcularLayout(raiz: NodoDendrograma, ajustar = false): Layout {
  const alturaMax = raiz.altura > 0 ? raiz.altura : 1;
  // Con "ajustar", el eje arranca un poco antes de la primera unión en vez de en 0. Sirve cuando todas
  // las uniones ocurren a distancias parecidas y, a escala completa, quedan apretadas a la derecha.
  const base = ajustar ? Math.max(0, menorUnion(raiz) - 0.1 * (alturaMax - menorUnion(raiz))) : 0;
  const rango = alturaMax - base > 0 ? alturaMax - base : 1;
  const escala = (h: number) => ((h - base) / rango) * ANCHO_GRAFICO;
  const hojas: Hoja[] = [];
  const trazos: string[] = [];

  const dibujar = (nodo: NodoDendrograma): { x: number; y: number } => {
    if (!nodo.izquierda || !nodo.derecha) {
      const y = hojas.length * ALTO_FILA + ALTO_FILA / 2;
      hojas.push({ id: nodo.articuloId ?? '?', y });
      return { x: 0, y };
    }
    const a = dibujar(nodo.izquierda);
    const b = dibujar(nodo.derecha);
    const x = escala(nodo.altura);
    trazos.push(`M${a.x} ${a.y}H${x}V${b.y}H${b.x}`);
    return { x, y: (a.y + b.y) / 2 };
  };
  dibujar(raiz);

  const marcas: Marca[] = [];
  const pasos = 4;
  for (let i = 0; i <= pasos; i++) {
    marcas.push({ x: (ANCHO_GRAFICO * i) / pasos, texto: (base + (rango * i) / pasos).toFixed(2) });
  }

  const altoGrafico = hojas.length * ALTO_FILA;
  return {
    trazos,
    hojas,
    marcas,
    anchoGrafico: ANCHO_GRAFICO,
    altoGrafico,
    ancho: MARGEN.izq + ANCHO_GRAFICO + MARGEN.der,
    alto: MARGEN.arriba + altoGrafico + MARGEN.abajo,
  };
}

/** Altura de la unión más baja del árbol (la primera que ocurre). */
function menorUnion(nodo: NodoDendrograma): number {
  if (!nodo.izquierda || !nodo.derecha) {
    return Infinity;
  }
  return Math.min(nodo.altura, menorUnion(nodo.izquierda), menorUnion(nodo.derecha));
}
