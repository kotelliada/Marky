/*
  Marky · Margin component kit (v1.0)
  Builds HTML for the classes in components.css, plus sample notes, a small Markdown renderer
  (the spec's node set), search ranking, and device frames for documentation.
  Exposes window.Marky.
*/
(function () {
"use strict";
const M = {};

const esc = M.esc = s => String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
const stc = s => s && s !== "default" && s !== "disabled" ? " is-" + s : "";
const dis = s => s === "disabled" ? " disabled" : "";

/* ---------- Icons: 24 px grid, 1.75 stroke, round caps and joins ---------- */
const dot = (x, y) => `<circle cx="${x}" cy="${y}" r="1.4" fill="currentColor" stroke="none"/>`;
const P = {
  search: '<circle cx="11" cy="11" r="6.5"/><path d="M20 20l-4.2-4.2"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  import: '<path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"/><path d="M14 3v5h5"/><path d="M12 11v6M9.5 14.5 12 17l2.5-2.5"/>',
  export: '<path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"/><path d="M14 3v5h5"/><path d="M12 17v-6M9.5 13.5 12 11l2.5 2.5"/>',
  pin: '<path class="mk-pin-head" d="M9 4h6l-1 5 3 3v2H7v-2l3-3z"/><path d="M12 14v6"/>',
  unpin: '<path d="M9 4h6l-1 5 3 3v2H7v-2l3-3z"/><path d="M12 14v6M4 4l16 16"/>',
  more: dot(12, 5.5) + dot(12, 12) + dot(12, 18.5),
  back: '<path d="M15 5l-7 7 7 7"/>',
  close: '<path d="M6 6l12 12M18 6 6 18"/>',
  clear: '<circle cx="12" cy="12" r="8.5"/><path d="M9.5 9.5l5 5M14.5 9.5l-5 5"/>',
  edit: '<path d="M4 20h4L19.5 8.5a2.1 2.1 0 0 0-3-3L5 17z"/><path d="M14.5 7.5l2 2"/>',
  eye: '<path d="M2.5 12S6 5.5 12 5.5 21.5 12 21.5 12 18 18.5 12 18.5 2.5 12 2.5 12z"/><circle cx="12" cy="12" r="2.8"/>',
  trash: '<path d="M4 7h16M10 11v6M14 11v6M9 7V4.5h6V7M6 7l1 13h10l1-13"/>',
  heading: '<path d="M6 5v14M17 5v14M6 12h11"/>',
  bold: '<path d="M7 5h6a3.5 3.5 0 0 1 0 7H7zM7 12h7a3.5 3.5 0 0 1 0 7H7z"/>',
  italic: '<path d="M10 5h8M6 19h8M14.5 5l-5 14"/>',
  list: '<path d="M10 6h10M10 12h10M10 18h10"/>' + dot(5, 6) + dot(5, 12) + dot(5, 18),
  olist: '<path d="M11 6h9M11 12h9M11 18h9"/><path d="M4.5 4.5h1.2V9M4 9h3M4 14.6c.4-.7 2.8-.9 2.8.6 0 1.2-2.8 2-2.8 3.3h3"/>',
  task: '<rect x="4" y="4" width="16" height="16" rx="3"/><path d="M8.5 12.2l2.4 2.4 4.6-5"/>',
  quote: '<path d="M5 5v14M9.5 8h9.5M9.5 12h9.5M9.5 16h6"/>',
  code: '<path d="M8.5 7 3.5 12l5 5M15.5 7l5 5-5 5"/>',
  link: '<path d="M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1 1"/><path d="M14 10a4 4 0 0 0-5.7 0l-3 3a4 4 0 0 0 5.7 5.7l1-1"/>',
  check: '<path d="M5.5 12.5l4 4 9-9.5"/>',
  copy: '<rect x="8" y="8" width="12" height="12" rx="2"/><path d="M16 8V6a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h2"/>',
  external: '<path d="M14 4h6v6M20 4l-9 9M18 14v4a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4"/>',
  split: '<rect x="3.5" y="4.5" width="17" height="15" rx="2"/><path d="M12 4.5v15"/>',
  alert: '<circle cx="12" cy="12" r="8.5"/><path d="M12 7.5v5.5"/>' + dot(12, 16.3),
  note: '<path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"/><path d="M14 3v5h5M8.5 13h7M8.5 17h5"/>'
};
M.ICONS = [
  ["search", "Search", "Search entry, search screen"],
  ["plus", "New note", "Bottom bar, desktop sidebar"],
  ["import", "Import .md", "Notes list app bar"],
  ["export", "Export as .md", "Note menu"],
  ["pin", "Pin", "Note app bar, row indicator (filled)"],
  ["unpin", "Unpin", "Note menu when pinned"],
  ["more", "More actions", "Note app bar"],
  ["back", "Back", "Note and search app bars"],
  ["close", "Close", "Dialogs on desktop, dismiss"],
  ["clear", "Clear", "Search field"],
  ["edit", "Edit", "Empty preview action"],
  ["eye", "Preview", "Reserved for icon-only mode switch"],
  ["trash", "Delete", "Note menu"],
  ["heading", "Heading", "Formatting toolbar"],
  ["bold", "Bold", "Formatting toolbar"],
  ["italic", "Italic", "Formatting toolbar"],
  ["list", "Bulleted list", "Formatting toolbar"],
  ["olist", "Numbered list", "Formatting toolbar"],
  ["task", "Checklist", "Formatting toolbar"],
  ["quote", "Quote", "Formatting toolbar"],
  ["code", "Code block", "Formatting toolbar"],
  ["link", "Link", "Formatting toolbar"],
  ["check", "Check", "Checkbox, copied state"],
  ["copy", "Copy", "Code block"],
  ["external", "Opens in browser", "Link affordance on desktop hover"],
  ["split", "Split view", "Desktop, wide windows"],
  ["alert", "Error", "Snackbar for failed import"],
  ["note", "Note", "File and note references"]
];
M.icon = (n, o = {}) => `<svg class="mk-ic${o.cls ? " " + o.cls : ""}" viewBox="0 0 24 24"${o.size ? ` style="width:${o.size}px;height:${o.size}px"` : ""} aria-hidden="true" fill="none" stroke="currentColor" stroke-width="${o.sw || 1.75}" stroke-linecap="round" stroke-linejoin="round">${P[n] || ""}</svg>`;

/* ---------- Primitives ---------- */
M.btn = ({ v = "primary", size = "m", label, icon, state, block, attrs = "" } = {}) =>
  `<button type="button" class="mk-btn mk-btn--${v}${size === "l" ? " mk-btn--l" : ""}${block ? " mk-btn--block" : ""}${stc(state)}"${dis(state)} ${attrs}>${icon ? M.icon(icon) : ""}<span>${label}</span></button>`;

M.iconBtn = ({ icon, label, state, on, attrs = "" } = {}) =>
  `<button type="button" class="mk-iconbtn${on ? " is-on" : ""}${stc(state)}" aria-label="${label}" title="${label}"${on ? ' aria-pressed="true"' : ""}${dis(state)} ${attrs}>${M.icon(icon)}</button>`;

M.seg = ({ opts = ["Edit", "Preview"], sel = "Preview", state, stateIdx = -1, disabled } = {}) =>
  `<div class="mk-seg" role="group" aria-label="Mode">${opts.map((o, i) => `<button type="button" class="mk-seg__opt${o === sel ? " is-selected" : ""}${i === stateIdx ? stc(state) : ""}" aria-pressed="${o === sel}" data-act="mode" data-val="${o.toLowerCase()}"${disabled ? " disabled" : ""}>${o}</button>`).join("")}</div>`;

M.field = ({ ph = "Search notes", value = "", state, kbd, input, id = "mk-q", act } = {}) => {
  const focus = state === "focus", caret = '<span class="mk-caret"></span>';
  const inner = input
    ? `<input class="mk-field__input" id="${id}" type="search" aria-label="${ph}" placeholder="${ph}" value="${esc(value)}" autocomplete="off" spellcheck="false"${dis(state)}><button type="button" class="mk-field__clear" aria-label="Clear search" data-act="clear-q">${M.icon("clear", { size: 18 })}</button>`
    : `<span class="mk-field__input${value ? "" : " is-ph"}">${focus && !value ? caret : ""}${value ? esc(value) : ph}${focus && value ? caret : ""}</span>${value && state !== "disabled" ? `<span class="mk-field__clear" aria-hidden="true">${M.icon("clear", { size: 18 })}</span>` : ""}`;
  const tag = act ? "button" : "div";
  return `<${tag}${act ? ` type="button" data-act="${act}" aria-label="${ph}"` : ""} class="mk-field${stc(state)}${state === "disabled" ? " is-disabled" : ""}">${M.icon("search", { size: 18 })}${inner}${kbd ? `<kbd class="mk-kbd">${kbd}</kbd>` : ""}</${tag}>`;
};

M.titleField = ({ value = "", state } = {}) => {
  const focus = state === "focus", caret = '<span class="mk-caret"></span>';
  return `<div class="mk-titlefield${value ? "" : " is-empty"}" role="textbox" aria-label="Title">${focus && !value ? caret : ""}${value ? esc(value) : "Untitled"}${focus && value ? caret : ""}</div>`;
};

M.kbd = k => `<kbd class="mk-kbd">${k}</kbd>`;
M.overline = t => `<div class="mk-overline">${t}</div>`;
M.tooltip = (t, k) => `<span class="mk-tooltip" role="tooltip">${t}${k ? `<kbd class="mk-kbd mk-kbd--inverse">${k}</kbd>` : ""}</span>`;

/* ---------- Text helpers ---------- */
M.plain = md => String(md || "").split("\n").filter(l => !/^```/.test(l))
  .map(l => l.replace(/^#{1,6} /, "").replace(/^>\s?/, "").replace(/^\s*- \[[ x]\] /, "").replace(/^\s*([-*]|\d+\.) /, ""))
  .join(" ").replace(/\*\*|\*|`/g, "").replace(/\[([^\]]+)\]\([^)]+\)/g, "$1").replace(/\s+/g, " ").trim();
