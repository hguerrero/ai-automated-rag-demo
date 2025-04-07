package org.acme;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

import java.io.File;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import io.quarkiverse.langchain4j.redis.RedisEmbeddingStore;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class IngestorExampleWithRedis {

    private static final String INGEST_ENDPOINT = "http://localhost:9001/ai-rag-injector/87254c25-0ed9-593b-8cd7-39207e22e97b/ingest_chunk";

    @Inject
    RedisEmbeddingStore store;

    @Inject
    EmbeddingModel embeddingModel;

    public void ingest(@Observes StartupEvent event) {
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(
                new File("src/main/resources/documents").toPath(),
                new TextDocumentParser());
        Log.infof("Ingesting documents...");
        DocumentSplitter splitter = recursive(500, 0);
        List<TextSegment> segments = splitter.splitAll(documents);

        Client client = ClientBuilder.newClient();

        segments.stream().forEach(segment -> {
            try {
                Map<String, String> payload = new HashMap<>();
                payload.put("content", segment.text());

                Log.debugf("Sending segment to endpoint: %s", payload);
                String response = client.target(INGEST_ENDPOINT)
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.json(payload), String.class);
                Log.debugf("Response received: %s", response);
            } catch (Exception e) {
                Log.errorf("Error sending segment: %s", e.getMessage());
            }
        });

        client.close();
        Log.infof("Processed %d segments from %d documents.%n", segments.size(), documents.size());
    }
}
