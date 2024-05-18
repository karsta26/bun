package com.karsta26.bun.run

import com.intellij.diagnostic.logging.LogsGroupFragment
import com.intellij.execution.ui.*
import com.intellij.openapi.externalSystem.service.execution.configuration.fragments.SettingsEditorFragmentContainer
import com.karsta26.bun.run.fragments.BunParameterFragments

class BunSettingsEditor(runConfiguration: BunRunConfiguration) :
    RunConfigurationFragmentedEditor<BunRunConfiguration>(runConfiguration) {

    override fun createRunFragments(): List<SettingsEditorFragment<BunRunConfiguration, *>> {
        val commonParameterFragments: CommonParameterFragments<BunRunConfiguration> =
            CommonParameterFragments(project) { null }

        return SettingsEditorFragmentContainer.fragments {
            add(CommonParameterFragments.createRunHeader())
            add(CommonParameterFragments.createWorkingDirectory(project) { null })
            add(commonParameterFragments.programArguments())
            add(BunParameterFragments.scriptFile())
            add(CommonParameterFragments.createEnvParameters())
            addAll(BeforeRunFragment.createGroup())
            add(CommonTags.parallelRun())
            add(LogsGroupFragment())
        }
    }
}