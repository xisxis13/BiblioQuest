<template>
  <div class="flex items-start gap-4 p-4 bg-box border border-border rounded-3xl">
    <div class="w-12 h-12 bg-accent/15 rounded-lg flex items-center justify-center flex-shrink-0">
      <BookOpen class="stroke-accent" />
    </div>

    <div class="w-full h-fit flex flex-col items-start justify-start gap-3">
      <div class="w-full h-fit flex flex-col items-start justify-start">
        <span class="font-semibold text-primary-text mb-1">{{ bookTitle }}</span>
        <span class="text-sm text-secondary-text mb-2">
          Pages {{ session.startPage }} - {{ session.endPage }} • {{ (session.endPage - session.startPage) }} pages lues
        </span>
        <div class="flex items-center gap-4 text-xs text-secondary-text">
          <span class="flex items-center gap-1">
            <Clock :size="16" />
            {{ session.duration }} min
          </span>
          <span>{{ formattedDate }}</span>
        </div>
      </div>
      <span v-if="session.sessionNotes" class="w-full text-sm text-primary-text mt-2 italic">
        "{{ session.sessionNotes }}"
      </span>
    </div>
  </div>
</template>

<script>
import { BookOpen, Clock } from 'lucide-vue-next';
import bookService from '@/services/bookService';

export default {
  name: 'SessionCard',
  components: {
    BookOpen,
    Clock,
  },
  props: {
    session: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      bookTitle: '',
    }
  },
  computed: {
    formattedDate() {
      const date = new Date(this.session.sessionDate)
      const now = new Date()
      const diffTime = Math.abs(now - date)
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
      
      if (diffDays === 0) return "Aujourd'hui"
      if (diffDays === 1) return 'Hier'
      if (diffDays < 7) return `Il y a ${diffDays} jours`
      
      return date.toLocaleDateString('fr-FR')
    }
  },
  async mounted() {
    const book = await bookService.getById(this.session.bookId)
    this.bookTitle = book.title
    console.log(this.bookTitle)
  }
}
</script>