M.words = md => { const t = M.plain(md); return t ? t.split(" ").length : 0; };
const qWords = q => String(q || "").toLowerCase().trim().split(/\s+/).filter(Boolean);
const reEsc = s => s.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
M.hl = (text, q) => {
  const w = qWords(q); if (!w.length) return esc(text);
  const re = new RegExp("(" + w.map(reEsc).join("|") + ")", "gi");
  return String(text).split(re).map((p, i) => i % 2 ? `<mark class="mk-hl">${esc(p)}</mark>` : esc(p)).join("");
};
M.snippet = (md, q, len = 120) => {
  const t = M.plain(md), lo = t.toLowerCase(), w = qWords(q);
  let i = -1;
  for (const x of w) { const j = lo.indexOf(x); if (j >= 0 && (i < 0 || j < i)) i = j; }
  if (i < 0) return M.hl(t.slice(0, len), q) + (t.length > len ? "…" : "");
  let s = Math.max(0, i - 32);
  if (s > 0) { const sp = t.indexOf(" ", s); if (sp >= 0 && sp < i) s = sp + 1; }
  const e = Math.min(t.length, s + len);
  return (s > 0 ? "…" : "") + M.hl(t.slice(s, e), q) + (e < t.length ? "…" : "");
};
/* Title matches rank above text matches; inside each group, most recently edited first. */
M.search = (notes, q) => {
  const w = qWords(q); if (!w.length) return [];
  const hits = notes.map(n => {
    const ti = (n.title || "").toLowerCase(), b = M.plain(n.body).toLowerCase();
    if (!w.every(x => ti.includes(x) || b.includes(x))) return null;
    return { n, title: w.some(x => ti.includes(x)) };
  }).filter(Boolean).sort((a, b) => a.n.age - b.n.age);
  return hits.filter(h => h.title).concat(hits.filter(h => !h.title));
};

