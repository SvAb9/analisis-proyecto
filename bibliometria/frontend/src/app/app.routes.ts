import { Routes } from '@angular/router';

import { ClusteringComponent } from './pages/clustering/clustering.component';
import { SimilitudComponent } from './pages/similitud/similitud.component';

/**
 * Rutas de la aplicación. Cuando Zuleta termine sus pantallas (corpus, busqueda, rendimiento)
 * se agregan aquí y en el menú de app.component.ts.
 */
export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'clustering' },
  { path: 'similitud', component: SimilitudComponent, title: 'Similitud textual' },
  { path: 'clustering', component: ClusteringComponent, title: 'Agrupamiento jerárquico' },
  { path: '**', redirectTo: 'clustering' },
];