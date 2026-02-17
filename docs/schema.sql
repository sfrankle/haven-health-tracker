-- Haven — Initial Schema Reference
-- Note: Room generates this from Kotlin @Entity classes.
-- This file is a reference doc, not executed directly.

-- ============================================================
-- LOOKUP / CONFIGURATION TABLES
-- ============================================================

CREATE TABLE measurement_type (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,         -- numeric, label_select, label_select_severity, label_category_select
    display_name TEXT NOT NULL
);

CREATE TABLE category (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE           -- Connect, Move, Reflect, Breathe, Nourish, Create, Ground, Structure
);

CREATE TABLE entry_type (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,          -- Food, Emotion, Hydration, Sleep, Symptom, Activity
    measurement_type_id INTEGER NOT NULL,
    prompt TEXT,                         -- "What did you eat?"
    icon TEXT,                           -- icon reference for Tend grid
    is_enabled INTEGER NOT NULL DEFAULT 1,
    is_default INTEGER NOT NULL DEFAULT 1,
    sort_order INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (measurement_type_id) REFERENCES measurement_type(id)
);

-- ============================================================
-- LABELS + TAGS (two-layer model)
-- ============================================================

CREATE TABLE label (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    entry_type_id INTEGER NOT NULL,     -- which EntryType this belongs to
    name TEXT NOT NULL,                  -- "cheese", "overwhelmed", "gassy", "yoga"
    parent_id INTEGER,                  -- self-ref: body part→symptoms, valence→emotions, category→activities
    category_id INTEGER,                -- for Activity labels only
    is_default INTEGER NOT NULL DEFAULT 1,
    is_enabled INTEGER NOT NULL DEFAULT 1,
    sort_order INTEGER NOT NULL DEFAULT 0,
    seed_version INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (entry_type_id) REFERENCES entry_type(id),
    FOREIGN KEY (parent_id) REFERENCES label(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE tag (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,                  -- "dairy", "FODMAP", "nervous system", "cardio"
    tag_group TEXT NOT NULL,             -- "food", "symptom", "emotion", "activity", "anchor"
    seed_version INTEGER NOT NULL DEFAULT 1
);

-- Join: Label ↔ Tag (enables retroactive tagging)
CREATE TABLE label_tag (
    label_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    seed_version INTEGER NOT NULL DEFAULT 1,
    PRIMARY KEY (label_id, tag_id),
    FOREIGN KEY (label_id) REFERENCES label(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
);

-- ============================================================
-- ENTRIES (user data)
-- ============================================================

CREATE TABLE entry (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    entry_type_id INTEGER NOT NULL,
    source_type TEXT NOT NULL DEFAULT 'log',  -- "log" or "reflect"
    timestamp TEXT NOT NULL,                  -- ISO 8601: when event happened
    created_at TEXT NOT NULL,                 -- ISO 8601: when entry was recorded
    numeric_value REAL,                       -- hours (sleep), oz/ml (hydration), severity (symptom)
    notes TEXT,
    FOREIGN KEY (entry_type_id) REFERENCES entry_type(id)
);

-- Join: Entry ↔ Label (which labels the user selected)
CREATE TABLE entry_label (
    entry_id INTEGER NOT NULL,
    label_id INTEGER NOT NULL,
    PRIMARY KEY (entry_id, label_id),
    FOREIGN KEY (entry_id) REFERENCES entry(id) ON DELETE CASCADE,
    FOREIGN KEY (label_id) REFERENCES label(id)
);

-- ============================================================
-- ANCHOR (activity suggestions)
-- ============================================================

CREATE TABLE anchor_activity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    label_id INTEGER NOT NULL,           -- → Label (Activity labels only)
    title TEXT NOT NULL,                  -- display title
    icon TEXT,
    default_effort INTEGER NOT NULL,     -- 1-5
    user_effort INTEGER,                 -- 1-5 user override
    is_enabled INTEGER NOT NULL DEFAULT 1,
    is_default INTEGER NOT NULL DEFAULT 1,
    seed_version INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (label_id) REFERENCES label(id),
    CHECK (default_effort BETWEEN 1 AND 5),
    CHECK (user_effort IS NULL OR user_effort BETWEEN 1 AND 5)
);

-- Join: AnchorActivity ↔ Tag
CREATE TABLE anchor_tag (
    anchor_activity_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    PRIMARY KEY (anchor_activity_id, tag_id),
    FOREIGN KEY (anchor_activity_id) REFERENCES anchor_activity(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
);

-- ============================================================
-- INDEXES for common query patterns
-- ============================================================

CREATE INDEX idx_label_entry_type ON label(entry_type_id);
CREATE INDEX idx_label_parent ON label(parent_id);
CREATE INDEX idx_entry_entry_type ON entry(entry_type_id);
CREATE INDEX idx_entry_timestamp ON entry(timestamp);
CREATE INDEX idx_entry_source_type ON entry(source_type);
CREATE INDEX idx_entry_label_entry ON entry_label(entry_id);
CREATE INDEX idx_entry_label_label ON entry_label(label_id);
CREATE INDEX idx_label_tag_label ON label_tag(label_id);
CREATE INDEX idx_label_tag_tag ON label_tag(tag_id);

-- ============================================================
-- SEED DATA (v1)
-- ============================================================

-- Measurement Types
INSERT INTO measurement_type (name, display_name) VALUES
    ('numeric', 'Enter a number'),
    ('label_select', 'Select one or more'),
    ('label_select_severity', 'Select and rate severity'),
    ('label_category_select', 'Select by category');

-- Categories (for Activity labels)
INSERT INTO category (name) VALUES
    ('Connect'),
    ('Move'),
    ('Reflect'),
    ('Breathe'),
    ('Nourish'),
    ('Create'),
    ('Ground'),
    ('Structure');

-- Entry Types
INSERT INTO entry_type (name, measurement_type_id, prompt, sort_order) VALUES
    ('Food',      2, 'What did you eat?',        1),
    ('Emotion',   2, 'How are you feeling?',      2),
    ('Hydration', 1, 'How much did you drink?',   3),
    ('Sleep',     1, 'How many hours did you sleep?', 4),
    ('Symptom',   3, 'What are you experiencing?', 5),
    ('Activity',  4, 'What did you do?',          6);

-- ============================================================
-- SEED LABELS (samples — full list TBD)
-- ============================================================

-- Food labels (flat, no parents)
-- INSERT INTO label (entry_type_id, name, seed_version) VALUES
--     (1, 'Cheese', 1), (1, 'Bread', 1), (1, 'Rice', 1), (1, 'Chicken', 1), ...

-- Food tags
-- INSERT INTO tag (name, tag_group, seed_version) VALUES
--     ('dairy', 'food', 1), ('gluten', 'food', 1), ('FODMAP', 'food', 1), ...

-- Emotion labels (nested: valence → emotions)
-- INSERT INTO label (entry_type_id, name, seed_version) VALUES
--     (2, 'Pleasant', 1),    -- parent
--     (2, 'Neutral', 1),     -- parent
--     (2, 'Unpleasant', 1),  -- parent
-- INSERT INTO label (entry_type_id, name, parent_id, seed_version) VALUES
--     (2, 'Optimistic', <pleasant_id>, 1),
--     (2, 'Content', <pleasant_id>, 1),
--     (2, 'Anxious', <unpleasant_id>, 1), ...

-- Symptom labels (nested: body part → symptoms)
-- INSERT INTO label (entry_type_id, name, seed_version) VALUES
--     (5, 'Stomach', 1),     -- parent (body part)
--     (5, 'Head', 1),        -- parent (body part)
-- INSERT INTO label (entry_type_id, name, parent_id, seed_version) VALUES
--     (5, 'Gassy', <stomach_id>, 1),
--     (5, 'Cramps', <stomach_id>, 1),
--     (5, 'Headache', <head_id>, 1), ...

-- Activity labels (with categories)
-- INSERT INTO label (entry_type_id, name, category_id, seed_version) VALUES
--     (6, 'Yoga', <move_id>, 1),
--     (6, 'Painting', <create_id>, 1),
--     (6, 'Deep breathing', <breathe_id>, 1), ...
