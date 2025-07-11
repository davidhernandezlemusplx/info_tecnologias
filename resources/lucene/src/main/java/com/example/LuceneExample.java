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
