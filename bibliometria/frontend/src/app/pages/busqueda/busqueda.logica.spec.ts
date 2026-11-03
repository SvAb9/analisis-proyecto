import { desplazamientos, fragmentos } from './busqueda.logica';

describe('busqueda.logica', () => {
  it('marca una ocurrencia con su contexto', () => {
    expect(fragmentos('hola mundo', [5], 5, 2)).toEqual([
      [
        { texto: '…a ', marcado: false },
        { texto: 'mundo', marcado: true },
      ],
    ]);
  });

  it('une ocurrencias solapadas', () => {
    expect(fragmentos('aaaa', [0, 1, 2], 2, 0)).toEqual([[{ texto: 'aaaa', marcado: true }]]);
  });

  it('separa ocurrencias lejanas en fragmentos distintos', () => {
    expect(fragmentos('ab' + 'x'.repeat(50) + 'ab', [0, 52], 2, 5).length).toBe(2);
  });

  it('agrupa ocurrencias cercanas en un fragmento', () => {
    expect(fragmentos('ab xx ab', [0, 6], 2, 5).length).toBe(1);
  });

  it('sin posiciones no hay fragmentos', () => {
    expect(fragmentos('hola', [], 2)).toEqual([]);
  });

  it('lee los desplazamientos y omite las tablas', () => {
    const pasos = [
      { numero: 1, descripcion: 'Tabla de sufijo bueno', datos: 'desplazamiento=[5, 5, 1]' },
      { numero: 2, descripcion: 'Fallo', datos: 'alineacion=0, desplazamiento=3, j=0' },
      { numero: 3, descripcion: 'Hash', datos: 'hashPatron=7' },
    ];
    expect(desplazamientos(pasos)).toEqual([{ numero: 1, valor: 3, descripcion: 'Fallo' }]);
  });
});
