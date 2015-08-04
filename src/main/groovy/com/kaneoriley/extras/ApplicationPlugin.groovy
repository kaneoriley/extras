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

import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project;

@SuppressWarnings("GroovyUnusedDeclaration")
class ApplicationPlugin extends CorePlugin {

    @Override
    public void apply(Project project) {
        project.apply plugin: 'android-sdk-manager'
        project.apply plugin: 'com.android.application'
        project.apply plugin: 'io.fabric'

        applySettings(project)

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
        super.apply(project)

        project.apt {
            arguments {
                goudaPackageName project.android.defaultConfig.applicationId
                goudaPrivateFields 'true'
            }
        }
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static def applySettings(context) {

        context.android {
            defaultConfig {
                applicationId context.extraApplicationId
                versionName context.extraVersionName
            }
        }

    }

}
