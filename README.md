# Android Module Template

![Build](https://img.shields.io/github/actions/workflow/status/x-syaifullah-x/android-modules/linux_os.yml?master&style=flat-square)


[//]: # (![Build]&#40;https://shields.io/github/workflow/status/x-syaifullah-x/android-modules/build__publish_maven_local/master?event=push&logo=github&label=Build&#41;)
[//]: # ([![x-syaifullah-x]&#40;https://circleci.com/gh/x-syaifullah-x/android-module/tree/master.svg?style=svg&#41;]&#40;https://circleci.com/gh/x-syaifullah-x/android-module/tree/master&#41;)

### Publish all module to maven local
```bash
./gradlew publishToMavenLocal
```

### Publish selected module to maven local
```
./gradlew :module_name:publishToMavenLocal
```

```gradle
repositories {
    mavenLocal()
}

dependencies {
    implementation "id.xxx.module:module_name:$vModule"
}
```
---
