package src.main.gui

import java.io.File

sealed class TreeItem(private val name: String) {
    override fun toString(): String = name
    override fun hashCode(): Int = name.hashCode()
    override fun equals(other: Any?): Boolean = false

    class FileItem(val file: File) : TreeItem(file.name)
}