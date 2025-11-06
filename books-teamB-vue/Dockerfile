# ---------- Build (Vue) ----------
FROM node:lts-alpine AS build
WORKDIR /app

# cache deps
COPY package*.json ./
RUN npm ci

# code + build
COPY . .
RUN npm run build

# ---------- Runtime (Nginx + envsubst) ----------
FROM nginx:1.25-alpine

# (facultatif) valeur par défaut pour dev; sera écrasée par l'env Azure/Compose
ENV SERVER_URL=http://server:8080
# (optionnel) précise où écrire la conf générée
ENV NGINX_ENVSUBST_OUTPUT_DIR=/etc/nginx/conf.d

# modèle lu par /docker-entrypoint.d/20-envsubst-on-templates.sh
COPY default.conf.template /etc/nginx/templates/default.conf.template

# fichiers statiques
COPY --from=build /app/dist/ /usr/share/nginx/html/

EXPOSE 80
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://127.0.0.1/healthz || exit 1

# pas besoin d'ENTRYPOINT/CMD custom: l'image nginx lance envsubst toute seule
