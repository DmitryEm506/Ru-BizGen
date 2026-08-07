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
        <#-- Доп. разделы после версии: hub Guides + быстрый переход к coverage. -->
        <@template_cmd name="pathToRoot">
            <div class="bizgen-extras">
                <details class="bizgen-docs">
                    <summary class="bizgen-docs__toggle" title="Дополнительная документация">
                        <span class="bizgen-docs__toggle-text">Guides</span>
                        <span class="bizgen-docs__chevron" aria-hidden="true"></span>
                    </summary>
                    <div class="bizgen-docs__panel" role="menu">
                        <p class="bizgen-docs__caption">Документация</p>
                        <a class="bizgen-docs__item" href="${pathToRoot}images/archunit/index.html" role="menuitem">
                            <span class="bizgen-docs__item-name">ArchUnit</span>
                            <span class="bizgen-docs__item-hint">Исполняемая архитектура</span>
                        </a>
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
        })();
    </script>
</#macro>
