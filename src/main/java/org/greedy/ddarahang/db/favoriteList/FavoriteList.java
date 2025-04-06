package org.greedy.ddarahang.db.favoriteList;

import jakarta.persistence.*;
import lombok.*;
import org.greedy.ddarahang.db.place.Place;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "favoriteLists")
public class FavoriteList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String listName;

    @ManyToMany
    @JoinTable(name = "favorite_list_place",
            joinColumns = @JoinColumn(name = "favorite_list_id"),
            inverseJoinColumns = @JoinColumn(name = "place_id")
    )
    @Builder.Default
    private List<Place> places = new ArrayList<>();
}
