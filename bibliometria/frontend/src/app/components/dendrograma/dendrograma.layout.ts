// Calcula la posición de los nodos del dendrograma.
import { NodoDendrograma } from '../../models/api.models';

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
  trazos: string[];
  hojas: Hoja[];
  marcas: Marca[];
  anchoGrafico: number;
  altoGrafico: number;
  ancho: number;
  alto: number;
}

export function calcularLayout(raiz: NodoDendrograma, ajustar = false): Layout {
  const alturaMax = raiz.altura > 0 ? raiz.altura : 1;
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

function menorUnion(nodo: NodoDendrograma): number {
  if (!nodo.izquierda || !nodo.derecha) {
    return Infinity;
  }
  return Math.min(nodo.altura, menorUnion(nodo.izquierda), menorUnion(nodo.derecha));
}
