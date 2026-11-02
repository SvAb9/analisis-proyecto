// Selección de artículos y tabla comparativa clásicos vs IA.
//
// Componente standalone (Angular 17+). Endpoints que usa (ver docs/contratos.md):
//   GET  /api/corpus                -> lista de Articulo (id, titulo, autores, abstractCrudo)
//   GET  /api/similitud/algoritmos  -> nombres de los algoritmos disponibles
//   POST /api/similitud             -> { ids, algoritmos, conTraza } => { pares: [{ idA, idB, resultados }] }
// Las llamadas HTTP están aquí mientras services/api.service.ts no exponga métodos de R1;
// al integrarlo basta con mover los tres métodos marcados con "API".
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

interface ArticuloCorpus {
  id: string;
  titulo: string;
}

interface AlgoritmoInfo {
  nombre: string;
  etiqueta: string;
  tipo: 'CLASICO' | 'IA';
}

interface Paso {
  numero: number;
  descripcion: string;
  datos: string;
}

interface ResultadoSimilitud {
  algoritmo: string;
  similitud: number;
  valorCrudo: number;
  tiempoNs: number;
  pasos: Paso[];
}

interface SimilitudResponse {
  pares: { idA: string; idB: string; resultados: ResultadoSimilitud[] }[];
}

/** Vista armada en el cliente a partir de los pares: una matriz por algoritmo. */
interface ResultadoAlgoritmo {
  nombre: string;
  etiqueta: string;
  tipo: 'CLASICO' | 'IA';
  matriz: number[][];
  primero: ResultadoSimilitud;
  tiempoTotalMs: number;
}

interface ResumenPar {
  idA: string;
  idB: string;
  promedioClasicos: number;
  promedioIA: number;
  diferencia: number;
  algoritmoMayor: string;
  algoritmoMenor: string;
}

interface Vista {
  articulos: ArticuloCorpus[];
  algoritmos: ResultadoAlgoritmo[];
  resumen: ResumenPar[];
}

// Nombres estables del contrato -> texto para la pantalla.
const ETIQUETAS: Record<string, { etiqueta: string; tipo: 'CLASICO' | 'IA'; crudo: string }> = {
  Levenshtein: { etiqueta: 'Distancia de Levenshtein', tipo: 'CLASICO', crudo: 'distancia de edición' },
  NeedlemanWunsch: { etiqueta: 'Needleman-Wunsch', tipo: 'CLASICO', crudo: 'puntaje de alineamiento' },
  Jaccard: { etiqueta: 'Coeficiente de Jaccard', tipo: 'CLASICO', crudo: 'términos comunes' },
  CosenoTfIdf: { etiqueta: 'Coseno TF-IDF', tipo: 'CLASICO', crudo: 'coseno' },
  EmbeddingsOracion: { etiqueta: 'Embeddings de oración (MiniLM)', tipo: 'IA', crudo: 'distancia euclidiana' },
  EmbeddingsPalabras: { etiqueta: 'Embeddings de palabras (GloVe)', tipo: 'IA', crudo: 'distancia euclidiana' },
};

// Rampa secuencial azul (claro -> oscuro) para la matriz: similitud 0 -> paso 100, 1 -> paso 700.
const RAMPA = ['#cde2fb', '#b7d3f6', '#9ec5f4', '#86b6ef', '#6da7ec', '#5598e7', '#3987e5',
  '#2a78d6', '#256abf', '#1c5cab', '#184f95', '#104281', '#0d366b'];

