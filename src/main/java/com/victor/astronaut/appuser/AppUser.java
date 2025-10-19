package com.victor.astronaut.appuser;

import com.victor.astronaut.snippets.Snippet;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "app_user_id")
    private Long id;

    @Column(name = "username", nullable = false, length = 30)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 70)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "role", nullable = false)
    private AppUserRole role;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Snippet> snippet = new ArrayList<>();

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "enableFuzzySearch")
    private Boolean enableFuzzySearch;
    @PrePersist
    public void createdAt(){
        this.createdAt = LocalDateTime.now();
        this.enableFuzzySearch = false;
    }


}
