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

import org.gradle.api.Project;

@SuppressWarnings("GroovyUnusedDeclaration")
class LibraryPlugin extends CorePlugin {

    @Override
    public void apply(Project project) {
        project.apply plugin: 'android-sdk-manager'
        project.apply plugin: 'com.android.library'
        project.apply plugin: 'com.kaneoriley.fixcrashlytics'

        applySettings(project)

        super.apply(project)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static def applySettings(context) {

        context.android {
            defaultConfig {
                buildConfigField 'String', 'APP_PACKAGE_NAME', "\"${context.projectApplicationId}\""
                buildConfigField 'String', 'APP_DISPLAY_NAME', "\"${context.projectApplicationName}\""
            }
        }

    }

}