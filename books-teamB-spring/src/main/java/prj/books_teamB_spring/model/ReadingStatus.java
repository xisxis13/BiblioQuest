package prj.books_teamB_spring.model;

/**
 * Statuts de lecture pour les livres dans la bibliothèque utilisateur
 */
public enum ReadingStatus {
    TO_READ("À lire", 0),
    IN_PROGRESS("En cours", 1),
    COMPLETED("Terminé", 2),
    ABANDONED("Abandonné", 3);

    private final String displayName;
    private final Integer id;

    ReadingStatus(String displayName, Integer id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getId() {
        return id;
    }

    public static ReadingStatus fromId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ReadingStatus id cannot be null");
        }

        for (ReadingStatus status : values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown ReadingStatus id: " + id);
    }
}