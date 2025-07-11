# Configuración de Apache Lucene en Contenedores

![raw](../../images/lucene/lucene.png)

Este documento describe cómo configurar Apache Lucene para indexación y búsqueda de texto en un entorno containerizado, asegurando compatibilidad con todas las plataformas (Linux, macOS, Windows, incluyendo WSL).

## Tabla de Contenidos

- [Configuración de Apache Lucene en Contenedores](#configuración-de-apache-lucene-en-contenedores)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [Prerrequisitos](#prerrequisitos)
  - [Paso 1: Crear un Proyecto Maven para Lucene](#paso-1-crear-un-proyecto-maven-para-lucene)
  - [Paso 2: Crear un Docker Compose File](#paso-2-crear-un-docker-compose-file)
  - [Paso 2: Desplegar Lucene con Docker Compose](#paso-2-desplegar-lucene-con-docker-compose)
    - [Gestión rápida con Makefile](#gestión-rápida-con-makefile)
  - [Paso 3: Desplegar Lucene con Docker Compose](#paso-3-desplegar-lucene-con-docker-compose)
  - [Paso 4: Verificar el Despliegue](#paso-4-verificar-el-despliegue)
  - [Paso 5: Integración con Otros Sistemas](#paso-5-integración-con-otros-sistemas)
    - [Dependencias Maven necesarias](#dependencias-maven-necesarias)
    - [Dependencias Maven](#dependencias-maven)
  - [Paso 6: Gestión del Contenedor de Lucene](#paso-6-gestión-del-contenedor-de-lucene)
  - [Consejos para Compatibilidad Multiplataforma](#consejos-para-compatibilidad-multiplataforma)
  - [Resolución de Problemas](#resolución-de-problemas)
  - [Recursos Adicionales](#recursos-adicionales)
  - [Volver a su ficha](#volver-a-su-ficha)

## Prerrequisitos

Antes de comenzar, asegúrate de lo siguiente:

- **Docker** está instalado y en ejecución.
- **Docker Compose** está instalado (se recomienda la versión 2.29.x o superior). Verifica con:

> Puedes utilizar el script de `docker_install.sh` de la carpeta `/resources` para instalar ambas cosas en WSL/Ubuntu.

- Tienes suficiente espacio en disco para los índices de Lucene (al menos 1GB para pruebas).
- Un entorno de desarrollo Java (si deseas trabajar fuera del contenedor). La configuración usará un contenedor con Java y Maven preinstalados.

## Paso 1: Crear un Proyecto Maven para Lucene

Lucene es una biblioteca Java, por lo que crearemos un proyecto Maven para gestionar dependencias y ejecutarlo en un contenedor. Crea una estructura de directorio y los archivos necesarios. Tambien puedes usar los ficheros pregenerados de la carpeta `resources/lucene/`. Este enfoque asegura consistencia entre plataformas.

docker-compose.yml

```yml
services:
  lucene:
    image: maven:3.9.8-eclipse-temurin-11
    container_name: lucene
    volumes:
      - ./:/app
      - lucene_data:/lucene-index
    working_dir: /app
    command: mvn clean compile exec:java -Dexec.mainClass=com.example.LuceneExample
    healthcheck:
      test: ["CMD", "ls", "/lucene-index"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  lucene_data:
```

pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>lucene-example</artifactId>
    <version>1.0-SNAPSHOT</version>
    <dependencies>
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-core</artifactId>
            <version>9.11.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-analysis-common</artifactId>
            <version>9.11.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-queryparser</artifactId>
            <version>9.11.1</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

LuceneExample.java

```java
src/main/java/com/example/LuceneExample.java
package com.example;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import java.nio.file.*;
import java.io.*;
import java.util.*;

public class LuceneExample {
    public static void main(String[] args) throws Exception {
        String indexPath = "/lucene-index";
        String docsPath = "docs"; // Carpeta con varios txt
        String busqueda = args.length > 0 ? args[0] : "búsqueda";

        FSDirectory directory = FSDirectory.open(Paths.get(indexPath));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        // Indexar todos los archivos txt de la carpeta docs
        try (IndexWriter writer = new IndexWriter(directory, config)) {
            Files.list(Paths.get(docsPath))
                .filter(path -> path.toString().endsWith(".txt"))
                .forEach(path -> {
                    try {
                        String content = Files.readString(path);
                        Document doc = new Document();
                        doc.add(new TextField("content", content, Field.Store.YES));
                        doc.add(new StringField("filename", path.getFileName().toString(), Field.Store.YES));
                        writer.addDocument(doc);
                        System.out.println("[INDEXADO] " + path.getFileName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }

        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(busqueda);

            TopDocs results = searcher.search(query, 10);
            System.out.println("\nResultados encontrados: " + results.totalHits.value);

            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document resultDoc = searcher.doc(scoreDoc.doc);
                String filename = resultDoc.get("filename");
                String content = resultDoc.get("content");
                float score = scoreDoc.score;

                System.out.println(" - Archivo: " + filename);
                System.out.printf("   Score: %.4f%n", score);

                // Contar ocurrencias (ignorando mayúsculas/minúsculas)
                int count = countOccurrences(content.toLowerCase(), busqueda.toLowerCase());
                System.out.println("   Ocurrencias de '" + busqueda + "': " + count);

                // Mostrar líneas donde aparece la palabra
                List<Integer> lines = findLinesContainingWord(content, busqueda);
                System.out.print("   Líneas: ");
                if (lines.isEmpty()) {
                    System.out.println("No se encontraron líneas (¿indexado reciente?)");
                } else {
                    System.out.println(lines);
                }
            }
        }
    }

    // Método para contar cuántas veces aparece una palabra en el texto
    private static int countOccurrences(String text, String word) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(word, idx)) != -1) {
            count++;
            idx += word.length();
        }
        return count;
    }

    // Método para encontrar números de línea que contienen la palabra
    private static List<Integer> findLinesContainingWord(String content, String word) {
        List<Integer> lines = new ArrayList<>();
        String[] splitLines = content.split("\\r?\\n");
        for (int i = 0; i < splitLines.length; i++) {
            if (splitLines[i].toLowerCase().contains(word.toLowerCase())) {
                lines.add(i + 1); // Índice de línea empezando en 1
            }
        }
        return lines;
    }
}
```

- **Explicación**:
  - **pom.xml**: Define un proyecto Maven con las dependencias de Lucene (`lucene-core`, `lucene-analysis-common`, `lucene-queryparser`) en la versión 9.11.1 (la más reciente a julio de 2025). Usa Java 11 para compatibilidad.
  - **LuceneExample.java**: Implementa el ejemplo proporcionado, creando un índice, añadiendo un documento y realizando una búsqueda. El directorio de índices se establece en `/lucene-index` para usarlo dentro del contenedor.
  - **Estructura**: Crea la estructura de directorios estándar de Maven (`src/main/java/com/example`).

## Paso 2: Crear un Docker Compose File

Crea un archivo `docker-compose.yml` para ejecutar el proyecto Lucene en un contenedor con Java y Maven, asegurando portabilidad.

```bash
cat <<EOF > docker-compose.yml
services:
  lucene:
    image: maven:3.9.8-eclipse-temurin-11
    container_name: lucene
    volumes:
      - ./:/app
      - lucene_data:/lucene-index
    working_dir: /app
    command: mvn clean compile exec:java -Dexec.mainClass=com.example.LuceneExample
    healthcheck:
      test: ["CMD", "ls", "/lucene-index"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  lucene_data:
EOF
```

- **Explicación**:
  - **Imagen**: Usa `maven:3.9.8-eclipse-temurin-11`, que incluye Maven 3.9.8 y Java 11, compatible con Lucene.
  - **Container Name**: Asigna el nombre `lucene` para referencia sencilla.
  - **Volúmenes**:
    - Monta el directorio local (`./`) en `/app` para acceder al proyecto Maven.
    - Usa un volumen Docker `lucene_data` para persistir los índices en `/lucene-index`.
  - **Comando**: Ejecuta `mvn clean compile exec:java` para compilar y ejecutar la clase `LuceneExample`.
  - **Healthcheck**: Verifica que el directorio de índices exista, indicando que el contenedor está operativo.

## Paso 2: Desplegar Lucene con Docker Compose

Inicia los contenedores usando Docker Compose.

```bash
docker-compose up -d
```

- **Explicación**:
  - El flag `-d` ejecuta los contenedores en segundo plano.
  - Verifica que los contenedores estén en ejecución:

    ```bash
    docker ps
    ```

    Deberías ver el contenedor `lucene`.

---

### Gestión rápida con Makefile

En la carpeta `resources/lucene/` tienes un `Makefile` preparado para gestionar Lucene fácilmente:

```makefile
LUCENE_COMPOSE=docker-compose.yml

up:
    docker compose -f $(LUCENE_COMPOSE) up -d

down:
    docker compose -f $(LUCENE_COMPOSE) down

restart:
    docker compose -f $(LUCENE_COMPOSE) restart

logs:
    docker compose -f $(LUCENE_COMPOSE) logs -f
```

Ejecuta los siguientes comandos desde esa carpeta:

- Levantar Lucene:

  ```bash
  make up
  ```

- Parar Lucene:

  ```bash
  make down
  ```

- Reiniciar Lucene:

  ```bash
  make restart
  ```

- Ver logs:

  ```bash
  make logs
  ```

> **Nota:** Para usar los comandos `make`, asegúrate de tener instalado el paquete `make`.
> Puedes instalarlo en sistemas basados en Debian/Ubuntu con:

```bash
sudo apt install make
```

---

## Paso 3: Desplegar Lucene con Docker Compose

Inicia el contenedor para compilar y ejecutar el programa Lucene.

```bash
docker-compose up -d
```

- **Explicación**:
  - El flag `-d` ejecuta el contenedor en segundo plano.
  - El contenedor compila el proyecto y ejecuta el programa, creando un índice y mostrando los resultados de la búsqueda.
  - Verifica que el contenedor esté en ejecución:
  
    ```bash
    docker ps
    ```

## Paso 4: Verificar el Despliegue

1. **Comprobar los Logs**:
   Revisa los logs del contenedor para confirmar que el programa se ejecutó correctamente:

   ```bash
   docker logs lucene
   ```

   Deberías ver una salida como:

   ```txt
    [INDEXADO] documento.txt
    [INDEXADO] inteligencia_artificial.txt

    Resultados encontrados: 1
    - Archivo: documento.txt
      Score: 0.5915
      Ocurrencias de 'búsqueda': 14
      Líneas: [3, 5, 7, 11, 13, 15, 17, 19, 23, 29]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time:  10.074 s
    [INFO] Finished at: 2025-07-04T11:57:04Z
    [INFO] ------------------------------------------------------------------------
   ```

   > EXPLICACIÓN: Dado dos ficheros de texto en la carpeta /docs, se ha buscado una palabra clave "búsqueda", que solo aparece en el fichero "documento.txt", y que además, aparece 14 veces indicandote las líneas en cuestión.

2. **Inspeccionar los Índices**:
   Verifica que los índices se crearon en el volumen:

   ```bash
   docker volume ls | grep lucene_data
   docker run --rm -v lucene_lucene_data:/lucene-index alpine ls /lucene-index
   ```

   Deberías ver archivos como `segments_1_`, `_0.cfs`, etc., indicando que el índice fue creado.

   También se puede hacer de forma interactiva en el contenedor:

    ```bash
     docker run --rm -it -v lucene_lucene_data:/lucene-index alpine sh
     ls /lucene-index/
    ```

## Paso 5: Integración con Otros Sistemas

Apache Lucene puede integrarse con otros sistemas para enriquecer las capacidades de indexación y búsqueda, como MinIO para almacenamiento distribuido o Kafka para ingestión en tiempo real de datos. A continuación, se describen ejemplos de integración con ambos.

1. **Integración con MinIO**:

   MinIO es un sistema de almacenamiento compatible con API S3 que permite guardar índices Lucene en la nube o en almacenamiento distribuido.

   ### Dependencias Maven necesarias

   Además de las dependencias básicas de Lucene, añade la dependencia para lucene-s3-directory y el AWS SDK:

   ```xml
    <dependency>
        <groupId>org.apache.lucene</groupId>
        <artifactId>lucene-s3-directory</artifactId>
        <version>9.11.1</version>
    </dependency>
    <dependency>
        <groupId>com.amazonaws</groupId>
        <artifactId>aws-java-sdk-s3</artifactId>
        <version>1.12.540</version>
    </dependency>
   ```

   Ejemplo básico para configurar el cliente S3 y usar S3Directory con MinIO:

   ```java
      import com.amazonaws.auth.AWSStaticCredentialsProvider;
      import com.amazonaws.auth.BasicAWSCredentials;
      import com.amazonaws.client.builder.AwsClientBuilder;
      import com.amazonaws.services.s3.AmazonS3;
      import com.amazonaws.services.s3.AmazonS3ClientBuilder;
      import org.apache.lucene.store.s3.S3Directory;

      public class LuceneMinIOExample {
          public static void main(String[] args) throws Exception {
              // Configura las credenciales (usa variables de entorno o config externa en producción)
              BasicAWSCredentials creds = new BasicAWSCredentials("admin", "admin123");

              // Configura el cliente S3 para MinIO (endpoint local)
              AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                  .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:9000", "us-east-1"))
                  .withCredentials(new AWSStaticCredentialsProvider(creds))
                  .withPathStyleAccessEnabled(true) // Importante para MinIO
                  .build();

              // Define el bucket y el prefijo para el índice
              String bucketName = "my-bucket";
              String indexPrefix = "lucene-index";

              // Crea el directorio S3 para Lucene
              S3Directory s3Directory = new S3Directory(s3Client, bucketName, indexPrefix);

              // Aquí continuarías con la creación del IndexWriter, etc.
          }
      }
   ```

2. **Integración con Kafka**:

   Kafka es una plataforma de mensajería distribuida muy usada para manejar flujos de datos en tiempo real. Puedes consumir mensajes y enviar su contenido para indexación en Lucene.

   ### Dependencias Maven

   Añade la dependencia oficial de Kafka para Java:

    ```xml
      <dependency>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka-clients</artifactId>
        <version>3.5.1</version>
      </dependency>
    ```

    Ejemplo básico de consumidor Kafka en Java con indexación en Lucene

   ```java
      import org.apache.kafka.clients.consumer.ConsumerRecord;
      import org.apache.kafka.clients.consumer.ConsumerRecords;
      import org.apache.kafka.clients.consumer.KafkaConsumer;
      import org.apache.lucene.document.Document;
      import org.apache.lucene.document.Field;
      import org.apache.lucene.document.TextField;
      import org.apache.lucene.index.IndexWriter;

      import java.time.Duration;
      import java.util.Arrays;
      import java.util.Properties;

      public class LuceneKafkaConsumer {
          public static void main(String[] args) {
              Properties props = new Properties();
              props.put("bootstrap.servers", "localhost:29092");
              props.put("group.id", "lucene-group");
              props.put("auto.offset.reset", "earliest");
              props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
              props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

              KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
              consumer.subscribe(Arrays.asList("test-topic"));

              // Supón que indexWriter ya está inicializado y es thread-safe o se sincroniza
              IndexWriter indexWriter = /* inicializa tu IndexWriter aquí */;

              try {
                  while (true) {
                      ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                      for (ConsumerRecord<String, String> record : records) {
                          Document doc = new Document();
                          doc.add(new TextField("content", record.value(), Field.Store.YES));
                          // Añadir más campos si quieres (timestamp, key, etc.)

                          indexWriter.addDocument(doc);
                      }
                      indexWriter.commit(); // confirma cambios periódicamente
                  }
              } catch (Exception e) {
                  e.printStackTrace();
              } finally {
                  consumer.close();
                  try {
                      indexWriter.close();
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          }
      }
   ```

## Paso 6: Gestión del Contenedor de Lucene

- **Detener el Contenedor**:

  ```bash
  docker-compose down
  ```

  Esto detiene y elimina el contenedor, pero conserva el volumen `lucene_data`.

- **Reiniciar el Contenedor**:

  ```bash
  docker-compose up -d
  ```

- **Eliminar el Volumen (si es necesario)**:
  Para borrar todos los índices:

  ```bash
  docker volume rm lucene-project_lucene_data
  ```

## Consejos para Compatibilidad Multiplataforma

- **Volúmenes de Docker**: El volumen `lucene_data` asegura la persistencia de los índices entre reinicios del contenedor y es compatible con todas las plataformas soportadas por Docker.
- **Analizadores Específicos**: Para textos en español, usa `SpanishAnalyzer`:

  ```xml
  <dependency>
      <groupId>org.apache.lucene</groupId>
      <artifactId>lucene-analyzers-common</artifactId>
      <version>9.11.1</version>
  </dependency>
  ```

  ```java
  import org.apache.lucene.analysis.es.SpanishAnalyzer;
  StandardAnalyzer analyzer = new SpanishAnalyzer();
  ```

- **WSL-Specific Notes**: En WSL, asegúrate de que Docker Desktop esté integrado con WSL2 o usa la instalación manual de Docker. Los índices persisten en el volumen Docker, accesible desde el host.
- **Compresión de Índices**: Habilita compresión para optimizar almacenamiento:

  ```java
  config.setUseCompoundFile(true);
  ```

## Resolución de Problemas

- **El Contenedor No Inicia**:
  Revisa los logs:

  ```bash
  docker logs lucene
  ```

  Problemas comunes: errores de compilación (verifica `pom.xml`), permisos en el directorio de índices, o falta de memoria.

- **Índices No Persisten**:
  Verifica que el volumen `lucene_data` exista:

  ```bash
  docker volume ls | grep lucene_data
  ```

- **Resultados de Búsqueda Inesperados**:
  Asegúrate de que el analizador usado en la indexación (`StandardAnalyzer`) coincida con el de la búsqueda. Revisa la consulta en `QueryParser`.

## Recursos Adicionales

- [Documentación de Apache Lucene](https://lucene.apache.org/core/9_11_1/)
- [Guía de Maven](https://maven.apache.org/guides/)
- [Docker Java Image](https://hub.docker.com/_/maven)

## Volver a su ficha

[Volver a la ficha de Lucene](../tecnologias/lucene.md)
