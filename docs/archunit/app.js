(() => {
  const sections = [...document.querySelectorAll("section.deck")];
  const tocLinks = [...document.querySelectorAll(".toc nav a")];
  const progress = document.querySelector(".progress");
  const toc = document.querySelector(".toc");
  const tocToggle = document.querySelector(".toc-toggle");
  const graphCaption = document.querySelector(".graph__caption");

  const sectionById = Object.fromEntries(sections.map((s) => [s.id, s]));

  function setCurrent(id) {
    tocLinks.forEach((a) => {
      const active = a.getAttribute("href") === `#${id}`;
      if (active) a.setAttribute("aria-current", "true");
      else a.removeAttribute("aria-current");
    });
    sections.forEach((s) => s.classList.toggle("is-active", s.id === id));
  }

  function goTo(id, { updateHash = true } = {}) {
    const el = sectionById[id];
    if (!el) return;
    el.scrollIntoView({ behavior: "smooth", block: "start" });
    setCurrent(id);
    if (updateHash) {
      history.replaceState(null, "", `#${id}`);
    }
    if (toc?.classList.contains("is-open")) {
      toc.classList.remove("is-open");
      tocToggle?.setAttribute("aria-expanded", "false");
    }
  }

  function currentIndex() {
    const y = window.scrollY + window.innerHeight * 0.28;
    let idx = 0;
    sections.forEach((s, i) => {
      if (s.offsetTop <= y) idx = i;
    });
    return idx;
  }

  function updateProgress() {
    const max = document.documentElement.scrollHeight - window.innerHeight;
    const pct = max > 0 ? (window.scrollY / max) * 100 : 0;
    if (progress) progress.style.width = `${pct}%`;
  }

  // IntersectionObserver for reveal + TOC sync
  const io = new IntersectionObserver(
    (entries) => {
      entries.forEach((e) => {
        if (e.isIntersecting) e.target.classList.add("is-visible");
      });
      const idx = currentIndex();
      if (sections[idx]) setCurrent(sections[idx].id);
      updateProgress();
    },
    { rootMargin: "-20% 0px -55% 0px", threshold: [0, 0.2, 0.5] }
  );
  sections.forEach((s) => io.observe(s));

  window.addEventListener("scroll", () => {
    updateProgress();
    const idx = currentIndex();
    if (sections[idx]) setCurrent(sections[idx].id);
  }, { passive: true });

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

  // Keyboard nav
  window.addEventListener("keydown", (ev) => {
    const tag = (ev.target && ev.target.tagName) || "";
    if (tag === "INPUT" || tag === "TEXTAREA" || ev.target?.isContentEditable) return;

    const idx = currentIndex();
    if (ev.key === "ArrowRight" || ev.key === "j" || ev.key === "J") {
      ev.preventDefault();
      if (sections[idx + 1]) goTo(sections[idx + 1].id);
    } else if (ev.key === "ArrowLeft" || ev.key === "k" || ev.key === "K") {
      ev.preventDefault();
      if (sections[idx - 1]) goTo(sections[idx - 1].id);
    } else if (ev.key === "Home") {
      ev.preventDefault();
      goTo(sections[0].id);
    } else if (ev.key === "End") {
      ev.preventDefault();
      goTo(sections[sections.length - 1].id);
    }
  });

  // TOC mobile
  tocToggle?.addEventListener("click", () => {
    const open = toc.classList.toggle("is-open");
    tocToggle.setAttribute("aria-expanded", open ? "true" : "false");
  });

  // Hash on load
  const initial = location.hash?.slice(1);
  if (initial && sectionById[initial]) {
    setTimeout(() => goTo(initial, { updateHash: false }), 50);
  } else if (sections[0]) {
    setCurrent(sections[0].id);
  }
  updateProgress();

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
  const captions = {
    core: "Ядро: только Kotlin stdlib. Не знает о plugin, mcp, perf.",
    plugin: "IntelliJ-адаптер. Зависит только от core. Правила границ — в PluginBoundaryArchTest.",
    mcp: "MCP-сервер (Ktor). Зависит только от core.",
    perf: "JMH-бенчмарки. Зависит только от core (+ JMH).",
    arch: "Тестовый модуль: читает bytecode core/mcp/perf (+ jmhJar), не тянет IntelliJ Platform.",
  };

  nodes.forEach((node) => {
    const id = node.getAttribute("data-node");
    node.addEventListener("mouseenter", () => {
      nodes.forEach((n) => n.classList.toggle("is-active", n === node));
      edges.forEach((e) => {
        const lit =
          e.getAttribute("data-from") === id || e.getAttribute("data-to") === id;
        e.classList.toggle("is-lit", lit);
      });
      if (graphCaption) graphCaption.textContent = captions[id] || "";
    });
    node.addEventListener("mouseleave", () => {
      nodes.forEach((n) => n.classList.remove("is-active"));
      edges.forEach((e) => e.classList.remove("is-lit"));
      if (graphCaption) graphCaption.textContent = "Наведите на модуль, чтобы подсветить связи.";
    });
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

    // Сначала уводим строки и комментарии в плейсхолдеры,
    // иначе подсветка `class` ломает атрибут class="tok-str".
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