/* Time since last edit. age is in minutes. Today is Monday 23 Nov 2026 in the samples. */
const MON = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
M.ago = m => {
  if (m < 1) return "Now";
  if (m < 60) return Math.floor(m) + "m";
  if (m < 1440) return Math.floor(m / 60) + "h";
  if (m < 2880) return "Yesterday";
  if (m < 10080) return Math.floor(m / 1440) + "d";
  if (m < 40320) return Math.floor(m / 10080) + "w";
  const d = new Date(2026, 10, 23, 12, 0, 0); d.setMinutes(d.getMinutes() - m);
  return d.getDate() + " " + MON[d.getMonth()];
};
M.agoLong = m => {
  if (m < 1) return "just now";
  if (m < 60) return Math.floor(m) + " min ago";
  if (m < 1440) return Math.floor(m / 60) + " h ago";
  if (m < 2880) return "yesterday";
  if (m < 10080) return Math.floor(m / 1440) + " days ago";
  return "on " + M.ago(m);
};
M.meta = n => { const w = M.words(n.body); return `Edited ${M.agoLong(n.age)} · ${w} ${w === 1 ? "word" : "words"}`; };

/* ---------- Composite components ---------- */
M.row = ({ n, state, desk, q, selected, isNew, flash } = {}) => {
  const t = n.title || "Untitled", p = M.plain(n.body);
  const prev = q ? M.snippet(n.body, q) : esc(p.slice(0, 180));
  return `<button type="button" class="mk-row${desk ? " mk-row--desk" : ""}${selected ? " is-selected" : ""}${isNew ? " is-new" : ""}${flash ? " is-flash" : ""}${stc(state)}" data-act="open" data-id="${n.id}"${selected ? ' aria-current="true"' : ""}>`
    + `<span class="mk-row__top">${n.pinned ? `<span class="mk-row__pin" title="Pinned">${M.icon("pin", { cls: "is-filled" })}</span>` : ""}<span class="mk-row__title${n.title ? "" : " is-untitled"}">${q ? M.hl(t, q) : esc(t)}</span><span class="mk-row__time">${M.ago(n.age)}</span></span>`
    + `<span class="mk-row__preview${p ? "" : " is-empty"}">${p ? prev : "No text yet"}</span></button>`;
};

