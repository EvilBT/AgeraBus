# AgeraBus
[![](https://jitpack.io/v/EvilBT/AgeraBus.svg)](https://jitpack.io/#EvilBT/AgeraBus)

It is an Android event bus based on Google [Agera](https://github.com/google/agera).

## Getting started
**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```
**Step 2.** Add the dependency
```
dependencies {
    compile 'com.github.EvilBT:AgeraBus:v1.0.1'
    compile 'com.google.android.agera:agera:1.2.0-beta3'
}
```

## Learn about Agera
- [Agera Explained](https://github.com/google/agera/wiki)
- [Chinese](https://github.com/captain-miao/AndroidAgeraTutorial/wiki)