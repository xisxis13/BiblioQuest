import { defineStore } from 'pinia';

export const useUiStore = defineStore('ui', {
  state: () => ({
    isMenuCollapsed: false,
  }),
  
  actions: {
    toggleMenu() {
      this.isMenuCollapsed = !this.isMenuCollapsed;
      localStorage.setItem('menuCollapsed', JSON.stringify(this.isMenuCollapsed));
    },
    
    initializeMenuState() {
      const saved = localStorage.getItem('menuCollapsed');
      if (saved !== null) {
        this.isMenuCollapsed = JSON.parse(saved);
      }
    }
  }
});
