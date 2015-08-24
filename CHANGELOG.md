To see everything that has changed between version vA.B.C and vX.Y.Z, visit:
https://github.com/j2objc-contrib/j2objc-gradle/compare/vA.B.C...vX.Y.Z

# Prelease Alphas

## v0.4.2-alpha
Functionality:
* Translation-only mode (skips building Objective-C libraries) #349
* Support for Windows/Linux (in translation-only mode) #349
* Cycle finding moved from assembly phase to test phase #338
* Automatic linking with related Xcode projects, like tests and WatchKit apps #353
* Per-environment configuration of iOS architectures to build #358
* Environment-specific config can be provided via environment variables in addition to local.properties #361 

Code quality:
* Travis Continuous Integration #365
* Additional test coverage (various)
* Documentation fixes (various)
* Updating package prefixes will now correctly cause retranslation/recompile

## 0.4.1-alpha
(Ignore - use v0.4.2 instead).

## v0.4.0-alpha
Functionality:
- Resources copied for unit tests and Xcode build
- Xcode debug and release targets now load distinct generated libraries
- j2objcConfig syntax standardized for translateClasspaths and translateSourcepaths
- Helpful error message upon failure with full command line, stdout and sterr

Code Quality:
- @CompileStatic for plugin build type checking
- Expanded unit test coverage now comprises 81 tests
- Numerous bug fixes

## v0.3.0-alpha
Functionality:
- Args groovy style syntax for config closure
- Multiple projects with dependencies
- Incremental compile using Gradle's native clang compiler
- Fix Plugins {...} syntax

Code Quality:
- Added unit test framework

## v0.2.2-alpha
- Requires buildscript syntax as a workaround to the plugins {} syntax not working
- Lots of fixes and improvements through the system

Upgrading in-place from v0.1.0-alpha is not supported: please read README.md and
J2objcPluginExtension.groovy for instructions on using and configuring the new version.

## v0.1.0-alpha
Initial working version.
There will be significant reworking of this before a beta and 1.0 release