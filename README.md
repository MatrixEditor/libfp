# LibFP - Library Fingerprinting Framework

[![java](https://img.shields.io:/static/v1?logo=oracle&label=Java&message=17.0.4&color=lightblue)](https://www.python.org/downloads/)
![Codestyle](https://img.shields.io:/static/v1?label=Codestyle&message=IDEA&color=black)
![License](https://img.shields.io:/static/v1?label=License&message=MIT&color=blue)
![Status](https://img.shields.io:/static/v1?label=Status&message=WIP&color=teal)

Recent years have witnessed rapid development in the detection of third-party libraries in Android applications.
However, each approach typically comes with its own implementation, limiting reusability for further development. To
address this, we present **LibFP** - a versatile third-party Library Fingerprinting Framework written in Java.

LibFP incorporates various existing approaches to third-party library detection and provides a highly configurable
profile generator.

## Introduction

Before delving into the technical details, let's discuss the challenges of detecting single third-party libraries (TPL)
within Android apps or Java bundles. The framework's scope encompasses the creation of profiles for libraries and apps,
benchmarking generated profiles, and detecting TPLs in Android apps.

The documentation will introduce state-of-the-art obfuscation techniques and their impact on TPL detection within
application bundles. Basics of Android apps and Java programs, along with their bytecode representation, will also be
presented. The core reference to the framework's architecture, including profile creation, similarity calculation,
dataset preparation, and ground-truth verification, will follow. The document concludes with an exploration of common
approaches to third-party library detection in Android applications and suggestions for implementation using this
framework.

## Installation and Build

This project uses Gradle as its build system. Follow these steps to build and install the LibFP framework:

1. Clone the repository:
   ```bash
   git clone https://github.com/example/libfp.git
   ```

2. Navigate to the project directory:
   ```bash
   cd libfp
   ```

3. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```

4. After a successful build, you can find each compiled JAR file in their corresponding `build/libs` directory.

## Modules

| Module          | Description                                                                                    |
|-----------------|------------------------------------------------------------------------------------------------|
| libfp-core      | Core module providing fundamental components for profile generation and third-party detection. |
| libfp-cha       | Module implementing the Class Hierarchy Analysis (CHA) strategy for creating library profiles. |
| libfp-benchmark | Module containing benchmarking functionalities, including test suites and result analysis.     |
| libfp-tlsh      | Module integrating the Trend Micro Locality-Sensitive Hashing (TLSH) algorithm for similarity. |
| libfp-impl      | Implementation module for additional features, extensions, and custom profile generation.      |

## Example Code Snippet

Here's a simple example demonstrating how to use the LibFP framework to perform benchmarking:

```java
// Import necessary classes and interfaces
// ...
// 1. retrieve the dataset
DataSet dataset= //...;

// 2. Create the TestSuite with all relevant factories.        
ICHAProfileContext context= //...;
CHATestSuite suite = new TestSuiteBuilder<>(CHATestSuite::new)
        .setDataSet(dataSet)
        .setContext(context)
        .createTestSuite();

// 3. Specify the app name(s) to test.
String appName = "003-nodomain.freeyourgadget.gadgetbridge";

// 3.5. (optional) Prepare the profiles.
suite.prepareApp(appName);
suite.prepareLibraries();

// 4. Benchmark a strategy by using a custom threshold configuration
IThresholdConfig config = new SimpleThresholdConfig()
        .set(CHAProfile.class, 0.8)   // profile similarity threshold
        .set(ClassProfile.class, 0.6) // class similarity threshold
        .set(TestResult.class, 0.68); // result threshold
        
BenchmarkResult result = suite.benchmark(appName, config);
// 5. Retrieve the accuracy of the chosen strategy by providing the
// appType (a default type is "").
Whitelist whitelist = dataSet
        .groundTruth()
        .getVersionWhitelist(appName);

TestAccuracy accuracy = result.getTestAccuracy(
        BenchmarkResult.defaultAppType, config, whitelist);
// ...
```

> Additional examples are placed in [libfp-impl:example/](/libfp-impl/src/test/java/io/github/example).

## TODOs

- [ ] Implement additional similarity strategies.
- [ ] Enhance documentation for profile generation.
- [ ] Explore support for more obfuscation techniques.
- [ ] Extend benchmarking capabilities with advanced metrics.
- [ ] Provide examples for custom profile strategies.
- [ ] Collaborate on expanding the dataset for diverse testing scenarios.
- [ ] Provide a way to generate a valid dataset (and links to existing ones)

## License

Distributed under the MIT license. See MIT.txt for more information.