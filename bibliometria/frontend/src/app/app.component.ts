import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <a class="saltar" href="#contenido">Saltar al contenido</a>
    <div class="aplicacion">
      <aside class="lateral">
        <a class="marca" routerLink="/corpus">
          <span class="sello" aria-hidden="true">B</span>
          <span class="nombre">Bibliometría<small>Análisis de Algoritmos</small></span>
        </a>
        <nav aria-label="Principal">
          <p class="grupo">Datos</p>
          <a routerLink="/corpus" routerLinkActive="activo"><span class="clave">C</span>Corpus</a>
          <p class="grupo">Requerimientos</p>
          <a routerLink="/similitud" routerLinkActive="activo"><span class="clave">R1</span>Similitud</a>
          <a routerLink="/clustering" routerLinkActive="activo"><span class="clave">R2</span>Agrupamiento</a>
          <a routerLink="/busqueda" routerLinkActive="activo"><span class="clave">R3</span>Búsqueda</a>
          <a routerLink="/rendimiento" routerLinkActive="activo"><span class="clave">R3</span>Rendimiento</a>
        </nav>
        <p class="pie">Universidad del Quindío · 2026</p>
      </aside>
      <main id="contenido" tabindex="-1"><router-outlet /></main>
    </div>
  `,
  styles: [`
    .saltar { position: absolute; left: -999px; z-index: 10; padding: 0.5rem 1rem; background: var(--superficie); color: var(--tinta); border-radius: 0 0 8px 0; }
    .saltar:focus { left: 0; top: 0; }
    .aplicacion { display: grid; grid-template-columns: 15rem minmax(0, 1fr); min-height: 100dvh; }
    main { min-width: 0; outline: none; }
    .lateral { position: sticky; top: 0; display: flex; flex-direction: column; height: 100dvh; padding: 1.5rem 0.9rem 1.1rem; background: var(--superficie); border-right: 1px solid var(--linea); }
    .marca { display: flex; align-items: center; gap: 0.65rem; padding: 0 0.55rem 1.1rem; color: var(--tinta); text-decoration: none; }
    .sello { display: grid; place-items: center; flex: none; width: 2.1rem; height: 2.1rem; border-radius: 8px; background: var(--acento); color: #ffffff; font: 600 1.15rem/1 var(--titulo); }
    .nombre { font: 600 1.05rem/1.2 var(--titulo); }
    .nombre small { display: block; margin-top: 0.1rem; font: 400 0.72rem/1.3 var(--sans); color: var(--tinta-3); }
    nav { display: flex; flex-direction: column; gap: 0.12rem; flex: 1; }
    .grupo { margin: 1.1rem 0.6rem 0.35rem; font-size: 0.7rem; font-weight: 600; letter-spacing: 0.06em; text-transform: uppercase; color: var(--tinta-3); }
    nav a { display: flex; align-items: center; gap: 0.6rem; padding: 0.5rem 0.6rem; border-radius: 8px; color: var(--tinta-2); font-size: 0.93rem; text-decoration: none; transition: background-color 0.15s, color 0.15s; }
    nav a:hover { background: var(--hundido); color: var(--tinta); }
    nav a.activo { background: var(--acento-suave); color: var(--acento-oscuro); font-weight: 600; }
    .clave { flex: none; width: 1.55rem; text-align: center; font: 400 0.7rem/1 var(--mono); color: var(--tinta-3); }
    nav a.activo .clave { color: var(--acento); }
    .pie { margin: 0; padding: 0 0.6rem; font-size: 0.72rem; color: var(--tinta-3); }
    @media (max-width: 860px) {
      .aplicacion { display: block; }
      .lateral { z-index: 5; height: auto; padding: 0.7rem 0.75rem 0.55rem; border-right: 0; border-bottom: 1px solid var(--linea); }
      .marca { padding: 0 0.25rem 0.55rem; }
      .sello { width: 1.8rem; height: 1.8rem; font-size: 1rem; }
      .nombre small, .grupo, .pie, .clave { display: none; }
      nav { flex-direction: row; gap: 0.2rem; overflow-x: auto; scrollbar-width: none; }
      nav a { padding: 0.35rem 0.65rem; white-space: nowrap; font-size: 0.88rem; }
    }
  `],
})
export class AppComponent {}
