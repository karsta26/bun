package com.karsta26.bun.run

enum class BunCommand {
    RUN,
    BUILD,
    X,
    INIT,
    CREATE,
    ADD,
    UPDATE,
    LINK,
    REMOVE,
    UNLINK,
    PM,
    DEV,
    UPGRADE,
    HELP,
    INSTALL;

    var command = name.lowercase()
    override fun toString() = command
}
