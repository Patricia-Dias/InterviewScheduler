# Stage 1 build app
FROM node:14 as build
WORKDIR /app
COPY . /app
# Replace localhost with correct hostname
#ARG API_HOSTNAME=13.81.83.209
ARG API_HOSTNAME=172.20.202.10
#172.20.202.10
RUN sed -i "s/localhost/${API_HOSTNAME}/g" /app/src/api/axios.js
CMD cat src/api/axios.js
RUN npm install --silent
RUN npm run build --silent


# stage 2 run in nginx
# FROM nginx:stable-alpine

# COPY  --from=build /app/src/api/axios.js /axios.js
# RUN cat /axios.js
# COPY  --from=build /app/build/ /usr/share/nginx/html

FROM nginx:stable-alpine
COPY --from=build /app/build /usr/share/nginx/html
# new
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]