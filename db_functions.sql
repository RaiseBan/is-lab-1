-- 1. Сгруппировать объекты по значению поля creationDate, вернуть количество элементов в каждой группе
CREATE OR REPLACE FUNCTION get_musicband_count_by_creation_date()
    RETURNS TABLE(creation_datee DATE, count BIGINT) AS $$
BEGIN
    RETURN QUERY
        SELECT creation_date::date, COUNT(*)
        FROM music_band
        GROUP BY creation_date::date
        ORDER BY creation_date::date;
END;
$$ LANGUAGE plpgsql;

-- 2. Вернуть количество объектов, значение поля label которых больше заданного
CREATE OR REPLACE FUNCTION count_musicbands_with_label_more_than(_label_threshold varchar)
    RETURNS BIGINT AS $$
BEGIN
    RETURN (SELECT COUNT(*)
            FROM music_band mb
                     JOIN label l ON mb.label_id = l.id
            WHERE l.name > _label_threshold);
END;
$$ LANGUAGE plpgsql;

-- 3. Вернуть массив объектов, значение поля description которых содержит заданную подстроку
CREATE OR REPLACE FUNCTION find_musicbands_by_description_substring(_substring VARCHAR)
    RETURNS SETOF music_band AS $$
BEGIN
    RETURN QUERY
        SELECT mb.*
        FROM music_band mb
        WHERE mb.description ILIKE '%' || _substring || '%';
END;
$$ LANGUAGE plpgsql;



-- 4. Добавить новый сингл указанной группе
CREATE OR REPLACE PROCEDURE add_single_to_musicband(_band_id BIGINT, _singles_count BIGINT)
    LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE music_band
    SET singles_count = music_band.singles_count + _singles_count
    WHERE id = _band_id;
END;
$$;


-- 5. Удалить из группы участника
CREATE OR REPLACE PROCEDURE remove_participant_from_musicband(_band_id BIGINT)
    LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE music_band SET number_of_participants = number_of_participants - 1 WHERE id = _band_id AND number_of_participants > 0;
END;
$$
