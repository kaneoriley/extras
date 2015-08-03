/*
 * Copyright (C) 2015 Kane O'Riley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kaneoriley.extras;

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

import java.text.SimpleDateFormat

@SuppressWarnings("GroovyUnusedDeclaration")
abstract class CorePlugin implements Plugin<Project> {

    public static String appCompatVersion = '22.2.1'
    public static String googleServicesVersion = '7.5.0'

    private boolean isLibrary;

    @Override
    public void apply(Project project) {

        if (project.plugins.findPlugin("com.android.library") || project.plugins.findPlugin("android-library")) {
            isLibrary = true;
        } else if (!(project.plugins.findPlugin("com.android.application") || project.plugins.findPlugin("android"))) {
            throw new ProjectConfigurationException("Must be applied after 'android' or 'android-library' plugin.", null)
        }

        project.ext.dateCode = getDate().toInteger()
        project.ext.shortDateCode = getShortDate().toInteger()
        project.ext.appCompatVersion = appCompatVersion
        project.ext.googleServicesVersion = googleServicesVersion

        applySettings(project)

        if (!isLibrary) {
            project.android {
                packagingOptions {
                    exclude 'META-INF/services/javax.annotation.processing.Processor'
                    exclude 'META-INF/LICENSE.txt'
                    exclude 'META-INF/LICENSE'
                    exclude 'META-INF/NOTICE.txt'
                    exclude 'META-INF/NOTICE'
                    exclude 'LICENSE.txt'
                }

                buildTypes {
                    release {
                        debuggable false
                        minifyEnabled false
                        shrinkResources false
                    }
                    debug {
                        debuggable true
                        versionNameSuffix ".${getShortDate()}-debug"
                        applicationIdSuffix '.debug'
                        minifyEnabled false
                        shrinkResources false
                    }
                    staging {
                        debuggable false
                        versionNameSuffix ".${getShortDate()}-staging"
                        applicationIdSuffix '.staging'
                    }
                }
            }

        }
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static def applySettings(context) {

        context.apply plugin: 'com.jakewharton.hugo'
        context.apply plugin: 'android-apt'
        context.apply plugin: 'me.tatarka.retrolambda'

        context.android {

            signingConfigs {
                global {
                    keyAlias context.extraKeyAlias
                    keyPassword context.extraKeyPassword
                    storeFile context.file(context.extraStoreFile)
                    storePassword context.extraStorePassword
                }
            }

            compileSdkVersion 22
            buildToolsVersion '23.0.0 rc3'

            defaultConfig {
                minSdkVersion 19
                targetSdkVersion 22

                manifestPlaceholders += [ manifestApplicationId: "${context.projectApplicationId}" ]

                versionCode = getDate().toInteger()
                signingConfig signingConfigs.global
            }

            buildTypes {
                release {}
                debug {}
                staging {}
            }

            compileOptions {
                sourceCompatibility JavaVersion.VERSION_1_8
                targetCompatibility JavaVersion.VERSION_1_8
            }

            publishNonDefault true
        }

        context.dependencies {
            compile context.fileTree(dir: 'libs', include: '*.jar')
            provided 'org.projectlombok:lombok:1.16.4'
        }

    }

    static def getDate() {
        def df = new SimpleDateFormat("yyMMddHH")
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        df.setTimeZone(tz)
        return df.format(new Date())
    }

    static def getShortDate() {
        def df = new SimpleDateFormat("yyMMdd")
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        df.setTimeZone(tz)
        return df.format(new Date())
    }

}