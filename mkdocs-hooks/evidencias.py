"""Hook do MkDocs: publica os prints da pasta evidencias/ (na raiz do repositório) no site.

As imagens continuam num lugar só; as páginas em docs/ apontam para elas como ``evidencias/<arquivo>.png``.
"""

from pathlib import Path

from mkdocs.structure.files import File


def on_files(files, config):
    raiz = Path(config["config_file_path"]).parent
    for imagem in sorted((raiz / "evidencias").glob("*.png")):
        files.append(
            File(
                f"evidencias/{imagem.name}",
                src_dir=str(raiz),
                dest_dir=config["site_dir"],
                use_directory_urls=config["use_directory_urls"],
            )
        )
    return files
