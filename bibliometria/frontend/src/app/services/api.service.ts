// Llamadas HTTP a la API.
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  Articulo,
  ArticuloResumen,
  BenchmarkResponse,
  BusquedaResponse,
  ClusteringResponse,
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api';

  corpus(): Observable<ArticuloResumen[]> {
    return this.http.get<ArticuloResumen[]>(`${this.base}/corpus`);
  }

  articulos(): Observable<Articulo[]> {
    return this.http.get<Articulo[]>(`${this.base}/corpus`);
  }

  buscar(patron: string, conTraza: boolean, articuloId: string | null): Observable<BusquedaResponse> {
    return this.http.post<BusquedaResponse>(`${this.base}/busqueda`, { patron, conTraza, articuloId });
  }

  benchmark(patron: string, repeticiones: number): Observable<BenchmarkResponse> {
    const params = new HttpParams().set('patron', patron).set('repeticiones', repeticiones);
    return this.http.get<BenchmarkResponse>(`${this.base}/benchmark`, { params });
  }

  algoritmosSimilitud(): Observable<string[]> {
    return this.http.get<string[]>(`${this.base}/similitud/algoritmos`);
  }

  agrupar(algoritmoSimilitud: string): Observable<ClusteringResponse> {
    return this.http.post<ClusteringResponse>(`${this.base}/clustering`, { algoritmoSimilitud });
  }
}
