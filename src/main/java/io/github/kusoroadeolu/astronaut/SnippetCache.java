package io.github.kusoroadeolu.astronaut;

import io.github.kusoroadeolu.astronaut.entities.SnippetIndex;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class SnippetCache {
    private final Map<String, SnippetIndex> snippets;

    public SnippetCache() {
        this.snippets = new ConcurrentHashMap<>();
    }

    public void add(@NonNull SnippetIndex s) {
        snippets.put(Objects.requireNonNull(s).getId(), s);
    }

    public SnippetIndex get(String id) {
        return snippets.get(id);
    }

    public boolean remove(String id) {
        return snippets.remove(id) != null;
    }

    public void addAll(@NonNull Collection<SnippetIndex> s) {
        Map<String, SnippetIndex> map = s.stream()
                .collect(Collectors.toMap(SnippetIndex::getId, si -> si));
        snippets.putAll(map);
    }

    public List<SnippetIndex> values(){
        return snippets.values().stream().toList();
    }



    public void shutdown() {
        snippets.clear();
    }

}
