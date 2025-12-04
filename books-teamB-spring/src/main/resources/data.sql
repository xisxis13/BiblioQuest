-- ============================================================================
-- 1. INSERTION DES UTILISATEURS DE TEST
-- ============================================================================
INSERT INTO users (id, username, email, password, role) VALUES 
(1, 'admin@example.com', 'admin@example.com', 'password123', 'USER');

-- ============================================================================
-- 2. INSERTION DES SUJETS (Genres)
-- ============================================================================
INSERT INTO subject (id, text) VALUES 
(1, 'Fantasy'),
(2, 'Young Adult'),
(3, 'Aventure'),
(4, 'Romance');

-- ============================================================================
-- 3. INSERTION DES AUTEURS
-- ============================================================================
INSERT INTO author (id, first_name, last_name) VALUES 
(1, 'Tracy', 'Deonn'),
(2, 'Sarah', 'Rees Brennan'),
(3, 'Laini', 'Taylor');


-- ============================================================================
-- 4. INSERTION DES LIVRES
-- J'utilise les données fournies (Cover, Dates, etc.)
-- ============================================================================

-- Livre 1 : Bloodmarked (Tracy Deonn)
INSERT INTO book (id, title, desc_txt, cover, serie_id, serie_pos, created_at, updated_at, subject_id) VALUES 
(200, 
 'Bloodmarked', 
 'The Legendborn Cycle, Book 2. Bree wants to heal, but the war has just begun.', 
 'https://covers.openlibrary.org/a/id/12617874-L.jpg', 
 'LEGENDBORN', 
 2, 
 '2010-03-02 03:44:17', 
 '2022-02-05 16:17:50', 
 1); -- Genre Fantasy

-- Livre 2 : In Other Lands (Sarah Rees Brennan)
INSERT INTO book (id, title, desc_txt, cover, serie_id, serie_pos, created_at, updated_at, subject_id) VALUES 
(201, 
 'In Other Lands', 
 'The Borderlands aren''t like anywhere else. Don''t try to smuggle a phone... Elliot is thirteen years old. He''s smart and just a tiny bit obnoxious.', 
 'https://covers.openlibrary.org/a/id/12354250-L.jpg', 
 NULL, 
 NULL, 
 '2017-10-20 01:27:09', 
 '2021-11-07 07:29:41', 
 2); -- Genre Young Adult

-- Livre 3 : Strange the Dreamer (Laini Taylor)
INSERT INTO book (id, title, desc_txt, cover, serie_id, serie_pos, created_at, updated_at, subject_id) VALUES 
(202, 
 'Strange the Dreamer', 
 'The dream chooses the dreamer, not the other way around. Lazlo Strange, war orphan and junior librarian, has always feared that his dream chose poorly.', 
 'https://covers.openlibrary.org/a/id/8367802-L.jpg', 
 'STRANGE_DREAMER', 
 1, 
 '2018-05-15 01:36:29', 
 '2020-10-11 09:17:05', 
 1); -- Genre Fantasy

-- Livre 4 : Muse of Nightmares (Laini Taylor) - Suite de Strange the Dreamer
INSERT INTO book (id, title, desc_txt, cover, serie_id, serie_pos, created_at, updated_at, subject_id) VALUES 
(203, 
 'Muse of Nightmares', 
 'In the wake of tragedy, neither Lazlo nor Sarai are who they were before. One a god, the other a ghost, they struggle to grasp the new boundaries of their selves.', 
 'https://covers.openlibrary.org/a/id/12366752-L.jpg', 
 'STRANGE_DREAMER', 
 2, 
 '2018-10-18 01:40:34', 
 '2021-11-11 12:46:26', 
 1); -- Genre Fantasy


-- ============================================================================
-- 5. RELATIONS LIVRES <-> AUTEURS
-- ============================================================================

INSERT INTO book_author (book_id, author_id) VALUES 
(200, 1), -- Bloodmarked -> Tracy Deonn
(201, 2), -- In Other Lands -> Sarah Rees Brennan
(202, 3), -- Strange the Dreamer -> Laini Taylor
(203, 3); -- Muse of Nightmares -> Laini Taylor