import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <nav class="menu" aria-label="Principal">
      <strong>Bibliometría</strong>
      <a routerLink="/corpus" routerLinkActive="activo">Corpus</a>
      <a routerLink="/similitud" routerLinkActive="activo">Similitud (R1)</a>
      <a routerLink="/clustering" routerLinkActive="activo">Clustering (R2)</a>
      <a routerLink="/busqueda" routerLinkActive="activo">Búsqueda (R3)</a>
      <a routerLink="/rendimiento" routerLinkActive="activo">Rendimiento (R3)</a>
    </nav>
    <router-outlet />
  `,
  styles: [`
    .menu { display: flex; flex-wrap: wrap; gap: 0.5rem 1.25rem; align-items: center; padding: 0.75rem 1rem;
      border-bottom: 1px solid #e3e2de; font-family: system-ui, sans-serif; }
    .menu a { color: #1c5cab; text-decoration: none; padding-bottom: 2px; }
    .menu a.activo { border-bottom: 2px solid #1c5cab; font-weight: 600; }
  `],
})
export class AppComponent {}