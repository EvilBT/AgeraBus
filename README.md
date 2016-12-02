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
    compile 'com.github.EvilBT:AgeraBus:v1.0.3'
    compile 'com.google.android.agera:agera:1.2.0'
}
```
**Step 3.** See this page 

[基于Agera的EventBus实现库](http://zpayh.xyz/2016/11/08/%E5%9F%BA%E4%BA%8EAgera%E7%9A%84EventBus%E5%AE%9E%E7%8E%B0%E5%BA%93/)

Add multi-process support

[基于Agera的EventBus实现库2-添加多进程支持](http://www.jianshu.com/p/b3b0b30f7e82)
## Learn about Agera
- [Agera Explained](https://github.com/google/agera/wiki)
- [Chinese](https://github.com/captain-miao/AndroidAgeraTutorial/wiki)

## License

> Copyright (C) 2016 zpayh.
     http://zpayh.xyz
>
>  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
>
>     http://www.apache.org/licenses/LICENSE-2.0
>
>  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.