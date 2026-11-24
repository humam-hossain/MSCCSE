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
  let selectedCode = null;
  let selectedTopic = null;
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
      overlapCodes: ['CSE752', 'CSE754'],
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
      overlapCodes: Array.isArray(candidate.overlapCodes) ? candidate.overlapCodes.filter(code => byCode.has(code)).slice(0, 3) : ['CSE752', 'CSE754'],
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
        return `<button type="button" class="course-option${selectedCode === course.code ? ' is-selected' : ''}" data-open-course="${escapeHtml(course.code)}" aria-pressed="${selectedCode === course.code}" draggable="true" data-drag-code="${escapeHtml(course.code)}">
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
        return `<article class="planned-course${selectedCode === code ? ' is-selected' : ''}" draggable="true" data-drag-code="${escapeHtml(code)}">
          <button class="planned-course-main" type="button" data-open-course="${escapeHtml(code)}" aria-label="Inspect ${escapeHtml(code)} ${escapeHtml(course.title)}"><b>${escapeHtml(code)} · ${course.credits} cr</b><span>${escapeHtml(course.title)}</span></button>
          <button class="course-remove" type="button" data-remove-course="${escapeHtml(code)}" aria-label="Remove ${escapeHtml(code)} from plan" title="Remove from plan">Remove</button>
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
    renderDependencyGraph();
    renderSkillsGraph();
    renderOverlap();
    renderCourseTable();
    renderSupportingNotes();
    if ($('#course-dialog').open && selectedCode) renderCourseDialog(selectedCode, false);
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
    next.overlapCodes = state.overlapCodes;
    state = next;
    selectedCode = null;
    statusMessage = `${plan.name} loaded · saved in this browser`;
    saveState();
    showToast(`${plan.name} loaded. You can now edit the semester board.`);
  }

  function topicsFor(course) {
    return Object.keys(course.topics || {}).map(id => topicById[id]?.name || id);
  }

  function openCourse(code) {
    if (!byCode.has(code)) return;
    selectedCode = code;
    selectedTopic = null;
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

  function setOverlapCodes(codes) {
    state.overlapCodes = codes.filter(Boolean).slice(0, 3);
    saveState();
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
    const nodeW = 188, nodeH = 60, columnGap = 38, left = 20, top = 39, bottom = 20, rowGap = 13;
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
        const formal = parent === 'CSE791' && course.code === 'CSE700B';
        edges.push({ from: parent, to: course.code, formal });
      }
    }
    if (positions.has('CSE791') && positions.has('CSE700B') && !edges.some(edge => edge.from === 'CSE791' && edge.to === 'CSE700B')) edges.push({ from: 'CSE791', to: 'CSE700B', formal: true });

    const edgeMarkup = edges.map(edge => {
      const a = positions.get(edge.from), b = positions.get(edge.to);
      const startX = a.x + nodeW, endX = b.x;
      const delta = Math.max(20, (endX - startX) * .5);
      const connected = !selectedCode || edge.from === selectedCode || edge.to === selectedCode;
      const path = a.x === b.x
        ? `M ${a.cx} ${a.y + nodeH} C ${a.cx + 22} ${a.y + nodeH + 8}, ${b.cx + 22} ${b.y - 8}, ${b.cx} ${b.y}`
        : `M ${startX} ${a.cy} C ${startX + delta} ${a.cy}, ${endX - delta} ${b.cy}, ${endX} ${b.cy}`;
      return `<path class="graph-edge${edge.formal ? ' formal' : ' advisory'}${selectedCode && connected ? ' is-highlight' : ''}${selectedCode && !connected ? ' is-dim' : ''}" d="${path}"/>`;
    }).join('');
    const columnsMarkup = activeStages.map((stage, column) => `<text class="graph-column-title" x="${left + column * (nodeW + columnGap)}" y="22">${escapeHtml(stage.name)}</text>`).join('');
    const nodesMarkup = activeStages.flatMap(stage => stage.courses).map(course => {
      const p = positions.get(course.code);
      const planned = currentLocation(course.code) !== null;
      const dim = selectedCode && selectedCode !== course.code && !(course.preparation || []).includes(selectedCode) && !(course.prerequisites || []).includes(selectedCode);
      const label = course.type === 'outside' ? course.sourceGroup : planned ? 'In your plan' : course.type === 'required' ? 'Mandatory' : course.type === 'thesis' ? 'Thesis' : 'Data Science';
      return `<g class="graph-node${planned ? ' is-planned' : ''}${selectedCode === course.code ? ' is-selected' : ''}${dim ? ' is-dim' : ''}" transform="translate(${p.x},${p.y})" tabindex="0" role="button" aria-label="Inspect ${escapeHtml(course.code)} ${escapeHtml(course.title)}" data-open-course="${escapeHtml(course.code)}"><rect width="${nodeW}" height="${nodeH}" rx="6"/><text x="10" y="18" font-size="11" font-weight="700">${escapeHtml(course.code)} · ${course.credits} cr</text><text x="10" y="36" font-size="11">${escapeHtml(course.title.length > 25 ? course.title.slice(0, 23) + '…' : course.title)}</text><text x="10" y="51" font-size="9" fill="var(--ink-soft)">${escapeHtml(label)}</text></g>`;
    }).join('');
    svg.setAttribute('viewBox', `0 0 ${width} ${height}`);
    svg.setAttribute('width', width);
    svg.setAttribute('height', height);
    svg.innerHTML = `${columnsMarkup}${edgeMarkup}${nodesMarkup}`;
  }

  function planTopicWeight(topicId) {
    return allScheduled().reduce((best, code) => Math.max(best, byCode.get(code)?.topics?.[topicId] || 0), 0);
  }

  function renderSkillsGraph() {
    const svg = $('#skills-graph');
    const courses = visibleCourses(true).filter(course => course.code !== 'CSE700');
    const leftX = 16, leftW = 278, rightX = 760, rightW = 295, nodeH = 25, rowStep = 31;
    let topicY = 30;
    const topicPositions = new Map();
    const topicHeaders = [];
    for (const group of DATA.topicGroups) {
      topicHeaders.push({ name: group.name, y: topicY + 15 });
      topicY += 23;
      for (const id of group.topics) { topicPositions.set(id, topicY); topicY += rowStep; }
      topicY += 6;
    }
    const courseTop = 30;
    const coursePositions = new Map(courses.map((course, index) => [course.code, courseTop + index * rowStep]));
    const height = Math.max(topicY + 6, courseTop + courses.length * rowStep + 15);
    const edges = [];
    for (const course of courses) {
      const y1 = coursePositions.get(course.code) + nodeH / 2;
      for (const [topicId, weight] of Object.entries(course.topics || {})) {
        if (!topicPositions.has(topicId)) continue;
        const y2 = topicPositions.get(topicId) + nodeH / 2;
        let dim = false;
        const topic = topicById[topicId];
        if (skillLens === 'academia' && topic.a < 3) dim = true;
        if (skillLens === 'industry' && topic.i < 3) dim = true;
        if (selectedCode && selectedCode !== course.code && !(byCode.get(selectedCode)?.topics?.[topicId])) dim = true;
        if (selectedTopic && selectedTopic !== topicId) dim = true;
        if (selectedTopic && selectedTopic === topicId) dim = false;
        const pathClass = `skill-edge${weight >= 2 ? ' core' : ''}${dim ? ' is-dim' : ''}${(selectedCode === course.code || selectedTopic === topicId) ? ' is-highlight' : ''}`;
        edges.push(`<path class="${pathClass}" d="M ${leftX + leftW} ${y1} C 515 ${y1}, 535 ${y2}, ${rightX} ${y2}"/>`);
      }
    }
    const headers = topicHeaders.map(item => `<text class="graph-column-title" x="${rightX}" y="${item.y}">${escapeHtml(item.name)}</text>`).join('');
    const courseNodes = courses.map(course => {
      const y = coursePositions.get(course.code);
      const selected = selectedCode === course.code;
      const planned = currentLocation(course.code) !== null;
      const fade = selectedTopic && !course.topics?.[selectedTopic];
      const caption = course.code + ' · ' + (course.short || course.title);
      return `<g class="skill-node graph-node${selected ? ' is-selected' : ''}${planned ? ' is-planned' : ''}${fade ? ' is-dim' : ''}" transform="translate(${leftX},${y})" tabindex="0" role="button" aria-label="Inspect ${escapeHtml(course.code)} ${escapeHtml(course.title)}" data-open-course="${escapeHtml(course.code)}"><rect width="${leftW}" height="${nodeH}" rx="5"/><text x="8" y="17" font-size="10">${escapeHtml(caption.length > 41 ? caption.slice(0, 39) + '…' : caption)}</text></g>`;
    }).join('');
    const topics = topicIds.map(id => {
      const topic = topicById[id];
      const y = topicPositions.get(id);
      const gap = planTopicWeight(id) < 2;
      const faded = (skillLens === 'academia' && topic.a < 3) || (skillLens === 'industry' && topic.i < 3);
      return `<g class="skill-node${selectedTopic === id ? ' is-selected' : ''}${gap ? ' is-gap' : ''}${faded ? ' is-dim' : ''}" transform="translate(${rightX},${y})" tabindex="0" role="button" aria-pressed="${selectedTopic === id}" aria-label="${escapeHtml(topic.name)}${gap ? ', a gap in the current plan' : ''}" data-topic="${escapeHtml(id)}"><rect width="${rightW}" height="${nodeH}" rx="5"/><text x="8" y="17" font-size="10">${escapeHtml(topic.name)}</text><text x="${rightW - 61}" y="17" font-size="8" fill="var(--ink-soft)">A ${topic.a} · I ${topic.i}</text></g>`;
    }).join('');
    svg.setAttribute('viewBox', `0 0 1080 ${height}`);
    svg.setAttribute('width', '1080');
    svg.setAttribute('height', height);
    svg.innerHTML = `${headers}${edges.join('')}${courseNodes}${topics}`;
    if (selectedTopic) {
      const topic = topicById[selectedTopic];
      const common = courses.filter(course => course.topics?.[selectedTopic]).map(course => course.code).join(', ');
      $('#selected-topic').innerHTML = `<b>${escapeHtml(topic.name)}:</b> ${escapeHtml(topic.acad)} ${escapeHtml(topic.ind)} <span>Courses mapped here: ${escapeHtml(common || 'none')}.</span>`;
    } else {
      $('#selected-topic').textContent = 'Select a topic to read its academic and industry context. Course topic mappings are advisory.';
    }
  }

  function overlapCount(a, b) {
    const one = byCode.get(a), two = byCode.get(b);
    if (!one || !two) return 0;
    return Object.keys(one.topics || {}).filter(id => two.topics?.[id]).length;
  }

  function renderOverlapSelectors() {
    const courses = electiveCourses().sort((a, b) => a.code.localeCompare(b.code));
    if (!state.overlapCodes || state.overlapCodes.length < 2) state.overlapCodes = ['CSE752', 'CSE754'];
    const controls = [0, 1, 2].map(index => {
      const value = state.overlapCodes[index] || '';
      const label = index === 0 ? 'Course A' : index === 1 ? 'Course B' : 'Course C (optional)';
      return `<label class="overlap-select">${label}<select data-overlap-select="${index}" aria-label="${label}"><option value="">Choose a course</option>${courses.map(course => `<option value="${escapeHtml(course.code)}" ${course.code === value ? 'selected' : ''}>${escapeHtml(course.code)} · ${escapeHtml(course.title)}</option>`).join('')}</select></label>`;
    }).join('');
    $('#overlap-selectors').innerHTML = controls;
  }

  function renderVenn() {
    const codes = (state.overlapCodes || []).filter(Boolean).slice(0, 3);
    const courses = codes.map(code => byCode.get(code)).filter(Boolean);
    const box = $('#venn-view');
    if (courses.length < 2) {
      box.innerHTML = '<p class="empty-message">Choose at least two courses to compare their mapped topics.</p>';
      return;
    }
    const keys = Array.from({ length: (1 << courses.length) - 1 }, (_, index) => index + 1);
    const buckets = new Map(keys.map(key => [key, []]));
    for (const id of topicIds) {
      let mask = 0;
      courses.forEach((course, index) => { if (course.topics?.[id]) mask |= (1 << index); });
      if (mask) buckets.get(mask)?.push(topicById[id]?.name || id);
    }
    const labels = keys.map(mask => {
      const members = courses.filter((course, index) => mask & (1 << index)).map(course => course.code);
      const absent = courses.filter((course, index) => !(mask & (1 << index))).map(course => course.code);
      return { mask, label: `${members.join(' + ')} only${absent.length ? ` · not ${absent.join(', ')}` : ''}`, topics: buckets.get(mask) || [] };
    });
    box.innerHTML = `<h4 class="venn-title">${courses.map(course => escapeHtml(course.code)).join(' compared with ')}</h4><div class="venn-regions">${labels.map(region => `<div class="venn-region${(region.mask & (region.mask - 1)) ? ' is-shared' : ''}"><span class="venn-region-label">${escapeHtml(region.label)}</span><div class="venn-topics">${region.topics.length ? region.topics.map(escapeHtml).join(', ') : 'No mapped topics'}</div></div>`).join('')}</div>`;
  }

  function renderOverlapMatrix() {
    const courses = electiveCourses().sort((a, b) => a.code.localeCompare(b.code));
    if (!courses.length) { $('#overlap-matrix').innerHTML = '<p class="empty-message">No elective courses to compare.</p>'; return; }
    const head = courses.map(course => `<th scope="col" title="${escapeHtml(course.title)}">${escapeHtml(course.code.replace('CSE', ''))}</th>`).join('');
    const rows = courses.map((course, rowIndex) => `<tr><th scope="row" title="${escapeHtml(course.title)}">${escapeHtml(course.code)} · ${escapeHtml(course.short || course.title)}</th>${courses.map((other, colIndex) => {
      if (rowIndex === colIndex) return '<td><span class="overlap-cell diagonal">·</span></td>';
      const count = overlapCount(course.code, other.code);
      const heat = count ? `heat-${Math.min(5, count)}` : 'empty';
      return `<td><button class="overlap-cell ${heat}" type="button" data-overlap-pair="${escapeHtml(course.code)}|${escapeHtml(other.code)}" aria-label="Compare ${escapeHtml(course.code)} and ${escapeHtml(other.code)}: ${count} mapped topics in common" title="${count} topics in common">${count || ''}</button></td>`;
    }).join('')}</tr>`).join('');
    $('#overlap-matrix').innerHTML = `<table class="overlap-table"><thead><tr><th scope="col">Course</th>${head}</tr></thead><tbody>${rows}</tbody></table>`;
  }

  function renderOverlap() {
    renderOverlapSelectors();
    renderVenn();
    renderOverlapMatrix();
  }

  function renderCourseTable() {
    const query = ($('#table-search').value || '').trim().toLowerCase();
    const courses = visibleCourses(true).filter(course => {
      if (!query) return true;
      const topics = topicsFor(course).join(' ');
      return `${course.code} ${course.title} ${course.sourceGroup} ${topics}`.toLowerCase().includes(query);
    });
    const rows = courses.map(course => {
      const location = currentLocation(course.code);
      const status = location === null ? 'Not planned' : `Semester ${location + 1}`;
      return `<tr><th scope="row"><button class="course-row-button" type="button" data-open-course="${escapeHtml(course.code)}">${escapeHtml(course.code)}</button></th><td>${escapeHtml(course.title)}</td><td>${course.credits}</td><td>${escapeHtml(course.type === 'outside' ? course.sourceGroup : course.type === 'datascience' ? 'Data Science' : course.type === 'required' ? 'Mandatory' : 'Thesis')}</td><td>${escapeHtml(status)}</td></tr>`;
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

    $('#overlap-selectors').addEventListener('change', event => {
      const select = event.target.closest('[data-overlap-select]');
      if (!select) return;
      const index = Number(select.dataset.overlapSelect);
      const next = [...state.overlapCodes];
      while (next.length <= index) next.push('');
      next[index] = select.value;
      const compact = next.filter((code, at) => code && next.indexOf(code) === at);
      state.overlapCodes = compact.slice(0, 3);
      saveState();
    });
    $('#overlap-matrix').addEventListener('click', event => {
      const cell = event.target.closest('[data-overlap-pair]');
      if (!cell) return;
      const [a, b] = cell.dataset.overlapPair.split('|');
      state.overlapCodes = [a, b];
      statusMessage = `${a} compared with ${b}`;
      saveState();
    });

    document.addEventListener('click', event => {
      const opener = event.target.closest('[data-open-course]');
      if (opener) { openCourse(opener.dataset.openCourse); return; }
      const taskButton = event.target.closest('[data-add-task]');
      if (taskButton) { addChecklistTask(taskButton.dataset.addTask); return; }
      const topic = event.target.closest('[data-topic]');
      if (topic) {
        selectedTopic = selectedTopic === topic.dataset.topic ? null : topic.dataset.topic;
        renderSkillsGraph();
      }
    });
    document.addEventListener('keydown', event => {
      const node = event.target.closest('[data-open-course], [data-topic]');
      if (!node || !['Enter', ' '].includes(event.key)) return;
      if (event.target.closest('button')) return;
      event.preventDefault();
      if (node.dataset.openCourse) openCourse(node.dataset.openCourse);
      else {
        selectedTopic = selectedTopic === node.dataset.topic ? null : node.dataset.topic;
        renderSkillsGraph();
      }
    });

    $('#assign-course').addEventListener('click', () => {
      if (!selectedCode) return;
      const index = Number($('#assign-semester').value);
      moveCourse(selectedCode, index);
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
