package com.karsta26.bun.run.before

import com.intellij.execution.BeforeRunTask
import com.intellij.openapi.components.PersistentStateComponent
import com.karsta26.bun.run.BunRunConfigurationOptions

class BunBeforeRunTask : BeforeRunTask<BunBeforeRunTask>(BUN_RUN_PROVIDER_ID),
    PersistentStateComponent<BunRunConfigurationOptions> {
    private var state = BunRunConfigurationOptions()
    override fun getState() = state
    override fun loadState(state: BunRunConfigurationOptions) {
        this.state = state
    }
}
