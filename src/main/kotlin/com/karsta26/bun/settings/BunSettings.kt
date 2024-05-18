package com.karsta26.bun.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.messages.Topic

@Service(Service.Level.PROJECT)
@State(name = "BunSettings", storages = [(Storage("bun.xml"))])
class BunSettings(private val project: Project) :
    SimplePersistentStateComponent<BunSettingsState>(BunSettingsState()) {

    var executablePath
        get() = state.executablePath ?: ""
        set(value) {
            state.executablePath = value
        }

    override fun noStateLoaded() {
        super.noStateLoaded()
        loadState(BunSettingsState())
    }

    @Synchronized
    fun update(block: (BunSettings) -> Unit) {
        val publisher = project.messageBus.syncPublisher(ChangeListener.TOPIC)
        block(this)
        publisher.settingsChanged(this)
    }

    interface ChangeListener {
        fun settingsChanged(settings: BunSettings) {
        }

        companion object {
            @Topic.ProjectLevel
            @JvmField
            val TOPIC = Topic("BunSettingsChanged", ChangeListener::class.java, Topic.BroadcastDirection.NONE)
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): BunSettings = project.service()
    }
}