-- Migration pour rendre les champs email et phone optionnels dans la table coach

-- Supprimer la contrainte UNIQUE sur l'email
ALTER TABLE coach
    DROP CONSTRAINT IF EXISTS coach_email_key;

-- Supprimer la contrainte de vérification existante sur le téléphone
ALTER TABLE coach
    DROP CONSTRAINT IF EXISTS coach_phone_check;

-- Modifier la colonne email pour permettre les valeurs NULL
ALTER TABLE coach
    ALTER COLUMN email DROP NOT NULL;

-- Modifier la colonne phone pour permettre les valeurs NULL
ALTER TABLE coach
    ALTER COLUMN phone DROP NOT NULL;

-- Ajouter une nouvelle contrainte de vérification pour le téléphone qui accepte les chaînes vides
ALTER TABLE coach
    ADD CONSTRAINT coach_phone_check
        CHECK (phone IS NULL OR phone = '' OR phone ~ '^\+?[0-9]{10,15}$');
