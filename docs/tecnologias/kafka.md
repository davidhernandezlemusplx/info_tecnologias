# Apache Kafka

![raw](../../images/kafka/kafka.png)

Apache Kafka es una plataforma distribuida de mensajería orientada a eventos. Permite publicar, suscribirse, almacenar y procesar flujos de registros en tiempo real.

## Características principales

- **Alta escalabilidad**: Soporta grandes volúmenes de datos y múltiples consumidores/produtores.
- **Tolerancia a fallos**: Réplica de datos entre brokers para garantizar disponibilidad.
- **Persistencia de mensajes**: Almacena registros en disco con configuraciones de retención personalizables.
- **Integración con microservicios**: Compatible con arquitecturas modernas y flujos de datos en tiempo real.

## Casos de uso

- Procesamiento de pagos en tiempo real.
- Monitorización de sistemas y aplicaciones en tiempo real.
- Pipelines de datos para ETL (Extracción, Transformación y Carga).
- Transmisión de eventos para aplicaciones de streaming.

## Zookeeper

**Zookeeper** es un servicio de coordinación distribuida que maneja:

- **Gestión de metadatos**: Almacena información sobre brokers, tópicos y particiones.
- **Liderazgo y sincronización**: Coordina la elección de líderes entre brokers y mantiene la consistencia.
- **Configuración dinámica**: Permite ajustes en tiempo real del clúster.

En la configuración clásica, Zookeeper se despliega como un contenedor separado para soportar un clúster Kafka de uno o varios nodos [documento técnico](../pliegos-tecnicos/kafka-config_zookeeper.md).

## KRaft

**KRaft** (Kafka Raft Metadata mode).

- **Gestión interna de metadatos**: El propio broker Kafka almacena y replica los metadatos usando el protocolo Raft.
- **Arranque más rápido y menos puntos de fallo**.

En el [documento técnico de KRaft](../pliegos-tecnicos/kafka-config_kraft.md) tienes la guía completa para este modo.

## Pliegos Técnicos

A continuación tienes dos guías detalladas para desplegar y administrar Kafka en contenedores Docker, según el modo de operación:

- [Configuración clásica con Zookeeper (Legacy)](../pliegos-tecnicos/kafka-config_zookeeper.md):
  Explica el despliegue tradicional de Kafka que requiere Zookeeper para la gestión de metadatos y coordinación del clúster. Útil para entornos legacy o si necesitas compatibilidad con versiones antiguas.

- [Configuración moderna con KRaft (sin Zookeeper)](../pliegos-tecnicos/kafka-config_kraft.md):
  Guía para desplegar Kafka en modo KRaft, usando las últimas versiones y mejores prácticas recomendadas por la comunidad. Recomendado para nuevos proyectos o migraciones.

> **¿Cuál usar?**

- Usa la guía **KRaft** para nuevos despliegues, pruebas, desarrollo y producción moderna.
- Usa la guía **Zookeeper** solo si tienes dependencias legacy o necesitas compatibilidad con herramientas que aún no soportan KRaft.

## Enlaces útiles

- [Sitio oficial de Apache Kafka](https://kafka.apache.org/)
- [Documentación técnica de Kafka](https://kafka.apache.org/documentation/)
- [Documentación de Zookeeper](https://zookeeper.apache.org/doc/current/)
  
## [⬅ Volver al Readme](../../README.md)
