import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';

import { Articulo, BusquedaResponse, ResultadoBusqueda } from '../../models/api.models';
import { ApiService } from '../../services/api.service';
import { Trozo, desplazamientos, fragmentos } from './busqueda.logica';

const MAXIMO_PASOS_EN_TABLA = 200;
const MAXIMO_BARRAS = 400;

interface FilaArticulo {
  articulo: Articulo;
  total: number;
  fragmentos: Trozo[][];
}

/** Campo de patrón y resultados resaltados por algoritmo. */
@Component({
  selector: 'app-busqueda',
  imports: [DecimalPipe],
  templateUrl: './busqueda.component.html',
  styleUrls: ['../pagina-comun.css', './busqueda.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BusquedaComponent implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly ejemplos = ['generative', 'higher education', 'artificial intelligence', 'chatgpt'];
  protected readonly maximoPasos = MAXIMO_PASOS_EN_TABLA;

  protected readonly articulos = signal<Articulo[]>([]);
  protected readonly patron = signal('');
  protected readonly conTraza = signal(false);
  protected readonly articuloTraza = signal<string | null>(null);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly respuesta = signal<BusquedaResponse | null>(null);

  /** true si los tres algoritmos devolvieron exactamente las mismas posiciones en cada artículo. */
  protected readonly coinciden = computed(() => {
    const r = this.respuesta();
    if (!r || r.resultados.length === 0) {
      return true;
    }
    const referencia = JSON.stringify(r.resultados[0].porArticulo);
    return r.resultados.every((x) => JSON.stringify(x.porArticulo) === referencia);
  });

  protected readonly filas = computed<FilaArticulo[]>(() => {
    const r = this.respuesta();
    if (!r || r.resultados.length === 0) {
      return [];
    }
    const porId = new Map(this.articulos().map((a) => [a.id, a]));
    const filas: FilaArticulo[] = [];
    for (const c of r.resultados[0].porArticulo) {
      const articulo = porId.get(c.articuloId);
      if (articulo) {
        filas.push({
          articulo,
          total: c.posiciones.length,
          fragmentos: fragmentos(articulo.abstractProcesado, c.posiciones, r.patron.length),
        });
      }
    }
    return filas.sort((a, b) => b.total - a.total);
  });

  ngOnInit(): void {
    this.api.articulos().subscribe({
      next: (articulos) => this.articulos.set(articulos),
      error: () => this.error.set('No se pudo cargar el corpus. ¿Está encendido el backend?'),
    });
  }

  protected usarEjemplo(ejemplo: string): void {
    this.patron.set(ejemplo);
    this.buscar();
  }

  protected buscar(): void {
    if (!this.patron().trim()) {
      this.error.set('Escribe el patrón que quieres buscar.');
      return;
    }
    this.cargando.set(true);
    this.error.set(null);
    this.api.buscar(this.patron(), this.conTraza(), this.articuloTraza()).subscribe({
      next: (r) => {
        this.respuesta.set(r);
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.respuesta.set(null);
        this.error.set(e.error?.error ?? 'No se pudo conectar con el servidor.');
        this.cargando.set(false);
      },
    });
  }

  protected microsegundos(ns: number): number {
    return ns / 1000;
  }

  /** Barras de desplazamiento de un algoritmo, con su altura en porcentaje del mayor desplazamiento. */
  protected barras(resultado: ResultadoBusqueda) {
    const valores = desplazamientos(resultado.traza?.pasos ?? []);
    const maximo = valores.reduce((m, v) => Math.max(m, v.valor), 1);
    return {
      maximo,
      total: valores.length,
      recortado: valores.length > MAXIMO_BARRAS,
      suma: valores.reduce((s, v) => s + v.valor, 0),
      barras: valores.slice(0, MAXIMO_BARRAS).map((v) => ({ ...v, alto: (v.valor / maximo) * 100 })),
    };
  }
}
