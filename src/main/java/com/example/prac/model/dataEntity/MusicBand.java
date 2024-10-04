package com.example.prac.model.dataEntity;

import com.example.prac.model.authEntity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;

@Entity
@Table(name = "music_band")
@Data
@ToString
public class MusicBand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private long id;  // Значение должно генерироваться автоматически, уникально и больше 0

    @NotBlank(message = "Name cannot be null or empty")
    @Column(nullable = false)
    private String name;  // Поле не может быть null, строка не может быть пустой

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coordinates_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Coordinates cannot be null")
    private Coordinates coordinates;  // Поле не может быть null


    @Column(nullable = false)
    private ZonedDateTime creationDate = ZonedDateTime.now();  // Значение должно генерироваться автоматически

    @Enumerated(EnumType.STRING)
    private MusicGenre genre;  // Поле может быть null

    @Min(value = 1, message = "Number of participants must be greater than 0")
    @Column(nullable = false)
    private long numberOfParticipants;  // Значение должно быть больше 0

    @NotNull(message = "Singles count cannot be null")
    @Min(value = 1, message = "Singles count must be greater than 0")
    @Column(nullable = false)
    private Long singlesCount;  // Поле не может быть null и значение должно быть больше 0

    @NotBlank(message = "Description cannot be null or empty")
    @Column(nullable = false)
    private String description;  // Поле не может быть null

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "album_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Best album cannot be null")
    private Album bestAlbum;  // Поле не может быть null

    @Min(value = 1, message = "Albums count must be greater than 0")
    @Column(nullable = false)
    private long albumsCount;  // Значение должно быть больше 0


    @NotNull(message = "Establishment date cannot be null")
    @Column(nullable = false)
    private ZonedDateTime establishmentDate;  // Поле не может быть null

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "label_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Label cannot be null")
    private Label label;  // Поле не может быть null

    // Добавляем поле User, указывающее на владельца группы
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Owner cannot be null")
    private User owner;  // Поле, указывающее на владельца (пользователя), не может быть null

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy; // Пользователь, который создал объект

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private User updatedBy; // Последний пользователь, который обновлял объект

    private ZonedDateTime updatedAt; // Дата последнего обновления объекта
    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }

}