@Component({
  selector: 'app-similitud',
  standalone: true,
  imports: [CommonModule, FormsModule, DecimalPipe],
  template: `
    <section class="similitud">
      <h1>R1 · Similitud textual</h1>
      <p class="ayuda">Elige dos o más artículos y los algoritmos a aplicar. Con exactamente dos
        artículos puedes ver los pasos intermedios de cada algoritmo.</p>

      <div class="columnas">
        <div class="panel">
          <h2>Artículos ({{ seleccionados().size }} seleccionados)</h2>
          <input type="search" placeholder="Filtrar por título" [ngModel]="filtro()"
                 (ngModelChange)="filtro.set($event)" />
          <ul class="lista">
            @for (a of articulosFiltrados(); track a.id) {
              <li>
                <label>
                  <input type="checkbox" [checked]="seleccionados().has(a.id)" (change)="alternarArticulo(a.id)" />
                  <span class="id">{{ a.id }}</span> {{ a.titulo }}
                </label>
              </li>
            } @empty {
              <li class="vacio">{{ errorCorpus() ?? 'Cargando corpus…' }}</li>
            }
          </ul>
        </div>

        <div class="panel">
          <h2>Algoritmos</h2>
          @for (tipo of ['CLASICO', 'IA']; track tipo) {
            <h3>{{ tipo === 'IA' ? 'Inteligencia artificial' : 'Clásicos' }}</h3>
            @for (al of algoritmosDeTipo(tipo); track al.nombre) {
              <label class="algoritmo">
                <input type="checkbox" [checked]="algoritmosElegidos().has(al.nombre)"
                       (change)="alternarAlgoritmo(al.nombre)" />
                {{ al.etiqueta }}
              </label>
            } @empty {
              <p class="nota">Ninguno disponible.</p>
            }
          }
          <label class="traza">
            <input type="checkbox" [ngModel]="incluirTraza()" (ngModelChange)="incluirTraza.set($event)"
                   [disabled]="seleccionados().size !== 2" />
            Mostrar pasos intermedios (solo con 2 artículos)
          </label>
          <button type="button" (click)="comparar()" [disabled]="!puedeComparar() || cargando()">
            {{ cargando() ? 'Calculando…' : 'Comparar' }}
          </button>
          @if (error()) { <p class="error" role="alert">{{ error() }}</p> }
        </div>
      </div>

      @if (vista(); as r) {
        @if (r.articulos.length === 2) {
          <h2>Resultado: {{ r.articulos[0].id }} vs {{ r.articulos[1].id }}</h2>
          <table class="resultados">
            <thead>
              <tr><th>Algoritmo</th><th>Tipo</th><th>Similitud</th><th>Valor crudo</th><th>Tiempo</th></tr>
            </thead>
            <tbody>
              @for (al of r.algoritmos; track al.nombre) {
                <tr>
                  <td>{{ al.etiqueta }}</td>
                  <td>{{ al.tipo === 'IA' ? 'IA' : 'Clásico' }}</td>
                  <td class="valor" [title]="al.etiqueta + ': ' + (al.primero.similitud | number: '1.4-4')">
                    <span class="barra"><span [style.width.%]="al.primero.similitud * 100"></span></span>
                    {{ al.primero.similitud | number: '1.4-4' }}
                  </td>
                  <td class="detalle">{{ al.primero.valorCrudo | number: '1.0-4' }} ({{ descripcionCrudo(al.nombre) }})</td>
                  <td>{{ al.primero.tiempoNs / 1e6 | number: '1.2-2' }} ms</td>
                </tr>
              }
            </tbody>
          </table>
        } @else {
          <h2>Matrices de similitud</h2>
          <div class="matrices">
            @for (al of r.algoritmos; track al.nombre) {
              <figure>
                <figcaption>{{ al.etiqueta }} <small>({{ al.tipo === 'IA' ? 'IA' : 'clásico' }},
                  {{ al.tiempoTotalMs | number: '1.1-1' }} ms)</small></figcaption>
                <table class="matriz">
                  <tr><th></th>@for (a of r.articulos; track a.id) { <th [title]="a.titulo">{{ a.id }}</th> }</tr>
                  @for (fila of al.matriz; track $index; let i = $index) {
                    <tr>
                      <th [title]="r.articulos[i].titulo">{{ r.articulos[i].id }}</th>
                      @for (v of fila; track $index; let j = $index) {
                        <td [style.background]="colorCelda(v)" [style.color]="v > 0.55 ? '#ffffff' : '#1a1a19'"
                            [title]="r.articulos[i].id + ' vs ' + r.articulos[j].id + ': ' + (v | number: '1.4-4')">
                          {{ v | number: '1.2-2' }}
                        </td>
                      }
                    </tr>
                  }
                </table>
              </figure>
            }
          </div>
        }

        @if (r.resumen.length) {
          <h2>Clásicos frente a IA</h2>
          <table class="resultados">
            <thead>
              <tr><th>Par</th><th>Promedio clásicos</th><th>Promedio IA</th><th>Diferencia (IA − clásicos)</th>
                <th>Valor más alto</th><th>Valor más bajo</th></tr>
            </thead>
            <tbody>
              @for (p of r.resumen; track p.idA + '-' + p.idB) {
                <tr>
                  <td>{{ p.idA }} – {{ p.idB }}</td>
                  <td>{{ p.promedioClasicos | number: '1.4-4' }}</td>
                  <td>{{ p.promedioIA | number: '1.4-4' }}</td>
                  <td>{{ p.diferencia | number: '1.4-4' }}</td>
                  <td>{{ p.algoritmoMayor }}</td>
                  <td>{{ p.algoritmoMenor }}</td>
                </tr>
              }
            </tbody>
          </table>
          <p class="nota">Las escalas no son equivalentes: Levenshtein compara caracteres y casi nunca baja de 0,2
            entre textos en inglés, y el promedio de vectores GloVe queda cerca de 0,9 para cualquier par del corpus.
            Compare cada algoritmo contra sí mismo (qué par queda más alto), no los valores absolutos entre algoritmos.</p>
        }

        @if (r.articulos.length === 2 && incluirTraza()) {
          <h2>Pasos intermedios</h2>
          @for (al of r.algoritmos; track al.nombre) {
            @if (al.primero.pasos.length) {
              <details>
                <summary>{{ al.etiqueta }} · {{ al.primero.pasos.length }} pasos</summary>
                <table class="pasos">
                  @for (p of al.primero.pasos; track p.numero) {
                    <tr><td>{{ p.numero }}</td><td><code>{{ p.descripcion }}</code></td><td>{{ p.datos }}</td></tr>
                  }
                </table>
              </details>
            }
          }
        }
      }
    </section>
  `,
  styles: [`
    .similitud { max-width: 1200px; margin: 0 auto; padding: 1rem; font-family: system-ui, sans-serif; color: #1a1a19; }
    .ayuda, .nota { color: #5f5e5a; }
    .columnas { display: grid; grid-template-columns: 2fr 1fr; gap: 1rem; }
    .panel { border: 1px solid #e3e2de; border-radius: 8px; padding: 1rem; background: #fcfcfb; }
    .lista { list-style: none; padding: 0; max-height: 360px; overflow-y: auto; }
    .lista li { padding: 4px 0; }
    .id { display: inline-block; min-width: 2em; font-weight: 600; }
    .algoritmo, .traza { display: block; margin: 4px 0; }
    .deshabilitado { color: #8a8984; }
    input[type=search] { width: 100%; padding: 6px; box-sizing: border-box; }
    button { margin-top: 1rem; padding: 8px 16px; }
    .error { color: #b3261e; }
    table { border-collapse: collapse; }
    .resultados { width: 100%; margin-bottom: 1rem; }
    .resultados th, .resultados td { border-bottom: 1px solid #e3e2de; padding: 6px 8px; text-align: left; vertical-align: top; }
    .valor { white-space: nowrap; font-variant-numeric: tabular-nums; }
    .barra { display: inline-block; width: 120px; height: 10px; background: #f0efec; vertical-align: middle; margin-right: 6px; }
    .barra span { display: block; height: 100%; background: #2a78d6; border-radius: 0 4px 4px 0; }
    .detalle { color: #5f5e5a; font-size: 0.9em; }
    .matrices { display: flex; flex-wrap: wrap; gap: 1.5rem; }
    .matriz th, .matriz td { padding: 4px 6px; text-align: center; font-variant-numeric: tabular-nums; }
    .matriz td { border: 2px solid #fcfcfb; min-width: 3em; }
    .pasos td { border-bottom: 1px solid #f0efec; padding: 2px 6px; font-size: 0.85em; }
    @media (max-width: 800px) { .columnas { grid-template-columns: 1fr; } }
  `],
})
export class SimilitudComponent implements OnInit {
  private readonly http = inject(HttpClient);

