#Stage - 1, build react applicaion
FROM node:20-alpine AS builder
WORKDIR /build
COPY package*.json .
RUN npm ci
COPY . .
RUN npm run build

#Stage - 2, deploy using nginx
FROM nginx:stable-alpine
COPY --from=builder /build/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]