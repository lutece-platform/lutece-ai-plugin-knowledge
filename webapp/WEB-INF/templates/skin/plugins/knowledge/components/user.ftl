<header class="navbar navbar-expand-lg bg-glass w-100 position-absolute w-100 z-1 py-3 px-3">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">
            <span class="font-sarala fw-bold">LuteceAI</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link disabled" aria-current="page" href="Portal.jsp">Accueil</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="Portal.jsp">Assistants</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link disabled" aria-current="page" href="Portal.jsp">Applications</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link disabled" aria-current="page" href="Portal.jsp">Espaces</a>
                </li>
            </ul>
            <button class="btn btn-light btn-circle rounded-5 me-3"
                type="button" id="themeSwitchBtn"><i class="ti ti-moon-stars fs-5"></i></button>
            <div class="dropdown">
                <button class="btn btn-light dropdown-toggle rounded-5 py-2" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-expanded="false">
                    ${mylutece_user.lastName} ${mylutece_user.firstName}
                </button>
                <ul class="dropdown-menu shadow-lg border-0 p-0 mt-2 rounded-5" aria-labelledby="dropdownMenuButton">
                    <li class="rounded-5"><a class="dropdown-item rounded-5 py-2" href="plugins/mylutece/DoMyLuteceLogout.jsp">Se d&eacute;connecter</a></li>
                </ul>
            </div>
        </div>
    </div>
</header>