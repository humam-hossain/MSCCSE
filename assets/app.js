(() => {
  'use strict';

  const DATA = window.MSC_DATA;
  const $ = (selector, root = document) => root.querySelector(selector);
  const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];
  const byCode = new Map(DATA.courses.map(course => [course.code, course]));
  const topicById = DATA.topics;
  const topicIds = DATA.topicGroups.flatMap(group => group.topics);
  const STORE_KEY = 'brac-msccse-planner-v2';
  const defaultPlan = DATA.plans.find(plan => plan.id === 'C');
  let selectedCourses = new Set();
  let selectedTopic = null;
  let dialogCode = null;
  let catalogFilter = 'all';
  let graphScope = 'plan';
  let skillLens = 'all';
  let draggingCode = null;
  let toastTimer = 0;
  let statusMessage = 'Plan C loaded';

  function stateFromPlan(plan) {
    return {
      semesters: plan.semesters.map((codes, index) => ({ courses: [...codes], cap: index === 0 ? 6 : 12 })),
      showOutside: false,
      checks: {},
      customTasks: [],
      selectedCourses: ['CSE752', 'CSE754'],
      preset: plan.id,
      activeTab: 'dependencies'
    };
  }

  function normalizeState(candidate) {
    if (!candidate || !Array.isArray(candidate.semesters) || !candidate.semesters.length) return stateFromPlan(defaultPlan);
    const semesters = candidate.semesters.slice(0, 10).map((semester, index) => ({
      cap: Math.max(0, Math.min(36, Number.parseInt(semester.cap, 10) || (index === 0 ? 6 : 12))),
      courses: Array.isArray(semester.courses) ? [...new Set(semester.courses.filter(code => byCode.has(code)))] : []
    }));
    const seen = new Set();
    for (const semester of semesters) {
      semester.courses = semester.courses.filter(code => {
        if (seen.has(code)) return false;
        seen.add(code);
        return true;
      });
    }
    return {
      semesters,
      showOutside: Boolean(candidate.showOutside),
      checks: candidate.checks && typeof candidate.checks === 'object' ? candidate.checks : {},
      customTasks: Array.isArray(candidate.customTasks) ? candidate.customTasks.filter(task => task && typeof task.text === 'string').slice(0, 150) : [],
      selectedCourses: Array.isArray(candidate.selectedCourses) ? candidate.selectedCourses.filter(code => byCode.has(code)) : Array.isArray(candidate.overlapCodes) ? candidate.overlapCodes.filter(code => byCode.has(code)) : ['CSE752', 'CSE754'],
      preset: DATA.plans.some(plan => plan.id === candidate.preset) ? candidate.preset : null,
      activeTab: ['dependencies', 'skills', 'overlap', 'table'].includes(candidate.activeTab) ? candidate.activeTab : 'dependencies'
    };
  }

  function decodeShare(encoded) {
    const base64 = encoded.replace(/-/g, '+').replace(/_/g, '/');
    const binary = atob(base64 + '='.repeat((4 - base64.length % 4) % 4));
    const bytes = Uint8Array.from(binary, char => char.charCodeAt(0));
    return JSON.parse(new TextDecoder().decode(bytes));
  }

  function encodeShare(value) {
    const bytes = new TextEncoder().encode(JSON.stringify(value));
    let binary = '';
    for (const byte of bytes) binary += String.fromCharCode(byte);
    return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '');
  }

  function loadState() {
    const fragment = location.hash.match(/(?:^#|&)plan=([^&]+)/);
    if (fragment) {
      try { return normalizeState(decodeShare(decodeURIComponent(fragment[1]))); }
      catch (error) { /* A malformed link falls back to the browser copy. */ }
    }
    try {
      const local = localStorage.getItem(STORE_KEY);
      if (local) return normalizeState(JSON.parse(local));
    } catch (error) { /* Storage can be unavailable in private browsing. */ }
    const initial = stateFromPlan(defaultPlan);
    try {
      const legacy = JSON.parse(localStorage.getItem('msc-plan-checks-v1') || '{}');
      const oldIds = ['d1','d2','d3','d4','d5','d6','s1','s2','s3','s4','g1','g2','g3','g4','g5','t1','t2','t3','t4','t5','t6','p1','p2','p3','p4','p5','p6'];
      let cursor = 0;
      for (const group of DATA.checklists) {
        for (let index = 0; index < group.items.length; index += 1) {
          if (legacy[oldIds[cursor]]) initial.checks[`${group.id}:${index}`] = true;
          cursor += 1;
        }
      }
    } catch (error) { /* There may be no checklist from the older page. */ }
    return initial;
  }

  let state = loadState();
  selectedCourses = new Set(state.selectedCourses.filter(code => byCode.has(code)));

  function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
  }

  function saveState(render = true) {
    try { localStorage.setItem(STORE_KEY, JSON.stringify(state)); }
    catch (error) { statusMessage = 'Browser storage is unavailable'; }
    if (render) renderAll();
  }

  function showToast(message) {
    const toast = $('#toast');
    toast.textContent = message;
    toast.classList.add('is-visible');
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => toast.classList.remove('is-visible'), 2600);
  }

  function isVisible(course) {
    return course.type !== 'outside' || state.showOutside;
  }

  function visibleCourses(includeThesis = true) {
    return DATA.courses.filter(course => isVisible(course) && (includeThesis || !['required', 'thesis'].includes(course.type)));
  }

  function electiveCourses() {
    return DATA.courses.filter(course => course.type === 'datascience' || (course.type === 'outside' && state.showOutside));
  }

  function currentLocation(code) {
    const index = state.semesters.findIndex(semester => semester.courses.includes(code));
    return index < 0 ? null : index;
  }

  function allScheduled() {
    const seen = new Set();
    return state.semesters.flatMap(semester => semester.courses).filter(code => {
      if (seen.has(code)) return false;
      seen.add(code);
      return byCode.has(code);
    });
  }

  function countCredits(codes) {
    return codes.reduce((total, code) => total + (byCode.get(code)?.credits || 0), 0);
  }

  function courseTag(course) {
    if (course.type === 'required') return '<span class="course-tag mandatory">Mandatory</span>';
    if (course.type === 'thesis') return '<span class="course-tag thesis">Thesis</span>';
    if (course.recommended) return '<span class="course-tag foundation">Recommended</span>';
    return '<span class="course-tag">' + escapeHtml(course.sourceGroup || 'Elective') + '</span>';
  }

  function renderCatalog() {
    const search = ($('#course-search').value || '').trim().toLowerCase();
    const items = DATA.courses.filter(course => {
      if (!isVisible(course)) return false;
      if (catalogFilter === 'datascience' && course.type !== 'datascience') return false;
      if (catalogFilter === 'recommended' && !(course.type === 'datascience' && course.recommended)) return false;
      if (search) {
        const topics = Object.keys(course.topics || {}).map(id => topicById[id]?.name || id).join(' ');
        const haystack = `${course.code} ${course.title} ${course.role} ${course.sourceGroup} ${topics}`.toLowerCase();
        if (!haystack.includes(search)) return false;
      }
      return true;
    });
    const groups = [
      ['Data Science electives', items.filter(course => course.type === 'datascience')],
      ['Outside courses', items.filter(course => course.type === 'outside')],
      ['Mandatory and thesis', items.filter(course => course.type === 'required' || course.type === 'thesis')]
    ].filter(([, courses]) => courses.length);
    $('#catalog-count').textContent = `${items.length} courses`;
    if (!items.length) {
      $('#catalog-list').innerHTML = '<p class="empty-message">No courses match that search. Try a shorter title or code.</p>';
      return;
    }
    $('#catalog-list').innerHTML = groups.map(([label, courses]) => `
      <div class="catalog-group-label">${escapeHtml(label)}</div>
      ${courses.map(course => {
        const location = currentLocation(course.code);
        const status = location === null ? (course.type === 'datascience' && course.recommended ? '<span class="inline-status recommended">Recommended</span>' : '') : `<span class="inline-status">S${location + 1}</span>`;
        return `<button type="button" class="course-option${selectedCourses.has(course.code) ? ' is-selected' : ''}" data-open-course="${escapeHtml(course.code)}" draggable="true" data-drag-code="${escapeHtml(course.code)}">
          <span class="course-option-copy"><span class="course-option-code">${escapeHtml(course.code)} · ${course.credits} cr</span><span class="course-option-title">${escapeHtml(course.title)}</span><span class="course-option-meta">${escapeHtml(course.sourceGroup || course.category)}</span></span>${status}
        </button>`;
      }).join('')}
    `).join('');
  }

  function renderBoard() {
    $('#semester-grid').innerHTML = state.semesters.map((semester, index) => {
      const credits = countCredits(semester.courses);
      const coursesHtml = semester.courses.length ? semester.courses.map(code => {
        const course = byCode.get(code);
        if (!course) return '';
        return `<article class="planned-course is-planned${selectedCourses.has(code) ? ' is-selected' : ''}" draggable="true" data-drag-code="${escapeHtml(code)}">
          <button class="planned-course-main" type="button" data-select-course="${escapeHtml(code)}" aria-pressed="${selectedCourses.has(code)}" aria-label="Select ${escapeHtml(code)} ${escapeHtml(course.title)}"><b>${escapeHtml(code)} · ${course.credits} cr</b><span>${escapeHtml(course.title)}</span></button>
          <button class="course-detail-action" type="button" data-course-details="${escapeHtml(code)}">Details</button><button class="course-remove" type="button" data-remove-course="${escapeHtml(code)}" aria-label="Remove ${escapeHtml(code)} from plan" title="Remove from plan">Remove</button>
          ${courseTag(course)}
        </article>`;
      }).join('') : '<div class="semester-empty">Drag or add a course here</div>';
      const last = index === state.semesters.length - 1;
      const removeButton = last && state.semesters.length > 1 ? `<button class="remove-semester" type="button" data-remove-semester="${index}" ${semester.courses.length ? 'disabled title="Move courses out before removing this semester"' : 'title="Remove this empty semester"'}>Remove empty semester</button>` : '';
      return `<section class="semester" data-semester="${index}" aria-label="Semester ${index + 1}, ${credits} credits">
        <header class="semester-head"><div class="semester-title"><b>Semester ${index + 1}</b><span>${credits} credits planned</span></div><label class="cap-control">cap <input type="number" min="0" max="36" step="1" value="${semester.cap}" data-cap="${index}" aria-label="Semester ${index + 1} credit warning cap"> cr</label></header>
        <div class="semester-courses" data-drop-target="${index}">${coursesHtml}</div>${removeButton}
      </section>`;
    }).join('');
    const total = countCredits(allScheduled());
    $('#board-total').textContent = `${total} credits across ${state.semesters.length} semesters`;
    $$('.semester-courses').forEach(zone => {
      zone.addEventListener('dragover', event => { event.preventDefault(); zone.closest('.semester').classList.add('is-drop-target'); });
      zone.addEventListener('dragleave', event => { if (!zone.contains(event.relatedTarget)) zone.closest('.semester').classList.remove('is-drop-target'); });
      zone.addEventListener('drop', event => {
        event.preventDefault();
        zone.closest('.semester').classList.remove('is-drop-target');
        const code = event.dataTransfer.getData('text/course') || draggingCode;
        if (code) moveCourse(code, Number(zone.dataset.dropTarget));
      });
    });
  }

  function scheduledPairs() {
    const electives = allScheduled().map(code => byCode.get(code)).filter(course => course && (course.type === 'datascience' || course.type === 'outside'));
    const pairs = [];
    for (let i = 0; i < electives.length; i += 1) {
      for (let j = i + 1; j < electives.length; j += 1) {
        const shared = Object.keys(electives[i].topics || {}).filter(id => (electives[j].topics || {})[id]);
        if (shared.length >= 4) pairs.push({ a: electives[i], b: electives[j], shared });
      }
    }
    return pairs.sort((a, b) => b.shared.length - a.shared.length);
  }

  function renderSummary() {
    const codes = allScheduled();
    const electives = codes.map(code => byCode.get(code)).filter(course => course && (course.type === 'datascience' || course.type === 'outside'));
    const outside = electives.filter(course => course.type === 'outside').length;
    const credits = countCredits(codes);
    $('#summary-electives').textContent = `${electives.length} / ${DATA.program.electiveCount}`;
    $('#summary-credits').textContent = `${credits} / ${DATA.program.credits}`;
    $('#summary-outside').textContent = `${outside} / ${DATA.program.maxOutsideElectives}`;
    $('#summary-status').textContent = statusMessage || 'Saved in this browser';
    $('#outside-toggle').checked = state.showOutside;
    $('#validation-panel').innerHTML = buildValidationMessages(electives, outside, credits);
    const activePreset = state.preset;
    $$('[data-preset]').forEach(button => {
      const active = button.dataset.preset === activePreset;
      button.classList.toggle('is-active', active);
      button.setAttribute('aria-pressed', String(active));
    });
  }

  function buildValidationMessages(electives, outside, credits) {
    const messages = [];
    if (electives.length < DATA.program.electiveCount) messages.push({ type: 'warning', title: 'Elective count', text: `${DATA.program.electiveCount - electives.length} more elective${DATA.program.electiveCount - electives.length === 1 ? '' : 's'} needed to reach seven.` });
    else if (electives.length > DATA.program.electiveCount) messages.push({ type: 'error', title: 'Elective count', text: `${electives.length - DATA.program.electiveCount} elective${electives.length - DATA.program.electiveCount === 1 ? '' : 's'} over the seven-course plan.` });
    else messages.push({ type: 'ok', title: 'Electives', text: 'Seven elective courses are placed.' });
    if (outside > DATA.program.maxOutsideElectives) messages.push({ type: 'error', title: 'Outside-elective limit', text: `${outside} outside electives selected; the programme allows up to two.` });
    else messages.push({ type: 'ok', title: 'Outside electives', text: `${outside} of ${DATA.program.maxOutsideElectives} used.` });
    if (credits < DATA.program.credits) messages.push({ type: 'warning', title: 'Degree credits', text: `${DATA.program.credits - credits} credits remain to reach 36.` });
    else if (credits > DATA.program.credits) messages.push({ type: 'error', title: 'Degree credits', text: `${credits - DATA.program.credits} credits above the 36-credit degree plan.` });
    else messages.push({ type: 'ok', title: 'Degree credits', text: '36 credits are placed.' });

    state.semesters.forEach((semester, index) => {
      const termCredits = countCredits(semester.courses);
      if (termCredits > semester.cap) messages.push({ type: 'warning', title: `Semester ${index + 1} load`, text: `${termCredits} credits planned against the ${semester.cap}-credit warning cap.` });
    });

    const locations = new Map();
    state.semesters.forEach((semester, index) => semester.courses.forEach(code => locations.set(code, index)));
    if (locations.has('CSE700B')) {
      const b = locations.get('CSE700B');
      if (!locations.has('CSE791') || locations.get('CSE791') >= b) messages.push({ type: 'error', title: 'Thesis prerequisite', text: 'CSE791 must be placed before CSE700B.' });
    }
    if (locations.has('CSE700') && (locations.has('CSE700A') || locations.has('CSE700B'))) messages.push({ type: 'warning', title: 'Thesis choice', text: 'Choose the 12-credit thesis block or the split CSE700A/B path.' });
    if (locations.has('CSE700A') && locations.has('CSE700B') && locations.get('CSE700A') >= locations.get('CSE700B')) messages.push({ type: 'warning', title: 'Thesis sequence', text: 'Place CSE700A before CSE700B.' });

    for (const code of locations.keys()) {
      const course = byCode.get(code);
      (course?.preparation || []).forEach(prep => {
        if (locations.has(prep) && locations.get(prep) > locations.get(code)) {
          messages.push({ type: 'warning', title: 'Suggested order', text: `${prep} is useful preparation for ${code}; consider moving it earlier.` });
        }
      });
    }

    const overlaps = scheduledPairs();
    if (overlaps.length) {
      const pair = overlaps[0];
      messages.push({ type: 'warning', title: 'Topic overlap', text: `${pair.a.code} and ${pair.b.code} share ${pair.shared.length} mapped topics: ${pair.shared.slice(0, 4).map(id => topicById[id]?.name || id).join(', ')}${pair.shared.length > 4 ? '…' : ''}.` });
    }
    return messages.map(message => `<div class="validation-message ${message.type}"><b>${escapeHtml(message.title)}</b>${escapeHtml(message.text)}</div>`).join('');
  }

  function renderAll() {
    renderSummary();
    renderCatalog();
    renderBoard();
    renderExplorerViews();
    renderSupportingNotes();
    if ($('#course-dialog').open && dialogCode) renderCourseDialog(dialogCode, false);
  }

  function markCustom() { state.preset = null; }

  function moveCourse(code, semesterIndex) {
    if (!byCode.has(code) || !state.semesters[semesterIndex]) return;
    state.semesters.forEach(semester => { semester.courses = semester.courses.filter(existing => existing !== code); });
    state.semesters[semesterIndex].courses.push(code);
    markCustom();
    statusMessage = `${code} placed in semester ${semesterIndex + 1}`;
    saveState();
  }

  function removeCourse(code) {
    state.semesters.forEach(semester => { semester.courses = semester.courses.filter(existing => existing !== code); });
    markCustom();
    statusMessage = `${code} removed from the plan`;
    saveState();
  }

  function removeLastSemester(index) {
    if (index !== state.semesters.length - 1 || state.semesters.length <= 1) return;
    if (state.semesters[index].courses.length) { showToast('Move courses out before removing this semester.'); return; }
    state.semesters.pop();
    markCustom();
    statusMessage = `Semester removed · ${state.semesters.length} remain`;
    saveState();
  }

  function addSemester() {
    if (state.semesters.length >= 10) { showToast('The planner supports up to 10 semesters.'); return; }
    state.semesters.push({ cap: 12, courses: [] });
    markCustom();
    statusMessage = `Semester ${state.semesters.length} added`;
    saveState();
    $('#planner').scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  function applyPreset(id) {
    const plan = DATA.plans.find(item => item.id === id);
    if (!plan) return;
    const next = stateFromPlan(plan);
    next.showOutside = state.showOutside;
    next.checks = state.checks;
    next.customTasks = state.customTasks;
    next.selectedCourses = [...selectedCourses];
    state = next;
    statusMessage = `${plan.name} loaded · saved in this browser`;
    saveState();
    showToast(`${plan.name} loaded. You can now edit the semester board.`);
  }

  function topicsFor(course) {
    return Object.keys(course.topics || {}).map(id => topicById[id]?.name || id);
  }

  function openCourse(code) {
    if (!byCode.has(code)) return;
    dialogCode = code;
    renderAll();
    renderCourseDialog(code, true);
  }

  function renderCourseDialog(code, open = true) {
    const course = byCode.get(code);
    if (!course) return;
    $('#dialog-category').textContent = course.type === 'outside' ? `${course.sourceGroup} · outside elective` : (course.type === 'datascience' ? `${course.category === 'found' || course.category === 'core' ? 'Data Science · foundation' : course.category === 'systems' ? 'Data Science · systems' : 'Data Science · advanced'}` : course.type === 'required' ? 'Mandatory course' : 'Thesis option');
    $('#course-detail').innerHTML = `
      <p class="dialog-course-code">${escapeHtml(course.code)} · ${course.credits} credits</p>
      <h2 class="dialog-course-title" id="dialog-title">${escapeHtml(course.title)}</h2>
      <p class="dialog-course-description">${escapeHtml(course.description)}</p>
      ${course.role ? `<div class="dialog-detail-row"><h4>Role in the programme</h4><p>${escapeHtml(course.role)}</p></div>` : ''}
      ${course.why ? `<div class="dialog-detail-row"><h4>${course.recommended ? 'Why it is recommended' : 'Planning note'}</h4><p>${escapeHtml(course.why)}</p></div>` : ''}
      ${topicsFor(course).length ? `<div class="dialog-detail-row"><h4>Mapped topics</h4><div class="topic-tags">${topicsFor(course).map(topic => `<span class="topic-pill">${escapeHtml(topic)}</span>`).join('')}</div></div>` : ''}
      ${(course.preparation || []).length ? `<div class="dialog-detail-row"><h4>Suggested preparation</h4><p>${course.preparation.map(code => escapeHtml(byCode.get(code)?.title || code)).join(', ')}. These links are advisory.</p></div>` : ''}
      <div class="dialog-detail-row"><h4>Study resources</h4><div class="resource-list">${(course.resources || []).map(resource => `<a href="${escapeHtml(resource[1])}" target="_blank" rel="noopener noreferrer">${escapeHtml(resource[0])} ↗</a>`).join('')}
        <a class="search-resource" href="https://www.youtube.com/results?search_query=${encodeURIComponent(course.title + ' course') }" target="_blank" rel="noopener noreferrer">Search YouTube</a>
        <a class="search-resource" href="https://www.coursera.org/search?query=${encodeURIComponent(course.title)}" target="_blank" rel="noopener noreferrer">Search Coursera</a>
        <a class="search-resource" href="https://www.udemy.com/courses/search/?q=${encodeURIComponent(course.title)}" target="_blank" rel="noopener noreferrer">Search Udemy</a></div></div>
      <div class="dialog-detail-row"><a class="text-link" href="${escapeHtml(course.officialUrl)}" target="_blank" rel="noopener noreferrer">Official BRAC CSE course page ↗</a></div>
    `;
    const select = $('#assign-semester');
    const existing = currentLocation(code);
    const target = existing === null ? 0 : existing;
    select.innerHTML = state.semesters.map((semester, index) => `<option value="${index}" ${index === target ? 'selected' : ''}>Semester ${index + 1} · ${countCredits(semester.courses)} / ${semester.cap} credits</option>`).join('');
    $('#assign-course').textContent = existing === null ? 'Add to plan' : `Move from semester ${existing + 1}`;
    $('#add-course-task').dataset.addTask = `Review ${course.code}: ${course.title} and its official outline.`;
    if (open && !$('#course-dialog').open) $('#course-dialog').showModal();
  }

  function addChecklistTask(text) {
    const clean = String(text || '').trim().slice(0, 160);
    if (!clean) return;
    const existing = [...DATA.checklists.flatMap(group => group.items), ...state.customTasks.map(task => task.text)];
    if (existing.some(item => item.toLowerCase() === clean.toLowerCase())) { showToast('That item is already on your checklist.'); return; }
    state.customTasks.push({ id: `task-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`, text: clean });
    statusMessage = 'Checklist updated · saved in this browser';
    saveState();
    showToast('Added to your checklist.');
  }

  function renderExplorerViews() {
    renderExplorerSelection();
    renderDependencyGraph();
    renderSkillsGraph();
    renderOverlap();
    renderCourseTable();
  }

  function renderExplorerSelection() {
    const codes = [...selectedCourses];
    $('#selection-summary').textContent = codes.length
      ? `${codes.length} course${codes.length === 1 ? '' : 's'} selected · click a course to toggle it`
      : 'No courses selected · select courses in any view to compare them';
    $('#selection-course-list').innerHTML = codes.map(code => {
      return `<span class="selection-chip"><span>${escapeHtml(code)}</span><button type="button" data-course-details="${escapeHtml(code)}">Details</button><button type="button" class="selection-remove" data-remove-selection="${escapeHtml(code)}" aria-label="Remove ${escapeHtml(code)} from selection">×</button></span>`;
    }).join('');
    $('#clear-selection').disabled = codes.length === 0 && !selectedTopic;
  }

  function toggleCourseSelection(code) {
    if (!byCode.has(code)) return;
    if (selectedCourses.has(code)) selectedCourses.delete(code);
    else selectedCourses.add(code);
    state.selectedCourses = [...selectedCourses];
    statusMessage = selectedCourses.has(code) ? `${code} selected` : `${code} deselected`;
    saveState();
  }

  function clearExplorerSelection() {
    if (!selectedCourses.size && !selectedTopic) return;
    selectedCourses.clear();
    selectedTopic = null;
    state.selectedCourses = [];
    statusMessage = 'Explorer selection cleared';
    saveState();
  }

  function planTopicStatus(topicId) {
    const weights = allScheduled().map(code => byCode.get(code)?.topics?.[topicId] || 0);
    if (weights.some(weight => weight >= 2)) return 'core';
    if (weights.some(weight => weight > 0)) return 'related';
    return 'gap';
  }

  function selectedTopicIds() {
    const ids = new Set();
    for (const code of selectedCourses) {
      for (const topicId of Object.keys(byCode.get(code)?.topics || {})) ids.add(topicId);
    }
    return ids;
  }

  function renderDependencyGraph() {
    const svg = $('#dependency-graph');
    let courses = visibleCourses(true);
    if (graphScope === 'plan') courses = courses.filter(course => currentLocation(course.code) !== null);
    const visible = new Map(courses.map(course => [course.code, course]));
    const baseStages = [
      { name: 'Preparation', codes: ['CSE711', 'CSE717', 'CSE756', 'CSE716', 'CSE741', 'CSE706', 'CSE708'] },
      { name: 'Core methods', codes: ['CSE710', 'CSE713', 'CSE715', 'CSE752', 'CSE758', 'CSE709', 'CSE718'] },
      { name: 'Advanced AI', codes: ['CSE751', 'CSE753', 'CSE754', 'CSE755', 'CSE762', 'CSE720', 'CSE732'] },
      { name: 'Systems and domains', codes: ['CSE757', 'CSE759', 'CSE761', 'CSE763', 'CSE705', 'CSE731'] },
      { name: 'Research and thesis', codes: ['CSE791', 'CSE700', 'CSE700A', 'CSE700B'] }
    ];
    const stages = baseStages.map(stage => ({ ...stage, courses: stage.codes.map(code => visible.get(code)).filter(Boolean) }));
    const remainingOutside = courses.filter(course => course.type === 'outside' && !stages.some(stage => stage.codes.includes(course.code)));
    if (remainingOutside.length) stages.push({ name: 'Outside options', courses: remainingOutside });
    const activeStages = stages.filter(stage => stage.courses.length);
    const nodeW = 252, nodeH = 88, columnGap = 52, left = 26, top = 54, bottom = 26, rowGap = 18;
    const maxRows = Math.max(1, ...activeStages.map(stage => stage.courses.length));
    const height = top + bottom + maxRows * (nodeH + rowGap) - rowGap;
    const width = left * 2 + activeStages.length * nodeW + Math.max(0, activeStages.length - 1) * columnGap;
    const positions = new Map();
    activeStages.forEach((stage, column) => {
      stage.courses.forEach((course, row) => positions.set(course.code, { x: left + column * (nodeW + columnGap), y: top + row * (nodeH + rowGap), cx: left + column * (nodeW + columnGap) + nodeW / 2, cy: top + row * (nodeH + rowGap) + nodeH / 2 }));
    });
    const edges = [];
    for (const course of courses) {
      const parents = [...(course.prerequisites || []), ...(course.preparation || [])];
      for (const parent of new Set(parents)) {
        if (!positions.has(parent) || !positions.has(course.code)) continue;
        edges.push({ from: parent, to: course.code, formal: parent === 'CSE791' && course.code === 'CSE700B' });
      }
    }
    if (positions.has('CSE791') && positions.has('CSE700B') && !edges.some(edge => edge.from === 'CSE791' && edge.to === 'CSE700B')) edges.push({ from: 'CSE791', to: 'CSE700B', formal: true });

    const edgeMarkup = edges.map(edge => {
      const a = positions.get(edge.from), b = positions.get(edge.to);
      const startX = a.x + nodeW, endX = b.x;
      const delta = Math.max(20, (endX - startX) * .5);
      const connected = [...selectedCourses].some(code => edge.from === code || edge.to === code);
      const path = a.x === b.x
        ? `M ${a.cx} ${a.y + nodeH} C ${a.cx + 22} ${a.y + nodeH + 8}, ${b.cx + 22} ${b.y - 8}, ${b.cx} ${b.y}`
        : `M ${startX} ${a.cy} C ${startX + delta} ${a.cy}, ${endX - delta} ${b.cy}, ${endX} ${b.cy}`;
      return `<path class="graph-edge${edge.formal ? ' formal' : ' advisory'}${selectedCourses.size && connected ? ' is-highlight' : ''}${selectedCourses.size && !connected ? ' is-dim' : ''}" d="${path}"/>`;
    }).join('');
    const columnsMarkup = activeStages.map((stage, column) => `<text class="graph-column-title" x="${left + column * (nodeW + columnGap)}" y="30">${escapeHtml(stage.name)}</text>`).join('');
    const nodesMarkup = activeStages.flatMap(stage => stage.courses).map(course => {
      const point = positions.get(course.code);
      const planned = currentLocation(course.code) !== null;
      const selected = selectedCourses.has(course.code);
      const related = [...selectedCourses].some(code => (byCode.get(code)?.preparation || []).includes(course.code) || (byCode.get(code)?.prerequisites || []).includes(course.code) || (course.preparation || []).includes(code) || (course.prerequisites || []).includes(code));
      const dim = selectedCourses.size > 0 && !selected && !related;
      const label = planned ? 'In your plan' : course.type === 'outside' ? course.sourceGroup : course.type === 'required' ? 'Mandatory' : course.type === 'thesis' ? 'Thesis' : 'Data Science';
      const title = course.title.length > 29 ? course.title.slice(0, 27) + '…' : course.title;
      return `<g class="graph-node${planned ? ' is-planned' : ''}${selected ? ' is-selected' : ''}${dim ? ' is-dim' : ''}" transform="translate(${point.x},${point.y})"><g class="graph-node-select" tabindex="0" role="button" aria-pressed="${selected}" aria-label="Select ${escapeHtml(course.code)} ${escapeHtml(course.title)}" data-select-course="${escapeHtml(course.code)}"><rect width="${nodeW}" height="${nodeH}" rx="8"/><text x="14" y="25" font-size="16" font-weight="700">${escapeHtml(course.code)} · ${course.credits} cr</text><text x="14" y="49" font-size="15">${escapeHtml(title)}</text><text class="node-status" x="14" y="72" font-size="13">${escapeHtml(label)}</text></g><g class="graph-node-action" transform="translate(${nodeW - 88},8)" tabindex="0" role="button" aria-label="Open details for ${escapeHtml(course.code)}" data-course-details="${escapeHtml(course.code)}"><rect width="76" height="30" rx="5"/><text x="38" y="20" text-anchor="middle" font-size="13">Details</text></g></g>`;
    }).join('');
    svg.setAttribute('viewBox', `0 0 ${width} ${height}`);
    svg.setAttribute('width', width);
    svg.setAttribute('height', height);
    svg.innerHTML = `${columnsMarkup}${edgeMarkup}${nodesMarkup}`;
    const availableCount = visibleCourses(true).length;
    $('#dependencies-count').textContent = graphScope === 'plan' ? `${courses.length} planned courses shown · ${availableCount} courses available in the catalog` : `${courses.length} of ${availableCount} available courses shown`;
  }

  function renderSkillsGraph() {
    const svg = $('#skills-graph');
    const courses = visibleCourses(true).filter(course => course.code !== 'CSE700');
    const selectedTopics = selectedTopicIds();
    const leftX = 28, leftW = 438, rightX = 1510, rightW = 540, nodeH = 40, rowStep = 49;
    let topicY = 42;
    const topicPositions = new Map();
    const topicHeaders = [];
    for (const group of DATA.topicGroups) {
      topicHeaders.push({ name: group.name, y: topicY + 19 });
      topicY += 28;
      for (const id of group.topics) { topicPositions.set(id, topicY); topicY += rowStep; }
      topicY += 12;
    }
    const courseTop = 42;
    const coursePositions = new Map(courses.map((course, index) => [course.code, courseTop + index * rowStep]));
    const height = Math.max(topicY + 14, courseTop + courses.length * rowStep + 22);
    const edges = [];
    for (const course of courses) {
      const y1 = coursePositions.get(course.code) + nodeH / 2;
      for (const [topicId, weight] of Object.entries(course.topics || {})) {
        if (!topicPositions.has(topicId)) continue;
        const y2 = topicPositions.get(topicId) + nodeH / 2;
        const topic = topicById[topicId];
        const lensDim = (skillLens === 'academia' && topic.a < 3) || (skillLens === 'industry' && topic.i < 3);
        const selectionDim = selectedCourses.size > 0 && !selectedCourses.has(course.code) && !selectedTopics.has(topicId);
        const topicDim = selectedTopic && selectedTopic !== topicId;
        const highlighted = selectedCourses.has(course.code) || selectedTopic === topicId;
        const planned = currentLocation(course.code) !== null;
        const classes = `skill-edge${weight >= 2 ? ' core' : ''}${planned ? ' is-plan' : ''}${highlighted ? ' is-highlight' : ''}${lensDim || selectionDim || topicDim ? ' is-dim' : ''}`;
        edges.push(`<path class="${classes}" d="M ${leftX + leftW} ${y1} C 880 ${y1}, 1080 ${y2}, ${rightX} ${y2}"/>`);
      }
    }
    const headers = topicHeaders.map(item => `<text class="graph-column-title" x="${rightX}" y="${item.y}">${escapeHtml(item.name)}</text>`).join('');
    const courseNodes = courses.map(course => {
      const y = coursePositions.get(course.code);
      const selected = selectedCourses.has(course.code);
      const planned = currentLocation(course.code) !== null;
      const related = Object.keys(course.topics || {}).some(id => selectedTopics.has(id));
      const fade = (selectedCourses.size > 0 && !selected && !related) || (selectedTopic && !course.topics?.[selectedTopic]);
      const caption = course.code + ' · ' + (course.short || course.title);
      return `<g class="skill-node graph-node${selected ? ' is-selected' : ''}${planned ? ' is-planned' : ''}${fade ? ' is-dim' : ''}" transform="translate(${leftX},${y})"><g class="graph-node-select" tabindex="0" role="button" aria-pressed="${selected}" aria-label="Select ${escapeHtml(course.code)} ${escapeHtml(course.title)}" data-select-course="${escapeHtml(course.code)}"><rect width="${leftW}" height="${nodeH}" rx="7"/><text x="12" y="26" font-size="16">${escapeHtml(caption.length > 36 ? caption.slice(0, 34) + '…' : caption)}</text></g><g class="graph-node-action" transform="translate(${leftW - 98},6)" tabindex="0" role="button" aria-label="Open details for ${escapeHtml(course.code)}" data-course-details="${escapeHtml(course.code)}"><rect width="86" height="28" rx="5"/><text x="43" y="19" text-anchor="middle" font-size="13">Details</text></g></g>`;
    }).join('');
    const topics = topicIds.map(id => {
      const topic = topicById[id];
      const y = topicPositions.get(id);
      const planStatus = planTopicStatus(id);
      const selected = selectedTopic === id;
      const touched = selectedTopics.has(id);
      const faded = (skillLens === 'academia' && topic.a < 3) || (skillLens === 'industry' && topic.i < 3) || (selectedCourses.size > 0 && !touched);
      return `<g class="skill-node topic-node ${planStatus}${selected ? ' is-selected' : ''}${touched ? ' is-touched' : ''}${faded ? ' is-dim' : ''}" transform="translate(${rightX},${y})" tabindex="0" role="button" aria-pressed="${selected}" aria-label="${escapeHtml(topic.name)}; ${planStatus === 'gap' ? 'a gap in the current plan' : `${planStatus} coverage in the current plan`}" data-topic="${escapeHtml(id)}"><rect width="${rightW}" height="${nodeH}" rx="7"/><text x="12" y="26" font-size="16">${escapeHtml(topic.name)}</text><text class="topic-status" x="${rightW - 174}" y="25" font-size="12">${planStatus === 'core' ? 'Core in plan' : planStatus === 'related' ? 'Related in plan' : 'Gap in plan'} · A ${topic.a} / I ${topic.i}</text></g>`;
    }).join('');
    svg.setAttribute('viewBox', `0 0 2080 ${height}`);
    svg.setAttribute('width', '2080');
    svg.setAttribute('height', height);
    svg.innerHTML = `${headers}${edges.join('')}${courseNodes}${topics}`;
    const plannedCount = courses.filter(course => currentLocation(course.code) !== null).length;
    $('#skills-count').textContent = `${courses.length} courses with topic mappings · ${visibleCourses(true).length} available overall · ${plannedCount} in this map · ${topicIds.length} topics`;
    if (selectedTopic) {
      const topic = topicById[selectedTopic];
      const mapped = courses.filter(course => course.topics?.[selectedTopic]);
      const selectedMapped = mapped.filter(course => selectedCourses.has(course.code)).map(course => course.code);
      const status = planTopicStatus(selectedTopic);
      $('#selected-topic').innerHTML = `<b>${escapeHtml(topic.name)}:</b> ${escapeHtml(topic.acad)} ${escapeHtml(topic.ind)} <span>${status === 'gap' ? 'Gap in the current plan.' : `${status} coverage in the current plan.`} Courses mapped here: ${escapeHtml(mapped.map(course => course.code).join(', ') || 'none')}.${selectedCourses.size ? ` Selected courses covering it: ${escapeHtml(selectedMapped.join(', ') || 'none')}.` : ''}</span>`;
    } else {
      $('#selected-topic').textContent = `Select a topic to see its academic and industry context. ${topicIds.length} topics are mapped across ${courses.length} visible courses.`;
    }
  }

  function renderVenn() {
    const codes = [...selectedCourses].slice(0, 3);
    const courses = codes.map(code => byCode.get(code)).filter(Boolean);
    const box = $('#venn-view');
    if (courses.length < 2) {
      box.innerHTML = `<div class="venn-empty"><b>Select two or three courses to compare their mapped topics.</b><p>${selectedCourses.size ? `${selectedCourses.size} course selected. Add another course in any view.` : 'Choose courses on the Skills map, Dependencies view, or topic matrix.'}</p></div>`;
      return;
    }
    const regions = new Map();
    for (let mask = 1; mask < (1 << courses.length); mask += 1) regions.set(mask, []);
    for (const id of topicIds) {
      let mask = 0;
      courses.forEach((course, index) => { if (course.topics?.[id]) mask |= (1 << index); });
      if (mask) regions.get(mask)?.push(topicById[id]?.name || id);
    }
    const positions = courses.length === 2
      ? { 1: [265, 235, 'Only in A'], 3: [450, 235, 'Shared'], 2: [635, 235, 'Only in B'] }
      : { 1: [315, 170, 'Only in A'], 2: [585, 170, 'Only in B'], 4: [450, 402, 'Only in C'], 3: [450, 116, 'A + B'], 5: [360, 292, 'A + C'], 6: [540, 292, 'B + C'], 7: [450, 222, 'Shared by all'] };
    const circles = courses.length === 2
      ? `<circle class="venn-circle set-a" cx="350" cy="235" r="175"/><circle class="venn-circle set-b" cx="550" cy="235" r="175"/>`
      : `<circle class="venn-circle set-a" cx="355" cy="185" r="172"/><circle class="venn-circle set-b" cx="545" cy="185" r="172"/><circle class="venn-circle set-c" cx="450" cy="315" r="172"/>`;
    const setLabels = courses.map((course, index) => {
      const points = courses.length === 2 ? [[282, 42], [570, 42]] : [[240, 40], [560, 40], [400, 492]];
      return `<text class="venn-set-label set-${'abc'[index]}" x="${points[index][0]}" y="${points[index][1]}">${escapeHtml(course.code)}</text>`;
    }).join('');
    const labels = [...regions.entries()].map(([mask, topics]) => {
      const [x, y, label] = positions[mask];
      const shared = mask === (1 << courses.length) - 1 || (mask & (mask - 1)) !== 0;
      return `<g class="venn-region-count${shared ? ' shared' : ''}" transform="translate(${x},${y})"><text class="venn-count" text-anchor="middle">${topics.length}</text><text class="venn-caption" y="21" text-anchor="middle">${escapeHtml(label)}</text></g>`;
    }).join('');
    const topicLists = [...regions.entries()].map(([mask, topics]) => {
      const members = courses.filter((course, index) => mask & (1 << index)).map(course => course.code);
      const label = members.length === courses.length ? 'Shared by all selected courses' : `${members.join(' + ')} only`;
      return `<details class="venn-region-detail"><summary><span>${escapeHtml(label)}</span><b>${topics.length}</b></summary><p>${topics.length ? topics.map(escapeHtml).join(', ') : 'No mapped topics'}</p></details>`;
    }).join('');
    const extra = selectedCourses.size > 3 ? `<p class="venn-note">Showing the first three of ${selectedCourses.size} selected courses. The coverage matrix below includes every visible course.</p>` : '';
    box.innerHTML = `<svg class="venn-diagram ${courses.length === 2 ? 'two-sets' : 'three-sets'}" viewBox="0 0 900 ${courses.length === 2 ? 470 : 520}" role="img" aria-label="Venn diagram comparing ${courses.map(course => escapeHtml(course.code)).join(', ')}">${circles}${setLabels}${labels}</svg><div class="venn-region-list">${topicLists}</div>${extra}`;
  }

  function renderTopicCoverageMatrix() {
    const courses = visibleCourses(true);
    const head = courses.map(course => {
      const planned = currentLocation(course.code) !== null;
      const selected = selectedCourses.has(course.code);
      return `<th scope="col" class="coverage-course-head${planned ? ' in-plan' : ''}${selected ? ' is-selected' : ''}"><button class="coverage-course-select" type="button" data-select-course="${escapeHtml(course.code)}" aria-pressed="${selected}" title="Toggle ${escapeHtml(course.code)} selection"><b>${escapeHtml(course.code)}</b><span>${escapeHtml(course.short || course.title)}</span><small>${planned ? 'In plan' : `${course.credits} credits`}</small></button><button class="matrix-detail-button" type="button" data-course-details="${escapeHtml(course.code)}">Details</button></th>`;
    }).join('');
    const body = DATA.topicGroups.map(group => {
      const groupRow = `<tr class="topic-group-row"><th colspan="${courses.length + 1}">${escapeHtml(group.name)}</th></tr>`;
      const rows = group.topics.map(id => {
        const topic = topicById[id];
        const status = planTopicStatus(id);
        const touched = selectedTopicIds().has(id);
        const selected = selectedTopic === id;
        const cells = courses.map(course => {
          const weight = course.topics?.[id] || 0;
          const className = `${weight >= 2 ? 'core' : weight ? 'related' : 'none'}${currentLocation(course.code) !== null ? ' plan-col' : ''}${selectedCourses.has(course.code) ? ' selected-col' : ''}${selectedCourses.has(course.code) && weight ? ' selected-hit' : ''}`;
          const mark = weight >= 2 ? '●' : weight ? '○' : '—';
          const label = weight >= 2 ? 'Core coverage' : weight ? 'Related coverage' : 'Not mapped';
          return `<td class="${className}" title="${escapeHtml(course.code)}: ${label}"><button class="coverage-cell" type="button" data-select-course="${escapeHtml(course.code)}" data-select-topic="${escapeHtml(id)}" aria-pressed="${selectedCourses.has(course.code)}" aria-label="${escapeHtml(topic.name)} in ${escapeHtml(course.code)}: ${label}; select course and topic">${mark}</button></td>`;
        }).join('');
        return `<tr class="topic-row ${status}${touched ? ' touched-by-selection' : ''}${selected ? ' is-selected' : ''}" data-topic-row="${escapeHtml(id)}"><th scope="row"><button class="topic-row-select" type="button" data-select-topic="${escapeHtml(id)}" aria-pressed="${selected}">${escapeHtml(topic.name)}</button><span class="plan-topic-status ${status}">${status === 'core' ? 'Core in plan' : status === 'related' ? 'Related in plan' : 'Gap in plan'}</span></th>${cells}</tr>`;
      }).join('');
      return groupRow + rows;
    }).join('');
    $('#topic-matrix').innerHTML = `<table class="topic-coverage-table"><thead><tr><th scope="col" class="topic-sticky-head">Topic · plan coverage</th>${head}</tr></thead><tbody>${body}</tbody></table>`;
    const plannedCount = courses.filter(course => currentLocation(course.code) !== null).length;
    $('#overlap-count').textContent = `${courses.length} courses available · ${plannedCount} in your plan · ${topicIds.length} topics`;
  }

  function renderOverlap() {
    renderVenn();
    renderTopicCoverageMatrix();
  }

  function renderCourseTable() {
    const query = ($('#table-search').value || '').trim().toLowerCase();
    const available = visibleCourses(true);
    const courses = available.filter(course => {
      if (!query) return true;
      const topics = topicsFor(course).join(' ');
      return `${course.code} ${course.title} ${course.sourceGroup} ${topics}`.toLowerCase().includes(query);
    });
    $('#table-count').textContent = query ? `Showing ${courses.length} of ${available.length} courses` : `${available.length} courses available`;
    const rows = courses.map(course => {
      const location = currentLocation(course.code);
      const status = location === null ? 'Not planned' : `Semester ${location + 1}`;
      const selected = selectedCourses.has(course.code);
      return `<tr class="course-data-row${location !== null ? ' is-planned' : ''}${selected ? ' is-selected' : ''}"><th scope="row"><button class="course-row-button" type="button" data-select-course="${escapeHtml(course.code)}" aria-pressed="${selected}">${escapeHtml(course.code)}</button><button class="table-detail-button" type="button" data-course-details="${escapeHtml(course.code)}">Details</button></th><td>${escapeHtml(course.title)}</td><td>${course.credits}</td><td>${escapeHtml(course.type === 'outside' ? course.sourceGroup : course.type === 'datascience' ? 'Data Science' : course.type === 'required' ? 'Mandatory' : 'Thesis')}</td><td>${escapeHtml(status)}</td></tr>`;
    }).join('');
    $('#course-table').innerHTML = `<table class="course-data-table"><thead><tr><th scope="col">Code</th><th scope="col">Course</th><th scope="col">Credits</th><th scope="col">Group</th><th scope="col">In this plan</th></tr></thead><tbody>${rows || '<tr><td colspan="5">No courses match that filter.</td></tr>'}</tbody></table>`;
  }

  function renderSupportingNotes() {
    $('#gap-list').innerHTML = `<div class="gap-list">${DATA.gaps.map(gap => `<article class="gap-item"><h4>${escapeHtml(gap.topic)} <span class="inline-status">${escapeHtml(gap.priority)}</span></h4><p>${escapeHtml(gap.how)}</p><button class="text-button" type="button" data-add-task="${escapeHtml(`Close gap: ${gap.topic}. ${gap.how}`)}">Add to checklist</button></article>`).join('')}</div>`;
    $('#books-list').innerHTML = `<table class="books-table"><tbody>${DATA.books.map(([course, book]) => `<tr><th scope="row">${escapeHtml(course)}</th><td>${escapeHtml(book)} <button class="text-button" type="button" data-add-task="Read ${escapeHtml(book)}">Add to checklist</button></td></tr>`).join('')}</tbody></table>`;
    renderChecklist();
  }

  function renderChecklist() {
    let completed = 0, total = 0;
    const groups = DATA.checklists.map(group => {
      const items = group.items.map((text, index) => {
        const key = `${group.id}:${index}`;
        const done = Boolean(state.checks[key]);
        total += 1; if (done) completed += 1;
        return `<label class="checklist-item${done ? ' is-done' : ''}"><input type="checkbox" data-check="${escapeHtml(key)}" ${done ? 'checked' : ''}><span>${escapeHtml(text)}</span></label>`;
      }).join('');
      return `<section class="checklist-group"><h4>${escapeHtml(group.title)}</h4>${items}</section>`;
    }).join('');
    const custom = state.customTasks.map(task => {
      const done = Boolean(state.checks[task.id]);
      total += 1; if (done) completed += 1;
      return `<label class="checklist-item${done ? ' is-done' : ''}"><input type="checkbox" data-check="${escapeHtml(task.id)}" ${done ? 'checked' : ''}><span>${escapeHtml(task.text)}</span></label>`;
    }).join('');
    const customGroup = state.customTasks.length ? `<section class="checklist-group"><h4>My reminders</h4>${custom}</section>` : '';
    $('#checklist-grid').innerHTML = groups + customGroup;
    $('#checklist-summary').textContent = `${completed} of ${total} items complete · saved in this browser`;
  }

  function renderTabs() {
    const active = state.activeTab || 'dependencies';
    $$('.explorer-tab').forEach(tab => {
      const current = tab.dataset.tab === active;
      tab.classList.toggle('is-active', current);
      tab.setAttribute('aria-selected', String(current));
      tab.tabIndex = current ? 0 : -1;
    });
    $$('.explorer-panel').forEach(panel => { panel.hidden = panel.dataset.panel !== active; });
  }

  function activateTab(name, focus = false) {
    if (!['dependencies', 'skills', 'overlap', 'table'].includes(name)) return;
    state.activeTab = name;
    try { localStorage.setItem(STORE_KEY, JSON.stringify(state)); } catch (error) {}
    renderTabs();
    if (focus) $(`[data-tab="${name}"]`).focus();
  }

  function bindEvents() {
    $$('.filter-chip').forEach(button => button.addEventListener('click', () => {
      catalogFilter = button.dataset.filter;
      $$('.filter-chip').forEach(chip => { const active = chip === button; chip.classList.toggle('is-active', active); chip.setAttribute('aria-pressed', String(active)); });
      renderCatalog();
    }));

    $('#course-search').addEventListener('input', renderCatalog);
    $('#table-search').addEventListener('input', renderCourseTable);
    $('#outside-toggle').addEventListener('change', event => {
      state.showOutside = event.target.checked;
      statusMessage = state.showOutside ? 'Outside courses are visible' : 'Outside courses are hidden';
      saveState();
    });
    $('#add-semester').addEventListener('click', addSemester);
    $$('[data-preset]').forEach(button => button.addEventListener('click', () => applyPreset(button.dataset.preset)));

    $('#catalog-list').addEventListener('dragstart', event => {
      const item = event.target.closest('[data-drag-code]');
      if (!item) return;
      draggingCode = item.dataset.dragCode;
      event.dataTransfer.setData('text/course', draggingCode);
      event.dataTransfer.effectAllowed = 'move';
    });
    $('#semester-grid').addEventListener('dragstart', event => {
      const item = event.target.closest('[data-drag-code]');
      if (!item) return;
      draggingCode = item.dataset.dragCode;
      event.dataTransfer.setData('text/course', draggingCode);
      event.dataTransfer.effectAllowed = 'move';
    });
    document.addEventListener('dragend', () => { draggingCode = null; $$('.semester.is-drop-target').forEach(item => item.classList.remove('is-drop-target')); });

    $('#semester-grid').addEventListener('change', event => {
      const capInput = event.target.closest('[data-cap]');
      if (!capInput) return;
      const index = Number(capInput.dataset.cap);
      state.semesters[index].cap = Math.max(0, Math.min(36, Number.parseInt(capInput.value, 10) || 0));
      markCustom();
      statusMessage = `Semester ${index + 1} warning cap updated`;
      saveState();
    });
    $('#semester-grid').addEventListener('click', event => {
      const remove = event.target.closest('[data-remove-course]');
      if (remove) { event.stopPropagation(); removeCourse(remove.dataset.removeCourse); return; }
      const semesterRemove = event.target.closest('[data-remove-semester]');
      if (semesterRemove) removeLastSemester(Number(semesterRemove.dataset.removeSemester));
    });

    $$('.explorer-tab').forEach(tab => tab.addEventListener('click', () => activateTab(tab.dataset.tab)));
    $('.explorer-tabs').addEventListener('keydown', event => {
      if (!['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(event.key)) return;
      const tabs = $$('.explorer-tab');
      const current = tabs.indexOf(document.activeElement);
      let next = current;
      if (event.key === 'ArrowLeft') next = (current + tabs.length - 1) % tabs.length;
      if (event.key === 'ArrowRight') next = (current + 1) % tabs.length;
      if (event.key === 'Home') next = 0;
      if (event.key === 'End') next = tabs.length - 1;
      event.preventDefault(); activateTab(tabs[next].dataset.tab, true);
    });

    $$('[data-graph-scope]').forEach(button => button.addEventListener('click', () => {
      graphScope = button.dataset.graphScope;
      $$('[data-graph-scope]').forEach(item => item.setAttribute('aria-pressed', String(item === button)));
      renderDependencyGraph();
    }));
    $$('[data-lens]').forEach(button => button.addEventListener('click', () => {
      skillLens = button.dataset.lens;
      $$('[data-lens]').forEach(item => item.setAttribute('aria-pressed', String(item === button)));
      renderSkillsGraph();
    }));

    $('#clear-selection').addEventListener('click', clearExplorerSelection);

    document.addEventListener('click', event => {
      const details = event.target.closest('[data-course-details]');
      if (details) { openCourse(details.dataset.courseDetails); return; }
      const removeSelection = event.target.closest('[data-remove-selection]');
      if (removeSelection) { toggleCourseSelection(removeSelection.dataset.removeSelection); return; }
      const course = event.target.closest('[data-select-course]');
      if (course) {
        toggleCourseSelection(course.dataset.selectCourse);
        if (course.dataset.selectTopic) {
          selectedTopic = course.dataset.selectTopic;
          renderExplorerViews();
        }
        return;
      }
      const topic = event.target.closest('[data-topic], [data-select-topic]');
      if (topic) {
        const id = topic.dataset.topic || topic.dataset.selectTopic;
        selectedTopic = selectedTopic === id ? null : id;
        renderExplorerViews();
        return;
      }
      const opener = event.target.closest('[data-open-course]');
      if (opener) { openCourse(opener.dataset.openCourse); return; }
      const clear = event.target.closest('#clear-selection');
      if (clear) { clearExplorerSelection(); return; }
      const taskButton = event.target.closest('[data-add-task]');
      if (taskButton) { addChecklistTask(taskButton.dataset.addTask); return; }
      if (event.target.closest('.course-dialog')) return;
      if (!event.target.closest('button, a, input, select, textarea, summary')) clearExplorerSelection();
    });
    document.addEventListener('keydown', event => {
      if (!['Enter', ' '].includes(event.key) || event.target.closest('button')) return;
      const details = event.target.closest('[data-course-details]');
      const course = event.target.closest('[data-select-course]');
      const topic = event.target.closest('[data-topic], [data-select-topic]');
      if (!details && !course && !topic) return;
      event.preventDefault();
      if (details) openCourse(details.dataset.courseDetails);
      else if (course) toggleCourseSelection(course.dataset.selectCourse);
      else {
        const id = topic.dataset.topic || topic.dataset.selectTopic;
        selectedTopic = selectedTopic === id ? null : id;
        renderExplorerViews();
      }
    });

    $('#assign-course').addEventListener('click', () => {
      if (!dialogCode) return;
      const index = Number($('#assign-semester').value);
      moveCourse(dialogCode, index);
      $('#course-dialog').close();
    });
    $$('.close-dialog').forEach(button => button.addEventListener('click', () => $('#course-dialog').close()));
    $('#course-dialog').addEventListener('click', event => { if (event.target === $('#course-dialog')) $('#course-dialog').close(); });
    $('#add-course-task').addEventListener('click', event => addChecklistTask(event.currentTarget.dataset.addTask));

    $('#custom-task-form').addEventListener('submit', event => {
      event.preventDefault();
      addChecklistTask($('#custom-task').value);
      $('#custom-task').value = '';
    });
    $('#checklist-grid').addEventListener('change', event => {
      const checkbox = event.target.closest('[data-check]');
      if (!checkbox) return;
      state.checks[checkbox.dataset.check] = checkbox.checked;
      statusMessage = 'Checklist saved in this browser';
      saveState();
    });
    $('#clear-checks').addEventListener('click', () => {
      state.checks = {};
      statusMessage = 'Checklist ticks cleared';
      saveState();
      showToast('Completed ticks cleared. Your reminder items are still here.');
    });

    $('#copy-link').addEventListener('click', async () => {
      const url = new URL(location.href);
      url.hash = `plan=${encodeURIComponent(encodeShare(state))}`;
      try {
        await navigator.clipboard.writeText(url.toString());
        statusMessage = 'Share link copied';
        $('#summary-status').textContent = statusMessage;
        showToast('Your plan and checklist are in the copied link.');
      } catch (error) {
        const field = document.createElement('textarea');
        field.value = url.toString(); field.setAttribute('readonly', '');
        field.style.position = 'fixed'; field.style.opacity = '0';
        document.body.appendChild(field); field.select();
        const copied = document.execCommand('copy'); field.remove();
        showToast(copied ? 'Your plan link was copied.' : 'Copy failed. You can copy the address bar after adding a share link.');
      }
    });

    $('#theme-toggle').addEventListener('click', () => {
      const next = document.documentElement.dataset.theme === 'dark' ? 'light' : 'dark';
      document.documentElement.dataset.theme = next;
      try { localStorage.setItem('msc-planner-theme', next); } catch (error) {}
      $('#theme-toggle').setAttribute('aria-label', next === 'dark' ? 'Switch to light theme' : 'Switch to dark theme');
    });
  }

  function init() {
    renderTabs();
    bindEvents();
    renderAll();
    if (location.hash.startsWith('#plan=')) statusMessage = 'Shared plan loaded';
    else statusMessage = state.preset ? `Plan ${state.preset} loaded · saved in this browser` : 'Saved in this browser';
    $('#summary-status').textContent = statusMessage;
    $('#theme-toggle').setAttribute('aria-label', document.documentElement.dataset.theme === 'dark' ? 'Switch to light theme' : 'Switch to dark theme');
  }

  init();
})();
