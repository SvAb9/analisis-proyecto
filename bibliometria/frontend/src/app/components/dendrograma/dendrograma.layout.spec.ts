import { NodoDendrograma } from '../../models/api.models';
import { ALTO_FILA, ANCHO_GRAFICO, calcularLayout } from './dendrograma.layout';

const hoja = (id: string): NodoDendrograma => ({ articuloId: id, altura: 0, izquierda: null, derecha: null });
const union = (altura: number, izquierda: NodoDendrograma, derecha: NodoDendrograma): NodoDendrograma => ({
  articuloId: null,
  altura,
  izquierda,
  derecha,
});

/** ((a, b) a distancia 0.2, c) a distancia 0.5 */
const arbol = union(0.5, union(0.2, hoja('a'), hoja('b')), hoja('c'));

describe('calcularLayout', () => {
  it('ubica las hojas en el orden del árbol, una por fila', () => {
    const l = calcularLayout(arbol);
    expect(l.hojas.map((h) => h.id)).toEqual(['a', 'b', 'c']);
    expect(l.hojas.map((h) => h.y)).toEqual([ALTO_FILA / 2, ALTO_FILA * 1.5, ALTO_FILA * 2.5]);
  });

  it('dibuja un trazo por cada unión', () => {
    expect(calcularLayout(arbol).trazos.length).toBe(2);
  });

  it('pone la raíz en el extremo derecho y el eje de 0 a la altura de la raíz', () => {
    const l = calcularLayout(arbol);
    expect(l.trazos[1]).toContain(`H${ANCHO_GRAFICO}V`);
    expect(l.marcas[0].texto).toBe('0.00');
    expect(l.marcas[l.marcas.length - 1].texto).toBe('0.50');
  });

  it('con ajuste, el eje empieza después de 0 pero la raíz sigue en el extremo', () => {
    const l = calcularLayout(arbol, true);
    expect(Number(l.marcas[0].texto)).toBeGreaterThan(0);
    expect(Number(l.marcas[0].texto)).toBeLessThan(0.2);
    expect(l.marcas[l.marcas.length - 1].texto).toBe('0.50');
    expect(l.trazos[1]).toContain(`H${ANCHO_GRAFICO}V`);
  });
});
