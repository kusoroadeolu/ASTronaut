package com.victor.astronaut.snippets;

import com.victor.astronaut.appuser.AppUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
@Getter
@Setter
public class Snippet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "draft")
    private boolean isDraft = true;

    @Column(name = "content", length = 10000)
    private String content = "";

    @Column(name = "extra_notes", length = 100)
    private String extraNotes = "";

    @Column(name = "tags")
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    private Set<String> classNames = new HashSet<>();

    @ElementCollection
    private Set<String> classAnnotations = new HashSet<>();

    @ElementCollection
    private Set<String> classDependencies = new HashSet<>();

    @ElementCollection
    private Set<String> methodReturnTypes = new HashSet<>();

    @ElementCollection
    private Set<String> methodAnnotations = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void createdAt(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updatedAt(){
        this.updatedAt = LocalDateTime.now();
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
}
