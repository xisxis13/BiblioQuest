
import { defineStore } from 'pinia'
import reviewService from '@/services/reviewService'

export const useReviewStore = defineStore('review', {
	state: () => ({
		reviews: [],
		loading: false,
		error: null
	}),
	actions: {
		async getBookReviews(bookId) {
			this.loading = true
			this.error = null
			try {
				const data = await reviewService.getBookReviews(bookId)
				this.reviews = data
			} catch (err) {
				this.error = err
			} finally {
				this.loading = false
			}
		},

		async addReview(bookId, review) {
			this.loading = true
			this.error = null
			try {
				const data = await reviewService.addReview(bookId, review)
				// Optionnel: recharger les avis après ajout
				await this.getBookReviews(bookId)
				return data
			} catch (err) {
				this.error = err
				throw err
			} finally {
				this.loading = false
			}
		},

		async deleteReview(bookId) {
			this.loading = true
			this.error = null
			try {
				await reviewService.deleteReview(bookId)
				// Optionnel: recharger les avis après suppression
				await this.getBookReviews(bookId)
			} catch (err) {
				this.error = err
				throw err
			} finally {
				this.loading = false
			}
		},

		async updateReview(bookId, review) {
			this.loading = true
			this.error = null
			try {
				const data = await reviewService.updateReview(bookId, review)
				// Optionnel: recharger les avis après modification
				await this.getBookReviews(bookId)
				return data
			} catch (err) {
				this.error = err
				throw err
			} finally {
				this.loading = false
			}
		}
	}
})

