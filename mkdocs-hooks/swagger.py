"""Hook do MkDocs: publica a especificação OpenAPI da API no site, em ``swagger/openapi.json``.

O arquivo é gerado pelos testes (``OpenApiDocsTest`` grava ``target/openapi.json``), então a Swagger UI
do site mostra sempre a especificação do código. Sem o arquivo, o site é gerado do mesmo jeito.
"""

import logging
from pathlib import Path

from mkdocs.structure.files import File

log = logging.getLogger("mkdocs.hooks.swagger")


def on_files(files, config):
    raiz = Path(config["config_file_path"]).parent
    if not (raiz / "target" / "openapi.json").exists():
        log.info("target/openapi.json não encontrado: rode ./mvnw test para gerar a especificação da Swagger UI")
        return files
    arquivo = File(
        "target/openapi.json",
        src_dir=str(raiz),
        dest_dir=config["site_dir"],
        use_directory_urls=config["use_directory_urls"],
    )
    # no site o arquivo fica ao lado da página da Swagger UI
    arquivo.dest_uri = "swagger/openapi.json"
    files.append(arquivo)
    return files
