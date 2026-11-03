import { Routes } from '@angular/router';

import { BusquedaComponent } from './pages/busqueda/busqueda.component';
import { ClusteringComponent } from './pages/clustering/clustering.component';
import { CorpusComponent } from './pages/corpus/corpus.component';
import { RendimientoComponent } from './pages/rendimiento/rendimiento.component';
import { SimilitudComponent } from './pages/similitud/similitud.component';

/** Rutas de la aplicación; cada pantalla también va en el menú de app.component.ts. */
export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'corpus' },
  { path: 'corpus', component: CorpusComponent, title: 'Corpus' },
  { path: 'similitud', component: SimilitudComponent, title: 'Similitud textual' },
  { path: 'clustering', component: ClusteringComponent, title: 'Agrupamiento jerárquico' },
  { path: 'busqueda', component: BusquedaComponent, title: 'Búsqueda de patrones' },
  { path: 'rendimiento', component: RendimientoComponent, title: 'Rendimiento' },
  { path: '**', redirectTo: 'corpus' },
];