const BASE_URL = '/api'

async function request(url, options = {}) {
  const resp = await fetch(`${BASE_URL}${url}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  })
  if (!resp.ok) {
    let msg = `请求失败 (${resp.status})`
    try {
      const err = await resp.json()
      msg = err.message || msg
    } catch {}
    throw new Error(msg)
  }
  const data = await resp.json()
  if (data.code !== 200) {
    throw new Error(data.message || '请求失败')
  }
  return data.data
}

export const ganttApi = {
  getGanttData(projectId) {
    const params = projectId ? `?projectId=${projectId}` : ''
    return request(`/gantt/tasks${params}`)
  }
}

export const taskApi = {
  list(projectId, keyword) {
    const params = new URLSearchParams()
    if (projectId) params.set('projectId', projectId)
    if (keyword) params.set('keyword', keyword)
    return request(`/tasks?${params}`)
  },

  getById(id) {
    return request(`/tasks/${id}`)
  },

  create(task) {
    return request('/tasks', { method: 'POST', body: JSON.stringify(task) })
  },

  update(id, task) {
    return request(`/tasks/${id}`, { method: 'PUT', body: JSON.stringify(task) })
  },

  delete(id) {
    return request(`/tasks/${id}`, { method: 'DELETE' })
  },

  setLaunchDate(id, launchDate) {
    return request(`/tasks/${id}/launch`, {
      method: 'PUT',
      body: JSON.stringify({ launchDate })
    })
  }
}
