-- ======================
-- Führen Sie die Abfrage nicht vollständig aus.     !!!!!!!
-- Führen Sie sie kontrolliert Stück für Stück aus.  !!!!!!!!!!!!!!!!!!!
-- ================


USE cnr_kochrezepte;

-- =====================================================
-- Test 1: Alle Rezepte mit Kategorie anzeigen (LEFT JOIN,
-- damit auch das Rezept ohne Kategorie erscheint)
-- =====================================================
SELECT r.ID, r.Titel, r.Zubereitungszeit, k.Name AS Kategorie
FROM Rezept r
LEFT JOIN Kategorie k ON r.KategorieID = k.ID;

-- =====================================================
-- Test 2: Ein Rezept mit allen Zutaten anzeigen (Detailansicht)
-- =====================================================
SELECT r.Titel, z.Name, z.Menge, z.Einheit
FROM Rezept r
JOIN Zutat z ON z.RezeptID = r.ID
WHERE r.ID = 1;

-- =====================================================
-- Test 3: Suche nach Titel, Kategorie ODER Zutat (Volltextsuche)
-- =====================================================
SELECT DISTINCT r.ID, r.Titel
FROM Rezept r
LEFT JOIN Kategorie k ON r.KategorieID = k.ID
LEFT JOIN Zutat z ON z.RezeptID = r.ID
WHERE r.Titel LIKE '%kuchen%'
   OR k.Name LIKE '%kuchen%'
   OR z.Name LIKE '%kuchen%';

-- =====================================================
-- Test 4: Kaskadenloeschung testen
-- Zutaten eines Rezepts muessen automatisch verschwinden
-- =====================================================
SELECT COUNT(*) AS AnzahlVorher FROM Zutat WHERE RezeptID = 3;

DELETE FROM Rezept WHERE ID = 3;

SELECT COUNT(*) AS AnzahlNachher FROM Zutat WHERE RezeptID = 3;


-- =====================================================
-- Test 5: Kategorie loeschen, Rezept bleibt erhalten (ON DELETE SET NULL)
-- =====================================================
SELECT ID, Titel, KategorieID FROM Rezept WHERE KategorieID = 2;

DELETE FROM Kategorie WHERE Name = 'Dessert';

SELECT ID, Titel, KategorieID FROM Rezept WHERE Titel = 'Apfelkuchen';



-- ===================================================
-- Nur fuer wiederholtes Testen 
-- ===================================================
USE cnr_kochrezepte;

-- DELETE
DELETE FROM Zutat;
DELETE FROM Rezept;
DELETE FROM Kategorie;

-- für IDs 1
ALTER TABLE Kategorie AUTO_INCREMENT = 1;
ALTER TABLE Rezept AUTO_INCREMENT = 1;
ALTER TABLE Zutat AUTO_INCREMENT = 1;


USE cnr_kochrezepte;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE Zutat;
TRUNCATE TABLE Rezept;
TRUNCATE TABLE Kategorie;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO Kategorie (ID, Name) VALUES
(1, 'Hauptgericht'),
(2, 'Dessert'),
(3, 'Frühstück'),
(4, 'Suppe'),
(5, 'Snack & Vorspeise');

