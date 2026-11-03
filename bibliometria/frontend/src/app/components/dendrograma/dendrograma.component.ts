// Dibuja el dendrograma en SVG.
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import { NodoDendrograma } from '../../models/api.models';
import { MARGEN, calcularLayout } from './dendrograma.layout';

@Component({
  selector: 'app-dendrograma',
  templateUrl: './dendrograma.component.html',
  styleUrl: './dendrograma.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DendrogramaComponent {
  readonly arbol = input.required<NodoDendrograma>();
  readonly etiquetas = input<Record<string, string>>({});
  readonly ajustar = input(false);
  readonly descripcion = input('Dendrograma');

  protected readonly margen = MARGEN;
  protected readonly layout = computed(() => calcularLayout(this.arbol(), this.ajustar()));

  protected etiqueta(id: string): string {
    const titulo = this.etiquetas()[id];
    if (!titulo) {
      return `Artículo ${id}`;
    }
    const corto = titulo.length > 30 ? titulo.slice(0, 29) + '…' : titulo;
    return `${id} · ${corto}`;
  }

  protected titulo(id: string): string {
    return this.etiquetas()[id] ?? `Artículo ${id}`;
  }
}
