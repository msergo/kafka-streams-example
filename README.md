# WikiStreams Hello-world

A minimal Kafka Streams "hello-world" that consumes Wikipedia change events, filters out bot edits, and prints article titles. Uses public wiki change data for testing.

## Quick overview
- Main class: `org.msergo.WikiStreamsApp`
- Topic used: `wikipedia-changes`
- Purpose: experiment with Kafka Streams topology and custom timestamp extraction.

## Prerequisites
- Java (11+)
- Maven
- Kafka broker reachable at `localhost:9092`
- Topic `wikipedia-changes` with JSON messages

## Build
```bash
mvn package