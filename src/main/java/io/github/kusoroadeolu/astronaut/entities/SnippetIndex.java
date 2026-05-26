package io.github.kusoroadeolu.astronaut.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@Builder
public class SnippetIndex {

    private String id;
    private String fileName;
    private String description;
    private String language;
    private Set<String> tags;
    private Set<String> classNames;
    private Set<String> methodNames;
    private String createdAt;
    private String updatedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        SnippetIndex that = (SnippetIndex) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SnippetIndex[" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", tags=" + tags +
                ", classNames=" + classNames +
                ", methodNames=" + methodNames +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ']';
    }
}
