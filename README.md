# Quarkus Micrometer Registry extensions
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-2-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

This repository hosts extensions that support additional Micrometer registry implementations:

* Azure Monitor
* Datadog
* JMX
* New Relic
* SignalFx
* Stackdriver
* StatsD

## Example applications

Example applications can be found inside the integration-test folder.

## WARNING

This project is still in its early stages. The goal is for all of these additional registries to work in native mode,
and that will happen over time.

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://www.ebullient.dev"><img src="https://avatars1.githubusercontent.com/u/808713?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Erin Schnabel</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-micrometer-registry/commits?author=ebullient" title="Code">💻</a> <a href="#maintenance-ebullient" title="Maintenance">🚧</a></td>
    <td align="center"><a href="http://cemnura.com"><img src="https://avatars.githubusercontent.com/u/24714913?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Cem Nura</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-micrometer-registry/commits?author=cemnura" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

Feel free to ask questions in either the [Quarkus Zulip](https://quarkusio.zulipchat.com/) chatroom, or the #quarkus channel in the [Micrometer Slack](https://join.slack.com/t/micrometer-metrics/shared_invite/zt-ewo3kcs0-Ji3aOAqTxnjYPEFBBI5HqQ) workspace.
## Contributing an additional registry

Pile on! The help is welcome. It should be easy enough to follow the model of other registry implementations and sort out what else needs to happen to make your new registry work well. 

### Docs

* The `docs` folder contains the root of the tree for building documentation.
* Configuration docs are automatically generated. They will show up in the `target` directory of root project after you build.
* The `docs` module build will copy those files into the `docs/modules/ROOT/pages/includes` directory
* Create a document, `micrometer-registry-<name>` for the new module in the `docs/modules/ROOT/pages/` directory.
    It should minimally contain the following: 
    ```asciidoc
    = Quarkus - Micrometer Metrics: <Pretty Name>               // (1)

    include::./includes/attributes.adoc[] 

    == Configuration
    include::./includes/quarkus-micrometer-export-<name>.adoc[] // (2)
    ```
    1. Each registry has a nice name for titles. ;)
    2. If you look in the `target` directory of the root project after a build, you should see three files generated for each registry: one for build configuration, one for runtime configuration, and one for both. Use the one that contains both, which should be named similarly.
* Add this document (alphabetically) to `docs/modules/ROOT/nav.adoc`
