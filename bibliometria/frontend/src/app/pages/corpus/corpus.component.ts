import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';

import { Articulo } from '../../models/api.models';
import { ApiService } from '../../services/api.service';

/** Lista de artículos y vista del abstract. */
@Component({
  selector: 'app-corpus',
  imports: [DecimalPipe],
  templateUrl: './corpus.component.html',
  styleUrl: './corpus.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CorpusComponent implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly articulos = signal<Articulo[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly filtro = signal('');
  protected readonly verProcesado = signal(false);

  protected readonly visibles = computed(() => {
    const texto = this.filtro().trim().toLowerCase();
    if (!texto) {
      return this.articulos();
    }
    return this.articulos().filter(
      (a) => a.titulo.toLowerCase().includes(texto) || a.autores.join(' ').toLowerCase().includes(texto),
    );
  });

  protected readonly cifras = computed(() => {
    const articulos = this.articulos();
    const palabras = articulos.reduce((total, a) => total + this.palabras(a.abstractCrudo), 0);
    return {
      articulos: articulos.length,
      autores: new Set(articulos.flatMap((a) => a.autores)).size,
      palabras,
      promedio: articulos.length ? Math.round(palabras / articulos.length) : 0,
    };
  });

  ngOnInit(): void {
    this.api.articulos().subscribe({
      next: (articulos) => {
        this.articulos.set(articulos);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar el corpus. ¿Está encendido el backend?');
        this.cargando.set(false);
      },
    });
  }

  protected palabras(texto: string): number {
    return texto.trim().split(/\s+/).length;
  }
}
