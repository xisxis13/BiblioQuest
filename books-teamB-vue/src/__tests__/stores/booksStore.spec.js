import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useBooksStore } from '@/stores/booksStore'

// Mock the booksService
vi.mock('@/services/booksService', () => ({
  default: {
    getUserBooks: vi.fn(() => Promise.resolve([
      { id: 1, title: 'Book 1', status: 'TO_READ' },
      { id: 2, title: 'Book 2', status: 'IN_PROGRESS' },
      { id: 3, title: 'Book 3', status: 'COMPLETED' }
    ])),
    addBook: vi.fn((book) => Promise.resolve({ id: 4, ...book })),
    updateBook: vi.fn((id, updates) => Promise.resolve({ id, ...updates })),
    deleteBook: vi.fn(() => Promise.resolve())
  }
}))

describe('Books Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('initializes with empty state', () => {
    const store = useBooksStore()
    
    expect(store.books).toEqual([])
    expect(store.loading).toBe(false)
    expect(store.error).toBe(null)
  })

  it('fetches user books', async () => {
    const store = useBooksStore()
    
    await store.fetchUserBooks()
    
    expect(store.books).toHaveLength(3)
    expect(store.loading).toBe(false)
    expect(store.error).toBe(null)
  })

  it('filters books by status', async () => {
    const store = useBooksStore()
    await store.fetchUserBooks()
    
    expect(store.toReadBooks).toHaveLength(1)
    expect(store.inProgressBooks).toHaveLength(1)
    expect(store.completedBooks).toHaveLength(1)
  })

  it('calculates stats correctly', async () => {
    const store = useBooksStore()
    await store.fetchUserBooks()
    
    expect(store.stats.total).toBe(3)
    expect(store.stats.toRead).toBe(1)
    expect(store.stats.inProgress).toBe(1)
    expect(store.stats.completed).toBe(1)
  })

  it('returns all books', async () => {
    const store = useBooksStore()
    await store.fetchUserBooks()
    
    expect(store.allBooks).toHaveLength(3)
  })
})
