# Plugins for [get.me](https://github.com/nyrkovalex/get.me) installer

This repository includes core `get.me` plugins

## `com.github.nyrkovalex.get.me.mvn.MvnBuilder`

Builds and installs a project calling provided `goals`.

Valid local maven installation is required.

```json
{
  "class": "com.github.nyrkovalex.get.me.mvn.MvnBuilder",
  "params": {
    "goals": [
      "clean",
      "package"
    ]
  }
},
``` 

## `com.github.nyrkovalex.get.me.install.ExecJarInstaller`

Copies target `jar` to `$JARPATH` making it executable

```json
{
  "class": "com.github.nyrkovalex.get.me.install.ExecJarInstaller",
  "params": {
    "jar": "target/get.me-example.jar"
  }
}
``` 

## `com.github.nyrkovalex.get.me.install.PluginInstaller`

Installs a `get.me` plugin from the provided `jar`

```json
{
  "class": "com.github.nyrkovalex.get.me.install.PluginInstaller",
  "params": {
    "jar": "target/get.me-example.jar"
  }
}

```