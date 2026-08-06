# cos

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-21-orange)](https://github.com/easy-4-java/cos) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](https://www.apache.org/licenses/LICENSE-2.0.txt)

> 经典的 O'Reilly COS（com.oreilly.servlet）工具库——文件上传、HTTP 消息与
> Servlet 辅助类，适配 Jakarta EE Servlet API 的维护分支。

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 功能与状态](#2-功能与状态)
- [3. 环境要求与兼容性](#3-环境要求与兼容性)
- [4. 架构与模块](#4-架构与模块)
- [5. 安装](#5-安装)
- [6. 快速开始](#6-快速开始)
- [7. 配置](#7-配置)
- [8. 核心用法 / API](#8-核心用法--api)
- [9. 测试与构建](#9-测试与构建)
- [10. 版本与分支](#10-版本与分支)
- [11. 贡献与许可](#11-贡献与许可)

## 1. 项目概述

`cos` 是经典 O'Reilly COS 库（`com.oreilly.servlet`）的维护分支，已适配 Jakarta
EE Servlet API。提供久经生产验证的服务端 Web 工具：

- **文件上传** — `MultipartRequest`（"推"模型：解析 `multipart/form-data` 请求时
  直接将文件保存到磁盘）与 `MultipartParser`（"拉"模型），支持可插拔
  `FileRenamePolicy`、可配置大小上限与编码。
- **HTTP 消息** — `HttpMessage` / `HttpsMessage` 发送带请求头与参数的 GET / POST
  请求，`MailMessage` 发送邮件。
- **Servlet 辅助** — `CookieParser`、`ParameterParser`、`ServletUtils`、
  `HttpUtils`、`Base64Encoder` / `Base64Decoder`、`LocaleNegotiator`、
  `CacheHttpServlet` / `DaemonHttpServlet` / `RemoteHttpServlet` 基类。

它不是：

- 框架或 Servlet 容器——它运行在任何 Servlet 容器内。
- 积极扩张的 API 面——代码刻意保持经典、稳定的 COS 接口。

典型场景：

| 场景 | 使用内容 |
| :--- | :--- |
| 在 Servlet 中处理 `multipart/form-data` 上传 | `MultipartRequest`（推）/ `MultipartParser`（拉） |
| 重命名 / 迁移上传文件 | `FileRenamePolicy`、`DefaultFileRenamePolicy` |
| 服务端发送 HTTP GET / POST | `HttpMessage`、`HttpsMessage` |
| 安全读取 Cookie / 参数 | `CookieParser`、`ParameterParser` |
| 发送普通邮件 | `MailMessage` |

## 2. 功能与状态

| 能力 | 状态 | 说明 |
| :--- | :--- | :--- |
| `MultipartRequest`（推式上传） | 稳定 | 构造时保存文件到磁盘；`getFile`、`getParameter`、`getContentType`、`getFilesystemName`、`getOriginalFileName` |
| `MultipartParser`（拉式上传） | 稳定 | 流式解析 part；`FilePart`、`ParamPart`、`Part` |
| 文件重命名策略 | 稳定 | `FileRenamePolicy`、`DefaultFileRenamePolicy`、`ExceededSizeException` |
| HTTP 客户端辅助 | 稳定 | `HttpMessage`、`HttpsMessage`（`sendGetMessage`、`sendPostMessage`） |
| 邮件辅助 | 稳定 | `MailMessage` |
| Servlet 工具 | 稳定 | `ServletUtils`、`HttpUtils`、`CookieParser`、`ParameterParser`、`Base64Encoder` / `Base64Decoder`、`LocaleNegotiator` |
| Servlet 基类 | 稳定 | `CacheHttpServlet`、`DaemonHttpServlet`、`RemoteHttpServlet`、`MultipartFilter`、`MultipartWrapper` |

## 3. 环境要求与兼容性

| 要求 | 版本 / 说明 |
| :--- | :--- |
| JDK | 21+（见下方版本线矩阵） |
| Maven | 3.0+（enforcer 强制） |
| Servlet API | Jakarta EE 9+（`jakarta.servlet-api`，`provided` 作用域——由容器提供） |

版本线（本项目沿用上游 COS 的 `6.0.x.*` 版本线）：

| 分支 | JDK | 版本 |
| :--- | :--- | :--- |
| `feature/1.0.x` | 11 | `6.0.x.*` |
| `feature/2.0.x` | 17 | `6.0.x.*` |
| `feature/3.0.x` | 21 | `6.0.x.*` |

> 与其他 easy4j 组件不同，`cos` 在每个分支上都沿用与上游一致的 `6.0.x.*`
> 版本线（已在分支 pom 中核实）；各分支仅 JDK 基线不同。

## 4. 架构与模块

```text
+---------------------+   +--------------------------------------+
| HTTP request        |   | cos (com.oreilly.servlet)           |
| (multipart/form-    |-->|  upload : MultipartRequest,         |
|  data, query params)|   |           MultipartParser, FilePart |
|                     |   |  http   : HttpMessage, HttpsMessage  |
| Servlet container   |   |  servlet: CookieParser, ServletUtils,|
| (jakarta.servlet)   |-->|           Base64Encoder/Decoder,     |
|                     |   |           CacheHttpServlet ...       |
+---------------------+   +-------------------+------------------+
                                              |
                                              v
                     +-------------------------------------------+
                     | Files saved / response to client          |
                     +-------------------------------------------+
```

单模块 Maven 工程（`packaging: jar`，含最小 `src/main/webapp` 描述文件），
无子模块。

| 构件 | 职责 |
| :--- | :--- |
| `io.github.easy4j:cos` | 文件上传、HTTP 消息与 Servlet 工具类 |

关键包：

| 包 | 内容 |
| :--- | :--- |
| `com.oreilly.servlet` | `MultipartRequest`、`HttpMessage`、`HttpsMessage`、`MailMessage`、`ServletUtils`、`HttpUtils`、`CookieParser`、`Base64Encoder` / `Base64Decoder`、Servlet 基类 |
| `com.oreilly.servlet.multipart` | `MultipartParser`、`FilePart`、`ParamPart`、`Part`、`FileRenamePolicy`、`DefaultFileRenamePolicy`、`ExceededSizeException` |

## 5. 安装

项目**尚未发布到 Maven Central**。快照 / 发布版本通过阿里云 Maven 仓库与 GitHub
Releases 分发。

Maven：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>cos</artifactId>
    <version>6.0.x.20241003.RELEASE</version>
</dependency>
```

Gradle：

```groovy
implementation 'io.github.easy4j:cos:6.0.x.20241003.RELEASE'
```

## 6. 快速开始

在 Servlet 中处理文件上传：

```java
import com.oreilly.servlet.MultipartRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;

public class UploadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // 解析 multipart/form-data，上传文件保存到 /tmp/upload
        MultipartRequest multi = new MultipartRequest(request, "/tmp/upload");

        String name = multi.getParameter("name");   // 普通表单字段
        File uploaded = multi.getFile("file");      // 上传的文件（可能为 null）
        String fileName = multi.getFilesystemName("file");
        String originalName = multi.getOriginalFileName("file");

        response.getWriter().write("saved=" + fileName + " (original: " + originalName + ")");
    }
}
```

预期结果：上传文件被写入 `/tmp/upload`（发生重名冲突时按默认策略改名），
Servlet 返回保存后的文件名。

## 7. 配置

本库没有配置文件或属性前缀。行为通过构造器按次控制：

| 构造器 / 方法 | 说明 |
| :--- | :--- |
| `MultipartRequest(request, saveDirectory)` | 默认：最大请求体 1 MB、默认编码 |
| `MultipartRequest(request, saveDirectory, maxPostSize)` | 调高 1 MB 默认上限 |
| `MultipartRequest(request, saveDirectory, maxPostSize, encoding)` | 支持国际化文件名 |
| `MultipartRequest(request, saveDirectory, maxPostSize, encoding, FileRenamePolicy)` | 自定义重命名 / 迁移策略 |
| `MultipartParser(request, maxPostSize)` | 拉模型：遍历 `Part`（`FilePart`、`ParamPart`） |

## 8. 核心用法 / API

### 8.1 使用 `MultipartParser` 的拉模型

需要完全控制（如把文件写入数据库）时使用拉模型：

```java
import com.oreilly.servlet.multipart.*;

MultipartParser parser = new MultipartParser(request, 10 * 1024 * 1024);
Part part;
while ((part = parser.readNextPart()) != null) {
    if (part.isParam()) {
        ParamPart param = (ParamPart) part;
        System.out.println(param.getName() + " = " + param.getStringValue());
    } else if (part.isFile()) {
        FilePart filePart = (FilePart) part;
        filePart.writeTo(new java.io.FileOutputStream("/tmp/" + filePart.getFileName()));
    }
}
```

### 8.2 使用 `HttpMessage` 发送 HTTP 消息

```java
import com.oreilly.servlet.HttpMessage;
import java.net.URL;
import java.io.InputStream;

HttpMessage msg = new HttpMessage(new URL("https://example.com/api"));
msg.setHeader("Authorization", "Bearer token");
InputStream in = msg.sendPostMessage();  // 或 sendGetMessage()
```

## 9. 测试与构建

```bash
mvn clean verify
```

- 构建配置了 JaCoCo Maven 插件（报告 + 绑定在 `verify` 阶段的 `check` 目标，
  行覆盖率规则为 90%；`haltOnFailure=false`）。
- **假设**：1.0.x 分支当前 `src/test` 下未提交测试源码；覆盖率门禁仅在存在测试时生效。
- 本 worktree 的 `.github/` 下无 CI 工作流文件。
- 注意：本 worktree 没有 Maven Wrapper，请使用本机 Maven 3.x。

## 10. 版本与分支

| 分支 | JDK | 版本 | 说明 |
| :--- | :--- | :--- | :--- |
| `feature/1.0.x` | 11 | `6.0.x.*` | 当前分支，维护中 |
| `feature/2.0.x` | 17 | `6.0.x.*` | JDK 17 版本线 |
| `feature/3.0.x` | 21 | `6.0.x.*` | JDK 21 版本线 |

该组件沿用上游 COS 版本线（`6.0.x.*`），而不是其他 easy4j 组件的
`1.0.x.*` / `2.0.x.*` / `3.0.x.*` 命名；各分支仅 JDK 基线不同。发布物通过阿里云
Maven 仓库与 GitHub Releases 分发；项目尚未发布到 Maven Central。

## 11. 贡献与许可

欢迎通过 GitHub Issue 或 Pull Request 参与贡献。

本项目基于 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt) 许可。
