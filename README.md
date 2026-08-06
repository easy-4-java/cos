# cos

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-11-orange)](https://github.com/easy-4-java/cos) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](https://www.apache.org/licenses/LICENSE-2.0.txt)

> The classic O'Reilly COS (com.oreilly.servlet) utility library — file upload,
> HTTP messaging and servlet helpers — maintained for the Jakarta EE Servlet API.

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

`cos` is a maintained fork of the classic O'Reilly COS library
(`com.oreilly.servlet`), modernized for the Jakarta EE Servlet API. It provides
battle-tested server-side web utilities:

- **File upload** — `MultipartRequest` ("push" model: files are saved to disk while
  parsing the `multipart/form-data` request) and `MultipartParser` ("pull" model),
  with pluggable `FileRenamePolicy`, configurable size limits and encodings.
- **HTTP messaging** — `HttpMessage` / `HttpsMessage` for GET / POST requests with
  headers and properties, `MailMessage` for sending email.
- **Servlet helpers** — `CookieParser`, `ParameterParser`, `ServletUtils`,
  `HttpUtils`, `Base64Encoder` / `Base64Decoder`, `LocaleNegotiator`,
  `CacheHttpServlet` / `DaemonHttpServlet` / `RemoteHttpServlet` base classes.

What it is **not**:

- Not a framework or a servlet container — it runs inside any Servlet container.
- Not an actively extended API surface — the code base deliberately keeps the
  classic, stable COS interfaces.

Typical scenarios:

| Scenario | What you use |
| :--- | :--- |
| Handle `multipart/form-data` upload in a servlet | `MultipartRequest` (push) / `MultipartParser` (pull) |
| Rename / relocate uploaded files | `FileRenamePolicy`, `DefaultFileRenamePolicy` |
| Send HTTP GET / POST from the server | `HttpMessage`, `HttpsMessage` |
| Read cookies / parameters safely | `CookieParser`, `ParameterParser` |
| Send plain email | `MailMessage` |

## 2. Features & Status

| Capability | Status | Notes |
| :--- | :--- | :--- |
| `MultipartRequest` (push upload) | Stable | Saves files to disk during construction; `getFile`, `getParameter`, `getContentType`, `getFilesystemName`, `getOriginalFileName` |
| `MultipartParser` (pull upload) | Stable | Streaming part parsing; `FilePart`, `ParamPart`, `Part` |
| File rename policies | Stable | `FileRenamePolicy`, `DefaultFileRenamePolicy`, `ExceededSizeException` |
| HTTP client helpers | Stable | `HttpMessage`, `HttpsMessage` (`sendGetMessage`, `sendPostMessage`) |
| Email helper | Stable | `MailMessage` |
| Servlet utilities | Stable | `ServletUtils`, `HttpUtils`, `CookieParser`, `ParameterParser`, `Base64Encoder` / `Base64Decoder`, `LocaleNegotiator` |
| Base servlet classes | Stable | `CacheHttpServlet`, `DaemonHttpServlet`, `RemoteHttpServlet`, `MultipartFilter`, `MultipartWrapper` |

## 3. Requirements & Compatibility

| Requirement | Version / Notes |
| :--- | :--- |
| JDK | 11+ (see version matrix below) |
| Maven | 3.0+ (enforced) |
| Servlet API | Jakarta EE 9+ (`jakarta.servlet-api`, `provided` scope — supplied by your container) |

Version lines (this project follows the upstream COS version line `6.0.x.*`):

| Branch | JDK | Version |
| :--- | :--- | :--- |
| `feature/1.0.x` | 11 | `6.0.x.*` |
| `feature/2.0.x` | 17 | `6.0.x.*` |
| `feature/3.0.x` | 21 | `6.0.x.*` |

> Unlike the other easy4j components, `cos` keeps the upstream-aligned `6.0.x.*`
> version line on every branch (verified in the branch poms); only the JDK baseline
> differs per branch.

## 4. Architecture & Modules

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

Single-module Maven project (`packaging: jar`, plus a minimal `src/main/webapp`
web descriptor). No child modules.

| Artifact | Responsibility |
| :--- | :--- |
| `io.github.easy4j:cos` | File upload, HTTP messaging and servlet utility classes |

Key packages:

| Package | Content |
| :--- | :--- |
| `com.oreilly.servlet` | `MultipartRequest`, `HttpMessage`, `HttpsMessage`, `MailMessage`, `ServletUtils`, `HttpUtils`, `CookieParser`, `Base64Encoder` / `Base64Decoder`, servlet base classes |
| `com.oreilly.servlet.multipart` | `MultipartParser`, `FilePart`, `ParamPart`, `Part`, `FileRenamePolicy`, `DefaultFileRenamePolicy`, `ExceededSizeException` |

## 5. Installation

The project is **not yet published to Maven Central**. Snapshots/releases are
distributed through the Aliyun Maven repository and GitHub Releases.

Maven:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>cos</artifactId>
    <version>6.0.x.20260806.RELEASE</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.easy4j:cos:6.0.x.20260806.RELEASE'
```

## 6. Quick Start

Handle a file upload in a servlet:

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

        // parse multipart/form-data, saving uploaded files to /tmp/upload
        MultipartRequest multi = new MultipartRequest(request, "/tmp/upload");

        String name = multi.getParameter("name");   // ordinary form field
        File uploaded = multi.getFile("file");      // uploaded file (or null)
        String fileName = multi.getFilesystemName("file");
        String originalName = multi.getOriginalFileName("file");

        response.getWriter().write("saved=" + fileName + " (original: " + originalName + ")");
    }
}
```

Expected result: the uploaded file is written under `/tmp/upload` (renamed by the
default policy if a collision occurs), and the servlet reports the saved file name.

## 7. Configuration

The library has no configuration file or property prefix. Behaviour is controlled
per call through the constructors:

| Constructor / method | Description |
| :--- | :--- |
| `MultipartRequest(request, saveDirectory)` | Default: max post size 1 MB, default encoding |
| `MultipartRequest(request, saveDirectory, maxPostSize)` | Raise the 1 MB default limit |
| `MultipartRequest(request, saveDirectory, maxPostSize, encoding)` | Internationalized file names |
| `MultipartRequest(request, saveDirectory, maxPostSize, encoding, FileRenamePolicy)` | Custom rename / relocation policy |
| `MultipartParser(request, maxPostSize)` | Pull model: iterate `Part` objects (`FilePart`, `ParamPart`) |

## 8. Core Usage / API

### 8.1 Pull model with `MultipartParser`

Use the pull model when you want full control (e.g. write files to a database):

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

### 8.2 HTTP messaging with `HttpMessage`

```java
import com.oreilly.servlet.HttpMessage;
import java.net.URL;
import java.io.InputStream;

HttpMessage msg = new HttpMessage(new URL("https://example.com/api"));
msg.setHeader("Authorization", "Bearer token");
InputStream in = msg.sendPostMessage();  // or sendGetMessage()
```

## 9. Testing & Build

```bash
mvn clean verify
```

- The build is configured with the JaCoCo Maven plugin (report + `check` goal with a
  90% line-coverage rule bound to the `verify` phase; `haltOnFailure=false`).
- **Assumption**: the 1.0.x branch currently checks in no test sources under
  `src/test`; coverage thresholds are therefore enforced only when tests exist.
- No CI workflow files are present under `.github/` in this worktree.
- Note: this worktree has no Maven Wrapper; use a local Maven 3.x installation.

## 10. Versioning & Branches

| Branch | JDK | Version | Notes |
| :--- | :--- | :--- | :--- |
| `feature/1.0.x` | 11 | `6.0.x.*` | Current branch, maintained |
| `feature/2.0.x` | 17 | `6.0.x.*` | JDK 17 line |
| `feature/3.0.x` | 21 | `6.0.x.*` | JDK 21 line |

This component keeps the upstream COS version line (`6.0.x.*`) instead of the
`1.0.x.*` / `2.0.x.*` / `3.0.x.*` scheme used by the other easy4j components; the
JDK baseline is the only thing that changes per branch. Releases are published to
the Aliyun Maven repository and as GitHub Releases; the project is not yet
published to Maven Central.

## 11. Contributing & License

Contributions are welcome — please open issues or pull requests on GitHub.

Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).
