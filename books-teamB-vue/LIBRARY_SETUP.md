# 📚 Library Feature - Documentation

## ✅ Fichiers Créés

### **Pages Vue**
- ✅ `/src/views/Library.vue` - Page principale de la bibliothèque

### **Composants**
- ✅ `/src/components/BookCard.vue` - Carte de livre avec actions
- ✅ `/src/components/BookModal.vue` - Modal d'ajout/édition de livre

### **Stores Pinia**
- ✅ `/src/stores/booksStore.js` - Gestion d'état des livres

### **Services API**
- ✅ `/src/services/booksService.js` - Appels API vers le backend

### **Configuration**
- ✅ `/src/router/index.js` - Route `/library` ajoutée
- ✅ `/src/views/Home.vue` - Lien vers la bibliothèque ajouté
- ✅ `/.env.example` - Variables d'environnement documentées

---

## 🚀 Fonctionnalités Implémentées

### **Page Library**
- ✅ Navigation avec logo BCO et menu
- ✅ Barre de recherche par titre/auteur
- ✅ Filtres par statut (Tous, À lire, En cours, Terminés)
- ✅ Statistiques rapides (Total, Terminés, En cours, À lire)
- ✅ Grid responsive de livres
- ✅ Modal d'ajout/édition de livre
- ✅ États de chargement et vide

### **Composant BookCard**
- ✅ Affichage de la couverture (avec placeholder)
- ✅ Badge de statut coloré
- ✅ Barre de progression (pour livres en cours)
- ✅ Métadonnées (pages, genre)
- ✅ Boutons d'actions contextuels
- ✅ Menu dropdown (Modifier, Changer statut, Supprimer)

### **Composant BookModal**
- ✅ Formulaire complet (Titre, Auteur, ISBN, Pages, Genre, Description, Couverture)
- ✅ Validation des champs obligatoires
- ✅ Sélection de statut avec radio buttons
- ✅ Slider de progression (si en cours)
- ✅ Mode ajout et édition

### **Store Pinia (booksStore)**
- ✅ State : books, currentBook, isLoading, error
- ✅ Actions : fetchUserBooks, addBook, updateBook, deleteBook, updateBookStatus
- ✅ Getters : toReadBooks, inProgressBooks, completedBooks, booksCount

### **Service API (booksService)**
- ✅ getUserBooks(userId)
- ✅ addBook(userId, bookData)
- ✅ updateBook(userId, bookId, bookData)
- ✅ deleteBook(userId, bookId)
- ✅ searchBooks(query)
- ✅ updateProgress(userId, bookId, progress)

---

## 🔗 Endpoints Backend Requis

Le frontend appelle ces endpoints (à implémenter dans Spring Boot) :

```java
GET    /api/users/{userId}/library          // Récupérer tous les livres
POST   /api/users/{userId}/books            // Ajouter un livre
PUT    /api/users/{userId}/books/{bookId}   // Mettre à jour un livre
DELETE /api/users/{userId}/books/{bookId}   // Supprimer un livre
GET    /api/books/search?query={query}      // Rechercher des livres
GET    /api/books/{bookId}                  // Récupérer un livre par ID
PATCH  /api/users/{userId}/books/{bookId}/progress  // Mettre à jour progression
```

### **Format des données attendues**

**Book Object:**
```json
{
  "id": 1,
  "title": "Le Seigneur des Anneaux",
  "author": "J.R.R. Tolkien",
  "isbn": "978-3-16-148410-0",
  "pages": 1178,
  "genre": "Fantasy",
  "description": "L'histoire de Frodon...",
  "coverUrl": "https://example.com/cover.jpg",
  "status": "IN_PROGRESS",
  "progress": 45,
  "startDate": "2024-01-15",
  "endDate": null
}
```

**Status Enum:**
- `TO_READ` - À lire
- `IN_PROGRESS` - En cours
- `COMPLETED` - Terminé

---

## ⚙️ Configuration Requise

### **Variables d'Environnement**

Créer un fichier `.env` à la racine :

```bash
VITE_API_URL=http://localhost:8080/api
```

### **Installation**

```bash
# Installer les dépendances (déjà fait normalement)
npm install

# Lancer le serveur de développement
npm run dev
```

---

## 🎨 Design System BCO

Le design utilise le **système BCO** existant :

- **Couleurs** : Bleu (#2563eb), Cyan (#06b6d4)
- **Police** : Inter
- **Composants CSS** : `.btn`, `.card`, `.input`, `.navbar`
- **Animations** : `fadeIn`, `scaleIn`, `slideIn`

---

## 📝 À Faire Ensuite

### **Pages suivantes à implémenter :**
1. ⏳ `/reading/:bookId` - Page de session de lecture
2. ⏳ `/stats` - Statistiques de lecture
3. ⏳ `/goals` - Objectifs de lecture
4. ⏳ `/notes` - Journal de notes

### **Composants additionnels :**
1. ⏳ `ProgressBar.vue` - Barre de progression réutilisable
2. ⏳ `ReadingTimer.vue` - Chronomètre de session
3. ⏳ `NoteEditor.vue` - Éditeur de notes riche
4. ⏳ `StatsChart.vue` - Graphiques Chart.js

---

## 🧪 Tests

À implémenter :
- Tests unitaires des composants (Vitest)
- Tests du store Pinia
- Tests d'intégration avec mock API

---

## 📞 Support

Pour toute question :
- Vérifier que le backend Spring est lancé sur `http://localhost:8080`
- Vérifier la console navigateur pour les erreurs
- Vérifier que le token JWT est présent dans localStorage

---

**Statut : ✅ Library Feature - Prête à tester !**
