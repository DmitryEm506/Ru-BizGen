(() => {
  const sections = [...document.querySelectorAll("section.deck")];
  const tocLinks = [...document.querySelectorAll(".toc nav a")];
  const progress = document.querySelector(".progress");
  const toc = document.querySelector(".toc");
  const tocToggle = document.querySelector(".toc-toggle");
  const graphCaption = document.querySelector(".graph__caption");
  const slideCounter = document.querySelector(".slide-counter");
  const navPrev = document.querySelector("[data-nav='prev']");
  const navNext = document.querySelector("[data-nav='next']");

  const sectionById = Object.fromEntries(sections.map((s) => [s.id, s]));
  const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  const SLIDE_MS = reduceMotion ? 0 : 420;

  let activeIndex = 0;
  let animating = false;

  function setCurrent(id) {
    tocLinks.forEach((a) => {
      const active = a.getAttribute("href") === `#${id}`;
      if (active) a.setAttribute("aria-current", "true");
      else a.removeAttribute("aria-current");
    });
  }

  function updateChrome(idx) {
    if (progress) {
      const pct = sections.length > 1 ? ((idx + 1) / sections.length) * 100 : 100;
      progress.style.width = `${pct}%`;
    }
    if (slideCounter) {
      slideCounter.textContent = `${idx + 1} / ${sections.length}`;
    }
    if (navPrev) navPrev.disabled = idx <= 0;
    if (navNext) navNext.disabled = idx >= sections.length - 1;
  }

  function clearAnimClasses(el) {
    el.classList.remove(
      "is-active",
      "is-enter-from-right",
      "is-enter-from-left",
      "is-exit-to-left",
      "is-exit-to-right"
    );
  }

  function goTo(id, { updateHash = true, dir } = {}) {
    const nextIndex = sections.findIndex((s) => s.id === id);
    if (nextIndex < 0 || nextIndex === activeIndex || animating) return;

    const from = sections[activeIndex];
    const to = sections[nextIndex];
    const direction = dir ?? (nextIndex > activeIndex ? 1 : -1);

    setCurrent(id);
    updateChrome(nextIndex);

    if (updateHash) {
      history.replaceState(null, "", `#${id}`);
    }
    if (toc?.classList.contains("is-open")) {
      toc.classList.remove("is-open");
      tocToggle?.setAttribute("aria-expanded", "false");
    }

    if (reduceMotion || SLIDE_MS === 0) {
      clearAnimClasses(from);
      clearAnimClasses(to);
      to.classList.add("is-active");
      to.scrollTop = 0;
      activeIndex = nextIndex;
      return;
    }

    animating = true;
    clearAnimClasses(to);
    to.scrollTop = 0;

    // Prepare incoming slide off-screen, then flip in the same frame.
    to.classList.add(direction > 0 ? "is-enter-from-right" : "is-enter-from-left");
    // Force layout so the starting transform is applied before we transition.
    void to.offsetWidth;

    from.classList.add(direction > 0 ? "is-exit-to-left" : "is-exit-to-right");
    from.classList.remove("is-active");
    to.classList.add("is-active");
    to.classList.remove("is-enter-from-right", "is-enter-from-left");

    window.setTimeout(() => {
      clearAnimClasses(from);
      to.classList.add("is-active");
      activeIndex = nextIndex;
      animating = false;
    }, SLIDE_MS);
  }

  function goBy(delta) {
    const next = activeIndex + delta;
    if (next < 0 || next >= sections.length) return;
    goTo(sections[next].id, { dir: delta > 0 ? 1 : -1 });
  }

  // Initial state
  sections.forEach((s, i) => {
    clearAnimClasses(s);
    if (i === 0) s.classList.add("is-active");
  });

  tocLinks.forEach((a) => {
    a.addEventListener("click", (ev) => {
      ev.preventDefault();
      const id = a.getAttribute("href")?.slice(1);
      if (id) goTo(id);
    });
  });

  document.querySelectorAll("[data-go]").forEach((btn) => {
    btn.addEventListener("click", () => goTo(btn.getAttribute("data-go")));
  });

  navPrev?.addEventListener("click", () => goBy(-1));
  navNext?.addEventListener("click", () => goBy(1));

  // Keyboard: ← → or J K (no Home / End)
  window.addEventListener("keydown", (ev) => {
    const tag = (ev.target && ev.target.tagName) || "";
    if (tag === "INPUT" || tag === "TEXTAREA" || ev.target?.isContentEditable) return;

    if (ev.key === "ArrowRight" || ev.key === "j" || ev.key === "J") {
      ev.preventDefault();
      goBy(1);
    } else if (ev.key === "ArrowLeft" || ev.key === "k" || ev.key === "K") {
      ev.preventDefault();
      goBy(-1);
    }
  });

  // Touch swipe (horizontal)
  let touchX = null;
  let touchY = null;
  document.querySelector(".main")?.addEventListener(
    "touchstart",
    (ev) => {
      const t = ev.changedTouches[0];
      touchX = t.clientX;
      touchY = t.clientY;
    },
    { passive: true }
  );
  document.querySelector(".main")?.addEventListener(
    "touchend",
    (ev) => {
      if (touchX == null) return;
      const t = ev.changedTouches[0];
      const dx = t.clientX - touchX;
      const dy = t.clientY - touchY;
      touchX = null;
      touchY = null;
      if (Math.abs(dx) < 56 || Math.abs(dx) < Math.abs(dy) * 1.2) return;
      goBy(dx < 0 ? 1 : -1);
    },
    { passive: true }
  );

  // TOC mobile
  tocToggle?.addEventListener("click", () => {
    const open = toc.classList.toggle("is-open");
    tocToggle.setAttribute("aria-expanded", open ? "true" : "false");
  });

  // Hash on load
  const initial = location.hash?.slice(1);
  if (initial && sectionById[initial]) {
    const idx = sections.findIndex((s) => s.id === initial);
    sections.forEach((s) => clearAnimClasses(s));
    sections[idx].classList.add("is-active");
    activeIndex = idx;
    setCurrent(initial);
    updateChrome(idx);
  } else if (sections[0]) {
    setCurrent(sections[0].id);
    updateChrome(0);
  }

  // Glossary / code highlight interactivity
  const glossaryItems = [...document.querySelectorAll(".glossary__item[data-api]")];
  const chips = [...document.querySelectorAll(".chip[data-api]")];
  const codeHits = [...document.querySelectorAll(".tok-hi[data-api]")];

  function spotlightApi(api) {
    glossaryItems.forEach((item) => {
      const match = item.getAttribute("data-api") === api;
      item.classList.toggle("is-spotlight", match);
      if (match) {
        item.open = true;
        item.scrollIntoView({ behavior: "smooth", block: "nearest" });
      }
    });
    chips.forEach((c) => c.classList.toggle("is-active", c.getAttribute("data-api") === api));
    codeHits.forEach((h) => h.classList.toggle("is-active", h.getAttribute("data-api") === api));
  }

  chips.forEach((chip) => {
    chip.addEventListener("click", () => spotlightApi(chip.getAttribute("data-api")));
  });
  codeHits.forEach((hit) => {
    hit.addEventListener("click", () => spotlightApi(hit.getAttribute("data-api")));
  });

  // Module graph hotspots
  const edges = [...document.querySelectorAll(".graph__edge")];
  const nodes = [...document.querySelectorAll(".graph__node")];
  const mapMods = [...document.querySelectorAll(".map-mod[data-map]")];
  const captions = {
    core: "Ядро: только Kotlin stdlib. Не знает о plugin, mcp, perf.",
    plugin: "IntelliJ-адаптер. Зависит только от core. Правила границ — в PluginBoundaryArchTest.",
    mcp: "MCP-сервер (Ktor). Зависит только от core.",
    perf: "JMH-бенчмарки. Зависит только от core (+ JMH).",
    arch: "Тестовый модуль: читает bytecode core/mcp/perf (+ jmhJar), не тянет IntelliJ Platform.",
  };

  function setMapActive(id) {
    nodes.forEach((n) => n.classList.toggle("is-active", n.getAttribute("data-node") === id));
    mapMods.forEach((m) => m.classList.toggle("is-active", m.getAttribute("data-map") === id));
    edges.forEach((e) => {
      const lit =
        e.getAttribute("data-from") === id || e.getAttribute("data-to") === id;
      e.classList.toggle("is-lit", lit);
    });
    if (graphCaption) graphCaption.textContent = captions[id] || "";
  }

  function clearMapActive() {
    nodes.forEach((n) => n.classList.remove("is-active"));
    mapMods.forEach((m) => m.classList.remove("is-active"));
    edges.forEach((e) => e.classList.remove("is-lit"));
    if (graphCaption) graphCaption.textContent = "Наведите на модуль, чтобы подсветить связи.";
  }

  nodes.forEach((node) => {
    const id = node.getAttribute("data-node");
    node.addEventListener("mouseenter", () => setMapActive(id));
    node.addEventListener("mouseleave", clearMapActive);
  });

  mapMods.forEach((mod) => {
    const id = mod.getAttribute("data-map");
    mod.addEventListener("mouseenter", () => setMapActive(id));
    mod.addEventListener("mouseleave", clearMapActive);
  });

  // Minimal Kotlin-ish highlighter for static blocks marked data-highlight
  document.querySelectorAll("pre.code[data-highlight]").forEach((pre) => {
    if (pre.dataset.done) return;
    const placeholders = [];
    const park = (html) => {
      placeholders.push(html);
      return `\u0000${placeholders.length - 1}\u0000`;
    };

    let html = pre.textContent
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;");

    html = html
      .replace(/(\/\/.*)$/gm, (m) => park(`<span class="tok-cm">${m}</span>`))
      .replace(/("(?:\\.|[^"\\])*")/g, (m) => park(`<span class="tok-str">${m}</span>`));

    html = html.replace(
      /\b(val|fun|class|object|import|internal|private|override|return|if|in|true|false)\b/g,
      '<span class="tok-kw">$1</span>'
    );

    html = html.replace(/\u0000(\d+)\u0000/g, (_, i) => placeholders[Number(i)]);

    pre.innerHTML = html;
    pre.classList.add("code-block");
    pre.dataset.done = "1";
  });
})();