M.TOOLS = [["heading", "Heading"], ["bold", "Bold"], ["italic", "Italic"], "|", ["list", "Bulleted list"], ["olist", "Numbered list"], ["task", "Checklist"], "|", ["quote", "Quote"], ["code", "Code block"], ["link", "Link"]];
M.tool = ({ icon, label, active, state } = {}) =>
  `<button type="button" class="mk-tool${active ? " is-active" : ""}${stc(state)}" aria-label="${label}" title="${label}"${active ? ' aria-pressed="true"' : ""}${dis(state)} data-act="tool">${M.icon(icon)}</button>`;
M.toolbar = ({ active = [], disabled = false, top = false, state, stateTool } = {}) =>
  `<div class="mk-toolbar${top ? " mk-toolbar--top" : ""}" role="toolbar" aria-label="Formatting">${M.TOOLS.map(t => t === "|" ? '<span class="mk-toolbar__sep" aria-hidden="true"></span>'
    : M.tool({ icon: t[0], label: t[1], active: active.includes(t[0]), state: disabled ? "disabled" : (t[0] === stateTool ? state : undefined) })).join("")}</div>`;

M.task = ({ label, checked, state, line, disabled } = {}) =>
  `<button type="button" class="mk-task${checked ? " is-checked" : ""}${stc(state)}" role="checkbox" aria-checked="${!!checked}" data-act="task"${line != null ? ` data-line="${line}"` : ""}${disabled || state === "disabled" ? " disabled" : ""}><span class="mk-check">${M.icon("check", { sw: 2.75 })}</span><span class="mk-task__text">${label}</span></button>`;

M.menu = (items, { anchored } = {}) =>
  `<div class="mk-menu${anchored ? " mk-menu--anchored" : ""}" role="menu">${items.map(it => it === "-" ? '<div class="mk-menu__sep" role="separator"></div>'
    : `<button type="button" role="menuitem" class="mk-menu__item${it.danger ? " mk-menu__item--danger" : ""}${stc(it.state)}" data-act="${it.act || ""}"${dis(it.state)}>${M.icon(it.icon)}<span>${it.label}</span>${it.kbd ? M.kbd(it.kbd) : ""}</button>`).join("")}</div>`;
