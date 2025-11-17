import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ProgressBar from '@/components/ProgressBar.vue'

describe('ProgressBar Component', () => {
  it('renders properly with required props', () => {
    const wrapper = mount(ProgressBar, {
      props: {
        currentValue: 150,
        maxValue: 400
      }
    })
    
    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('.progress-bar-container').exists()).toBe(true)
  })

  it('calculates percentage correctly', () => {
    const wrapper = mount(ProgressBar, {
      props: {
        currentValue: 150,
        maxValue: 400
      }
    })
    
    // 150/400 = 37.5% rounded to 38%
    expect(wrapper.vm.percentage).toBe(38)
  })

  it('displays correct label and percentage', () => {
    const wrapper = mount(ProgressBar, {
      props: {
        label: 'Progression de lecture',
        currentValue: 200,
        maxValue: 500
      }
    })
    
    expect(wrapper.text()).toContain('Progression de lecture')
    expect(wrapper.text()).toContain('40%')
  })

  it('shows details when showDetails is true', () => {
    const wrapper = mount(ProgressBar, {
      props: {
        currentValue: 100,
        maxValue: 400,
        unit: 'pages',
        showDetails: true
      }
    })
    
    expect(wrapper.text()).toContain('100 / 400 pages')
    expect(wrapper.text()).toContain('300 pages restants')
  })

  it('hides details when showDetails is false', () => {
    const wrapper = mount(ProgressBar, {
      props: {
        currentValue: 100,
        maxValue: 400,
        showDetails: false
      }
    })
    
    expect(wrapper.text()).not.toContain('100 / 400')
  })

  it('handles 0 maxValue without errors', () => {
    const wrapper = mount(ProgressBar, {
      props: {
        currentValue: 50,
        maxValue: 0
      }
    })
    
    expect(wrapper.vm.percentage).toBe(0)
  })

  it('caps percentage at 100%', () => {
    const wrapper = mount(ProgressBar, {
      props: {
        currentValue: 500,
        maxValue: 400
      }
    })
    
    expect(wrapper.vm.percentage).toBe(100)
  })

  it('applies correct color class', () => {
    const wrapper = mount(ProgressBar, {
      props: {
        currentValue: 50,
        maxValue: 100,
        color: 'success'
      }
    })
    
    expect(wrapper.find('.progress-bar-fill').classes()).toContain('bg-green-500')
  })
})
