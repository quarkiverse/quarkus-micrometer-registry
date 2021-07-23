# Quarkus Micrometer Registry extensions
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-3-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

This repository hosts extensions that support additional Micrometer registry implementations:

* Azure Monitor
* Datadog
* Graphite
* JMX
* New Relic
* SignalFx
* Stackdriver
* StatsD

See the [Micrometer documentation](https://micrometer.io/) for details about Micrometer itself. Using Micrometer with Quarkus is explained in the [Quarkus Micrometer Guide](https://quarkus.io/guides/micrometer). [Quarkus Micrometer Extension documentation](https://quarkiverse.github.io/quarkiverse-docs/quarkus-micrometer-registry/dev/index.html) is generated from this project, and specifically covers how to configure Quarkus micrometer registry extensions.

Feel free to ask questions in either the [Quarkus Zulip](https://quarkusio.zulipchat.com/) chatroom, or the #quarkus channel in the [Micrometer Slack](https://join.slack.com/t/micrometer-metrics/shared_invite/zt-ewo3kcs0-Ji3aOAqTxnjYPEFBBI5HqQ) workspace.

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
    <td align="center"><a href="https://github.com/luneo7"><img src="https://avatars.githubusercontent.com/u/8834774?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Lucas Rogerio Caetano Ferreira</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-micrometer-registry/commits?author=luneo7" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions are welcome!