M.noteMenu = (n, o = {}) => M.menu([
  { icon: n.pinned ? "unpin" : "pin", label: n.pinned ? "Unpin" : "Pin to top", act: "pin" },
  { icon: "export", label: "Export as .md", act: "export" },
  "-",
  { icon: "trash", label: "Delete note", danger: true, act: "ask-delete" }
], o);

M.dialog = ({ title, body, actions }) =>
  `<div class="mk-dialog" role="alertdialog" aria-modal="true" aria-label="${esc(title.replace(/<[^>]+>/g, ""))}"><h2 class="mk-dialog__title">${title}</h2><p class="mk-dialog__body">${body}</p><div class="mk-dialog__actions">${actions}</div></div>`;
M.deleteDialog = n => M.dialog({
  title: `Delete “${esc(n.title || "Untitled")}”?`,
  body: "The note is removed from this device. This can’t be undone.",
  actions: M.btn({ v: "secondary", label: "Cancel", attrs: 'data-act="cancel"' }) + M.btn({ v: "danger", label: "Delete", attrs: 'data-act="delete"' })
});

M.snack = ({ msg, action, icon, state } = {}) =>
  `<div class="mk-snack" role="status">${icon ? M.icon(icon) : ""}<span class="mk-snack__msg">${msg}</span>${action ? `<button type="button" class="mk-snack__action${stc(state)}" data-act="snack-action">${action}</button>` : ""}</div>`;

M.empty = ({ glyph, title, body, actions = "", size } = {}) =>
  `<div class="mk-empty${size === "s" ? " mk-empty--s" : ""}"><div class="mk-empty__glyph" aria-hidden="true">${glyph}</div><h2 class="mk-empty__title">${title}</h2><p class="mk-empty__body">${body}</p>${actions ? `<div class="mk-empty__actions">${actions}</div>` : ""}</div>`;
M.EMPTY = {
  notes: ({ desk, size } = {}) => M.empty({
    size, glyph: "#", title: "No notes yet",
    body: desk ? "Write your first note in Markdown, or import a .md file from another editor." : "Tap New note to write your first one, or import a .md file from another editor.",
    actions: size === "s" ? "" : (desk ? M.btn({ v: "primary", label: "New note", icon: "plus", attrs: 'data-act="new"' }) : "") + M.btn({ v: "secondary", label: "Import .md file", icon: "import", attrs: 'data-act="import"' })
  }),
  search: ({ size } = {}) => M.empty({ size, glyph: M.icon("search", { size: size === "s" ? 18 : 22 }), title: "Search your notes", body: "Type a word from a title or from the text. Notes with a matching title come first." }),
  none: (q, { size } = {}) => M.empty({ size, glyph: "0", title: `No notes match “${esc(q)}”`, body: "Check the spelling or try fewer words. Search looks through every title and all note text." }),
  preview: () => M.empty({ glyph: "¶", title: "Nothing to preview", body: "This note is empty. Switch to Edit and start writing.", actions: M.btn({ v: "secondary", label: "Edit note", icon: "edit", attrs: 'data-act="mode" data-val="edit"' }) }),
  noNote: () => M.empty({ glyph: "↵", title: "No note open", body: "Choose a note from the list, or start a new one." })
};

M.appbarList = () => `<header class="mk-appbar"><h1 class="mk-appbar__title">Notes</h1>${M.iconBtn({ icon: "import", label: "Import .md file", attrs: 'data-act="import"' })}</header>`;
M.appbarNote = ({ n, mode = "preview", menuOpen, scrolled } = {}) =>
  `<header class="mk-appbar${scrolled ? " is-scrolled" : ""}">${M.iconBtn({ icon: "back", label: "Back to notes", attrs: 'data-act="back"' })}${M.seg({ sel: mode === "edit" ? "Edit" : "Preview" })}<span class="mk-spacer"></span>${M.iconBtn({ icon: "pin", label: n.pinned ? "Unpin" : "Pin to top", on: n.pinned, attrs: 'data-act="pin"' })}${M.iconBtn({ icon: "more", label: "More actions", state: menuOpen ? "pressed" : undefined, attrs: 'data-act="menu" aria-haspopup="menu"' })}</header>`;
