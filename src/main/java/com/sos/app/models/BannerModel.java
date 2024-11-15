package com.sos.app.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_banners")
public class BannerModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ElementCollection(fetch = FetchType.LAZY)
  @Column(name = "images", nullable = false)
  private List<String> images;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "height", nullable = false)
  private Integer height;

  @Column(name = "width", nullable = false)
  private Integer width;

}