INSERT INTO Rezept (ID, Titel, Beschreibung, Zubereitung, Zubereitungszeit, KategorieID) VALUES
(1, 'Spaghetti Carbonara', 'Der römische Klassiker', 'Spaghetti kochen. Guanciale Speck in einer Pfanne knusprig braten. Eigelb mit Pecorino und Pfeffer verrühren. Nudeln mit etwas Nudelwasser in die Pfanne geben, vom Herd nehmen und die Eimischung schnell unterrühren, bis es cremig wird.', 25, 1),
(2, 'Apfelkuchen', 'Omas traditioneller gedeckter Apfelkuchen', 'Aus Mehl, Zucker, Butter und Ei einen Mürbeteig kneten und kühlen. Äpfel schälen und schneiden. Die Hälfte des Teigs in die Form legen, Äpfel darauf verteilen, mit restlichem Teig abdecken. Bei 180 Grad ca. 45 Minuten backen.', 60, 2),
(3, 'Menemen', 'Traditionelles türkisches Pfannengericht', 'Spitzpaprika klein schneiden und in Öl anbraten. Gewürfelte Tomaten hinzufügen und köcheln lassen, bis sie weich sind. Die Eier direkt in die Pfanne schlagen, salzen und leicht verrühren. Saftig servieren.', 15, 3),
(4, 'Rote Linsensuppe', 'Klassische türkische Mercimek Çorbası', 'Zwiebeln, Karotten und Kartoffeln in Öl andünsten. Rote Linsen und Tomatenmark hinzufügen. Mit Gemüsebrühe aufgießen und 25 Minuten köcheln lassen. Alles fein pürieren und mit Zitronensaft servieren.', 35, 4),
(5, 'Caesar Salad', 'Frischer Salat mit cremigem Dressing', 'Römersalat waschen und zupfen. Hähnchenbrust anbraten und in Streifen schneiden. Für das Dressing Mayonnaise, Parmesan, Knoblauch und etwas Zitronensaft mixen. Salat mit Dressing, Hähnchen und Croutons anrichten.', 20, 5),
(6, 'Lasagne Bolognese', 'Schicht für Schicht ein Genuss', 'Hackfleisch mit Zwiebeln, Karotten und Tomaten zu einer Bolognese einkochen. Eine Béchamelsauce zubereiten. In einer Auflaufform abwechselnd Sauce, Bolognese und Lasagneplatten schichten. Mit viel Käse bei 200 Grad 35 Minuten backen.', 75, 1),
(7, 'Klassisches Tiramisu', 'Italienischer Traum mit Kaffee', 'Eigelb mit Zucker schlagen, Mascarpone unterheben. Eischnee steif schlagen und unterziehen. Löffelbiskuits kurz in kalten Espresso tauchen. Schichtweise Biskuits und Creme in eine Form füllen. Vor dem Servieren mit Kakao bestreuen.', 40, 2),
(8, 'Fluffy Pancakes', 'Amerikanische dicke Pfannkuchen', 'Mehl, Backpulver, Zucker und eine Prise Salz mischen. Milch, geschmolzene Butter und Ei verrühren. Die flüssigen Zutaten unter die trockenen heben. In einer Pfanne portionsweise von beiden Seiten goldbraun backen.', 20, 3),
(9, 'Tomatensuppe', 'Cremige Tomatensuppe', 'Knoblauch und Zwiebeln andünsten. Passierte und gestückelte Tomaten dazugeben. Mit Gemüsebrühe köcheln lassen. Sahne hinzufügen, mit Salz, Pfeffer und Zucker abschmecken und mit frischem Basilikum garnieren.', 25, 4),
(10, 'Frische Guacamole', 'Mexikanischer Avocado-Dip', 'Avocados halbieren, den Kern entfernen und das Fruchtfleisch mit einer Gabel zerdrücken. Fein gewürfelte Tomaten, Zwiebeln und Koriander untermischen. Mit Limettensaft, Salz und Pfeffer abschmecken. Mit Nachos servieren.', 10, 5),
(11, 'Cremiges Hähnchen Curry', 'Mit Kokosmilch und Gemüse', 'Hähnchenbrustwürfel in einer Pfanne scharf anbraten. Gemüse dazugeben. Currypaste einrühren und mit Kokosmilch ablöschen. 15 Minuten köcheln lassen, bis die Sauce eindickt. Mit Reis servieren.', 30, 1),
(12, 'Schoko Brownies', 'Außen knusprig, innen saftig', 'Butter und dunkle Schokolade schmelzen. Eier und Zucker cremig schlagen. Die Schokomasse einrühren. Mehl und Kakao vorsichtig unterheben. In einer eckigen Form bei 175 Grad ca. 22 Minuten backen.', 35, 2),
(13, 'Avocado Ei Toast', 'Modernes, gesundes Frühstück', 'Vollkornbrot kross toasten. Avocado mit etwas Zitronensaft, Salz und Pfeffer zerdrücken und auf dem Toast verstreichen. In einer pfanne zwei Spiegeleier braten und auf die Avocadoschicht legen. Mit Chiliflocken garnieren.', 12, 3),
(14, 'Kürbis-Ingwer Suppe', 'Wärmend für kalte Tage', 'Hokkaidokürbis entkernen und würfeln. Mit Zwiebeln und frischem Ingwer in Butter anschwitzen. Mit Brühe weich kochen. Kokosmilch dazugeben, fein pürieren und mit Kürbiskernen anrichten.', 40, 4),
(15, 'Schnelle Pizza-Toasts', 'Der perfekte Mitternachtssnack', 'Toastbrot mit Tomatenmark bestreichen. Mit Salz, Pfeffer und Oregano würzen. Nach Belieben mit Salami, Champignons und viel geriebenem Gouda belegen. Im Ofen bei 200 Grad backen, bis der Käse schmilzt.', 15, NULL);

