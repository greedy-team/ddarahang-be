package org.greedy.ddarahang.db.favoriteList;

import jakarta.persistence.*;
import lombok.*;
import org.greedy.ddarahang.db.place.Place;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "favorite_list_place")
public class FavoriteListPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_list_id", nullable = false)
    private FavoriteList favoriteList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "order_in_list", nullable = false)
    private Integer orderInList;
}
