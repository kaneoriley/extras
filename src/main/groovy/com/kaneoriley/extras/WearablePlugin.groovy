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
class WearablePlugin extends ApplicationPlugin {

    @Override
    public void apply(Project project) {
        super.apply(project)

        applySettings(project)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static def applySettings(context) {

        context.dependencies {
            compile "com.google.android.gms:play-services-wearable:${googleServicesVersion}"

            debugWearApp project(path: ':wearable', configuration: 'debug')
            stagingWearApp project(path: ':wearable', configuration: 'staging')
            releaseWearApp project(path: ':wearable', configuration: 'release')
        }

    }

}