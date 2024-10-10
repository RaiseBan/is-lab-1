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
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "MusicBand.findByDescriptionSubstring",
                query = "SELECT * FROM find_musicbands_by_description_substring(:substring)",
                resultClass = MusicBand.class
        ),
        @NamedNativeQuery(
                name = "MusicBand.addSingleToMusicBand",
                query = "CALL add_single_to_musicband(:bandId, :singlesCount)"
        ),
        @NamedNativeQuery(
                name = "MusicBand.removeParticipantFromMusicBand",
                query = "CALL remove_participant_from_musicband(:bandId)"
        )
})
@Data
@ToString
public class MusicBand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @NotBlank(message = "Name cannot be null or empty")
    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coordinates_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Coordinates cannot be null")
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false)
    private ZonedDateTime creationDate = ZonedDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private MusicGenre genre;

    @Min(value = 1, message = "Number of participants must be greater than 0")
    @Column(name = "number_of_participants", nullable = false)
    private long numberOfParticipants;

    @NotNull(message = "Singles count cannot be null")
    @Min(value = 1, message = "Singles count must be greater than 0")
    @Column(name = "singles_count", nullable = false)
    private Long singlesCount;

    @NotBlank(message = "Description cannot be null or empty")
    @Column(name = "description", nullable = false)
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "best_album_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Best album cannot be null")
    private Album bestAlbum;

    @Min(value = 1, message = "Albums count must be greater than 0")
    @Column(name = "albums_count", nullable = false)
    private long albumsCount;

    @NotNull(message = "Establishment date cannot be null")
    @Column(name = "establishment_date", nullable = false)
    private ZonedDateTime establishmentDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "label_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Label cannot be null")
    private Label label;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Owner cannot be null")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}
