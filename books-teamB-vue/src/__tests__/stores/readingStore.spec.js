import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useReadingStore } from '@/stores/readingStore'

describe('Reading Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('initializes with empty state', () => {
    const store = useReadingStore()
    
    expect(store.readingSessions).toEqual([])
    expect(store.readingNotes).toEqual([])
    expect(store.readingGoals).toEqual([])
    expect(store.currentProgress).toEqual({})
    expect(store.isLoading).toBe(false)
    expect(store.error).toBe(null)
  })

  it('creates a reading session', async () => {
    const store = useReadingStore()
    
    const sessionData = {
      bookId: 1,
      startPage: 0,
      currentPage: 50,
      pagesRead: 50,
      duration: 30,
      notes: 'Great start!',
      totalPages: 400
    }
    
    const result = await store.createReadingSession(sessionData)
    
    expect(result.success).toBe(true)
    expect(store.readingSessions).toHaveLength(1)
    expect(store.readingSessions[0].bookId).toBe(1)
    expect(store.readingSessions[0].pagesRead).toBe(50)
  })

  it('updates progress when creating a session', async () => {
    const store = useReadingStore()
    
    await store.createReadingSession({
      bookId: 1,
      startPage: 0,
      currentPage: 100,
      pagesRead: 100,
      duration: 60,
      totalPages: 400
    })
    
    const progress = store.getProgressByBook(1)
    expect(progress.currentPage).toBe(100)
    expect(progress.totalPages).toBe(400)
    expect(progress.percentage).toBe(25)
  })

  it('creates a reading note', async () => {
    const store = useReadingStore()
    
    const noteData = {
      bookId: 1,
      page: 150,
      text: 'Interesting plot twist!',
      emotion: 'surprised'
    }
    
    const result = await store.createReadingNote(noteData)
    
    expect(result.success).toBe(true)
    expect(store.readingNotes).toHaveLength(1)
    expect(store.readingNotes[0].text).toBe('Interesting plot twist!')
  })

  it('creates a reading goal', async () => {
    const store = useReadingStore()
    
    const goalData = {
      title: 'Lire 50 pages par jour',
      target: 50,
      type: 'daily'
    }
    
    const result = await store.createReadingGoal(goalData)
    
    expect(result.success).toBe(true)
    expect(store.readingGoals).toHaveLength(1)
    expect(store.readingGoals[0].status).toBe('active')
    expect(store.readingGoals[0].progress).toBe(0)
  })

  it('gets sessions by book', async () => {
    const store = useReadingStore()
    
    await store.createReadingSession({
      bookId: 1,
      startPage: 0,
      currentPage: 50,
      pagesRead: 50,
      duration: 30,
      totalPages: 400
    })
    
    await store.createReadingSession({
      bookId: 2,
      startPage: 0,
      currentPage: 30,
      pagesRead: 30,
      duration: 20,
      totalPages: 300
    })
    
    const book1Sessions = store.getSessionsByBook(1)
    expect(book1Sessions).toHaveLength(1)
    expect(book1Sessions[0].bookId).toBe(1)
  })

  it('gets notes by book', async () => {
    const store = useReadingStore()
    
    await store.createReadingNote({
      bookId: 1,
      page: 100,
      text: 'Note 1'
    })
    
    await store.createReadingNote({
      bookId: 1,
      page: 200,
      text: 'Note 2'
    })
    
    await store.createReadingNote({
      bookId: 2,
      page: 50,
      text: 'Note 3'
    })
    
    const book1Notes = store.getNotesByBook(1)
    expect(book1Notes).toHaveLength(2)
  })

  it('calculates total pages read', async () => {
    const store = useReadingStore()
    
    await store.createReadingSession({
      bookId: 1,
      startPage: 0,
      currentPage: 50,
      pagesRead: 50,
      duration: 30,
      totalPages: 400
    })
    
    await store.createReadingSession({
      bookId: 1,
      startPage: 50,
      currentPage: 100,
      pagesRead: 50,
      duration: 30,
      totalPages: 400
    })
    
    expect(store.getTotalPagesRead).toBe(100)
  })

  it('deletes a note', async () => {
    const store = useReadingStore()
    
    const result = await store.createReadingNote({
      bookId: 1,
      page: 100,
      text: 'Test note'
    })
    
    const noteId = result.note.id
    await store.deleteNote(noteId)
    
    expect(store.readingNotes).toHaveLength(0)
  })

  it('updates goal progress', async () => {
    const store = useReadingStore()
    
    const result = await store.createReadingGoal({
      title: 'Read 5 books',
      target: 5,
      type: 'monthly'
    })
    
    const goalId = result.goal.id
    store.updateGoalProgress(goalId, 3)
    
    const goal = store.readingGoals.find(g => g.id === goalId)
    expect(goal.progress).toBe(3)
  })

  it('marks goal as completed when target reached', async () => {
    const store = useReadingStore()
    
    const result = await store.createReadingGoal({
      title: 'Read 5 books',
      target: 5,
      type: 'monthly'
    })
    
    const goalId = result.goal.id
    store.updateGoalProgress(goalId, 5)
    
    const goal = store.readingGoals.find(g => g.id === goalId)
    expect(goal.status).toBe('completed')
  })

  it('clears all reading data', async () => {
    const store = useReadingStore()
    
    await store.createReadingSession({
      bookId: 1,
      startPage: 0,
      currentPage: 50,
      pagesRead: 50,
      duration: 30,
      totalPages: 400
    })
    
    await store.createReadingNote({
      bookId: 1,
      page: 100,
      text: 'Note'
    })
    
    store.clearReadingData()
    
    expect(store.readingSessions).toEqual([])
    expect(store.readingNotes).toEqual([])
    expect(store.currentProgress).toEqual({})
  })
})
