<!--- @formatter:off --->
<div align="center">

![Header](https://capsule-render.vercel.app/api?type=Waving&color=timeGradient&height=200&animation=fadeIn&section=header&text=FloraCore&fontSize=100)
![Code-Size](https://img.shields.io/github/languages/code-size/FloraCore/FloraCore?style=flat-square)
![Release](https://img.shields.io/github/v/release/FloraCore/FloraCore?style=flat-square)
![Bstats](https://img.shields.io/bstats/servers/18690?style=flat-square)
![License](https://img.shields.io/github/license/FloraCore/FloraCore?style=plastic)
![Actions](https://img.shields.io/github/actions/workflow/status/FloraCore/FloraCore/gradle-publish.yml?style=flat-square)
[![JitPack](https://jitpack.io/v/FloraCore/FloraCore.svg)](https://jitpack.io/#FloraCore/FloraCore)
![Crowdin](https://badges.crowdin.net/floracore/localized.svg)

</div>

# FloraCore

FloraCore（简称FC）是一个基于Minecraft(Bukkit/BungeeCord)的开源基础插件。

该插件的名字来自两个主要元素。首先,“Flora”源于拉丁文,意为“植物”,因此可以体现插件与自然环境相关的功能。其次,“Core”意为“核心”,这代表了该插件的核心功能,即增强Minecraft服务器的自然环境,提供更多与自然元素相关的功能。

取名为FloraCore的原因是,它可以准确地表达该插件的主要功能和核心特点,同时也能与两个主要开发者（[@冬花ice](https://github.com/flowerinsnowdh)
和[@花花](https://github.com/xLikeWATCHDOG/)）的网名“花”相呼应。此外,该名字简单易记,同时也具有一定的高级感和专业感,适合作为一个基础插件的名称。

本插件支持Bukkit和BungeeCord,其中Bukkit支持1.8以上的所有版本,BungeeCord仅在最新版本提供支持,如果在使用BungeeCord版本过程遇到问题,请先尝试更新BungeeCord。

# 框架

FC的框架是基于[LuckPerms](https://luckperms.net/)
（简称LP）,进行了少数修改。保留了以下功能,用法几乎一致,可以在LP的相关[Wiki](https://luckperms.net/wiki)中进行查看用法。

- 依赖的下载和加载（[Wiki](https://luckperms.net/wiki/Extensions)）
- 配置文件的加载及其用法（[GitHub](https://github.com/LuckPerms/LuckPerms/tree/master/common/src/main/java/me/lucko/luckperms/common/config)）
- 数据库的加载及其用法（[GitHub](https://github.com/LuckPerms/LuckPerms/tree/master/common/src/main/java/me/lucko/luckperms/common/storage)）
- 拓展的加载及其用法（[GitHub](https://github.com/LuckPerms/LuckPerms/tree/master/common/src/main/java/me/lucko/luckperms/common/extension)）

为了优雅地实现反射,FC采用了[MzLib](https://github.com/BugCleanser/MzLib_old)。

# [MzLibAgent](./libs/MzLibAgent.jar)

在Docker容器等地方运行可能会报错并提示你安装MzLibAgent。

MzLibAgent不是一个Bukkit插件,请按照以下步骤安装：

- 下载MzLibAgent.jar并移动到你的服务端文件夹内（与核心同一目录）。
- 在服务端的启动参数内添加JVM参数`-javaagent:MzLibAgent.jar`（需添加在参数`-jar`之前）。
- 安装完成后重启服务端即可。

# Wiki

本插件的Wiki见[Wiki](https://github.com/FloraCore/FloraCore/wiki)。

# 开发

- [JavaDoc](https://floracore.github.io/index.html)
- [开发标准及规范](./documentation/STANDARD.md)（所有开发者必看）。
- [API](https://github.com/FloraCore/FloraCore/wiki/API)

# 访问人数

![](https://count.getloli.com/get/@FloraCore?theme=rule34)

# 支持我们

首先，感谢您考虑提供帮助，我们非常感谢!

# 感谢

[<img src="https://user-images.githubusercontent.com/21148213/121807008-8ffc6700-cc52-11eb-96a7-2f6f260f8fda.png" alt="" width="150">](https://www.jetbrains.com)

[JetBrains](https://www.jetbrains.com/), creators of the IntelliJ IDEA,
supports FloraCore with one of their [Open Source Licenses](https://jb.gg/OpenSourceSupport).
IntelliJ IDEA is the recommended IDE for working with FloraCore,
and most of the FloraCore team uses it.
