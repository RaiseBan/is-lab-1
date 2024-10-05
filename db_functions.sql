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
CREATE OR REPLACE FUNCTION count_musicbands_with_label_more_than(_label_threshold BIGINT)
    RETURNS BIGINT AS $$
BEGIN
    RETURN (SELECT COUNT(*)
            FROM music_band mb
                     JOIN label l ON mb.label_id = l.id
            WHERE l.id > _label_threshold);
END;
$$ LANGUAGE plpgsql;

-- 3. Вернуть массив объектов, значение поля description которых содержит заданную подстроку
CREATE OR REPLACE FUNCTION find_musicbands_by_description_substring(_substring TEXT)
    RETURNS TABLE(id BIGINT, name VARCHAR, description VARCHAR) AS $$
BEGIN
    RETURN QUERY
        SELECT id, name, description
        FROM music_band
        WHERE description ILIKE '%' || _substring || '%';
END;
$$ LANGUAGE plpgsql;

-- 4. Добавить новый сингл указанной группе
CREATE OR REPLACE FUNCTION add_single_to_musicband(_band_id BIGINT, _singles_count BIGINT)
    RETURNS VOID AS $$
BEGIN
    UPDATE music_band SET singlescount = singlescount + _singles_count WHERE id = _band_id;
END;
$$ LANGUAGE plpgsql;

-- 5. Удалить из группы участника
CREATE OR REPLACE FUNCTION remove_participant_from_musicband(_band_id BIGINT)
    RETURNS VOID AS $$
BEGIN
    UPDATE music_band SET numberofparticipants = numberofparticipants - 1 WHERE id = _band_id AND numberofparticipants > 0;
END;
$$ LANGUAGE plpgsql;