  readonly articulos = signal<ArticuloCorpus[]>([]);
  readonly algoritmos = signal<AlgoritmoInfo[]>([]);
  readonly seleccionados = signal<Set<string>>(new Set());
  readonly algoritmosElegidos = signal<Set<string>>(new Set());
  readonly filtro = signal('');
  readonly incluirTraza = signal(false);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly errorCorpus = signal<string | null>(null);
  readonly vista = signal<Vista | null>(null);

  readonly articulosFiltrados = computed(() => {
    const f = this.filtro().trim().toLowerCase();
    return f ? this.articulos().filter((a) => a.titulo.toLowerCase().includes(f)) : this.articulos();
  });

  readonly puedeComparar = computed(() => this.seleccionados().size >= 2 && this.algoritmosElegidos().size > 0);

  ngOnInit(): void {
    this.cargarCorpus();
    this.cargarAlgoritmos();
  }

  algoritmosDeTipo(tipo: string): AlgoritmoInfo[] {
    return this.algoritmos().filter((a) => a.tipo === tipo);
  }

  alternarArticulo(id: string): void {
    this.seleccionados.update((s) => alternar(s, id));
    if (this.seleccionados().size !== 2) {
      this.incluirTraza.set(false);
    }
  }

  alternarAlgoritmo(nombre: string): void {
    this.algoritmosElegidos.update((s) => alternar(s, nombre));
  }

