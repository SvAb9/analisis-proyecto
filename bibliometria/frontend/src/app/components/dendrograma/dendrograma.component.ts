import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import { NodoDendrograma } from '../../models/api.models';
import { MARGEN, calcularLayout } from './dendrograma.layout';

/**
 * Dibuja un dendrograma (árbol de agrupamiento) en SVG a partir del árbol que devuelve el backend.
 * La altura de cada unión es la distancia a la que se fusionaron los dos grupos: cuanto más a la
 * derecha se unen, menos se parecen.
 */
@Component({
  selector: 'app-dendrograma',
  templateUrl: './dendrograma.component.html',
  styleUrl: './dendrograma.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DendrogramaComponent {
  /** Raíz del árbol. */
  readonly arbol = input.required<NodoDendrograma>();
  /** Título de cada artículo por id; si falta, se muestra solo el id. */
  readonly etiquetas = input<Record<string, string>>({});
  /** Si es true, el eje no arranca en 0 sino cerca de la primera unión (más legible cuando las distancias son parecidas). */
  readonly ajustar = input(false);
  /** Texto alternativo para lectores de pantalla. */
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
