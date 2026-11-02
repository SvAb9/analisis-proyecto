import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ArticuloResumen, ClusteringResponse } from '../models/api.models';

/**
 * Llamadas HTTP al backend. Todas las rutas son relativas (/api/...): en desarrollo las reenvía
 * proxy.conf.json al backend en el puerto 8080, y en producción las atiende el mismo servidor.
 * Cada integrante agrega aquí los métodos de su requerimiento.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api';

  /** GET /api/corpus (módulo de corpus): artículos con su id y título. */
  corpus(): Observable<ArticuloResumen[]> {
    return this.http.get<ArticuloResumen[]>(`${this.base}/corpus`);
  }

  /** GET /api/similitud/algoritmos: nombres de los algoritmos de similitud disponibles. */
  algoritmosSimilitud(): Observable<string[]> {
    return this.http.get<string[]>(`${this.base}/similitud/algoritmos`);
  }

  /** POST /api/clustering: un dendrograma por criterio de enlace, con sus métricas. */
  agrupar(algoritmoSimilitud: string): Observable<ClusteringResponse> {
    return this.http.post<ClusteringResponse>(`${this.base}/clustering`, { algoritmoSimilitud });
  }
}
