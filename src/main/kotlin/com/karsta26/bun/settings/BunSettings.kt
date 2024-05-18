package com.karsta26.bun.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.messages.Topic
import com.karsta26.bun.BunBundle

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

    fun isBunLockFilePresent() = FilenameIndex.getVirtualFilesByName(
        BunBundle.message("bun.lock.file"),
        GlobalSearchScope.allScope(project)
    ).isNotEmpty()

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