import { PasoTraza } from '../../models/api.models';

/** Un trozo de texto; marcado indica que hace parte de una ocurrencia. */
export interface Trozo {
  texto: string;
  marcado: boolean;
}

/**
 * Convierte posiciones del patrón en fragmentos de texto con las ocurrencias marcadas. Las ocurrencias
 * solapadas se unen y las que quedan cerca comparten fragmento.
 */
export function fragmentos(texto: string, posiciones: number[], largo: number, contexto = 50): Trozo[][] {
  const rangos: [number, number][] = [];
  for (const p of [...posiciones].sort((a, b) => a - b)) {
    const ultimo = rangos[rangos.length - 1];
    if (ultimo && p <= ultimo[1]) {
      ultimo[1] = Math.max(ultimo[1], p + largo);
    } else {
      rangos.push([p, p + largo]);
    }
  }

  const resultado: Trozo[][] = [];
  let i = 0;
  while (i < rangos.length) {
    let fin = i;
    while (fin + 1 < rangos.length && rangos[fin + 1][0] - rangos[fin][1] <= 2 * contexto) {
      fin++;
    }
    const desde = Math.max(0, rangos[i][0] - contexto);
    const hasta = Math.min(texto.length, rangos[fin][1] + contexto);
    const trozos: Trozo[] = [];
    let cursor = desde;
    for (let r = i; r <= fin; r++) {
      if (rangos[r][0] > cursor) {
        trozos.push({ texto: texto.slice(cursor, rangos[r][0]), marcado: false });
      }
      trozos.push({ texto: texto.slice(rangos[r][0], rangos[r][1]), marcado: true });
      cursor = rangos[r][1];
    }
    if (hasta > cursor) {
      trozos.push({ texto: texto.slice(cursor, hasta), marcado: false });
    }
    if (desde > 0) {
      trozos[0] = { ...trozos[0], texto: '…' + trozos[0].texto };
    }
    if (hasta < texto.length) {
      const u = trozos.length - 1;
      trozos[u] = { ...trozos[u], texto: trozos[u].texto + '…' };
    }
    resultado.push(trozos);
    i = fin + 1;
  }
  return resultado;
}

/** Desplazamiento de cada iteración de la traza, leído del campo datos; omite las tablas iniciales. */
export function desplazamientos(pasos: PasoTraza[]): { numero: number; valor: number; descripcion: string }[] {
  const salida: { numero: number; valor: number; descripcion: string }[] = [];
  for (const paso of pasos) {
    const encontrado = /(?:^|, )desplazamiento=(\d+)/.exec(paso.datos);
    if (encontrado) {
      salida.push({ numero: salida.length + 1, valor: Number(encontrado[1]), descripcion: paso.descripcion });
    }
  }
  return salida;
}
