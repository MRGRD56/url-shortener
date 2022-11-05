CREATE OR REPLACE FUNCTION generate_short_url(length int) RETURNS text
    STRICT
    LANGUAGE plpgsql
AS
$$
DECLARE
    all_chars CONSTANT text := 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
BEGIN
    RETURN string_agg(substr(all_chars, ceil(random() * length(all_chars))::integer, 1), '')
    FROM generate_series(1, length);
END;
$$;

CREATE OR REPLACE FUNCTION generate_url_id() RETURNS text
    LANGUAGE plpgsql
AS
$$
DECLARE
    new_id text;
    done bool;
BEGIN
    done := false;
    WHILE NOT done LOOP
            new_id := generate_short_url(7);
            done := NOT exists(SELECT 1 FROM shortened_url su WHERE su.short_url = new_id);
        END LOOP;
    RETURN new_id;
END;
$$;