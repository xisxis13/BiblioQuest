package prj.books_teamB_spring.model;

/**
 * Statuts de lecture pour les livres dans la bibliothèque utilisateur
 */
public enum ReadingStatus {
    TO_READ("À lire"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminé"),
    ABANDONED("Abandonné");

    private final String displayName;

    ReadingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}