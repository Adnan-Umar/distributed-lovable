package com.adnanumar.distributed_lovable.account_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(unique = true)
    String stripePriceId;

    Integer maxProjects;

    Integer maxTokenPerDay;

    Integer maxPreview;     // max number of preview allowed per plan

    Boolean unlimitedAi;    // unlimited access to LLM, ignore maxTokenPerDay if true

    Boolean active;

}
