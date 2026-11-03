import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';

import { Articulo, BusquedaResponse, PasoTraza } from '../../models/api.models';
import { ApiService } from '../../services/api.service';
import { Trozo, desplazamientos, fragmentos } from './busqueda.logica';

const MAXIMO_PASOS_EN_TABLA = 200;
const MAXIMO_BARRAS = 400;

const NOMBRES: Record<string, string> = { KMP: 'KMP', BoyerMoore: 'Boyer-Moore', RabinKarp: 'Rabin-Karp' };

interface FilaArticulo {
  articulo: Articulo;
  total: number;
  fragmentos: Trozo[][];
}

/** Datos de la gráfica de desplazamientos y de la tabla de pasos de un algoritmo. */
interface Grafica {
  algoritmo: string;
  pasos: PasoTraza[];
  total: number;
  promedio: number;
  maximo: number;
  recortado: boolean;
  barras: { numero: number; valor: number; descripcion: string; alto: number }[];
}

/** Campo de patrón y resultados resaltados por algoritmo. */
@Component({
  selector: 'app-busqueda',
  imports: [DecimalPipe],
  templateUrl: './busqueda.component.html',
  styleUrl: './busqueda.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BusquedaComponent implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly ejemplos = ['generative', 'higher education', 'artificial intelligence', 'chatgpt'];
  protected readonly maximoPasos = MAXIMO_PASOS_EN_TABLA;
  protected readonly maximoBarras = MAXIMO_BARRAS;

  protected readonly articulos = signal<Articulo[]>([]);
  protected readonly patron = signal('');
  protected readonly conTraza = signal(false);
  protected readonly articuloTraza = signal<string | null>(null);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly respuesta = signal<BusquedaResponse | null>(null);
  /** Barra señalada en cada gráfica, por algoritmo. */
  protected readonly lectura = signal<Record<string, string>>({});

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

  protected readonly mayorComparaciones = computed(() =>
    Math.max(1, ...(this.respuesta()?.resultados ?? []).map((x) => x.comparaciones)),
  );

  protected readonly graficas = computed<Grafica[]>(() => {
    const graficas: Grafica[] = [];
    for (const x of this.respuesta()?.resultados ?? []) {
      if (x.traza) {
        const valores = desplazamientos(x.traza.pasos);
        const maximo = valores.reduce((m, v) => Math.max(m, v.valor), 1);
        graficas.push({
          algoritmo: x.algoritmo,
          pasos: x.traza.pasos,
          total: valores.length,
          promedio: valores.length ? valores.reduce((s, v) => s + v.valor, 0) / valores.length : 0,
          maximo,
          recortado: valores.length > MAXIMO_BARRAS,
          barras: valores.slice(0, MAXIMO_BARRAS).map((v) => ({ ...v, alto: (v.valor / maximo) * 100 })),
        });
      }
    }
    return graficas;
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
    this.lectura.set({});
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

  protected nombre(algoritmo: string): string {
    return NOMBRES[algoritmo] ?? algoritmo;
  }

  protected microsegundos(ns: number): number {
    return ns / 1000;
  }

  protected senalar(algoritmo: string, texto: string): void {
    this.lectura.update((l) => ({ ...l, [algoritmo]: texto }));
  }
}
