package com.karsta26.bun.tool

import com.intellij.lang.javascript.buildTools.base.actions.JsbtShowTasksAction

class ShowBunScriptsAction : JsbtShowTasksAction(BunScriptsApplicationService.getInstance())