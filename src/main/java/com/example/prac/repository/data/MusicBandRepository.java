package com.example.prac.repository.data;

import com.example.prac.model.dataEntity.MusicBand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicBandRepository extends JpaRepository<MusicBand, Long> {

    // Поиск групп по подстроке в описании
    @Query("SELECT mb FROM MusicBand mb WHERE mb.description LIKE %:substring%")
    List<MusicBand> findMusicBandsByDescriptionSubstring(@Param("substring") String substring);

    // Получение количества групп по дате создания (с использованием хранимой функции)
    @Query(value = "SELECT * FROM get_musicband_count_by_creation_date()", nativeQuery = true)
    List<Object[]> getMusicBandCountByCreationDate();

    // Получение количества групп, у которых лейбл превышает заданный порог
    @Query(value = "SELECT count_musicbands_with_label_more_than(:labelThreshold)", nativeQuery = true)
    Long countMusicBandsWithLabelMoreThan(@Param("labelThreshold") String labelThreshold);

    // Запрос на добавление сингла в группу через хранимую процедуру
    @Query(value = "CALL add_single_to_musicband(:bandId, :singlesCount)", nativeQuery = true)
    void addSingleToMusicBand(@Param("bandId") Long bandId, @Param("singlesCount") int singlesCount);

    // Запрос на удаление участника из группы через хранимую процедуру
    @Query(value = "CALL remove_participant_from_musicband(:bandId)", nativeQuery = true)
    void removeParticipantFromMusicBand(@Param("bandId") long bandId);

    // Подсчет количества групп по ID координат
    @Query("SELECT COUNT(mb.id) FROM MusicBand mb WHERE mb.coordinates.id = :coordinatesId")
    int countByCoordinatesId(@Param("coordinatesId") long coordinatesId);

    // Подсчет количества групп по ID лучшего альбома
    @Query("SELECT COUNT(mb.id) FROM MusicBand mb WHERE mb.bestAlbum.id = :bestAlbumId")
    int countByBestAlbumId(@Param("bestAlbumId") long bestAlbumId);

    // Подсчет количества групп по ID лейбла
    @Query("SELECT COUNT(mb.id) FROM MusicBand mb WHERE mb.label.id = :labelId")
    int countByLabelId(@Param("labelId") long labelId);

    // Сохранение всех групп (batch)
    @Override
    <S extends MusicBand> List<S> saveAll(Iterable<S> entities);
}
