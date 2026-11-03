import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';

import { BenchmarkResponse, MedicionBenchmark } from '../../models/api.models';
import { ApiService } from '../../services/api.service';

const REPETICIONES_POR_DEFECTO = 101;
const REPETICIONES_MAXIMAS = 1001;
const NOMBRES: Record<string, string> = { KMP: 'KMP', BoyerMoore: 'Boyer-Moore', RabinKarp: 'Rabin-Karp' };

interface Panel {
  titulo: string;
  barras: { algoritmo: string; valor: number; ancho: number }[];
}

/** Mide y compara los tiempos de los algoritmos de búsqueda. */
@Component({
  selector: 'app-rendimiento',
  imports: [DecimalPipe],
  templateUrl: './rendimiento.component.html',
  styleUrl: './rendimiento.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RendimientoComponent {
  private readonly api = inject(ApiService);

  protected readonly ejemplos = ['ai', 'learning', 'generative', 'artificial intelligence', 'systematic literature review'];
  protected readonly maximo = REPETICIONES_MAXIMAS;

  protected readonly patron = signal('generative');
  protected readonly repeticiones = signal(REPETICIONES_POR_DEFECTO);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly respuesta = signal<BenchmarkResponse | null>(null);
  protected readonly historial = signal<BenchmarkResponse[]>([]);

  protected readonly paneles = computed<Panel[]>(() => {
    const r = this.respuesta();
    if (!r) {
      return [];
    }
    return [
      this.panel('Preprocesamiento (µs)', r.mediciones, (m) => m.preprocesamientoNs),
      this.panel('Búsqueda (µs)', r.mediciones, (m) => m.busquedaNs),
    ];
  });

  protected usarEjemplo(ejemplo: string): void {
    this.patron.set(ejemplo);
    this.medir();
  }

  protected medir(): void {
    if (!this.patron().trim()) {
      this.error.set('Escribe el patrón que quieres medir.');
      return;
    }
    this.cargando.set(true);
    this.error.set(null);
    this.api.benchmark(this.patron(), this.repeticiones()).subscribe({
      next: (r) => {
        this.respuesta.set(r);
        this.historial.update((h) => [r, ...h]);
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.error.set(e.error?.error ?? 'No se pudo conectar con el servidor.');
        this.cargando.set(false);
      },
    });
  }

  protected nombre(algoritmo: string): string {
    return NOMBRES[algoritmo] ?? algoritmo;
  }

  protected readonly masRapido = computed(() => {
    const r = this.respuesta();
    if (!r || r.mediciones.length === 0) {
      return null;
    }
    return r.mediciones.reduce((mejor, m) =>
      m.preprocesamientoNs + m.busquedaNs < mejor.preprocesamientoNs + mejor.busquedaNs ? m : mejor,
    );
  });

  protected microsegundos(ns: number): number {
    return ns / 1000;
  }

  protected tiempoTotal(r: BenchmarkResponse, algoritmo: string): number {
    const m = r.mediciones.find((x) => x.algoritmo === algoritmo);
    return m ? (m.preprocesamientoNs + m.busquedaNs) / 1000 : 0;
  }

  private panel(titulo: string, mediciones: MedicionBenchmark[], valor: (m: MedicionBenchmark) => number): Panel {
    const mayor = mediciones.reduce((max, m) => Math.max(max, valor(m)), 1);
    return {
      titulo,
      barras: mediciones.map((m) => ({
        algoritmo: m.algoritmo,
        valor: valor(m) / 1000,
        ancho: (valor(m) / mayor) * 100,
      })),
    };
  }
}
