<#import "source_set_selector.ftl" as source_set_selector>
<#macro display>
    <header class="navigation theme-dark" id="navigation-wrapper" role="banner">
        <@template_cmd name="pathToRoot">
            <a class="library-name--link" href="${pathToRoot}index.html" tabindex="1">
                <@template_cmd name="projectName">
                    ${projectName}
                </@template_cmd>
            </a>
        </@template_cmd>
        <button class="navigation-controls--btn navigation-controls--btn_toc ui-kit_mobile-only" id="toc-toggle"
                type="button">Toggle table of contents
        </button>
        <div class="navigation-controls--break ui-kit_mobile-only"></div>
        <div class="library-version" id="library-version">
            <#-- This can be handled by the versioning plugin -->
            <@version/>
        </div>
        <#-- Guides: автосписок презентаций из images/presentations/guides.json -->
        <@template_cmd name="pathToRoot">
            <div class="bizgen-extras">
                <details class="bizgen-docs">
                    <summary class="bizgen-docs__toggle" title="Презентации">
                        <span class="bizgen-docs__toggle-text">Guides</span>
                        <span class="bizgen-docs__chevron" aria-hidden="true"></span>
                    </summary>
                    <div class="bizgen-docs__panel" role="menu"
                         id="bizgen-guides-panel"
                         data-path-to-root="${pathToRoot}"
                         data-guides-url="${pathToRoot}images/presentations/guides.json">
                        <p class="bizgen-docs__caption">Презентации</p>
                        <div id="bizgen-guides-list"></div>
                    </div>
                </details>
                <a class="bizgen-docs__toggle bizgen-docs__toggle_link"
                   href="${pathToRoot}images/kover/index.html"
                   title="Отчёт покрытия тестов Kover">Code Coverage</a>
            </div>
        </@template_cmd>
        <div class="navigation-controls">
            <@source_set_selector.display/>
            <#if homepageLink?has_content>
                <a class="navigation-controls--btn navigation-controls--btn_homepage" id="homepage-link"
                   href="${homepageLink}"></a>
            </#if>
            <button class="navigation-controls--btn navigation-controls--btn_theme" id="theme-toggle-button"
                    type="button">Switch theme
            </button>
            <div class="navigation-controls--btn navigation-controls--btn_search" id="searchBar" role="button">Search in
                API
            </div>
        </div>
    </header>
    <script>
        (function () {
            document.addEventListener("click", function (event) {
                document.querySelectorAll("details.bizgen-docs[open]").forEach(function (details) {
                    if (!details.contains(event.target)) {
                        details.removeAttribute("open");
                    }
                });
            });

            var panel = document.getElementById("bizgen-guides-panel");
            var list = document.getElementById("bizgen-guides-list");
            if (!panel || !list) {
                return;
            }

            var pathToRoot = panel.getAttribute("data-path-to-root") || "";
            var guidesUrl = panel.getAttribute("data-guides-url");
            if (!guidesUrl) {
                return;
            }

            fetch(guidesUrl)
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error("guides.json HTTP " + response.status);
                    }
                    return response.json();
                })
                .then(function (guides) {
                    if (!Array.isArray(guides) || guides.length === 0) {
                        list.innerHTML = "<p class=\"bizgen-docs__item-hint\" style=\"padding:8px 12px\">Пока нет презентаций</p>";
                        return;
                    }
                    guides.forEach(function (guide) {
                        var link = document.createElement("a");
                        link.className = "bizgen-docs__item";
                        link.setAttribute("role", "menuitem");
                        link.href = pathToRoot + (guide.href || "");

                        var name = document.createElement("span");
                        name.className = "bizgen-docs__item-name";
                        name.textContent = guide.name || guide.slug || "Guide";
                        link.appendChild(name);

                        if (guide.hint) {
                            var hint = document.createElement("span");
                            hint.className = "bizgen-docs__item-hint";
                            hint.textContent = guide.hint;
                            link.appendChild(hint);
                        }

                        list.appendChild(link);
                    });
                })
                .catch(function () {
                    list.innerHTML = "<p class=\"bizgen-docs__item-hint\" style=\"padding:8px 12px\">Не удалось загрузить Guides</p>";
                });
        })();
    </script>
</#macro>
