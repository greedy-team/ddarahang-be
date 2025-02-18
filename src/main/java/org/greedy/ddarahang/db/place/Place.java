package org.greedy.ddarahang.db.place;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.greedy.ddarahang.db.region.Region;

@Entity
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "place_name", nullable = false)
    private String name;

    @Column(name = "place_address", nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

}
