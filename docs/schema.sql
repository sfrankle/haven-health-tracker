-- Haven — Initial Schema Reference
-- Note: Room generates this from Kotlin @Entity classes.
-- This file is a reference doc, not executed directly.

-- ============================================================
-- LOOKUP / CONFIGURATION TABLES
-- ============================================================

CREATE TABLE MeasurementType (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,         -- numeric, label_select, label_select_severity, label_category_select
    displayName TEXT NOT NULL
);

CREATE TABLE Category (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE           -- Connect, Move, Reflect, Breathe, Nourish, Create, Ground, Structure
);

CREATE TABLE EntryType (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,          -- Food, Emotion, Hydration, Sleep, Symptom, Activity
    measurementTypeId INTEGER NOT NULL,
    prompt TEXT,                         -- "What did you eat?"
    icon TEXT,                           -- icon reference for Tend grid
    isEnabled INTEGER NOT NULL DEFAULT 1,
    isDefault INTEGER NOT NULL DEFAULT 1,
    sortOrder INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (measurementTypeId) REFERENCES MeasurementType(id)
);

-- ============================================================
-- LABELS + TAGS (two-layer model)
-- ============================================================

CREATE TABLE Label (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    entryTypeId INTEGER NOT NULL,       -- which EntryType this belongs to
    name TEXT NOT NULL,                  -- "cheese", "overwhelmed", "gassy", "yoga"
    parentId INTEGER,                   -- self-ref: body part→symptoms, valence→emotions, category→activities
    categoryId INTEGER,                 -- for Activity labels only
    isDefault INTEGER NOT NULL DEFAULT 1,
    isEnabled INTEGER NOT NULL DEFAULT 1,
    sortOrder INTEGER NOT NULL DEFAULT 0,
    seedVersion INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (entryTypeId) REFERENCES EntryType(id),
    FOREIGN KEY (parentId) REFERENCES Label(id),
    FOREIGN KEY (categoryId) REFERENCES Category(id)
);

CREATE TABLE Tag (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,                  -- "dairy", "FODMAP", "nervous system", "cardio"
    tagGroup TEXT NOT NULL,              -- "food", "symptom", "emotion", "activity", "anchor"
    seedVersion INTEGER NOT NULL DEFAULT 1
);

-- Join: Label ↔ Tag (enables retroactive tagging)
CREATE TABLE LabelTag (
    labelId INTEGER NOT NULL,
    tagId INTEGER NOT NULL,
    PRIMARY KEY (labelId, tagId),
    FOREIGN KEY (labelId) REFERENCES Label(id),
    FOREIGN KEY (tagId) REFERENCES Tag(id)
);

-- ============================================================
-- ENTRIES (user data)
-- ============================================================

CREATE TABLE Entry (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    entryTypeId INTEGER NOT NULL,
    sourceType TEXT NOT NULL DEFAULT 'log',  -- "log" or "reflect"
    timestamp TEXT NOT NULL,                  -- ISO 8601: when event happened
    createdAt TEXT NOT NULL,                  -- ISO 8601: when entry was recorded
    numericValue REAL,                        -- hours (sleep), oz/ml (hydration), severity (symptom)
    notes TEXT,
    FOREIGN KEY (entryTypeId) REFERENCES EntryType(id)
);

-- Join: Entry ↔ Label (which labels the user selected)
CREATE TABLE EntryLabel (
    entryId INTEGER NOT NULL,
    labelId INTEGER NOT NULL,
    PRIMARY KEY (entryId, labelId),
    FOREIGN KEY (entryId) REFERENCES Entry(id) ON DELETE CASCADE,
    FOREIGN KEY (labelId) REFERENCES Label(id)
);

-- ============================================================
-- ANCHOR (activity suggestions)
-- ============================================================