M.appbarSearch = ({ q = "", live = true, state } = {}) =>
  `<header class="mk-appbar mk-appbar--search">${M.iconBtn({ icon: "back", label: "Back to notes", attrs: 'data-act="back"' })}${M.field({ input: live, value: q, state })}</header>`;
M.bottombar = () => `<div class="mk-bottombar">${M.btn({ v: "primary", size: "l", block: true, icon: "plus", label: "New note", attrs: 'data-act="new"' })}</div>`;

/* ---------- Markdown preview renderer ---------- */
function inline(s) {
  const codes = [];
  s = esc(s).replace(/`([^`]+)`/g, (_, c) => { codes.push(c); return "\u0000" + (codes.length - 1) + "\u0000"; });
  s = s.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>")
    .replace(/\*([^*]+)\*/g, "<em>$1</em>")
    .replace(/\[([^\]]+)\]\(([^)\s]+)\)/g, '<a class="mk-md-link" href="$2" target="_blank" rel="noopener">$1</a>');
  return s.replace(/\u0000(\d+)\u0000/g, (_, i) => `<code class="mk-md-ic">${codes[i]}</code>`);
}
M.inline = inline;
function hlCode(s) {
  let out = "", last = 0, m;
  const re = /("[^"]*")|\b(fun|val|var|return|class|data|if|else|null|true|false)\b|(\/\/.*$)|\b(\d+)\b/g;
  while ((m = re.exec(s))) {
    out += esc(s.slice(last, m.index));
    out += `<span class="mk-tk-${m[1] ? "s" : m[2] ? "k" : m[3] ? "c" : "n"}">${esc(m[0])}</span>`;
    last = re.lastIndex;
  }
  return out + esc(s.slice(last));
}
const TASK = /^\s*- \[( |x)\] (.*)/, ITEM = /^(\s*)([-*]|\d+\.) (.*)/;
function parseList(L, i, indent) {
  const tag = /^\s*\d+\./.test(L[i]) ? "ol" : "ul"; let html = "";
  while (i < L.length) {
    const m = L[i].match(ITEM); if (!m || TASK.test(L[i])) break;
    const ind = m[1].length; if (ind < indent) break;
    if (ind > indent) { const r = parseList(L, i, ind); html = html.replace(/<\/li>$/, r[0] + "</li>"); i = r[1]; continue; }
    html += `<li>${inline(m[3])}</li>`; i++;
  }
  return [`<${tag} class="mk-md-list mk-md-${tag}">${html}</${tag}>`, i];
}
M.render = (md, { interactive = true } = {}) => {
  const L = String(md).split("\n"); let i = 0, out = "", m;
  while (i < L.length) {
    const l = L[i];
    if (!l.trim()) { i++; continue; }
    if (l.startsWith("```")) {
      const lang = l.slice(3).trim() || "text", buf = []; i++;
      while (i < L.length && !L[i].startsWith("```")) buf.push(L[i++]); i++;
      out += `<div class="mk-code"><div class="mk-code__head"><span>${esc(lang)}</span><button type="button" class="mk-code__copy" data-act="copy">${M.icon("copy")}<span>Copy</span></button></div><pre><code>${buf.map(x => `<span class="mk-ln">${hlCode(x) || " "}</span>`).join("")}</code></pre></div>`;
      continue;
    }
    if ((m = l.match(/^(#{1,3}) (.*)/))) { const n = m[1].length; out += `<div class="mk-md-h mk-md-h${n}" role="heading" aria-level="${n + 1}"><span class="mk-md-mark" aria-hidden="true">${m[1]}</span>${inline(m[2])}</div>`; i++; continue; }
    if (l.startsWith(">")) { const b = []; while (i < L.length && L[i].startsWith(">")) b.push(L[i++].replace(/^>\s?/, "")); out += `<blockquote class="mk-md-quote"><p>${inline(b.join(" "))}</p></blockquote>`; continue; }
    if (TASK.test(l)) {
      let items = "";
      while (i < L.length && (m = L[i].match(TASK))) { items += `<li>${M.task({ label: inline(m[2]), checked: m[1] === "x", line: i, disabled: !interactive })}</li>`; i++; }
      out += `<ul class="mk-md-tasks">${items}</ul>`; continue;
    }
    if (ITEM.test(l)) { const r = parseList(L, i, l.match(ITEM)[1].length); out += r[0]; i = r[1]; continue; }
    const b = []; while (i < L.length && L[i].trim() && !/^(#{1,3} |>|```|\s*([-*]|\d+\.) )/.test(L[i])) b.push(L[i++]);
    out += `<p>${inline(b.join(" "))}</p>`;
  }
  return `<div class="mk-md">${out}</div>`;
};

/* ---------- Markdown editor (raw source with syntax tinting) ---------- */
function synLine(l) {
  let m = l.match(/^(#{1,3} )(.*)/);
  if (m) return `<span class="mk-syn">${m[1]}</span><span class="mk-syn-h">${esc(m[2])}</span>`;
  const codes = [];
  let s = esc(l).replace(/`([^`]+)`/g, (_, c) => { codes.push(c); return "\u0000" + (codes.length - 1) + "\u0000"; });
  s = s.replace(/^(\s*)(- \[[ x]\] |[-*] |\d+\. |&gt; ?)/, '$1<span class="mk-syn">$2</span>');
  s = s.replace(/(^|[^*])\*([^*]+)\*(?!\*)/g, '$1<span class="mk-syn">*</span><em>$2</em><span class="mk-syn">*</span>');
  s = s.replace(/\*\*([^*]+)\*\*/g, '<span class="mk-syn">**</span><b>$1</b><span class="mk-syn">**</span>');
  s = s.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<span class="mk-syn">[</span><span class="mk-syn-link">$1</span><span class="mk-syn">](</span><span class="mk-syn-url">$2</span><span class="mk-syn">)</span>');
  return s.replace(/\u0000(\d+)\u0000/g, (_, i) => `<span class="mk-syn">\`</span>${codes[i]}<span class="mk-syn">\`</span>`);
}
M.source = (md, { sel, caretAfter, caretEnd, noCaret } = {}) => {
  const caret = '<span class="mk-caret"></span>';
  if (!String(md).trim()) return `<div class="mk-editor is-empty" aria-label="Note text">${noCaret ? "" : caret}Write in Markdown…</div>`;
  let fence = false;
  let html = String(md).split("\n").map(l => {
    if (l.startsWith("```")) { fence = !fence; return `<span class="mk-syn">${esc(l)}</span>`; }
    return fence ? `<span class="mk-syn-fence">${esc(l)}</span>` : synLine(l);
  }).join("\n");
  if (sel) html = html.replace(esc(sel), `<span class="mk-sel">${esc(sel)}</span>`);
  if (caretAfter) html = html.replace(esc(caretAfter), esc(caretAfter) + caret);
  else if (caretEnd && !noCaret) html += caret;
  return `<div class="mk-editor" aria-label="Note text">${html}</div>`;
};

/* ---------- Sample notes (first-launch samples plus a realistic library) ---------- */
M.SAMPLE = () => [
  { id: 1, pinned: true, age: 2, title: "Welcome to Marky", body:
`Marky keeps your notes as **plain Markdown**. Type the syntax yourself, or use the toolbar above the keyboard.

## Try it
- [x] Open this note
- [ ] Tick this box in preview
- [ ] Switch to *Edit* and look at the source

## Formatting
1. Wrap words in \`**\` for **bold**
2. Wrap words in \`*\` for *italic*
3. Start a line with \`>\` for a quote

> Your notes stay on this device unless you export them.

\`\`\`kotlin
val note = Note(title = "Ideas")
repository.save(note) // autosaved
\`\`\`

Learn the full syntax at [commonmark.org](https://commonmark.org/help/).` },
  { id: 2, pinned: true, age: 1500, title: "Groceries", body:
`- [ ] Oat milk
- [x] Lemons
- [ ] Sourdough
- [ ] Coffee beans
- [ ] Feta
- [ ] Cherry tomatoes` },
  { id: 3, pinned: false, age: 180, title: "Lisbon trip", body:
`Flights booked for **12 Dec**. Hotel is near *Príncipe Real*.

## To sort
- [x] Book flights
- [ ] Tram 28 pass
- [ ] Sintra day trip

> Pastéis de Belém opens at 8am. Go early.

Metro map: [metrolisboa.pt](https://www.metrolisboa.pt)` },
  { id: 4, pinned: false, age: 2900, title: "Book notes: Deep Work", body:
`## Key ideas
- **Deep work** is focused, cognitively demanding work.
- *Shallow work* is logistical and often done while distracted.

> Schedule every minute of your day.

## Try this week
- [ ] Two 90-minute deep blocks
- [ ] Batch email at 11:00 and 16:00` },
  { id: 5, pinned: false, age: 7300, title: "Standup 18 Nov", body:
`**Done:** import flow, snackbar copy.

**Next:** export as .md, delete confirmation dialog.

- Release notes draft for v1.2 is ready for review.` },
  { id: 6, pinned: false, age: 10100, title: "Gift ideas", body:
`- Mum: ceramics class voucher
- Leo: Lego Technic set
- Sam: a good chef's knife` },
  { id: 7, pinned: false, age: 30500, title: "Kotlin snippets", body:
`Debounce the search input:

\`\`\`kotlin
queryFlow
    .debounce(300)
    .flatMapLatest { repo.search(it) }
\`\`\`

Share state with \`stateIn(scope, WhileSubscribed(5000), initial)\`.` },
  { id: 8, pinned: false, age: 64000, title: "Reading list", body:
`- *The Pragmatic Programmer*
- *A Philosophy of Software Design*
- *Thinking in Systems*` }
];
M.IMPORTED = () => ({ id: 100, pinned: false, age: 0, title: "Meeting notes", body:
`## Agenda
1. Roadmap for December
2. Hiring update

- [ ] Send the summary to the team` });

/* ---------- Device frames (documentation only, not app UI) ---------- */
M.keyboard = () => {
  const r = ["qwertyuiop", "asdfghjkl", "zxcvbnm"];
  return `<div class="dev-kb" aria-hidden="true">${r.map((row, i) => `<div class="dev-kb__row">${i === 2 ? '<span class="dev-key dev-key--w">⇧</span>' : ""}${[...row].map(c => `<span class="dev-key">${c}</span>`).join("")}${i === 2 ? '<span class="dev-key dev-key--w">⌫</span>' : ""}</div>`).join("")}<div class="dev-kb__row"><span class="dev-key dev-key--w2">123</span><span class="dev-key dev-key--space">space</span><span class="dev-key dev-key--w2">return</span></div></div>`;
};
const STATUS = `<div class="dev-status" aria-hidden="true"><span>9:41</span><svg viewBox="0 0 44 12"><g fill="currentColor"><rect x="0" y="7" width="3" height="5" rx="1"/><rect x="5" y="5" width="3" height="7" rx="1"/><rect x="10" y="2.5" width="3" height="9.5" rx="1"/><rect x="15" y="0" width="3" height="12" rx="1"/><rect x="23" y="1" width="18" height="10" rx="3" fill="none" stroke="currentColor" stroke-width="1"/><rect x="25" y="3" width="12" height="6" rx="1.5"/><rect x="42" y="4" width="1.5" height="4" rx=".7"/></g></svg></div>`;
M.phone = (inner, { overlay = "" } = {}) =>
  `<div class="dev dev-phone mk" data-w="384"><div class="dev-screen">${STATUS}<div class="mk-screen">${inner}</div><div class="dev-home" aria-hidden="true"><i></i></div>${overlay}</div></div>`;
M.desktop = (inner, { overlay = "" } = {}) =>
  `<div class="dev dev-desk mk" data-w="1120"><div class="dev-titlebar" aria-hidden="true"><i></i><i></i><i></i><span>Marky</span></div><div class="mk-desk">${inner}${overlay}</div></div>`;

window.Marky = M;
})();