  descripcionCrudo(nombre: string): string {
    return ETIQUETAS[nombre]?.crudo ?? '';
  }

  colorCelda(v: number): string {
    const i = Math.round(Math.max(0, Math.min(1, v)) * (RAMPA.length - 1));
    return RAMPA[i];
  }

  // API
  private cargarCorpus(): void {
    this.http.get<{ id: string | number; titulo: string }[]>('/api/corpus').subscribe({
      next: (lista) => this.articulos.set(lista.map((a) => ({ id: String(a.id), titulo: a.titulo }))),
      error: () => this.errorCorpus.set('No se pudo cargar el corpus (GET /api/corpus).'),
    });
  }

  // API
  private cargarAlgoritmos(): void {
    this.http.get<string[]>('/api/similitud/algoritmos').subscribe({
      next: (nombres) => {
        this.algoritmos.set(nombres.map((nombre) => ({
          nombre,
          etiqueta: ETIQUETAS[nombre]?.etiqueta ?? nombre,
          tipo: ETIQUETAS[nombre]?.tipo ?? 'CLASICO',
        })));
        this.algoritmosElegidos.set(new Set(nombres));
      },
      error: () => this.error.set('No se pudo consultar la lista de algoritmos.'),
    });
  }

  // API
  comparar(): void {
    const ids = this.articulos().filter((a) => this.seleccionados().has(a.id)).map((a) => a.id);
    this.cargando.set(true);
    this.error.set(null);
    this.http.post<SimilitudResponse>('/api/similitud', {
      ids,
      algoritmos: [...this.algoritmosElegidos()],
      conTraza: this.incluirTraza() && ids.length === 2,
    }).subscribe({
      next: (r) => {
        this.vista.set(armarVista(r, this.articulos().filter((a) => ids.includes(a.id))));
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.error.set(e.error?.error ?? 'Error al calcular la similitud.');
        this.cargando.set(false);
      },
    });
  }
}

function alternar(conjunto: Set<string>, id: string): Set<string> {
  const copia = new Set(conjunto);
  copia.has(id) ? copia.delete(id) : copia.add(id);
  return copia;
}

/** Convierte la lista de pares del backend en una matriz k×k por algoritmo y el resumen clásicos vs IA. */
function armarVista(r: SimilitudResponse, articulos: ArticuloCorpus[]): Vista {
  const indice = new Map(articulos.map((a, i) => [a.id, i] as const));
  const k = articulos.length;
  const porAlgoritmo = new Map<string, ResultadoAlgoritmo>();
  const resumen: ResumenPar[] = [];
  for (const par of r.pares) {
    const i = indice.get(par.idA)!;
    const j = indice.get(par.idB)!;
    const clasicos: number[] = [];
    const ia: number[] = [];
    let mayor = par.resultados[0];
    let menor = par.resultados[0];
    for (const res of par.resultados) {
      let al = porAlgoritmo.get(res.algoritmo);
      if (!al) {
        const info = ETIQUETAS[res.algoritmo];
        al = {
          nombre: res.algoritmo, etiqueta: info?.etiqueta ?? res.algoritmo, tipo: info?.tipo ?? 'CLASICO',
          matriz: Array.from({ length: k }, (_, f) => Array.from({ length: k }, (_, c) => (f === c ? 1 : 0))),
          primero: res, tiempoTotalMs: 0,
        };
        porAlgoritmo.set(res.algoritmo, al);
      }
      al.matriz[i][j] = res.similitud;
      al.matriz[j][i] = res.similitud;
      al.tiempoTotalMs += res.tiempoNs / 1e6;
      (al.tipo === 'IA' ? ia : clasicos).push(res.similitud);
      if (res.similitud > mayor.similitud) mayor = res;
      if (res.similitud < menor.similitud) menor = res;
    }
    const prom = (v: number[]) => (v.length ? v.reduce((s, x) => s + x, 0) / v.length : 0);
    resumen.push({
      idA: par.idA, idB: par.idB, promedioClasicos: prom(clasicos), promedioIA: prom(ia),
      diferencia: clasicos.length && ia.length ? prom(ia) - prom(clasicos) : 0,
      algoritmoMayor: mayor?.algoritmo ?? '', algoritmoMenor: menor?.algoritmo ?? '',
    });
  }
  return { articulos, algoritmos: [...porAlgoritmo.values()], resumen };
}
