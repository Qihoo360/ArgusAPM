<p align="center">
  <img alt="ArgusAPM Logo" src="https://github.com/Qihoo360/ArgusAPM/blob/master/doc/img/readme/ArgusAPM.jpeg" width="400"/>
</p>

[![license](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat)](https://github.com/Qihoo360/ArgusAPM/blob/master/LICENSE)
[![Release Version](https://img.shields.io/badge/release-2.0.1.1006-brightgreen.svg)]()

**360开源又一力作——ArgusAPM移动性能监控平台**


**项目背景**

ArgusAPM是360手机卫士客户端团队继RePlugin之后开源的又一个重量级开源项目。ArgusAPM是360移动端产品使用的可视化性能监控平台，为移动端APP提供性能监控与管理，可以迅速发现和定位各类APP性能和使用问题，帮助APP不断的提升用户体验。


**产品价值**

- 实时掌控应用性能
- 降低性能定位成本
- 有效提升用户体验

**监控模块**

ArgusAPM目前支持如下性能指标：

- 交互分析：分析Activity生命周期耗时，帮助提升页面打开速度，优化用户UI体验
- 网络请求分析：监控流量使用情况，发现并定位各种网络问题
- 内存分析：全面监控内存使用情况，降低内存占用
- 进程监控：针对多进程应用，统计进程启动情况，发现启动异常（耗电、存活率等）
- 文件监控：监控APP私有文件大小/变化，避免私有文件过大导致的卡顿、存储空间占用等问题
- 卡顿分析：监控并发现卡顿原因，代码堆栈精准定位问题，解决明显的卡顿体验
- ANR分析：捕获ANR异常，解决APP的“未响应”问题

**ArgusAPM特性**

- **非侵入式**

​	无需修改原有工程结构，无侵入接入，接入成本低。

- **无性能损耗**

​	ArgusAPM针对各个性能采集模块，优化了采集时机，在不影响原有性能的基础上进行性能的采集和分析。

- **监控全面**

​	目前支持UI性能、网络性能、内存、进程、文件、卡顿、ANR等各个维度的性能数据分析，后续还会继续增加新的性能维度。

- **Debug模式**

​	独有的Debug模式，支持开发和测试阶段、实时采集性能数据，实时本地分析的能力，帮助开发和测试人员在上线前解决性能问题。

- **支持插件化方案**

​	在初始化阶段进行设置，可支持插件接入，目前360手机卫士采用的就是在RePlugin插件中接入ArgusAPM，并且性能方面无影响。

- **支持多进程采集**

​	针对多进程的情况，我们做了相应的数据采集及优化方案，使ArgusAPM即适合单进程APP也适合多进程APP。

- **节省用户流量**

​	ArgusAPM使用wifi状态下上传性能数据，这样避免了频繁网络请求带来的耗电问题及用户流量的消耗。

**ArgusAPM项目结构图**



![](https://raw.githubusercontent.com/Qihoo360/ArgusAPM/master/doc/img/readme/ArgusAPM架构图.png)

整体架构分为两部分：一是左边蓝色的部分：性能采集模块，一是右边的绿色部分：Gradle Plugin模块。

下面分别针对这两部分做简单的介绍：

一. 性能采集模块

该模块总共分为五个Module，并最终生成三个aar文件，即：

argus-apm-main.aar：APM项目的核心业务模块

argus-apm-aop.aar：AOP代码的织入模块

argus-apm-okhttp.aar：采集OKHTTP网络性能

其中之所以拆分那么多的模块，是为了能够让我们可插拔式的去使用里面的功能，例如，如果我项目中没有使用OKHTTP相关的功能，那么我们就可以关闭相应的依赖。

二. Gradle Plugin模块

该模块主要具备两个作用：

1. 支持AOP编程，方便ArgusAPM能够在编译期织入一些性能采集的代码；

2. 通过Gradle插件来管理依赖库，使用户接入ArgusAPM更简单。

   ![](https://raw.githubusercontent.com/Qihoo360/ArgusAPM/master/doc/img/readme/Gradle_APM.png)

最终，我们在接入ArgusAPM的时候，只需要简单的应用插件即可，而不需要再单独的去依赖各个aar文件。

**如何使用**

如果您想快速的接入ArgusAPM，请参考[《三分钟快速接入ArgusAPM》](https://github.com/Qihoo360/ArgusAPM/wiki/%E4%B8%89%E5%88%86%E9%92%9F%E5%BF%AB%E9%80%9F%E6%8E%A5%E5%85%A5)，依照文章指引，快速接入；

如果您想了解更多的ArgusAPM的使用技巧，请参考[《详细接入教程》](https://github.com/Qihoo360/ArgusAPM/wiki/%E7%A7%BB%E5%8A%A8%E6%80%A7%E8%83%BD%E7%9B%91%E6%8E%A7-SDK-%E8%AF%A6%E7%BB%86%E9%9B%86%E6%88%90%E6%96%87%E6%A1%A3)；

如果您想参查看官方的Sample，进而了解具体的用法，请点击这里查看[《Sample》](https://github.com/Qihoo360/ArgusAPM/tree/master/argus-apm/argus-apm-sample);

如果您在接入ArgusAPM的过程中遇到问题，请点击这里阅读[《FAQ》](https://github.com/Qihoo360/ArgusAPM/wiki/FAQ)，也可加入我们官方的QQ群，进行咨询。

**使用现状**

| ![手机卫士](https://raw.githubusercontent.com/Qihoo360/ArgusAPM/master/doc/img/readme/mobilesafe.png) | ![清理大师](https://raw.githubusercontent.com/Qihoo360/ArgusAPM/master/doc/img/readme/clean.png) | ![手机助手](https://raw.githubusercontent.com/Qihoo360/ArgusAPM/master/doc/img/readme/appstore.png) | ![影视大全](https://raw.githubusercontent.com/Qihoo360/ArgusAPM/master/doc/img/readme/movie.png) | ![花椒相机](https://raw.githubusercontent.com/Qihoo360/ArgusAPM/master/doc/img/readme/camera.png) | ![360手游大厅](https://raw.githubusercontent.com/Qihoo360/ArgusAPM/master/doc/img/readme/201736165776251_meitu_1.jpg) |
| :---------------------------------: | :---------------------------------: | :----------------------------: | :----------------------------: | :---------------------: | :--------------------------------------: |
|              手机卫士               |            清理大师            |            手机助手            |            影视大全            |        花椒相机         |                 游戏大厅                 |

**未来规划**

- 支持更多的性能指标
- 提升用户体验

**官方QQ群**

<p align="left">
  <img alt="ArgusAPM Logo" src="https://raw.githubusercontent.com/Qihoo360/ArgusAPM/master/doc/img/readme/qrcode_1542008553175.jpg" width="300"/>
</p>

**360移动技术微信公众号“奇卓社”**
<p align="left">
  <img alt="ArgusAPM Logo" src="https://raw.githubusercontent.com/Qihoo360/ArgusAPM/master/doc/img/readme/qizhuoshe_344.jpg" width="300" height="300"/>
  
## License

ArgusAPM is [Apache v2.0 licensed](./LICENSE).
