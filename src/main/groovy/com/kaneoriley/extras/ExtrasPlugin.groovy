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

package com.kaneoriley.extras

import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

import java.text.SimpleDateFormat

@SuppressWarnings("GroovyUnusedDeclaration")
class ExtrasPlugin implements Plugin<Project> {

    private boolean isLibrary;

    void apply(Project project) {
        if (project.plugins.findPlugin("com.android.library") || project.plugins.findPlugin("android-library")) {
            isLibrary = true;
        } else if (!(project.plugins.findPlugin("com.android.application") || project.plugins.findPlugin("android"))) {
            throw new ProjectConfigurationException("Must be applied after 'android' or 'android-library' plugin.", null)
        }

        project.ext.dateCode = getDate().toInteger()
        project.ext.shortDateCode = getShortDate().toInteger()

        // Set version code base on current date
        project.android.defaultConfig.versionCode = getDate().toInteger()

        // Publish non default so we can perform debug builds in libraries or for wear
        project.android.publishNonDefault true

        // Setup build types
        if (isLibrary) {
            project.android.buildTypes {
                release {}
                debug {}
                staging {}
            }
        } else {
            project.android.buildTypes {
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

            // special logic to assign a different version code to each apk architecture
            project.afterEvaluate {
                project.android.applicationVariants.each { BaseVariant variant ->
                    variant.outputs.each { ApkVariantOutput vo ->
                        if (!vo.outputFile.name.endsWith('.apk')) {
                            return;
                        }

                        String appName = project.name
                        Integer versionCode = variant.mergedFlavor.versionCode
                        String versionName = variant.mergedFlavor.versionName
                        String buildType = variant.buildType.name

                        String filename = "${appName}-${versionName}-${versionCode}-${buildType}.apk".toLowerCase()
                        //noinspection GroovyAssignabilityCheck
                        vo.outputFile = new File(vo.outputFile.parentFile, filename)
                    }
                }
            }
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