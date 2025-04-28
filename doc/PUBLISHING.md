# Publishing the Watermark Library to Maven Repository

This document explains how to publish the Watermark library to Maven repositories from GitHub.

## Prerequisites

1. GitHub account
2. GitHub repository for the Watermark library
3. Sonatype OSSRH (OSS Repository Hosting) account - for publishing to Maven Central
4. GPG key pair for signing artifacts

## Publishing Options

There are several options for publishing your Android library:

1. **Maven Central** - The official Maven repository
2. **JitPack** - A simple way to publish from GitHub
3. **GitHub Packages** - GitHub's package registry

Below are instructions for each option:

## Option 1: Publishing to Maven Central

### Step 1: Set up Sonatype OSSRH Account

1. Create a [Sonatype JIRA account](https://issues.sonatype.org/secure/Signup!default.jspa)
2. Create a new project ticket requesting a new repository
3. Provide your group ID (e.g., `com.musarayy`)
4. Verify ownership of the domain (if your group ID is based on a domain you own)

### Step 2: Set up GPG

1. Install GPG:
   ```bash
   # macOS
   brew install gnupg
   
   # Ubuntu
   sudo apt-get install gnupg
   ```

2. Generate a key pair:
   ```bash
   gpg --gen-key
   ```

3. List your keys:
   ```bash
   gpg --list-keys
   ```

4. Distribute your public key:
   ```bash
   gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
   ```

### Step 3: Configure Gradle for Publishing

1. Add the Maven Publish and Signing plugins to your library's build.gradle:

```gradle
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'maven-publish'
    id 'signing'
}

// ... your existing configuration ...

afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release

                groupId = 'com.musarayy'
                artifactId = 'watermark'
                version = '1.0.0'

                pom {
                    name = 'Watermark'
                    description = 'A simple and customizable watermark library for Android applications'
                    url = 'https://github.com/yourusername/watermark'
                    
                    licenses {
                        license {
                            name = 'MIT License'
                            url = 'https://opensource.org/licenses/MIT'
                        }
                    }
                    
                    developers {
                        developer {
                            id = 'yourusername'
                            name = 'Your Name'
                            email = 'your.email@example.com'
                        }
                    }
                    
                    scm {
                        connection = 'scm:git:github.com/yourusername/watermark.git'
                        developerConnection = 'scm:git:ssh://github.com/yourusername/watermark.git'
                        url = 'https://github.com/yourusername/watermark/tree/main'
                    }
                }
            }
        }
        
        repositories {
            maven {
                name = "OSSRH"
                url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }

    signing {
        sign publishing.publications.release
    }
}
```

2. Create a `gradle.properties` file in your project root (if it doesn't exist) and add your credentials:

```properties
# Sonatype OSSRH credentials
ossrhUsername=your_sonatype_username
ossrhPassword=your_sonatype_password

# Signing configuration
signing.keyId=your_gpg_key_id
signing.password=your_gpg_key_password
signing.secretKeyRingFile=/path/to/your/secring.gpg
```

### Step 4: Publish Your Library

Run the following command to publish your library:

```bash
./gradlew publishReleasePublicationToOSSRHRepository
```

### Step 5: Release on Sonatype

1. Log in to [Sonatype Nexus](https://s01.oss.sonatype.org/)
2. Go to "Staging Repositories"
3. Find your repository
4. Click "Close" and wait for validation
5. If validation passes, click "Release"

## Option 2: Publishing with JitPack (Easiest)

JitPack is the simplest way to publish your library. You don't need to set up any accounts or configure complex publishing tasks.

### Step 1: Prepare Your Library

Ensure your library's build.gradle has the maven-publish plugin:

```gradle
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'maven-publish'
}

// ... your existing configuration ...

afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release

                groupId = 'com.github.yourusername'
                artifactId = 'watermark'
                version = '1.0.0'
            }
        }
    }
}
```

### Step 2: Push to GitHub

1. Create a GitHub repository for your library
2. Push your code to the repository
3. Create a release or tag (e.g., v1.0.0)

### Step 3: Activate on JitPack

1. Go to [JitPack.io](https://jitpack.io/)
2. Enter your GitHub repository URL (e.g., https://github.com/yourusername/watermark)
3. Click "Look up"
4. Click "Get it" on the version you want to publish

That's it! JitPack will build your library and make it available for others to use.

## Option 3: Publishing to GitHub Packages

### Step 1: Generate a GitHub Personal Access Token

1. Go to GitHub Settings > Developer settings > Personal access tokens
2. Generate a new token with the `read:packages` and `write:packages` scopes
3. Copy the token

### Step 2: Configure Gradle for GitHub Packages

Add the following to your library's build.gradle:

```gradle
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'maven-publish'
}

// ... your existing configuration ...

afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release

                groupId = 'com.musarayy'
                artifactId = 'watermark'
                version = '1.0.0'
            }
        }
        
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/yourusername/watermark")
                credentials {
                    username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USERNAME")
                    password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
```

### Step 3: Set Up Credentials

Create a `gradle.properties` file in your project root (if it doesn't exist) and add your GitHub credentials:

```properties
gpr.user=your_github_username
gpr.key=your_github_personal_access_token
```

### Step 4: Publish Your Library

Run the following command to publish your library:

```bash
./gradlew publishReleasePublicationToGitHubPackagesRepository
```

## Conclusion

For your case, I recommend using JitPack as it's the simplest option and doesn't require setting up accounts or complex configurations. It's also free and allows you to keep your code private while still making the compiled library available for others to use.

If you need more control or want to publish to the official Maven Central repository, follow the instructions for Option 1.