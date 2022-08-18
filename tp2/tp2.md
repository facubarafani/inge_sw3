# Trabajo Práctico 2 - Ingenieria de Software III

**1) Instalar Docker Community Edition**

Diferentes opciones para cada sistema operativo
https://docs.docker.com/

Ejecutar el siguiente comando para comprobar versiones de cliente y demonio.

![Imagen 1](./src/ex1.png)

**2- Explorar DockerHub**
Registrase en docker hub: https://hub.docker.com/

Familiarizarse con el portal

**3) Obtener la imagen BusyBox**

Ejecutar el siguiente comando, para bajar una imagen de DockerHub

>`docker pull busybox`

![Imagen 2](./src/ex2.png)

Verificar qué versión y tamaño tiene la imagen bajada, obtener una lista de imágenes locales:

>`docker images`

![Imagen 3](./src/ex3.png)

**4) Ejecutando contenedores**

Ejecutar un contenedor utilizando el comando run de docker:

>`docker run busybox`

![Imagen 4](./src/ex4.png)

Explicar porque no se obtuvo ningún resultado

> Porque el comando `docker run` lo que hace es crear un contenedor a partir de la imagen, y en este caso al no estar pasandole ningun comando no realiza ninguna acción a no ser que la imagen tenga uno por defecto.

Especificamos algún comando a correr dentro del contendor, ejecutar por ejemplo:

>`docker run busybox echo "Hola Mundo"`

![Imagen 5](./src/ex4.1.png)

Ver los contendores ejecutados utilizando el comando ps:

>`docker ps`

![Imagen 6](./src/ex4.2.png)

Vemos que no existe nada en ejecución, correr entonces:

>`docker ps -a`

Mostrar el resultado y explicar que se obtuvo como salida del comando anterior.

![Imagen 6](./src/ex4.3.png)

> Nos ayudamos con el comando `docker ps --help` y nos da lo siguiente:
![Imagen 6](./src/ex4.4.png)
Es decir que agragando el parametro `-a` muestra todo los contenedores y no solo los que estan corriendo actualmente como hace el comando `docker ps` por defecto.

**5) Ejecutando en modo interactivo**

Ejecutar el siguiente comando

>`docker run -it busybox sh`

![Imagen 7](./src/ex5.png)

Para cada uno de los siguientes comandos dentro de contenedor, mostrar los resultados:

>`ps`

![Imagen 8](./src/ex5.1.png)

>`uptime`

![Imagen 9](./src/ex5.2.png)

>`free`

![Imagen 10](./src/ex5.3.png)

>`ls -l /`

![Imagen 11](./src/ex5.4.png)

Salimos del contendor con:

>`exit`

**6) Borrando contendores terminados**

Obtener la lista de contendores

>`docker ps -a`

![Imagen 12](./src/ex6.png)

Para borrar podemos utilizar el id o el nombre (autogenerado si no se especifica) de contendor que se desee, por ejemplo:

>`docker rm elated_lalande`

![Imagen 13](./src/ex6.1.png)

Para borrar todos los contendores que no estén corriendo, ejecutar cualquiera de los siguientes comandos:

>`docker rm $(docker ps -a -q -f status=exited)`

![Imagen 14](./src/ex6.2.png)

>`docker container prune`

![Imagen 14](./src/ex6.3.png)

**7) Montando volúmenes**

Hasta este punto los contenedores ejecutados no tenían contacto con el exterior, ellos corrían en su propio entorno hasta que terminaran su ejecución. 

Ahora veremos cómo montar un volumen dentro del contenedor para visualizar por ejemplo archivos del sistema huésped:

Ejecutar el siguiente comando, cambiar myusuario por el usuario que corresponda.

 En linux/Mac puede utilizarse /home/miusuario):

>`docker run -it -v C:\Users\misuario\Desktop:/var/escritorio busybox /bin/sh`

![Imagen 15](./src/ex7.png)

Dentro del contenedor correr

>`ls -l /var/escritorio`

![Imagen 15](./src/ex7.1.png)

>`touch /var/escritorio/hola.txt`

![Imagen 15](./src/ex7.2.png)

Verificar que el Archivo se ha creado en el escritorio o en el directorio home según corresponda.

![Imagen 16](./src/ex7.3.png)

**8) Publicando puertos**

En el caso de aplicaciones web o base de datos donde se interactúa con estas aplicaciones a través de un puerto al cual hay que acceder, estos puertos están visibles solo dentro del contenedor. Si queremos acceder desde el exterior deberemos exponerlos.

Ejecutar la siguiente imagen, en este caso utilizamos la bandera -d (detach) para que nos devuelva el control de la consola:

>`docker run -d daviey/nyan-cat-web`

![Imagen 17](./src/ex8.png)

Si ejecutamos un comando ps:

PS D:\> docker ps

CONTAINER ID        IMAGE                 COMMAND                  CREATED             STATUS              PORTS               NAMES
87d1c5f44809        daviey/nyan-cat-web   "nginx -g 'daemon of…"   2 minutes ago       Up 2 minutes        80/tcp, 443/tcp     compassionate_raman

Vemos que el contendor expone 2 puertos el 80 y el 443, pero si intentamos en un navegador acceder a http://localhost no sucede nada.

![Imagen 18](./src/ex8.1.png)

Procedemos entonces a parar y remover este contenedor:

>`docker kill compassionate_raman`

>`docker rm compassionate_raman`

![Imagen 18](./src/ex8.2.png)

Vamos a volver a correrlo otra vez, pero publicando uno de los puertos solamente, el este caso el 80

>`docker run -d -p 80:80 daviey/nyan-cat-web`

![Imagen 19](./src/ex8.3.png)


Accedamos nuevamente a http://localhost y expliquemos que sucede.

![Imagen 20](./src/ex8.4.png)

**9) Utilizando una base de datos**

Levantar una base de datos PostgreSQL

>`mkdir $HOME/.postgres`

>`docker run --name my-postgres -e POSTGRES_PASSWORD=mysecretpassword -v $HOME/.postgres:/var/lib/postgresql/data -p 5432:5432 -d postgres:9.4`

![Imagen 21](./src/ex9.png)

Ejecutar sentencias utilizando esta instancia

>`docker exec -it my-postgres /bin/bash`

![Imagen 22](./src/ex9.1.png)

>`psql -h localhost -U postgres`

![Imagen 20](./src/ex9.2.png)

#Estos comandos se corren una vez conectados a la base

>`\l
>create database test;
>\connect test
>create table tabla_a (mensaje varchar(50));
>insert into tabla_a (mensaje) values('Hola mundo!');
>select * from tabla_a;
>\q`

![Imagen 22](./src/ex9.3.png)

exit

Conectarse a la base utilizando alguna IDE (Dbeaver - https://dbeaver.io/, eclipse, IntelliJ, etc...). Interactuar con los objectos objectos creados.

![Imagen 23](./src/ex9.4.png)

Explicar que se logro con el comando docker run y docker exec ejecutados en este ejercicio.

> Con el comando docker run se pudo generar, y en este caso al no tener la imagen descargarla del motor de base de datos PostgreSQL version 9.4 llamando al contenedor `my_postgres` y asignando como contraseña del motor `mysecretpassword` en el puerto 5432.

> Y luego con el comando `docker exec` ejecutamos dentro del contenedor `/bin/bash` es decir para acceder a la consola de nuestro contenedor para desde ahi ejecutar sentencias de PostgreSQL.