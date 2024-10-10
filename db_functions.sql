-- 1. Сгруппировать объекты по значению поля creationDate, вернуть количество элементов в каждой группе
CREATE OR REPLACE FUNCTION get_musicband_count_by_creation_date()
    RETURNS TABLE(creation_date DATE, count BIGINT) AS $$
BEGIN
    RETURN QUERY
        SELECT creationdate::date, COUNT(*)
        FROM music_band
        GROUP BY creationdate::date
        ORDER BY creationdate::date;
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
    SET singlescount = music_band.singlescount + _singles_count
    WHERE id = _band_id;
END;
$$;


-- 5. Удалить из группы участника
CREATE OR REPLACE PROCEDURE remove_participant_from_musicband(_band_id BIGINT)
    LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE music_band SET numberofparticipants = numberofparticipants - 1 WHERE id = _band_id AND numberofparticipants > 0;
END;
$$