INSERT INTO Zutat (Name, Menge, Einheit, RezeptID) VALUES
('Spaghetti', 400, 'g', 1),
('Guanciale oder Speck', 150, 'g', 1),
('Eigelb', 4, 'Stück', 1),
('Pecorino Romano', 50, 'g', 1),
('Äpfel', 4, 'Stück', 2),
('Mehl', 300, 'g', 2),
('Zucker', 150, 'g', 2),
('Butter', 150, 'g', 2),
('Eier', 3, 'Stück', 3),
('Tomaten', 2, 'Stück', 3),
('Spitzpaprika', 3, 'Stück', 3),
('Olivenöl', 2, 'EL', 3),
('Rote Linsen', 200, 'g', 4),
('Karotte', 1, 'Stück', 4),
('Kartoffel', 1, 'Stück', 4),
('Gemüsebrühe', 1, 'Liter', 4),
('Römersalat', 1, 'Stück', 5),
('Hähnchenbrust', 250, 'g', 5),
('Mayonnaise', 3, 'EL', 5),
('Parmesan', 30, 'g', 5),
('Lasagneplatten', 12, 'Stück', 6),
('Hackfleisch', 500, 'g', 6),
('Gehackte Tomaten', 400, 'g', 6),
('Geriebener Käse', 200, 'g', 6),
('Mascarpone', 500, 'g', 7),
('Löffelbiskuits', 200, 'g', 7),
('Espresso (kalt)', 150, 'ml', 7),
('Zucker', 100, 'g', 7),
('Mehl', 200, 'g', 8),
('Milch', 200, 'ml', 8),
('Ei', 1, 'Stück', 8),
('Backpulver', 2, 'TL', 8),
('Passierte Tomaten', 500, 'g', 9),
('Gemüsebrühe', 250, 'ml', 9),
('Sahne', 100, 'ml', 9),
('Frischer Basilikum', 1, 'Bund', 9),
('Avocados', 2, 'Stück', 10),
('Tomate', 1, 'Stück', 10),
('Limettensaft', 1, 'EL', 10),
('Nachos', 150, 'g', 10),
('Hähnchenbrust', 400, 'g', 11),
('Kokosmilch', 400, 'ml', 11),
('Currypaste', 2, 'TL', 11),
('Paprika', 1, 'Stück', 11),
('Dunkle Schokolade', 200, 'g', 12),
('Butter', 150, 'g', 12),
('Eier', 3, 'Stück', 12),
('Kakao', 30, 'g', 12),
('Vollkornbrot', 2, 'Scheiben', 13),
('Avocado', 1, 'Stück', 13),
('Eier', 2, 'Stück', 13),
('Chiliflocken', 1, 'Prise', 13),
('Hokkaidokürbis', 800, 'g', 14),
('Frischer Ingwer', 20, 'g', 14),
('Kokosmilch', 200, 'ml', 14),
('Kürbiskerne', 2, 'EL', 14),
('Toastbrot', 4, 'Scheiben', 15),
('Tomatenmark', 4, 'TL', 15),
('Gouda Käse', 100, 'g', 15),
('Salami', 8, 'Scheiben', 15);