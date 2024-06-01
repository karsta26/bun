package com.karsta26.bun.listeners

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.karsta26.bun.settings.BunSettings

class BunProjectDetector : ProjectActivity {
    override suspend fun execute(project: Project) {
        readAction {
            if (BunSettings.getInstance(project).isBunLockFilePresent()) {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("bun")
                    .createNotification("It looks like you are using Bun!", NotificationType.INFORMATION)
                    .notify(project)
            }
        }
    }
}