# Contributing

Pile on! The help is welcome. 

## Contributing an additional registry

It should be easy enough to follow the model of other registry implementations and sort out what else needs to happen to make your new registry work well. 

### Docs

* The `docs` folder contains the root of the tree for building documentation.
* Configuration docs are automatically generated. They will show up in the `target` directory of root project after you build.
* The `docs` module build will copy those files into the `docs/modules/ROOT/pages/includes` directory
* Create a document, `micrometer-registry-<name>` for the new module in the `docs/modules/ROOT/pages/` directory.
    It should minimally contain the following: 
    ```asciidoc
    = <Pretty Registry Name>                                   // (1)

    include::./includes/attributes.adoc[] 

    == Configuration
    include::./includes/quarkus-micrometer-export-<name>.adoc[] // (2)
    ```
    1. Each registry has a nice name for titles. ;)
    2. If you look in the `target` directory of the root project after a build, you should see three files generated for each registry: one for build configuration, one for runtime configuration, and one for both. Use the one that contains both, which should be named similarly.
* Add this document (alphabetically) to `docs/modules/ROOT/nav.adoc`
