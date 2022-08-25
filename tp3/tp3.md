# Trabajo Práctico 3 - Arquitectura de Sistemas Distribuidos - Ingenieria de Software III

**1) Sistema distribuido simple**

Ejecutar el siguiente comando para crear una red en docker

>`docker network create -d bridge mybridge`

Instanciar una base de datos Redis conectada a esa Red.

>`docker run -d --net mybridge --name db redis:alpine`


Levantar una aplicacion web, que utilice esta base de datos

>`docker run -d --net mybridge -e REDIS_HOST=db -e REDIS_PORT=6379 -p 5000:5000 --name web alexisfr/flask-app:latest`

Abrir un navegador y acceder a la URL: http://localhost:5000/

![Imagen 1](./src/ex1.png)

Verificar el estado de los contenedores y redes en Docker, describir:

* ¿Cuáles puertos están abiertos?
![Imagen 2](./src/ex1.2.png)
* Mostrar detalles de la red mybridge con Docker.
![Imagen 3](./src/ex1.3.png)
* ¿Qué comandos utilizó?

> `docker ps`

>`docker network inspect mybridge`

> **ACLARACIÓN:** Se cambio el puerto a 5001 ya que macOS por defecto utiliza el puerto 5000 para AirPlay Receiver.

**2) Análisis del sistema**

Siendo el código de la aplicación web el siguiente:

```python
import os
from flask import Flask
from redis import Redis


app = Flask(__name__)
redis = Redis(host=os.environ['REDIS_HOST'], port=os.environ['REDIS_PORT'])
bind_port = int(os.environ['BIND_PORT'])


@app.route('/')
def hello():
    redis.incr('hits')
    total_hits = redis.get('hits').decode()
    return f'Hello from Redis! I have been seen {total_hits} times.'


if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True, port=bind_port)
```

* Explicar cómo funciona el sistema

> El sistema incrementa en uno el contador de "hits" por cada vez que se ingresa al sitio. Es decir cada vez que hay un hit al endpoint se aumenta el contador. Este valor se toma de la BD en el documento "hits".

* ¿Para qué se sirven y porque están los parámetros -e en el segundo Docker run del ejercicio 1?

> Los parametros `-e` sirven para pasar parametros al entorno que se va a ejecutar.

* ¿Qué pasa si ejecuta docker rm -f web y vuelve a correr docker run -d --net mybridge -e REDIS_HOST=db -e REDIS_PORT=6379 -p 5000:5000 --name web alexisfr/flask-app:latest ?

> El contador de "hits" se resetea


* ¿Qué occure en la página web cuando borro el contenedor de Redis con docker rm -f db?

![Imagen 4](./src/ex2.png)

> Ocurre un error ya que al intentar instanciar redis no lo encuentra en el entorno.

* Y si lo levanto nuevamente con docker run -d --net mybridge --name db redis:alpine ?

> Vuelve a andar nuevamente pero con la perdida del contador.

*¿Qué considera usted que haría falta para no perder la cuenta de las visitas?

> Que al incializar levante un backup con el contador de la ultima vez que se inicializo

* Para eliminar los elementos creados corremos:

```bash
docker rm -f db
docker rm -f web
docker network rm mybridge
```

**3) Utilizando docker compose**

Normalmente viene como parte de la solucion cuando se instaló Docker

De ser necesario instalarlo hay que ejecutar:
```
sudo pip install docker-compose
```

Crear el siguente archivo docker-compose.yaml en un directorio de trabajo:

```yaml
version: '3.6'
services:
  app:
    image: alexisfr/flask-app:latest
    depends_on:
      - db
    environment:
      - REDIS_HOST=db
      - REDIS_PORT=6379
    ports:
      - "5001:5000"
  db:
    image: redis:alpine
    volumes:
      - redis_data:/data
volumes:
  redis_data:
```

Ejecutar `docker-compose up -d`

Acceder a la url http://localhost:5000/

Ejecutar `docker ps`, `docker network ls` y `docker volume ls`

¿Qué hizo Docker Compose por nosotros? Explicar con detalle.

> El archivo `.yaml` realizo todos los pasos previos pero unificandolos. Es decir, que levanto la imagen de flask en el el container app, en una segunda instacia definio la variables de entorno de redis en el puerto correspondiente e incluso genero un volumen donde se respalda la información almacenada por redis.

Desde el directorio donde se encuentra el archivo docker-compose.yaml ejecutar:

`docker-compose down`

**4) Aumentando la complejidad, análisis de otro sistema distribuido.**

Este es un sistema compuesto por:

* Una aplicación web de Python que te permite votar entre dos opciones

    * Una cola de Redis que recolecta nuevos votos
    * Un trabajador .NET o Java que consume votos y los almacena en...
        * Una base de datos de Postgres respaldada por un volumen de Docker

        * Una aplicación web Node.js que muestra los resultados de la votación en tiempo real.

Pasos:

1) Clonar el repositorio https://github.com/dockersamples/example-voting-app

2) Abrir una línea de comandos y ejecutar
```bash
cd example-voting-app`

docker-compose -f docker-compose-javaworker.yml up -d
Una vez terminado acceder a http://localhost:5001/ y http://localhost:5002
```

![Imagen 5](./src/ex4.png)

![Imagen 6](./src/ex4.1.png)

3) Emitir un voto y ver el resultado en tiempo real.

![Imagen 7](./src/ex4.2.png)

![Imagen 8](./src/ex4.3.png)

4) Para emitir más votos, abrir varios navegadores diferentes para poder hacerlo

5) Explicar como está configurado el sistema, puertos, volumenes componenetes involucrados, utilizar el Docker compose como guía.

> El docker-compose esta compuesto de 5 contenedores:
* **vote:** Es el encargado de generar la webapp con Python en el puerto 5001, es el webapp que permite al usuario emitir un voto. Que tambien hace uso del volumen en el directorio `/vote/app`

* **result:** Es el encargado de generar la webapp con Python en el puerto 5002, es el webapp que permite al usuario ver los distintos votos emitidos. Que tambien hace uso del volumen en el directorio `/result/app`

Tanto `vote`  como `result`  se encuentran en las networks `front-tier` y `back-tier`.

* **worker:** Es el encargado de correr el Dockerfile ubicado en el directorio `/worker`.

* **redis:** Contenedor a cargo de ejecutar la imagen de redis en el puerto 6379. En la red `back-tier`.

* **db:** Este contenedor levanta la imagen de Postgres 9.4 con el usuario `postgres` y contraseña `postgres`. Este levanta el volumen en el directorio `db-data:/var/lib/postgresql/data`

**5) Análisis detallado**

* Exponer más puertos para ver la configuración de Redis, y las tablas de PostgreSQL con alguna IDE como dbeaver.

* Revisar el código de la aplicación Python `example-voting-app\vote\app.py` para ver como envía votos a Redis.

* Revisar el código del worker `example-voting-app\worker\src\main\java\worker\Worker.java` para entender como procesa los datos.

* Revisar el código de la aplicacion que muestra los resultados `example-voting-app\result\server.js` para entender como muestra los valores.

* Escribir un documento de arquitectura sencillo, pero con un nivel de detalle moderado, que incluya algunos diagramas de bloques, de sequencia, etc y descripciones de los distintos componentes involucrados es este sistema y como interactuan entre sí.
