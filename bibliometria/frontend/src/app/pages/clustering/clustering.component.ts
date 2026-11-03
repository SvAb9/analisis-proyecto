// Pantalla de agrupamiento jerárquico.
import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';

import { DendrogramaComponent } from '../../components/dendrograma/dendrograma.component';
import { ClusteringResponse, ResultadoClustering } from '../../models/api.models';
import { ApiService } from '../../services/api.service';

const SIMILITUD_POR_DEFECTO = 'CosenoTfIdf';

function mejorCriterio(
  respuesta: ClusteringResponse | null,
  metrica: (r: ResultadoClustering) => number,
): string | null {
  if (!respuesta || respuesta.resultados.length === 0) {
    return null;
  }
  return respuesta.resultados.reduce((mejor, r) => (metrica(r) > metrica(mejor) ? r : mejor)).criterio;
}

@Component({
  selector: 'app-clustering',
  imports: [DecimalPipe, DendrogramaComponent],
  templateUrl: './clustering.component.html',
  styleUrl: './clustering.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ClusteringComponent implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly algoritmos = signal<string[]>([]);
  protected readonly seleccionado = signal(SIMILITUD_POR_DEFECTO);
  protected readonly acercar = signal(true);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly respuesta = signal<ClusteringResponse | null>(null);
  protected readonly etiquetas = signal<Record<string, string>>({});

  protected readonly mejorCofenetica = computed(() =>
    mejorCriterio(this.respuesta(), (r) => r.correlacionCofenetica),
  );
  protected readonly mejorSilueta = computed(() => mejorCriterio(this.respuesta(), (r) => r.silhouette));

  ngOnInit(): void {
    this.api.algoritmosSimilitud().subscribe({
      next: (nombres) => {
        this.algoritmos.set(nombres);
        if (nombres.length > 0 && !nombres.includes(this.seleccionado())) {
          this.seleccionado.set(nombres[0]);
        }
      },
      error: () => this.error.set('No se pudo cargar la lista de algoritmos. ¿Está encendido el backend?'),
    });

    this.api.corpus().subscribe({
      next: (articulos) => {
        const porId: Record<string, string> = {};
        for (const a of articulos) {
          porId[String(a.id)] = a.titulo;
        }
        this.etiquetas.set(porId);
      },
      error: () => {},
    });
  }

  protected agrupar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.api.agrupar(this.seleccionado()).subscribe({
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
}