CREATE TABLE AnchorActivity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    labelId INTEGER NOT NULL,            -- → Label (Activity labels only)
    title TEXT NOT NULL,                  -- display title
    icon TEXT,
    defaultEffort INTEGER NOT NULL,      -- 1-5
    userEffort INTEGER,                  -- 1-5 user override
    isEnabled INTEGER NOT NULL DEFAULT 1,
    isDefault INTEGER NOT NULL DEFAULT 1,
    seedVersion INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (labelId) REFERENCES Label(id),
    CHECK (defaultEffort BETWEEN 1 AND 5),
    CHECK (userEffort IS NULL OR userEffort BETWEEN 1 AND 5)
);

-- Join: AnchorActivity ↔ Tag
CREATE TABLE AnchorTag (
    anchorActivityId INTEGER NOT NULL,
    tagId INTEGER NOT NULL,
    PRIMARY KEY (anchorActivityId, tagId),
    FOREIGN KEY (anchorActivityId) REFERENCES AnchorActivity(id),
    FOREIGN KEY (tagId) REFERENCES Tag(id)
);

-- ============================================================
-- INDEXES for common query patterns
-- ============================================================

CREATE INDEX idx_label_entryType ON Label(entryTypeId);
CREATE INDEX idx_label_parent ON Label(parentId);
CREATE INDEX idx_entry_entryType ON Entry(entryTypeId);
CREATE INDEX idx_entry_timestamp ON Entry(timestamp);
CREATE INDEX idx_entry_sourceType ON Entry(sourceType);
CREATE INDEX idx_entryLabel_entry ON EntryLabel(entryId);
CREATE INDEX idx_entryLabel_label ON EntryLabel(labelId);
CREATE INDEX idx_labelTag_label ON LabelTag(labelId);
CREATE INDEX idx_labelTag_tag ON LabelTag(tagId);

-- ============================================================
-- SEED DATA (v1)
-- ============================================================

-- Measurement Types
INSERT INTO MeasurementType (name, displayName) VALUES
    ('numeric', 'Enter a number'),
    ('label_select', 'Select one or more'),
    ('label_select_severity', 'Select and rate severity'),
    ('label_category_select', 'Select by category');

-- Categories (for Activity labels)
INSERT INTO Category (name) VALUES
    ('Connect'),
    ('Move'),
    ('Reflect'),
    ('Breathe'),
    ('Nourish'),
    ('Create'),
    ('Ground'),
    ('Structure');

-- Entry Types
INSERT INTO EntryType (name, measurementTypeId, prompt, sortOrder) VALUES
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
-- INSERT INTO Label (entryTypeId, name, seedVersion) VALUES
--     (1, 'Cheese', 1), (1, 'Bread', 1), (1, 'Rice', 1), (1, 'Chicken', 1), ...

-- Food tags
-- INSERT INTO Tag (name, tagGroup, seedVersion) VALUES
--     ('dairy', 'food', 1), ('gluten', 'food', 1), ('FODMAP', 'food', 1), ...

-- Emotion labels (nested: valence → emotions)
-- INSERT INTO Label (entryTypeId, name, seedVersion) VALUES
--     (2, 'Pleasant', 1),    -- parent
--     (2, 'Neutral', 1),     -- parent
--     (2, 'Unpleasant', 1),  -- parent
-- INSERT INTO Label (entryTypeId, name, parentId, seedVersion) VALUES
--     (2, 'Optimistic', <pleasant_id>, 1),
--     (2, 'Content', <pleasant_id>, 1),
--     (2, 'Anxious', <unpleasant_id>, 1), ...

-- Symptom labels (nested: body part → symptoms)
-- INSERT INTO Label (entryTypeId, name, seedVersion) VALUES
--     (5, 'Stomach', 1),     -- parent (body part)
--     (5, 'Head', 1),        -- parent (body part)
-- INSERT INTO Label (entryTypeId, name, parentId, seedVersion) VALUES
--     (5, 'Gassy', <stomach_id>, 1),
--     (5, 'Cramps', <stomach_id>, 1),
--     (5, 'Headache', <head_id>, 1), ...

-- Activity labels (with categories)
-- INSERT INTO Label (entryTypeId, name, categoryId, seedVersion) VALUES
--     (6, 'Yoga', <move_id>, 1),
--     (6, 'Painting', <create_id>, 1),
--     (6, 'Deep breathing', <breathe_id>, 1), ...
