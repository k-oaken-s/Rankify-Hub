ALTER TABLE item RENAME COLUMN image_url TO image;
ALTER TABLE item
ALTER COLUMN image TYPE bytea
    USING image::bytea;
