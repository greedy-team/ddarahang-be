package org.greedy.ddarahang.db.favoriteList;

import jakarta.persistence.*;
import lombok.*;
import org.greedy.ddarahang.db.place.Place;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "favorite_list_places")
public class FavoritePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_in_list", nullable = false)
    private Integer orderInList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_list_id", nullable = false)
    private FavoriteList favoriteList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
}
