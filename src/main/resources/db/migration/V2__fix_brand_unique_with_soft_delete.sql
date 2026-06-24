-- V2: Fix unique constraint on brands table (soft-delete aware)
-- Bug: UNIQUE(name) blocks re-creating a brand whose old version was soft-deleted.
-- Fix: Replace UNIQUE(name) with UNIQUE(name, is_deleted).
-- This script auto-detects the existing UNIQUE index name on column `name`
-- (Hibernate generates different names per version), so it's safe to re-run.
DROP PROCEDURE IF EXISTS fix_brands_unique;
CREATE PROCEDURE fix_brands_unique()
BEGIN
    DECLARE idx_name VARCHAR(64);

    -- 1) Find the existing UNIQUE index on column `name` (skip PRIMARY)
    SELECT INDEX_NAME INTO idx_name
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'brands'
      AND COLUMN_NAME  = 'name'
      AND NON_UNIQUE   = 0
      AND INDEX_NAME   <> 'PRIMARY'
    ORDER BY INDEX_NAME
    LIMIT 1;

    -- 2) Drop it if found (dynamic SQL because the name is unknown)
    IF idx_name IS NOT NULL THEN
        SET @sql_text := CONCAT('ALTER TABLE brands DROP INDEX `', idx_name, '`');
        PREPARE stmt FROM @sql_text;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;

    -- 3) Add the new composite UNIQUE(name, is_deleted) if not present
    SET @has_new := (
        SELECT COUNT(*)
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME   = 'brands'
          AND INDEX_NAME   = 'uk_brand_name_active'
    );
    IF @has_new = 0 THEN
        ALTER TABLE brands
            ADD CONSTRAINT uk_brand_name_active
            UNIQUE (name, is_deleted);
    END IF;
END
;
CALL fix_brands_unique();
DROP PROCEDURE fix_brands_unique;
