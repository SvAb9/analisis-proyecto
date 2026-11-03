# Despliegue (R4). Compila Angular y lo sirve con nginx, que además reenvía /api al backend.
FROM node:22-alpine AS compilacion
WORKDIR /build
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=compilacion /build/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
