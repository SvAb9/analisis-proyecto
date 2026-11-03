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
    .aplicacion { display: grid; grid-template-columns: 15.5rem minmax(0, 1fr); min-height: 100dvh; }
    main { min-width: 0; outline: none; }
    .lateral { position: sticky; top: 0; display: flex; flex-direction: column; height: 100dvh; padding: 1.5rem 0.9rem 1.1rem; background: var(--lateral); color: var(--lateral-tinta); }
    .marca { display: flex; align-items: center; gap: 0.7rem; padding: 0 0.55rem 1.3rem; color: inherit; text-decoration: none; }
    .sello { display: grid; place-items: center; flex: none; width: 2.2rem; height: 2.2rem; border-radius: 8px; background: #f7f5ef; color: #0f2a1c; font: 600 1.35rem/1 var(--serif); }
    .nombre { font: 600 1.14rem/1.2 var(--serif); }
    .nombre small { display: block; margin-top: 0.1rem; font: 400 0.72rem/1.3 var(--sans); color: var(--lateral-tenue); }
    nav { display: flex; flex-direction: column; gap: 0.12rem; flex: 1; }
    .grupo { margin: 1.1rem 0.6rem 0.35rem; font-size: 0.68rem; font-weight: 600; letter-spacing: 0.1em; text-transform: uppercase; color: var(--lateral-tenue); }
    nav a { display: flex; align-items: center; gap: 0.6rem; padding: 0.48rem 0.6rem; border-radius: 7px; color: var(--lateral-tinta); font-size: 0.93rem; text-decoration: none; transition: background-color 0.15s; }
    nav a:hover { background: rgb(255 255 255 / 0.07); }
    nav a:focus-visible, .marca:focus-visible { outline-color: #f8df8b; }
    nav a.activo { background: rgb(255 255 255 / 0.13); color: #ffffff; font-weight: 500; box-shadow: inset 2px 0 0 #f8df8b; }
    .clave { flex: none; width: 1.55rem; text-align: center; font: 400 0.7rem/1 var(--mono); color: var(--lateral-tenue); }
    nav a.activo .clave { color: #f8df8b; }
    .pie { margin: 0; padding: 0 0.6rem; font-size: 0.72rem; color: var(--lateral-tenue); }
    @media (max-width: 860px) {
      .aplicacion { display: block; }
      .lateral { position: sticky; z-index: 5; height: auto; padding: 0.7rem 0.75rem 0.55rem; }
      .marca { padding: 0 0.25rem 0.55rem; }
      .sello { width: 1.8rem; height: 1.8rem; font-size: 1.1rem; }
      .nombre small, .grupo, .pie, .clave { display: none; }
      nav { flex-direction: row; gap: 0.2rem; overflow-x: auto; scrollbar-width: none; }
      nav a { padding: 0.35rem 0.65rem; white-space: nowrap; font-size: 0.88rem; }
      nav a.activo { box-shadow: inset 0 -2px 0 #f8df8b; }
    }
  `],
})
export class AppComponent {}
