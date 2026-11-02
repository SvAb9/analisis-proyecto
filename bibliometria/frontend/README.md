# Frontend (Angular)

Aplicación web de los tres requerimientos. Cada integrante agrega su pantalla en `src/app/pages/` y su ruta en `src/app/app.routes.ts`.

## Cómo ejecutarlo
Requiere Node.js 20.19+ (o 22.12+) y el backend corriendo en el puerto 8080.

```
npm install
npm start          # http://localhost:4200 (reenvía /api al backend por proxy.conf.json)
npm run build      # compila a dist/
```

## Estructura (`src/app/`)
- `pages/clustering/`: pantalla de R2 (tabla comparativa y un dendrograma por criterio de enlace)
- `components/dendrograma/`: dibuja un dendrograma en SVG a partir del árbol que devuelve la API
- `models/api.models.ts`: tipos de las respuestas de la API
- `services/api.service.ts`: llamadas HTTP al backend (cada quien agrega aquí las de su requerimiento)
- `app.routes.ts` y `app.html`: rutas y menú; agregar aquí la pantalla propia

Las demás pantallas (`corpus`, `similitud`, `busqueda`, `rendimiento`) siguen como archivos vacíos hasta que su dueño las implemente.
